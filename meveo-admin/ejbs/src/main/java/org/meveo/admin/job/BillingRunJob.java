package org.meveo.admin.job;

import java.util.Collection;
import java.util.Date;
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

import org.apache.commons.lang.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;

@Startup
@Singleton
public class BillingRunJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	private Logger log;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute BillingRunJob.");

		Provider provider = currentUser.getProvider();
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			List<BillingRun> billruns = billingRunService.getbillingRuns(
					provider, parameter);
			boolean notTerminatedBillRun = false;
			if (billruns != null) {
				for (BillingRun billrun : billruns) {
					if (billrun.getStatus() == BillingRunStatusEnum.CONFIRMED
							|| billrun.getStatus() == BillingRunStatusEnum.NEW
							|| billrun.getStatus() == BillingRunStatusEnum.ON_GOING
							|| billrun.getStatus() == BillingRunStatusEnum.WAITING) {
						notTerminatedBillRun = true;
						break;
					}
				}
			}
			if (!notTerminatedBillRun && !StringUtils.isEmpty(parameter)) {
				BillingCycle billingCycle = billingCycleService
						.findByBillingCycleCode(parameter, provider);
				if (billingCycle != null) {
					BillingRun billingRun = new BillingRun();
					Auditable auditable = new Auditable();
					auditable.setCreated(new Date());
					billingRun.setAuditable(auditable);
					billingRun.setBillingCycle(billingCycle);
					billingRun.setStatus(BillingRunStatusEnum.NEW);
					billingRunService.create(billingRun);
					result.registerSucces();
				}
			}
		} catch (Exception e) {
			result.registerError(e.getMessage());
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
