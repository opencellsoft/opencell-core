package org.meveo.admin.job.dwh;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import org.meveocrm.model.dwh.MeasurementPeriodEnum;
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
			Date date, String report) {
		// FOR WEEKLY MEASUREMENT PERIOD
		Calendar cal = Calendar.getInstance(Locale.FRANCE);
		cal.setTime(date);

		while (cal.getTime().before(new Date())) {
			int firstDayOfTheWeek = cal.getFirstDayOfWeek();

			Calendar startDate = Calendar.getInstance(Locale.FRANCE);
			startDate.setTime(cal.getTime());

			int days = (startDate.get(Calendar.DAY_OF_WEEK) + 7 - firstDayOfTheWeek) % 7;
			startDate.add(Calendar.DATE, -days);

			Calendar endDate = Calendar.getInstance(Locale.FRANCE);
			endDate.setTime(startDate.getTime());
			endDate.add(Calendar.DATE, 6);
			endDate.add(Calendar.DAY_OF_MONTH, 1);
			try {
				for (MeasurableQuantity mq : mqService.list()) {
					long mvTotal = 0;
					List<MeasuredValue> mvList = mvService.getByDateAndPeriod(
							null, startDate.getTime(), endDate.getTime(),
							MeasurementPeriodEnum.DAILY, mq);
					if (mvList.size() > 0) {
						mvTotal = getMeasuredValueListValueSum(mvList);
					}

					List<MeasuredValue> mvWeeklyList = mvService
							.getByDateAndPeriod(null, startDate.getTime(),
									null, MeasurementPeriodEnum.WEEKLY, mq);
					MeasuredValue newMV = null;
					if (mvWeeklyList.size() > 0) {
						newMV = mvWeeklyList.get(0);
						newMV.setValue(mvTotal);
						mvService.update(newMV);
						result.registerSucces();
					} else {

						newMV = new MeasuredValue();
						newMV.setMeasurableQuantity(mq);
						newMV.setMeasurementPeriod(MeasurementPeriodEnum.WEEKLY);
						newMV.setValue(mvTotal);
						newMV.setDate(startDate.getTime());
						mvService.create(newMV);
						result.registerSucces();
					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}

			// FOR MONTHLY MEASUREMENT PERIOD
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			endDate.set(Calendar.DAY_OF_MONTH,
					cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			try {
				for (MeasurableQuantity mq : mqService.list()) {
					long mvTotal = 0;
					List<MeasuredValue> mvList = mvService.getByDateAndPeriod(
							null, startDate.getTime(), endDate.getTime(),
							MeasurementPeriodEnum.WEEKLY, mq);
					if (mvList.size() > 0) {
						mvTotal = getMeasuredValueListValueSum(mvList);
					}

					List<MeasuredValue> mvMonthlyList = mvService
							.getByDateAndPeriod(null, startDate.getTime(),
									null, MeasurementPeriodEnum.MONTHLY, mq);
					MeasuredValue newMV = null;
					if (mvMonthlyList.size() > 0) {
						newMV = mvMonthlyList.get(0);
						newMV.setValue(mvTotal);
						mvService.update(newMV);
						result.registerSucces();
					} else {

						newMV = new MeasuredValue();
						newMV.setMeasurableQuantity(mq);
						newMV.setMeasurementPeriod(MeasurementPeriodEnum.MONTHLY);
						newMV.setValue(mvTotal);
						newMV.setDate(startDate.getTime());
						mvService.create(newMV);
						result.registerSucces();

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
			log.info(cal.getTime().toString());

			cal.add(Calendar.DAY_OF_MONTH, 7);
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
				Date date = new Date();
				try {
					SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",
							Locale.FRANCE);
					date = df.parse(parameter);
					report = "Parameter Date = " + df.format(date);
					aggregateMeasuredValues(result, date, report);
					result.setReport(report);
					result.setDone(true);

					return result;
				} catch (ParseException e) {
					report = "Parameter Date = "
							+ Calendar.getInstance().getTime().toString();
					aggregateMeasuredValues(result, Calendar.getInstance()
							.getTime(), report);
					result.setReport(report);
					result.setDone(true);

					return result;
				}
			}
		}

		report = "Parameter Date = "
				+ Calendar.getInstance().getTime().toString();
		aggregateMeasuredValues(result, Calendar.getInstance().getTime(),
				report);
		result.setReport(report);
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
