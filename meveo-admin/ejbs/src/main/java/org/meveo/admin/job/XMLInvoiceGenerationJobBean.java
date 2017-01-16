package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.XmlInvoiceAsync;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;

@Stateless
public class XMLInvoiceGenerationJobBean {

	@Inject
	private Logger log;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private XmlInvoiceAsync xmlInvoiceAsync;
	
    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

	@SuppressWarnings("unchecked")
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, String parameter, User currentUser, JobInstance jobInstance) {
		log.debug("Running for user={}, parameter={}", currentUser, parameter);
		
		Provider provider = currentUser.getProvider();
		List<BillingRun> billingRuns = new ArrayList<BillingRun>();

		if (parameter != null && parameter.trim().length() > 0) {
			try {
				billingRuns.add(billingRunService.getBillingRunById(Long.parseLong(parameter), provider));
			} catch (Exception e) {
				log.error("error while getting billing run",e);
				result.registerError(e.getMessage());
			}
		} else {
			billingRuns = billingRunService.getValidatedBillingRuns(provider);
		}

		log.info("billingRuns to process={}", billingRuns.size());

		for (BillingRun billingRun : billingRuns) {
			try {

				Long nbRuns = new Long(1);		
				Long waitingMillis = new Long(0);
				try{
					nbRuns = (Long) customFieldInstanceService.getCFValue(jobInstance, "nbRuns", currentUser);             
	                waitingMillis = (Long) customFieldInstanceService.getCFValue(jobInstance, "waitingMillis", currentUser);
					if(nbRuns == -1){
						nbRuns = (long) Runtime.getRuntime().availableProcessors();
					}
				}catch(Exception e){
					nbRuns = new Long(1);
					waitingMillis = new Long(0);
					log.warn("Cant get customFields for "+jobInstance.getJobTemplate(),e.getMessage());
				}


				List<Future<String>> futures = new ArrayList<Future<String>>();
				SubListCreator subListCreator = new SubListCreator(invoiceService.getInvoices(billingRun),nbRuns.intValue());
				result.setNbItemsToProcess(subListCreator.getListSize());

				while (subListCreator.isHasNext()) {
					futures.add(xmlInvoiceAsync.launchAndForget((List<Invoice>) subListCreator.getNextWorkSet(), result,currentUser));
					if(result.getNbItemsProcessedWithError()==0){
					updateBillingRun(billingRun.getId(), currentUser);
					}
	                if (subListCreator.isHasNext()) {
	                    try {
	                        Thread.sleep(waitingMillis.longValue());
	                    } catch (InterruptedException e) {
	                        log.error("", e);
	                    }
	                }
				}

	            // Wait for all async methods to finish
	            for (Future<String> future : futures) {
	                try {
	                    future.get();

	                } catch (InterruptedException e) {
	                    // It was cancelled from outside - no interest
	                    
	                } catch (ExecutionException e) {
	                    Throwable cause = e.getCause();
	                    result.registerError(cause.getMessage());
	                    log.error("Failed to execute async method", cause);
	                }
	            }
	            
				
			} catch (Exception e) {
	            log.error("Failed to generate XML invoices",e);
	            result.registerError(e.getMessage());
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateBillingRun(Long billingRunId ,User currentUser) {
		BillingRun billingRun = billingRunService.findById(billingRunId, currentUser.getProvider());
		billingRun.setXmlInvoiceGenerated(true);
		billingRun.updateAudit(currentUser);
		billingRunService.updateNoCheck(billingRun);

	}

}
