package org.meveo.services.job;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;

import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.crm.impl.ProviderService;

@Startup
@Singleton
public class JobExample implements Job {

	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;
	
	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	private Logger log;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		long nbItemsToProcess = Math.round(Math.random() * 1000) + 100;
		result.setNbItemsToProcess(nbItemsToProcess); // it might well happen we
														// dont know in advance
														// how many items we
														// have to process, in
														// that case comment
														// this method
		for (int i = 0; i < nbItemsToProcess; i++) {
			long res = Math.round(Math.random() * 100);
			if (res == 0) {
				result.registerError("error" + i);
			} else if (res < 5) {
				result.registerWarning("warning" + i);
				System.out.println("warning in example job");
			} else {
				result.registerSucces();
			}
		}
		result.close("job example executed with parameter :" + parameter);
		return result;
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression, timerConfig);
		return timer.getHandle();
	}

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (info.isActive()) {
            Provider provider=providerService.findById(info.getProviderId());
            JobExecutionResult result=execute(info.getParametres(),provider);
            jobExecutionService.persistResult(this, result,info,provider);
		}
	}

	@Override
	public Collection<Timer> getTimers() {
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

}
