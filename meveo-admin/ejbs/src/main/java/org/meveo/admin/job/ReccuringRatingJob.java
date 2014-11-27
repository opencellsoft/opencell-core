package org.meveo.admin.job;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;

@Startup
@Singleton
public class ReccuringRatingJob implements Job {

	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;

	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	RecurringRatingJobBean recurringRatingJobBean;

	@Inject
	protected Logger log;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute RecurringRatingJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		recurringRatingJobBean.execute(result);
		result.close("");
		return result;
	}

	@Override
	public Timer createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(false);
		return timerService.createCalendarTimer(scheduleExpression,
				timerConfig);
	}

	boolean running = false;

	@Timeout()
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				running = true;
				Provider provider = providerService.findById(info
						.getProviderId());
				JobExecutionResult result = execute(info.getParametres(),
						provider);
				jobExecutionService.persistResult(this, result, info, provider);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				running = false;
			}
		}
	}


	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}
	

	@Override
	public void cleanAllTimers() {
		Collection<Timer> alltimers = timerService.getTimers();
		System.out.println("cancel "+alltimers.size() +" timers for"+this.getClass().getSimpleName());
		for(Timer timer:alltimers){
			try{
				timer.cancel();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
