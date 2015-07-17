package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PdfInvoiceResponse;
import org.meveo.api.invoice.PdfInvoiceApi;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.PdfInvoiceRs;
import org.slf4j.Logger;

/**
 * @author R.AITYAAZZA
 *
 */
@RequestScoped
@Interceptors({ LoggingInterceptor.class })
public class PdfInvoiceRsImpl extends BaseRs implements PdfInvoiceRs {

	@Inject
	private Logger log;

	@Inject
	private PdfInvoiceApi pdfInvoiceApi;

	@Override
	public PdfInvoiceResponse getPDFInvoice(@QueryParam("invoiceNumber") String invoiceNumber,
			@QueryParam("customerAccountCode") String customerAccountCode) throws Exception {

		PdfInvoiceResponse result = new PdfInvoiceResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setPdfInvoice(pdfInvoiceApi.getPDFInvoice(invoiceNumber, customerAccountCode, getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		log.debug("RESPONSE={}", result);
		return result;
	}

}
