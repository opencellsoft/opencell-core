package org.meveo.admin.job.dwh;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
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
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.TimerEntityService;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class MeasurableQuantityAggregationJob implements Job {

	protected Logger log = LoggerFactory.getLogger(MeasurableQuantityAggregationJob.class);

	@Inject
	private UserService userService;

	@Resource
	private TimerService timerService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private MeasurableQuantityService mqService;

	@Inject
	private MeasuredValueService mvService;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void aggregateMeasuredValues(JobExecutionResultImpl result, String report, MeasurableQuantity mq) {
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
				mv.setValue(new BigDecimal(mvObject[1] + ""));
				mvService.create(mv);
			}
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
		} catch (SecurityException e) {
			log.error(e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage());
		} catch (BusinessException e) {
			log.error(e.getMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void aggregateMeasuredValues(JobExecutionResultImpl result, String report, List<MeasurableQuantity> mq) {
		for (MeasurableQuantity measurableQuantity : mq) {
			aggregateMeasuredValues(result, report, measurableQuantity);
		}

	}

	@Override
	@Asynchronous
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	public void execute(TimerInfo info, User currentUser) {
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		if (!running && (info.isActive() || currentUser != null)) {
			try {
				running = true;
				if (currentUser == null) {
					currentUser = userService.findByIdLoadProvider(info.getUserId());
				}
				result.setProvider(currentUser.getProvider());
				mqService.setProvider(currentUser.getProvider());

				String report = "";
				if (info.getParametres() != null) {
					if (!info.getParametres().isEmpty()) {
						MeasurableQuantity mq = mqService.listByCode(info.getParametres()).get(0);
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

				jobExecutionService.persistResult(this, result, info, currentUser, getJobCategory());
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				running = false;
			}
		}
	}

	public BigDecimal getMeasuredValueListValueSum(List<MeasuredValue> mvList) {
		BigDecimal mvTotal = BigDecimal.ZERO;
		for (MeasuredValue mv : mvList) {
			mvTotal = mvTotal.add(mv.getValue());
		}
		return mvTotal;
	}

	boolean running = false;

	@Override
	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void trigger(Timer timer) {
		execute((TimerInfo) timer.getInfo(), null);
	}

	@Override
	public Timer createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(false);

		return timerService.createCalendarTimer(scheduleExpression, timerConfig);
	}

	@Override
	public void cleanAllTimers() {
		Collection<Timer> alltimers = timerService.getTimers();
		log.info("Cancel " + alltimers.size() + " timers for" + this.getClass().getSimpleName());
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

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.DWH;
	}
}
