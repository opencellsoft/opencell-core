package org.meveo.service.job;

import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.TimerInfo;

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
	@Interceptors({ JobLoggingInterceptor.class })
	public JobExecutionResult execute(String parameter, User currentUser);

	public Timer createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos);

	public void cleanAllTimers();

	/**
	 * You must implement this method and add the @Timeout annotation to it
	 * 
	 * @param timer
	 */
	public void trigger(Timer timer);

	public JobExecutionService getJobExecutionService();

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
