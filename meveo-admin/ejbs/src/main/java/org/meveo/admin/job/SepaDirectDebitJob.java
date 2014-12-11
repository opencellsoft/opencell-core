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

import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.sepa.SepaService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class SepaDirectDebitJob implements Job {

	private Logger log = LoggerFactory.getLogger(SepaDirectDebitJob.class);

	@Resource
	private TimerService timerService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private DDRequestLotOpService dDRequestLotOpService;

	@Inject
	private UserService userService;

	@Inject
	private SepaService SepaService;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute SepaDirectDebitJob.");
		
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		User user = null;
		try {
			user = userService.getSystemUser();
			List<DDRequestLotOp> ddrequestOps = dDRequestLotOpService
					.getDDRequestOps();
			log.info("ddrequestOps founded:" + ddrequestOps.size());
			
			for (DDRequestLotOp ddrequestLotOp : ddrequestOps) {
				try {
					if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.CREATE) {
						SepaService.createDDRquestLot(
								ddrequestLotOp.getFromDueDate(),
								ddrequestLotOp.getToDueDate(), user,
								ddrequestLotOp.getProvider());
					} else if (ddrequestLotOp.getDdrequestOp() == DDRequestOpEnum.FILE) {
						SepaService.exportDDRequestLot(ddrequestLotOp
								.getDdrequestLOT().getId());
					}
					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.PROCESSED);
				} catch (BusinessEntityException e) {
					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
					ddrequestLotOp.setErrorCause(StringUtils.truncate(
							e.getMessage(), 255, true));
				} catch (Exception e) {
					log.error(e.getMessage());
					
					ddrequestLotOp.setStatus(DDRequestOpStatusEnum.ERROR);
					ddrequestLotOp.setErrorCause(StringUtils.truncate(
							e.getMessage(), 255, true));
				}

			}
		} catch (Exception e) {
			log.error(e.getMessage());

		}

		result.close("");
		
		return result;
	}

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
		if ((!running) && (info.isActive())) {
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

	public Collection<Timer> getTimers() {
		return timerService.getTimers();
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
