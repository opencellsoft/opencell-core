package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingRunService;
import org.slf4j.Logger;

@Stateless
public class InvoicingJobBean {

	@Inject
	protected Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Interceptors({ JobLoggingInterceptor.class })
	public void execute(JobExecutionResultImpl result, User currentUser) {
		try {
			try {
				List<BillingRun> billingRuns = billingRunService
						.getbillingRuns(currentUser.getProvider(),
								BillingRunStatusEnum.NEW,
								BillingRunStatusEnum.ON_GOING,
								BillingRunStatusEnum.CONFIRMED);

				log.info("billingRuns to process={}", billingRuns.size());

				for (BillingRun billingRun : billingRuns) {
					try {
						billingRunService.processBillingRun(billingRun, result,
								currentUser);
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
