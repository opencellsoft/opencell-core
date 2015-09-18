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

import org.meveo.admin.async.PdfInvoiceAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.slf4j.Logger;

@Stateless
public class PDFInvoiceGenerationJobBean {

	@Inject
	private Logger log;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private BillingRunService billingRunService;

	@Inject
	private PdfInvoiceAsync pdfInvoiceAsync;

	@SuppressWarnings("unchecked")
	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void execute(JobExecutionResultImpl result, User currentUser,JobInstance jobInstance) {
		log.debug("Running for user={}, parameter={}", currentUser, jobInstance.getParametres());
		
		try{
			List<Invoice> invoices = new ArrayList<Invoice>();
			String parameter = jobInstance.getParametres();
			if (parameter != null && parameter.trim().length() > 0) {			
				try {
					invoices = invoiceService.getInvoices(billingRunService
							.getBillingRunById(Long.parseLong(parameter),
									currentUser.getProvider()));
				} catch (Exception e) {
					log.error("error while getting invoices ",e);
					result.registerError(e.getMessage());
				}
			} else {
				invoices = invoiceService.getValidatedInvoicesWithNoPdf(null,currentUser.getProvider());
			}

			result.setNbItemsToProcess(invoices.size());
			log.info("PDFInvoiceGenerationJob number of invoices to process="+ invoices.size());

			Long nbRuns = new Long(1);		
			Long waitingMillis = new Long(0);
			try{
				nbRuns = (Long) jobInstance.getCFValue("PDFInvoiceGenerationJob_nbRuns");  			
				waitingMillis = (Long) jobInstance.getCFValue("PDFInvoiceGenerationJob_waitingMillis");
				if(nbRuns == -1){
					nbRuns = (long) Runtime.getRuntime().availableProcessors();
				}
			}catch(Exception e){
				nbRuns = new Long(1);
				waitingMillis = new Long(0);
				log.warn("Cant get customFields for "+jobInstance.getJobTemplate());
			}

			List<Future<String>> futures = new ArrayList<Future<String>>();
			SubListCreator subListCreator = new SubListCreator(invoices,nbRuns.intValue());
			while (subListCreator.isHasNext()) {
				futures.add(pdfInvoiceAsync.launchAndForget((List<Invoice>) subListCreator.getNextWorkSet(), currentUser, result));

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
			log.error("Failed to generate PDF invoices",e);
			result.registerError(e.getMessage());
		}
	}
}