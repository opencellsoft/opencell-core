package org.meveo.service.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.event.qualifier.Processed;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

/**
 * Interface that must implement all jobs that are managed in meveo application by the JobService bean. The implementation must be a session EJB and must statically register itself
 * (through its jndi name) to the JobService.
 * 
 * @author seb
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
public abstract class Job {

    public static String CFT_PREFIX = "JobInstance";

    @Resource
    protected TimerService timerService;

    @Inject
    protected JobExecutionService jobExecutionService;

    @Inject
    private JobExecutionInJaasService jobExecutionInJaasService;

    @EJB
    protected JobInstanceService jobInstanceService;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    protected UserService userService;

    @Inject
    @Processed
    private Event<JobExecutionResultImpl> eventJobProcessed;

    @Inject
    protected ResourceBundle resourceMessages;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobCacheContainerProvider jobCacheContainerProvider;

    @Inject
    private AuditOrigin auditOrigin;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Execute job instance with results published to a given job execution result entity.
     * 
     * @param jobInstance Job instance to execute
     * @param executionResult Job execution results
     * @throws BusinessException business exception
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobInstance jobInstance, JobExecutionResultImpl executionResult) throws BusinessException {

        auditOrigin.setAuditOrigin(ChangeOriginEnum.JOB);
        auditOrigin.setAuditOriginName(jobInstance.getJobTemplate() + "/" + jobInstance.getCode());

        if (executionResult == null) {
            executionResult = new JobExecutionResultImpl();
            executionResult.setJobInstance(jobInstance);
        }

        JobRunningStatusEnum isRunning = jobCacheContainerProvider.markJobAsRunning(jobInstance.getId(), jobInstance.isLimitToSingleNode());
        if (isRunning == JobRunningStatusEnum.NOT_RUNNING || (isRunning == JobRunningStatusEnum.RUNNING_OTHER && !jobInstance.isLimitToSingleNode())) {
            log.debug("Starting Job {} of type {} with currentUser {}. Processors available {}, paralel procesors requested {}", jobInstance.getCode(),
                jobInstance.getJobTemplate(), currentUser.toStringShort(), Runtime.getRuntime().availableProcessors(),
                customFieldInstanceService.getCFValue(jobInstance, "nbRuns", false));

            try {
                execute(executionResult, jobInstance);
                executionResult.close();

                log.trace("Job {} of type {} executed. Persisting job execution results", jobInstance.getCode(), jobInstance.getJobTemplate());

                Boolean jobCompleted = jobExecutionService.persistResult(this, executionResult, jobInstance);
                log.debug("Job {} of type {} execution finished. Job completed {}", jobInstance.getCode(), jobInstance.getJobTemplate(), jobCompleted);
                eventJobProcessed.fire(executionResult);

                if (jobCompleted != null && jobCompleted) {
                    MeveoUser lastCurrentUser = currentUser.unProxy();
                    jobExecutionService.executeNextJob(this, jobInstance, !jobCompleted, lastCurrentUser);
                }

            } catch (Exception e) {
                log.error("Failed to execute a job {} of type {}", jobInstance.getJobTemplate(), jobInstance.getJobTemplate(), e);
                throw new BusinessException(e);
            } finally {
                jobCacheContainerProvider.markJobAsNotRunning(jobInstance.getId());
            }

        } else {
            log.trace("Job {} of type {} execution will be skipped. Reason: isRunning={}", jobInstance.getCode(), jobInstance.getJobTemplate(), isRunning);

            // Mark job a finished. Applies in cases where execution result was already saved to db - like when executing job from API
            if (!executionResult.isTransient()) {
                executionResult.close();
                jobExecutionService.persistResult(this, executionResult, jobInstance);
            }
        }

    }

    /**
     * Execute job instance with results published to a given job execution result entity. Executed in Asynchronous mode.
     * 
     * @param jobInstance Job instance to execute
     * @param result Job execution results
     * @throws BusinessException business exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeInNewTrans(JobInstance jobInstance, JobExecutionResultImpl result) throws BusinessException {

        execute(jobInstance, result);
    }

    /**
     * The actual job execution logic implementation.
     * 
     * @param result Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    protected abstract void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException;

    /**
     * Canceling timers associated to this job implmenentation- solves and issue when server is restarted and wildlfy data directory contains previously active timers.
     */
    public void cleanTimers() {

        Collection<Timer> alltimers = timerService.getTimers();
        log.info("Canceling job timers for job {}", this.getClass().getName());

        for (Timer timer : alltimers) {
            try {
                if (timer.getInfo() instanceof JobInstance) {
                    timer.cancel();
                }
            } catch (Exception e) {
                log.error("Failed to cancel timer {} for job{}", timer.getHandle(), this.getClass().getName(), e);
            }
        }
    }

    /**
     * Register/schedule a timer for a job instance.
     * 
     * @param scheduleExpression Schedule expression
     * @param jobInstance Job instance to execute
     * @return Instantiated timer object
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Timer createTimer(ScheduleExpression scheduleExpression, JobInstance jobInstance) {

        jobInstance.setProviderCode(currentUser.getProviderCode());

        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(jobInstance);
        // timerConfig.setPersistent(false);
        // log.error("AKK creating a timer for {}", jobInstance.getCode());
        return timerService.createCalendarTimer(scheduleExpression, timerConfig);
    }

    /**
     * Trigger job execution uppon scheduler timer expiration.
     * 
     * @param timer Timer configuration with jobInstance entity as Info attribute
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void trigger(Timer timer) {

        JobInstance jobInstance = (JobInstance) timer.getInfo();
        if (jobInstance == null) {
            return;
        }

        try {
            jobExecutionInJaasService.executeInJaas(jobInstance, this);
        } catch (Exception e) {
            log.error("Failed to execute a job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate(), e);
        }
    }

    /**
     * @return job category enum
     */
    public abstract JobCategoryEnum getJobCategory();

    /**
     * @return map of custom fields
     */
    public Map<String, CustomFieldTemplate> getCustomFields() {

        return null;
    }

    /**
     * Gets the parameter CF value if found, otherwise return CF value from job definition
     *
     * @param jobInstance the job instance
     * @param cfCode Custom field code
     * @param defaultValue Default value if no value found
     * @return Parameter or custom field value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode, Object defaultValue) {
        Object value = getParamOrCFValue(jobInstance, cfCode);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Gets the parameter CF value if found , otherwise return CF value from customFieldInstanceService
     *
     * @param jobInstance the job instance
     * @param cfCode the cf code
     * @return the param or CF value
     */
    protected Object getParamOrCFValue(JobInstance jobInstance, String cfCode) {
        Object value = jobInstance.getParamValue(cfCode);
        if (value == null) {
            return customFieldInstanceService.getCFValue(jobInstance, cfCode);
        }
        return value;
    }

    /*
     * those methods will be used later for asynchronous jobs
     * 
     * public JobExecutionResult pause();
     * 
     * public JobExecutionResult resume();
     * 
     * public JobExecutionResult stop();
     * 
     * public JobExecutionResult getResult();
     */
}
