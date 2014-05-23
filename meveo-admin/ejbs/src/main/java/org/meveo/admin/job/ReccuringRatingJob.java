package org.meveo.admin.job;

import java.math.BigDecimal;
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
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.admin.exception.IncorrectChargeTemplateException;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.InvoiceSubCategoryCountryService;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;

@Startup
@Singleton
public class ReccuringRatingJob implements Job {

	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;
	
	@Inject
	JobExecutionService jobExecutionService;

	@Inject
	private RecurringChargeInstanceService recurringChargeInstanceService;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatingService chargeApplicationRatingService;

	@Inject
	private InvoiceSubCategoryCountryService invoiceSubCategoryCountryService;
	
    @Inject
    protected Logger log;
    
	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute RecurringRatingJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			List<RecurringChargeInstance> activeRecurringChargeInstances = recurringChargeInstanceService
					.findByStatus(InstanceStatusEnum.ACTIVE, DateUtils.addDaysToDate(new Date(), 1));

			log.info("# charge to rate:" + activeRecurringChargeInstances.size());
			for (RecurringChargeInstance activeRecurringChargeInstance : activeRecurringChargeInstances) {
				try {
					RecurringChargeTemplate recurringChargeTemplate = (RecurringChargeTemplate) activeRecurringChargeInstance
							.getRecurringChargeTemplate();
					if (recurringChargeTemplate.getCalendar() == null) {
						// FIXME : should not stop the method execution
						throw new IncorrectChargeTemplateException(
								"Recurring charge template has no calendar: code="
										+ recurringChargeTemplate.getCode());
					}
					Date applicationDate = null;
					if (recurringChargeTemplate.getApplyInAdvance()) {
						applicationDate = activeRecurringChargeInstance.getNextChargeDate();
					} else {
						applicationDate = activeRecurringChargeInstance.getChargeDate();
					}

					log.info("applicationDate=" + applicationDate);

					applicationDate = DateUtils.parseDateWithPattern(applicationDate, "dd/MM/yyyy");

					if (!recurringChargeTemplate.getApplyInAdvance()) {
						walletOperationService
								.applyNotAppliedinAdvanceReccuringCharge(
										activeRecurringChargeInstance, false,
										recurringChargeTemplate, null);
						result.registerSucces();
					} else {
						walletOperationService.applyReccuringCharge(activeRecurringChargeInstance, false, recurringChargeTemplate, null);
						result.registerSucces();
					}
				} catch (Exception e) {
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.close("");
		return result;
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression, timerConfig);
		return timer.getHandle();
	}

	boolean running = false;

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				running = true;
                Provider provider=providerService.findById(info.getProviderId());
                JobExecutionResult result=execute(info.getParametres(),provider);
                jobExecutionService.persistResult(this, result,info,provider);
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
