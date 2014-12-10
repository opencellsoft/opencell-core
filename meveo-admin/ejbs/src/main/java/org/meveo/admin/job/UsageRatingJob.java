package org.meveo.admin.job;

import java.util.Collection;
import java.util.List;

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
import javax.persistence.EntityManager;

import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class UsageRatingJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private EdrService edrService;

	@Inject
	private UsageRatingService usageRatingService;

	@Inject
	@MeveoJpaForJobs
	protected EntityManager em;

	private Logger log = LoggerFactory.getLogger(UsageRatingJob.class);

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute UsageRatingJob.");
		
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			List<EDR> edrs = edrService.getEDRToRate(em);
			log.info("edr to rate:" + edrs.size());

			for (EDR edr : edrs) {
				log.info("rate edr " + edr.getId());
				
				try {
					usageRatingService.ratePostpaidUsage(edr);
					edrService.update(em, edr, currentUser);
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
			log.error(e.getMessage());
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
				User currentUser = userService.findById(info.getUserId());
				JobExecutionResult result = execute(info.getParametres(),
						currentUser);
				jobExecutionService.persistResult(this, result, info,
						currentUser);
			} catch (Exception e) {
				log.error(e.getMessage());
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
		log.info("Cancel " + alltimers.size() + " timers for"
				+ this.getClass().getSimpleName());
		
		for (Timer timer : alltimers) {
			try {
				timer.cancel();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
}
