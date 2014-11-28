package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.PdfInvoiceApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PdfInvoiceResponse;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author R.AITYAAZZA
 *
 */

@Path("/PdfInvoice")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
@RSSecured
public class PdfInvoiceRs extends BaseRs {

	@Inject
	private PdfInvoiceApi pdfInvoiceApi;

	@GET
	@Path("/")
	public PdfInvoiceResponse getPDFInvoice(
			@QueryParam("invoiceNumber") String invoiceNumber,
			@QueryParam("customerAccountCode") String customerAccountCode)
			throws Exception {

		PdfInvoiceResponse result = new PdfInvoiceResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setPdfInvoice(pdfInvoiceApi.getPDFInvoice(invoiceNumber,
					customerAccountCode, getCurrentUser()));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

}
