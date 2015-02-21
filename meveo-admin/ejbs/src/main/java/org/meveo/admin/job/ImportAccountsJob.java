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

import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.TimerEntityService;
import org.slf4j.Logger;

@Startup
@Singleton
public class ImportAccountsJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private Logger log;

	@Inject
	private UserService userService;

	@Inject
	private ImportAccountsJobBean importAccountsJobBean;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		importAccountsJobBean.execute(result, currentUser);
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
						currentUser,getJobCategory());
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

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.IMPORT_HIERARCHY;
	}
}
