package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.slf4j.Logger;

@Stateless
public class InvoicingJobBean {

	@Inject
	protected Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private InvoicingAsync invoicingAsync;

	@SuppressWarnings("unchecked")
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, User currentUser,JobInstance jobInstance) {

		try {
			Provider provider = currentUser.getProvider();
			List<BillingRun> billingRuns = billingRunService.getbillingRuns(provider, BillingRunStatusEnum.NEW,
					BillingRunStatusEnum.ON_GOING, BillingRunStatusEnum.CONFIRMED);

			log.info("billingRuns to process={}", billingRuns.size());
			Long nbRuns = new Long(1);		
			Long waitingMillis = new Long(0);
			try{
				nbRuns = jobInstance.getLongCustomValue("InvoicingJob_nbRuns").longValue();  			
				waitingMillis = jobInstance.getLongCustomValue("InvoicingJob_waitingMillis").longValue();
				if(nbRuns == -1){
					nbRuns  = (long) Runtime.getRuntime().availableProcessors();
				}
			}catch(Exception e){
				log.warn("Cant get customFields for "+jobInstance.getJobTemplate());
			}

			for (BillingRun billingRun : billingRuns) {
				try {
					if (BillingRunStatusEnum.NEW.equals(billingRun.getStatus())) {
						List<BillingAccount> billingAccounts = billingRunService.getBillingAccounts(billingRun);
						log.info("Nb billingAccounts to process={}",
								(billingAccounts != null ? billingAccounts.size() : 0));

						if (billingAccounts != null && billingAccounts.size() > 0) {
							int billableBA = 0;
							SubListCreator subListCreator = new SubListCreator(billingAccounts,nbRuns.intValue());
							List<Future<Integer>> asyncReturns = new ArrayList<Future<Integer>>();
							while (subListCreator.isHasNext()) {
								Future<Integer> count = invoicingAsync.launchAndForget((List<BillingAccount>) subListCreator.getNextWorkSet(), billingRun, currentUser);
								asyncReturns.add(count);
								try {
									Thread.sleep(waitingMillis.longValue());
								} catch (InterruptedException e) {
									log.error("", e);
								} 
							}

							for(Future<Integer> futureItsNow : asyncReturns){
								billableBA+= futureItsNow.get().intValue();	
							}

							log.info("Total billableBA:"+billableBA);

							updateBillingRun(billingRun.getId(),currentUser,billingAccounts.size(),billableBA,BillingRunStatusEnum.WAITING,new Date());

							if (billingRun.getProcessType() == BillingProcessTypesEnum.AUTOMATIC
									|| currentUser.getProvider().isAutomaticInvoicing()) {
								billingRunService.createAgregatesAndInvoice(billingRun.getId(),billingRun.getLastTransactionDate(), currentUser,nbRuns.longValue(),waitingMillis.longValue());										
								updateBillingRun(billingRun.getId(),currentUser,null,null,BillingRunStatusEnum.TERMINATED,null);
							}
						}
						result.registerSucces();
					} else if (BillingRunStatusEnum.ON_GOING.equals(billingRun.getStatus())) {
						billingRunService.createAgregatesAndInvoice(billingRun.getId(),billingRun.getLastTransactionDate(), currentUser,nbRuns.longValue(),waitingMillis.longValue());								
						updateBillingRun(billingRun.getId(),currentUser,null,null,BillingRunStatusEnum.TERMINATED,null);
						result.registerSucces();
					} else if (BillingRunStatusEnum.CONFIRMED.equals(billingRun.getStatus())) {
						billingRunService.validate(billingRun, currentUser);
						result.registerSucces();
					}
				} catch (Exception e) {
					log.error("Failed to run invoicing", e);
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error("Failed to run invoicing", e);
		}

		log.info("end Execute");
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateBillingRun(Long billingRunId ,User currentUser,Integer sizeBA,Integer billableBA,BillingRunStatusEnum status,Date dateStatus) {
		BillingRun billingRun = billingRunService.findById(billingRunId);

		if(sizeBA != null){
			billingRun.setBillingAccountNumber(sizeBA);
		}
		if(billableBA != null){
			billingRun.setBillableBillingAcountNumber(billableBA);
		}
		if(dateStatus != null){
			billingRun.setProcessDate(dateStatus);
		}
		billingRun.setStatus(status);
		billingRun.updateAudit(currentUser);
		billingRunService.updateNoCheck(billingRun);

	}

}
