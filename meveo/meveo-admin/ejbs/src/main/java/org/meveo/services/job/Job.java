package org.meveo.services.job;

import java.util.Collection;

import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;

import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.TimerInfo;


/**
 * 
 * Interface that must implement all jobs that are managed in meveo application
 * by the JobService bean.
 * The implementation must be a session EJB and must statically register itself (through its jndi name) to the JobService
 * 
 * @author seb
 *
 */
public interface Job {
	
    /**
     * Trigger the execution of the job
     * @param parameter a serialized representations of the parameters that the admin could manually put in the GUI
     *  when creating a timer
     * @return the result of execute(parameter,false) method
     */
    public JobExecutionResult execute(String parameter);
    
    /**
     * Trigger the execution of the job
     * @param parameter a serialized representations of the parameters that the admin could manually put in the GUI
     *  when creating a timer
     *  @param inSession true if the method is called with an active session context (for instance from the GUI)
     * @return the result of the job execution
     */
	public JobExecutionResult execute(String string, boolean inSession);

	public TimerHandle createTimer(ScheduleExpression scheduleExpression,TimerInfo infos);
	
	/**
	 * You must implement this method and add the @Timeout annotation to it 
	 * @param timer
	 */
	public void trigger(Timer timer);
	
	public Collection<Timer> getTimers();

	
    
    /*
     * those methods will be used later for asynchronous jobs
     * 
    public JobExecutionResult pause();
    
    public JobExecutionResult resume();
    
    public JobExecutionResult stop();
    
    public JobExecutionResult getResult();
    */
}
