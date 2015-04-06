package org.meveo.service.job;

import java.util.List;

import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;

import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.TimerEntity;

/**
 * 
 * Interface that must implement all jobs that are managed in meveo application
 * by the JobService bean. The implementation must be a session EJB and must
 * statically register itself (through its jndi name) to the JobService
 * 
 * @author seb
 *
 */
public interface Job {

	/**
	 * Trigger the execution of the job
	 * 
	 * @param parameter
	 *            a serialized representations of the parameters that the admin
	 *            could manually put in the GUI when creating a timer
	 * @param provider
	 *            the provider for which the job must apply.
	 * @return the result of execute(parameter,false) method
	 */
	public void execute(TimerEntity timerEntity, User currentUser);

	public Timer createTimer(ScheduleExpression scheduleExpression, TimerEntity timerEntity);

	public void cleanAllTimers();

	/**
	 * You must implement this method and add the @Timeout annotation to it
	 * 
	 * @param timer
	 */
	public void trigger(Timer timer);

	public JobExecutionService getJobExecutionService();

	public JobCategoryEnum getJobCategory();
	
	public List<CustomFieldTemplate> getCustomFields(User currentUser);

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
