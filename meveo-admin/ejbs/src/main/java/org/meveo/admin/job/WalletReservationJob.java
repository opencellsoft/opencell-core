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
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.billing.impl.ReservationService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

/**
 * @author Edward P. Legaspi
 **/

@Startup
@Singleton
public class WalletReservationJob implements Job {

	@Inject
	private Logger log;

	@Resource
	private TimerService timerService;

	@Inject
	private ProviderService providerService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private ReservationService reservationService;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute " + getClass().getName());

		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			int rowsUpdated = reservationService
					.updateExpiredReservation(provider);
			if (rowsUpdated != 0) {
				log.info(rowsUpdated + " rows updated.");
			}
		} catch (Exception e) {
			result.registerError(e.getMessage());
			log.error(e.getMessage());
		}

		result.close("");
		return result;
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression,
				timerConfig);
		return timer.getHandle();
	}

	boolean running = false;

	@Override
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
	public Collection<Timer> getTimers() {
		return timerService.getTimers();
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

}
