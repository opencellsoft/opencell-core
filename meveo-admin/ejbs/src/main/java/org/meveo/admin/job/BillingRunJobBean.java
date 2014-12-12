package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

@Stateless
public class BillingRunJobBean {

	@Inject
	private Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private BillingCycleService billingCycleService;

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter,
			User currentUser) {
		Provider provider = currentUser.getProvider();

		try {
			List<BillingRun> billruns = billingRunService.getbillingRuns(em,
					provider, parameter);

			boolean notTerminatedBillRun = false;
			if (billruns != null) {
				for (BillingRun billrun : billruns) {
					if (billrun.getStatus() == BillingRunStatusEnum.CONFIRMED
							|| billrun.getStatus() == BillingRunStatusEnum.NEW
							|| billrun.getStatus() == BillingRunStatusEnum.ON_GOING
							|| billrun.getStatus() == BillingRunStatusEnum.WAITING) {
						notTerminatedBillRun = true;
						break;
					}
				}
			}

			if (!notTerminatedBillRun && !StringUtils.isEmpty(parameter)) {
				BillingCycle billingCycle = billingCycleService
						.findByBillingCycleCode(parameter, provider);
				if (billingCycle != null) {
					BillingRun billingRun = new BillingRun();
					Auditable auditable = new Auditable();
					auditable.setCreated(new Date());
					billingRun.setAuditable(auditable);
					billingRun.setBillingCycle(billingCycle);
					billingRun.setStatus(BillingRunStatusEnum.NEW);
					billingRunService.create(em, billingRun, currentUser,
							currentUser.getProvider());
					result.registerSucces();
				}
			}
		} catch (Exception e) {
			result.registerError(e.getMessage());
			log.error(e.getMessage());
		}
	}

}
