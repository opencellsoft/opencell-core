package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.slf4j.Logger;

@Stateless
public class InvoicingJobBean {

	@Inject
	protected Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private BillingAccountService billingAccountService;
	
	@Inject
	RatedTransactionService ratedTransactionService;
	
	@Inject
	Conversation conversation;

	@Interceptors({ JobLoggingInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, User currentUser) {
		try {
			try {
				Provider provider = currentUser.getProvider();
				List<BillingRun> billingRuns = billingRunService
						.getbillingRuns(provider,
								BillingRunStatusEnum.NEW,
								BillingRunStatusEnum.ON_GOING,
								BillingRunStatusEnum.CONFIRMED);

				log.info("billingRuns to process={}", billingRuns.size());

				for (BillingRun billingRun : billingRuns) {
					try {
						try {
							if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {
								List<BillingAccount> billingAccounts = billingRunService.getBillingAccounts(billingRun);
								log.info("Nb billingAccounts to process={}",
										(billingAccounts != null ? billingAccounts.size() : 0));

								if (billingAccounts != null && billingAccounts.size() > 0) {
									int billableBA = 0;

									for (BillingAccount billingAccount : billingAccounts) {
										if (billingAccountService.updateBillingAccountTotalAmounts(billingAccount,billingRun,currentUser)) {
											billableBA++;
										}
									}

									billingRun.setBillingAccountNumber(billingAccounts.size());
									billingRun.setBillableBillingAcountNumber(billableBA);
									billingRun.setProcessDate(new Date());
									billingRun.setStatus(BillingRunStatusEnum.WAITING);
									billingRun.updateAudit(currentUser);
									billingRunService.updateNoCheck(billingRun);
									
									if (billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC
											|| currentUser.getProvider().isAutomaticInvoicing()) {
										billingRunService.createAgregatesAndInvoice( billingRun, currentUser);
									}
								}
							} else if (BillingRunStatusEnum.ON_GOING.equals(billingRun
									.getStatus())) {
								billingRunService.createAgregatesAndInvoice( billingRun, currentUser);
							} else if (BillingRunStatusEnum.CONFIRMED.equals(billingRun
									.getStatus())) {
								billingRunService.validate(billingRun,currentUser);
							}
						} catch (Exception e) {
							result.registerError(e.getMessage());
						}
					} catch (Exception e) {
						log.error("Error: {}", e.getMessage());
						result.registerError(e.getMessage());
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
