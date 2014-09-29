package org.meveo.admin.job;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

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

import org.apache.commons.lang.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class BillingRunJob implements Job {

	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;

	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	private BillingRunService billingRunService;
	
	@Inject
	private BillingCycleService billingCycleService;

	private Logger log = Logger.getLogger(BillingRunJob.class.getName());

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute BillingRunJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			List<BillingRun>  billruns = billingRunService.getbillingRuns(provider,parameter);
			boolean notTerminatedBillRun=false;
			if(billruns!=null){
				for(BillingRun billrun:billruns){
					if(billrun.getStatus()==BillingRunStatusEnum.CONFIRMED || 
							billrun.getStatus()==BillingRunStatusEnum.NEW ||
							billrun.getStatus()==BillingRunStatusEnum.ON_GOING ||
							billrun.getStatus()==BillingRunStatusEnum.WAITING){
						notTerminatedBillRun=true;
						break;
					}
				}
			}
			if(!notTerminatedBillRun && !StringUtils.isEmpty(parameter)){
				BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(parameter, provider);
				if(billingCycle!=null){
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
			e.printStackTrace();
		}
		result.close("");
		return result;
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(true);
		Timer timer = timerService.createCalendarTimer(scheduleExpression,
				timerConfig);
		return timer.getHandle();
	}

	boolean running = false;

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
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}
}
