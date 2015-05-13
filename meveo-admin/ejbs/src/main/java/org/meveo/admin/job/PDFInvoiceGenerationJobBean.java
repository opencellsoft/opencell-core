package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;

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
import org.meveo.model.jobs.TimerEntity;
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

	@Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter,User currentUser,TimerEntity timerEntity) {
		List<Invoice> invoices = new ArrayList<Invoice>();

		if (parameter != null && parameter.trim().length() > 0) {
			try {
				invoices = invoiceService.getInvoices(billingRunService
						.getBillingRunById(Long.parseLong(parameter),
								currentUser.getProvider()));
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		} else {
			invoices = invoiceService.getValidatedInvoicesWithNoPdf(null,currentUser.getProvider());
		}

		result.setNbItemsToProcess(invoices.size());
		log.info("PDFInvoiceGenerationJob number of invoices to process="+ invoices.size());
		try{
			Long nbRuns = new Long(1);		
			Long waitingMillis = new Long(0);
			try{
				nbRuns = timerEntity.getLongCustomValue("PDFInvoiceGenerationJob_nbRuns").longValue();  			
				waitingMillis = timerEntity.getLongCustomValue("PDFInvoiceGenerationJob_waitingMillis").longValue();
			}catch(Exception e){
				log.warn("Cant get customFields for "+timerEntity.getJobName());
			}
			SubListCreator subListCreator = new SubListCreator(invoices,nbRuns.intValue());
			while (subListCreator.isHasNext()) {
				pdfInvoiceAsync.launchAndForget((List<Invoice>) subListCreator.getNextWorkSet(),currentUser, result );
				try {
					Thread.sleep(waitingMillis.longValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			result.registerError(e.getMessage());
			e.printStackTrace();
		}

	}

}
