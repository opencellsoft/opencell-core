package org.meveo.service.job;

import java.util.Collection;
import java.util.Date;

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
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class JobPurge implements Job {

	private Logger log = LoggerFactory.getLogger(JobPurge.class);

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		String jobname = "";
		int nbDays = 30;
		try {
			String[] params = parameter.split("-");
			jobname = params[0];
			nbDays = Integer.parseInt(params[1]);
		} catch (Exception e) {
		}
		Date date = DateUtils.addDaysToDate(new Date(), nbDays * (-1));
		long nbItemsToProcess = jobExecutionService.countJobsToDelete(jobname,
				date);
		result.setNbItemsToProcess(nbItemsToProcess); // it might well happen we
														// dont know in advance
														// how many items we
														// have to process, in
														// that case comment
														// this method
		int nbSuccess = jobExecutionService.delete(jobname, date);
		result.setNbItemsCorrectlyProcessed(nbSuccess);
		result.setNbItemsProcessedWithError(nbItemsToProcess - nbSuccess);
		result.close(nbSuccess > 0 ? ("purged " + jobname) : "");
		
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

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (info.isActive()) {
			User currentUser = userService.findById(info.getUserId());
			JobExecutionResult result = execute(info.getParametres(),
					currentUser);
			jobExecutionService.persistResult(this, result, info, currentUser,getJobCategory());
		}
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

	@Override
	public void cleanAllTimers() {
		Collection<Timer> alltimers = timerService.getTimers();
		log.info("cancel " + alltimers.size() + " timers for"
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
		return JobCategoryEnum.UTILS;
	}
}
