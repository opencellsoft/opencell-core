package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.PdfInvoiceApi;
import org.meveo.api.rest.response.PdfInvoiceResponse;
 


/**
 * @author R.AITYAAZZA
 *
 */


@Path("/PdfInvoice")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class PdfInvoiceWS {

	@Inject
	private PdfInvoiceApi pdfInvoiceApi;

	@GET
	@Path("/index")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service");

		return result;
	}

	@GET
	@Path("/")
	public PdfInvoiceResponse getPDFInvoice(
			@QueryParam("invoiceNumber") String invoiceNumber,
			@QueryParam("customerAccountCode") String customerAccountCode, 
			@QueryParam("providerCode") String providerCode) throws Exception {

		
		
		PdfInvoiceResponse result = new PdfInvoiceResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setPdfInvoice(pdfInvoiceApi.getPDFInvoice(invoiceNumber,customerAccountCode,providerCode));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

	

}
