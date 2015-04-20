package org.meveo.service.job;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
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

    @Resource
    protected TimerService timerService;

    @Inject
    protected JobExecutionService jobExecutionService;

    @Inject
    protected UserService userService;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected boolean running = false;

    @PostConstruct
    public void init() {
        TimerEntityService.registerJob(this);
    }

    /**
     * Trigger the execution of the job
     * 
     * @param parameter a serialized representations of the parameters that the admin could manually put in the GUI when creating a timer
     * @param provider the provider for which the job must apply.
     * @return the result of execute(parameter,false) method
     */
    @Asynchronous
    public void execute(TimerEntity timerEntity, User currentUser) {
        JobExecutionResultImpl result = new JobExecutionResultImpl();
        TimerInfo info = timerEntity.getTimerInfo();
        if (!running && (info.isActive() || currentUser != null)) {
            try {
                running = true;
                if (currentUser == null) {
                    currentUser = userService.findByIdLoadProvider(info.getUserId());
                }
                execute(result, timerEntity, currentUser);
                result.close();

                jobExecutionService.persistResult(this, result, timerEntity, currentUser, getJobCategory());
                
            } catch (Exception e) {
                log.error("Failed to execute a job {}", timerEntity.getJobName(), e);
                
            } finally {
                running = false;
            }
        }
    }

    protected abstract void execute(JobExecutionResultImpl result, TimerEntity timerEntity, User currentUser) throws BusinessException;

    public Timer createTimer(ScheduleExpression scheduleExpression, TimerEntity infos) {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(infos);
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
                log.error(e.getMessage());
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
        execute((TimerEntity) timer.getInfo(), null);
    }

    public JobExecutionService getJobExecutionService() {
        return jobExecutionService;
    }

    public abstract JobCategoryEnum getJobCategory();

    public List<CustomFieldTemplate> getCustomFields(User currentUser) {

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
