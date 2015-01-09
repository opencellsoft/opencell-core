package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
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
	private PDFParametersConstruction pDFParametersConstruction;

	@Inject
	private PDFFilesOutputProducer pDFFilesOutputProducer;

	@Inject
	private BillingRunService billingRunService;

	@Interceptors({ JobLoggingInterceptor.class })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void execute(JobExecutionResultImpl result, String parameter,
			User currentUser) {
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
			invoices = invoiceService.getValidatedInvoicesWithNoPdf(null,
					currentUser.getProvider());
		}

		log.info("PDFInvoiceGenerationJob number of invoices to process="
				+ invoices.size());

		for (Invoice invoice : invoices) {
			try {
				Map<String, Object> parameters = pDFParametersConstruction
						.constructParameters(invoice);

				log.info("PDFInvoiceGenerationJob parameters=" + parameters);

				Future<Boolean> isPdfgenerated = pDFFilesOutputProducer
						.producePdf(parameters, result, currentUser);
				isPdfgenerated.get();
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		}
	}

}
