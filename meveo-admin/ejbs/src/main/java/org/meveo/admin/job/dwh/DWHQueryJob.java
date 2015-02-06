package org.meveo.admin.job.dwh;

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

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.TimerEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
/**
 * This job is made to create MeasuredValue of some MeasurableQuantity whose code is given as parameter
 * The JPA query to execute is stored in the MeasurableQuantity, and we assume it returns
 * a list of (Date measureDate, Long value)
 * each result is used to create a MeasuredValue
 */
public class DWHQueryJob implements Job {

	protected Logger log = LoggerFactory.getLogger(DWHQueryJob.class);

	@Resource
	private TimerService timerService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private UserService userService;

	@Inject
	private DWHQueryBean queryBean;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("executing DWHQueryJob for " + parameter);
		
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		result.setProvider(currentUser.getProvider());
		
		try {
			queryBean.executeQuery(result,parameter,currentUser.getProvider());
		} catch (BusinessException e) {
			result.setReport("error:"+e.getMessage());
			e.printStackTrace();
		}
		result.setDone(true);
		return result;
	}

	boolean running = false;

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				if (info.isActive()) {
					running = true;
					User currentUser = userService.findById(info.getUserId());
					JobExecutionResult result = execute(info.getParametres(),
							currentUser);
					jobExecutionService.persistResult(this, result, info,
							currentUser);
				}
			} catch (Exception e) {
				log.error("error in trigger", e);
			} finally {
				running = false;
			}
		}
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
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

}
