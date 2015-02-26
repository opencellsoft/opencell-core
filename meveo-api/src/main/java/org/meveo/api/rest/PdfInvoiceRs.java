package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.response.PdfInvoiceResponse;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/PdfInvoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface PdfInvoiceRs extends IBaseRs {

	@GET
	@Path("/")
	public PdfInvoiceResponse getPDFInvoice(@QueryParam("invoiceNumber") String invoiceNumber,
			@QueryParam("customerAccountCode") String customerAccountCode) throws Exception;
}
