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
import javax.persistence.EntityManager;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.util.MeveoJpaForJobs;
import org.slf4j.Logger;

@Stateless
public class PDFInvoiceGenerationJobBean {

	@Inject
	private Logger log;

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private PDFParametersConstruction pDFParametersConstruction;

	@Inject
	private PDFFilesOutputProducer pDFFilesOutputProducer;

	@Inject
	private BillingRunService billingRunService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	@Interceptors({ JobLoggingInterceptor.class })
	public void execute(JobExecutionResultImpl result, String parameter,
			User currentUser) {
		List<Invoice> invoices = new ArrayList<Invoice>();

		if (parameter != null && parameter.trim().length() > 0) {
			try {
				invoices = invoiceService.getInvoices(
						em,
						billingRunService.getBillingRunById(em,
								Long.parseLong(parameter),
								currentUser.getProvider()));
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		} else {
			invoices = invoiceService.getValidatedInvoicesWithNoPdf(em, null,
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
						.producePdf(em, parameters, result, currentUser);
				isPdfgenerated.get();
			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
		}
	}

}
