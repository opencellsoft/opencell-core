package org.meveo.admin.job;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.BillingRunService;
import org.slf4j.Logger;

@Stateless
public class BillingRunJobBean {

	@Inject
	private Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private BillingCycleService billingCycleService;

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter,String billingCycleCode,Date invoiceDate,Date lastTransactionDate, User currentUser) {
		Provider provider = currentUser.getProvider();
		if(!StringUtils.isBlank(billingCycleCode)){
			parameter=billingCycleCode;
		}
		try {
			List<BillingRun> billruns = billingRunService.getbillingRuns(provider, parameter);

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
				BillingCycle billingCycle = billingCycleService.findByBillingCycleCode(parameter, provider);

				if (billingCycle != null) {
					BillingRun billingRun = new BillingRun();
					Auditable auditable = new Auditable();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					billingRun.setAuditable(auditable);
					billingRun.setBillingCycle(billingCycle);
					billingRun.setProcessDate(auditable.getCreated());
					if(invoiceDate!=null){
						billingRun.setInvoiceDate(invoiceDate);
					}else if(billingCycle.getInvoiceDateProductionDelay()!=null){
					    billingRun.setInvoiceDate(DateUtils.addDaysToDate(billingRun.getProcessDate(),billingCycle.getInvoiceDateProductionDelay())); 
		            } else {
		                billingRun.setInvoiceDate(billingRun.getProcessDate());
		            }
					if(lastTransactionDate!=null){
						billingRun.setLastTransactionDate(lastTransactionDate);
					}else if(billingCycle.getTransactionDateDelay()!=null){
		                billingRun.setLastTransactionDate(DateUtils.addDaysToDate(billingRun.getProcessDate(),billingCycle.getTransactionDateDelay())); 
		            } else {
		                billingRun.setLastTransactionDate(billingRun.getProcessDate());
		            }
					billingRun.setProcessType(BillingProcessTypesEnum.AUTOMATIC);
					billingRun.setStatus(BillingRunStatusEnum.NEW);
					billingRunService.create(billingRun, currentUser, provider);
					result.registerSucces();
				} else {
					result.registerError("Cannot find billingCycle wit code '" + parameter
							+ "' (this code should be the parameter of the job)");
				}
			}
		} catch (Exception e) {
			result.registerError(e.getMessage());
			log.error("Failed to run billing ", e);
		}
	}

}
