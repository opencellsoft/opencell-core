package org.meveo.service.job;

import java.util.Collection;
import java.util.Map;

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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.event.qualifier.Processed;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Interface that must implement all jobs that are managed in meveo application by the JobService bean. The implementation must be a session EJB and must statically register itself
 * (through its jndi name) to the JobService
 * 
 * @author seb
 * 
 */
public abstract class Job {

    public static String CFT_PREFIX = "JOB";

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

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Execute job instance with results published to a given job execution result entity.
     * 
     * @param jobInstance Job instance to execute
     * @param result Job execution results
     * @throws BusinessException
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobInstance jobInstance, JobExecutionResultImpl result) throws BusinessException {

        if (result == null) {
            result = new JobExecutionResultImpl();
        }

        JobRunningStatusEnum isRunning = jobCacheContainerProvider.isJobRunning(jobInstance.getId());
        if (isRunning == JobRunningStatusEnum.NOT_RUNNING || (isRunning == JobRunningStatusEnum.RUNNING_OTHER && !jobInstance.isLimitToSingleNode())) {
            log.debug("Starting Job {} of type {} with currentUser {}. Processors available {}, paralel procesors requested {}", jobInstance.getCode(),
                jobInstance.getJobTemplate(), currentUser.toStringShort(), Runtime.getRuntime().availableProcessors(),
                customFieldInstanceService.getCFValue(jobInstance, "nbRuns", false));

            try {
                jobCacheContainerProvider.markJobAsRunning(jobInstance.getId());
                execute(result, jobInstance);
                result.close();

                log.trace("Job {} of type {} executed. Persisting job execution results", jobInstance.getCode(), jobInstance.getJobTemplate());

                Boolean jobCompleted = jobExecutionService.persistResult(this, result, jobInstance);
                log.debug("Job {} of type {} execution finished. Job completed {}", jobInstance.getCode(), jobInstance.getJobTemplate(), jobCompleted);
                eventJobProcessed.fire(result);

                if (jobCompleted != null) {
                    jobExecutionService.executeNextJob(this, jobInstance, !jobCompleted);
                }

            } catch (Exception e) {
                log.error("Failed to execute a job {} of type {}", jobInstance.getJobTemplate(), jobInstance.getJobTemplate(), e);
                throw new BusinessException(e);
            } finally {
                jobCacheContainerProvider.markJobAsNotRunning(jobInstance.getId());
            }

        } else {
            log.trace("Job {} of type {} execution will be skipped. Reason: isRunning={}", jobInstance.getCode(), jobInstance.getJobTemplate(), isRunning);
        }

    }

    /**
     * Execute job instance with results published to a given job execution result entity. Executed in Asynchronous mode.
     * 
     * @param jobInstance Job instance to execute
     * @param result Job execution results
     * @throws BusinessException
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeInNewTrans(JobInstance jobInstance, JobExecutionResultImpl result) throws BusinessException {

        execute(jobInstance, result);
    }

    /**
     * The actual job execution logic implementation
     * 
     * @param result Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    protected abstract void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException;

    /**
     * Canceling timers associated to this job implmenentation- solves and issue when server is restarted and wildlfy data directory contains previously active timers
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
     * Register/schedule a timer for a job instance
     * 
     * @param scheduleExpression Schedule expression
     * @param jobInstance Job instance to execute
     * @return Instantiated timer object
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Timer createTimer(ScheduleExpression scheduleExpression, JobInstance jobInstance) {

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

        try {
            jobExecutionInJaasService.executeInJaas((JobInstance) timer.getInfo(), this);
        } catch (Exception e) {
            log.error("Failed to execute a job {} of type {}", jobInstance.getCode(), jobInstance.getJobTemplate(), e);
        }
    }

    public abstract JobCategoryEnum getJobCategory();

    public Map<String, CustomFieldTemplate> getCustomFields() {

        return null;
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
