package org.meveo.service.job;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
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
@Lock(LockType.READ)
public abstract class Job {

    public static String CFT_PREFIX= "JOB";
    
    @Resource
    protected TimerService timerService;

    @Inject
    protected JobExecutionService jobExecutionService;
    
    @Inject
    protected JobInstanceService jobInstanceService;
    
    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    protected UserService userService;
    
    @Inject
    @Processed
    private Event<JobExecutionResultImpl> eventJobProcessed;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;
    
    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void init() {
        JobInstanceService.registerJob(this);
    }

    /**
     * Trigger the execution of the job
     * 
     * @param parameter a serialized representations of the parameters that the admin could manually put in the GUI when creating a timer
     * @return the result of execute(parameter,false) method
     */
        
    public void execute(JobInstance jobInstance) {
        JobExecutionResultImpl result = new JobExecutionResultImpl();
    
        if (!jobInstanceService.isJobRunning(jobInstance.getId()) && (jobInstance.isActive())) {
            log.debug("Job {} of type {} execution start", jobInstance.getCode(), jobInstance.getJobTemplate());

            try {            	            	
            	JobInstanceService.runningJobs.add(jobInstance.getId());
                
                log.debug("Executing job {} with currentUser {} and provider {} ", jobInstance.getCode(),currentUser.getSubject(), appProvider.getCode());
                
                execute(result, jobInstance);
                result.close();

                log.trace("Job {} of type {} executed. Persisting job execution results", jobInstance.getCode(), jobInstance.getJobTemplate());
                jobExecutionService.persistResult(this, result, jobInstance, getJobCategory());
                log.debug("Job {} of type {} execution finished", jobInstance.getCode(), jobInstance.getJobTemplate());
                eventJobProcessed.fire(result); 
            } catch (Exception e) {
                log.error("Failed to execute a job {} of type {}", jobInstance.getJobTemplate(), jobInstance.getJobTemplate(), e);
            } finally {            	
                JobInstanceService.runningJobs.remove(jobInstance.getId());
                
            }
        } else {
            log.trace("Job {} of type {} execution will be skipped. Reason: isRunning={}, isActive={}, currentUser={} and provider {}", jobInstance.getCode(), jobInstance.getJobTemplate(),
            		jobInstanceService.isJobRunning(jobInstance.getId()), jobInstance.isActive(), currentUser.getSubject(), appProvider.getCode());
        }
    }

    protected abstract void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException;
    
    /**
     * This method is called by the api to return the execution result id.
     * @param jobInstance
     * @param result
     * @param currentUser
     */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobInstance jobInstance, JobExecutionResultImpl result) {    
        if (!jobInstanceService.isJobRunning(jobInstance.getId()) && (jobInstance.isActive() || currentUser != null)) {
            log.debug("Job {} of type {} execution start", jobInstance.getCode(), jobInstance.getJobTemplate());

            try {
            	JobInstanceService.runningJobs.add(jobInstance.getId());
                execute(result, jobInstance);
                result.close();
                log.trace("Job {} of type {} executed. Persisting job execution results", jobInstance.getCode(), jobInstance.getJobTemplate());
                jobExecutionService.persistResult(this, result, jobInstance, getJobCategory());
                log.debug("Job {} of type {} execution finished", jobInstance.getCode(), jobInstance.getJobTemplate());
                eventJobProcessed.fire(result);
            } catch (Exception e) {
                log.error("Failed to execute a job {} of type {}", jobInstance.getJobTemplate(), jobInstance.getJobTemplate(), e);
            } finally {
            	JobInstanceService.runningJobs.remove(jobInstance.getId());
            }
        } else {
            log.trace("Job {} of type {} execution will be skipped. Reason: isRunning={}, isActive={}, currentUser={}", jobInstance.getCode(), jobInstance.getJobTemplate(),
            		jobInstanceService.isJobRunning(jobInstance.getId()), jobInstance.isActive());
        }
    }

    public Timer createTimer(ScheduleExpression scheduleExpression, JobInstance jobInstance) {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(jobInstance);
        timerConfig.setPersistent(false);
        return timerService.createCalendarTimer(scheduleExpression, timerConfig);
    }

    public void cleanAllTimers() {
        Collection<Timer> alltimers = timerService.getTimers();
        log.info("Cancel " + alltimers.size() + " timers for" + this.getClass().getSimpleName());

        for (Timer timer : alltimers) {
            try {
                timer.cancel();
            } catch (Exception e) {
                log.error("failed to clean all timers ",e);
            }
        }
    }

    /**
     * You must implement this method and add the @Timeout annotation to it
     * 
     * @param timer
     */
    @Timeout
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void trigger(Timer timer) {
        execute((JobInstance) timer.getInfo());
    }

    public JobExecutionService getJobExecutionService() {
        return jobExecutionService;
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
