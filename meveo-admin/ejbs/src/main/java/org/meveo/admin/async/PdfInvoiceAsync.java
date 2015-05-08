/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.InvoiceService;

/**
 * @author anasseh
 *
 */

@Stateless
public class PdfInvoiceAsync {

	@Inject
	private InvoiceService invoiceService;
	@Inject
	private PDFParametersConstruction pDFParametersConstruction;


	@Asynchronous
	public void launchAndForget(List<Invoice> invoices,User currentUser,JobExecutionResultImpl result) {
		for (Invoice invoice : invoices) {
			try {				
				Map<String, Object> parameters = pDFParametersConstruction.constructParameters(invoice.getId());
				invoiceService.producePdf(parameters, currentUser);
			} catch (Exception e) {	
				result.registerError(e.getMessage());
				e.printStackTrace();
			}			
		}
	}
}
