package org.meveo.admin.job;

import java.io.File;
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
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
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

		ParamBean param = ParamBean.getInstance();
		String invoicesDir = param.getProperty("providers.rootDir", "/tmp/meveo");

		for (BillingRun billingRun : billingRuns) {
			try {
				File billingRundir = new File(invoicesDir + File.separator + provider.getCode() + File.separator + "invoices" + File.separator + "xml" + File.separator + billingRun.getId());
				billingRundir.mkdirs();

				Long nbRuns = new Long(1);		
				Long waitingMillis = new Long(0);
				try{
					nbRuns = jobInstance.getLongCustomValue("XMLInvoiceGenerationJob_nbRuns").longValue();  			
					waitingMillis = jobInstance.getLongCustomValue("XMLInvoiceGenerationJob_waitingMillis").longValue();
					if(nbRuns == -1){
						nbRuns = (long) Runtime.getRuntime().availableProcessors();
					}
				}catch(Exception e){
					log.warn("Cant get customFields for "+jobInstance.getJobTemplate());
				}


				List<Future<String>> futures = new ArrayList<Future<String>>();
				SubListCreator subListCreator = new SubListCreator(invoiceService.getInvoices(billingRun),nbRuns.intValue());
				result.setNbItemsToProcess(subListCreator.getListSize());

				while (subListCreator.isHasNext()) {
					futures.add(xmlInvoiceAsync.launchAndForget((List<Invoice>) subListCreator.getNextWorkSet(), billingRundir,result));
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
	            
				updateBillingRun(billingRun.getId(), currentUser);
				result.setDone(true);
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
