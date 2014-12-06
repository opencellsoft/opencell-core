package org.meveo.admin.job;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

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
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class UsageRatingJob implements Job {

	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;

	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	EdrService edrService;

	@Inject
	UsageRatingService usageRatingService;

	private Logger log = Logger.getLogger(UsageRatingJob.class.getName());

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute UsageRatingJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			List<EDR> edrs = edrService.getEDRToRate();
			log.info("edr to rate:" + edrs.size());
			
			for (EDR edr : edrs) {
				log.info("rate edr " + edr.getId());
				try {
					usageRatingService.ratePostpaidUsage(edr);
					edrService.update(edr);
					if (edr.getStatus() == EDRStatusEnum.RATED) {
						result.registerSucces();
					} else {
						result.registerError(edr.getRejectReason());
					}
				} catch (Exception e) {
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.close("");
		return result;
	}

	@Override
	public Timer createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(false);
		return timerService
				.createCalendarTimer(scheduleExpression, timerConfig);
	}

	boolean running = false;

	@Timeout
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
		System.out.println("cancel " + alltimers.size() + " timers for"
				+ this.getClass().getSimpleName());
		for (Timer timer : alltimers) {
			try {
				timer.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
