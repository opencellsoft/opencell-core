package org.meveo.admin.job.dwh;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class MeasurableQuantityAggregationJob implements Job {

	protected Logger log = LoggerFactory
			.getLogger(MeasurableQuantityAggregationJob.class);

	@Inject
	private UserService userService;

	@Resource
	private TimerService timerService;

	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	private MeasurableQuantityService mqService;

	@Inject
	private MeasuredValueService mvService;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void aggregateMeasuredValues(JobExecutionResultImpl result,
			String report, MeasurableQuantity mq) {
		if (StringUtils.isBlank(report)) {
			report = "Generate Measured Value for : " + mq.getCode();
		} else {
			report += "," + mq.getCode();
		}
		Object[] mvObject = mqService.executeMeasurableQuantitySQL(mq);

		try {
			if (mvObject.length > 0) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				MeasuredValue mv = new MeasuredValue();
				mv.setMeasurableQuantity(mq);
				mv.setMeasurementPeriod(mq.getMeasurementPeriod());
				mv.setDate(sdf.parse(mvObject[0] + ""));
				mv.setValue(Long.parseLong(mvObject[1] + ""));
				mvService.create(mv);
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void aggregateMeasuredValues(JobExecutionResultImpl result,
			String report, List<MeasurableQuantity> mq) {
		for (MeasurableQuantity measurableQuantity : mq) {
			aggregateMeasuredValues(result, report, measurableQuantity);
		}

	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("executing MeasurableQuantityAggregation");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		result.setProvider(currentUser.getProvider());

		String report = "";
		if (parameter != null) {
			if (!parameter.isEmpty()) {
				MeasurableQuantity mq = mqService.listByCode(parameter).get(0);
				aggregateMeasuredValues(result, report, mq);
				result.setReport(report);
			} else {
				aggregateMeasuredValues(result, report, mqService.list());
				result.setReport(report);
			}
		} else {
			aggregateMeasuredValues(result, report, mqService.list());
			result.setReport(report);
		}

		result.setDone(true);

		return result;
	}

	public Long getMeasuredValueListValueSum(List<MeasuredValue> mvList) {
		long mvTotal = 0;
		for (MeasuredValue mv : mvList) {
			mvTotal += mv.getValue();
		}
		return mvTotal;
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
