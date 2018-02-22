package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.response.PdfInvoiceResponse;

/**
 * @author Edward P. Legaspi
 **/
@Path("/PdfInvoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PdfInvoiceRs extends IBaseRs {

    /**
     * Find a PDF invoice with a given invoice number and a customer account code.
     * 
     * @param invoiceNumber The invoice number
     * @param customerAccountCode The customer account's number
     * @return invoice's pdf
     */
    @GET
    @Path("/")
    PdfInvoiceResponse getPDFInvoice(@QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("customerAccountCode") String customerAccountCode);
}
