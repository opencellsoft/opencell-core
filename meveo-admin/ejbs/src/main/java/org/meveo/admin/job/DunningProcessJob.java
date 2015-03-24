package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

import org.meveo.admin.dunning.UpgradeDunningLevel;
import org.meveo.admin.dunning.UpgradeDunningReturn;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.BayadDunningInputHistory;
import org.meveo.model.admin.DunningHistory;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.payments.ActionDunning;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.admin.impl.BayadDunningInputHistoryService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.TimerEntityService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.DunningHistoryService;
import org.meveo.service.payments.impl.DunningLOTService;
import org.meveo.service.payments.impl.DunningPlanService;
import org.slf4j.Logger;

@Startup
@Singleton
public class DunningProcessJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private DunningPlanService dunningPlanService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private UpgradeDunningLevel upgradeDunning;

	@Inject
	private DunningLOTService dunningLOTService;

	@Inject
	private DunningHistoryService dunningHistoryService;

	@Inject
	private BayadDunningInputHistoryService bayadDunningInputHistoryService;

	@Inject
	private Logger log;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
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
				Provider provider = currentUser.getProvider();

				for (DunningPlan dunningPlan : dunningPlanService.getDunningPlans()) {
					int loadedCustomerAccounts = 0;
					int errorCustomerAccounts = 0;
					int updatedCustomerAccounts = 0;
					List<ActionDunning> listActionDunning = new ArrayList<ActionDunning>();
					List<OtherCreditAndCharge> listOCC = new ArrayList<OtherCreditAndCharge>();

					List<CustomerAccount> customerAccounts = customerAccountService.getCustomerAccounts(dunningPlan
							.getCreditCategory(), dunningPlan.getPaymentMethod(), dunningPlan.getProvider().getCode());
					log.info(String.format("Found %s CustomerAccounts to check", (customerAccounts == null ? "null"
							: customerAccounts.size())));

					for (CustomerAccount customerAccount : customerAccounts) {
						try {
							log.info("Processing  customerAccounts code " + customerAccount.getCode());
							loadedCustomerAccounts++;
							BigDecimal balanceExigible = customerAccountService
									.customerAccountBalanceExigibleWithoutLitigation(customerAccount.getId(), null,
											new Date());
							log.info("balanceExigible " + balanceExigible);

							if (DowngradeDunningLevel(customerAccount, balanceExigible)) {
								updatedCustomerAccounts++;
							} else {
								UpgradeDunningReturn upgradeDunningReturn = upgradeDunning.execute(customerAccount,
										balanceExigible, dunningPlan);
								if (upgradeDunningReturn.isUpgraded()) {
									updatedCustomerAccounts++;
									listActionDunning.addAll(upgradeDunningReturn.getListActionDunning());
									listOCC.addAll(upgradeDunningReturn.getListOCC());
								}
							}
						} catch (Exception e) {
							errorCustomerAccounts++;
							log.error(e.getMessage());
							e.printStackTrace();
						}
					}

					DunningHistory dunningHistory = new DunningHistory();
					dunningHistory.setExecutionDate(new Date());
					dunningHistory.setLinesRead(loadedCustomerAccounts);
					dunningHistory.setLinesRejected(errorCustomerAccounts);
					dunningHistory.setLinesInserted(updatedCustomerAccounts);
					dunningHistory.setProvider(dunningPlan.getProvider());
					dunningHistoryService.create(dunningHistory);
					BayadDunningInputHistory bayadDunningInputHistory = createNewInputHistory(loadedCustomerAccounts,
							updatedCustomerAccounts, errorCustomerAccounts, new Date(), dunningPlan.getProvider());
					bayadDunningInputHistoryService.create(bayadDunningInputHistory);
					dunningLOTService.createDunningLOTAndCsvFile(listActionDunning, dunningHistory, provider);
				}

				result.close("");

				jobExecutionService.persistResult(this, result, info, currentUser, getJobCategory());
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				running = false;
			}
		}
	}

	@Override
	public Timer createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(false);
		return timerService.createCalendarTimer(scheduleExpression, timerConfig);
	}

	boolean running = false;

	@Timeout
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void trigger(Timer timer) {
		execute((TimerInfo) timer.getInfo(), null);
	}

	public boolean DowngradeDunningLevel(CustomerAccount customerAccount, BigDecimal balanceExigible) throws Exception {
		log.info("DowngradeDunningLevelStep ...");
		boolean isDowngradelevel = false;
		if (balanceExigible.compareTo(BigDecimal.ZERO) <= 0 && customerAccount.getDunningLevel() != DunningLevelEnum.R0) {
			customerAccount.setDunningLevel(DunningLevelEnum.R0);
			customerAccount.setDateDunningLevel(new Date());
			isDowngradelevel = true;
			customerAccountService.update(customerAccount);
			log.info("customerAccount code:" + customerAccount.getCode() + " updated to R0");
		}
		// attente besoin pour par exp : R3--> R2 avec actions

		return isDowngradelevel;
	}

	/**
	 * Creates input history object, to save it to DB.
	 */
	private BayadDunningInputHistory createNewInputHistory(int nbTicketsParsed, int nbTicketsSucceeded,
			int nbTicketsRejected, Date startDate, Provider provider) {
		BayadDunningInputHistory inputHistory = new BayadDunningInputHistory();
		inputHistory.setName(startDate.toString());
		inputHistory.setParsedTickets(nbTicketsParsed);
		inputHistory.setRejectedTickets(nbTicketsRejected);
		inputHistory.setSucceededTickets(nbTicketsSucceeded);
		inputHistory.setAnalysisStartDate(startDate);
		inputHistory.setAnalysisEndDate(new Date());
		inputHistory.setProvider(provider);
		return inputHistory;
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
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
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.ACCOUNT_RECEIVABLES;
	}
}
