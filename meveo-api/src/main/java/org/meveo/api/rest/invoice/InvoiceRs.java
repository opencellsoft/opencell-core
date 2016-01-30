package org.meveo.api.rest.invoice;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoiceCreationResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.Invoice}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface InvoiceRs extends IBaseRs {

    /**
     * Create invoice. Invoice number depends on invoice type
     * 
     * @param invoiceDto
     * @return
     */
    @POST
    @Path("/")
    public InvoiceCreationResponse create(InvoiceDto invoiceDto);

    /**
     * Search for a list of invoice given a customer account code.
     * 
     * @param customerAccountCode Customer account code
     * @return
     */
    @GET
    @Path("/")
    public CustomerInvoicesResponse find(@QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * This operation generates rated transaction given a billing account and invoicing date, updates billing account amounts and generates aggregates and invoice.
     * 
     * @param generateInvoiceRequestDto Contains the code of the billing account, invoicing and last transaction date
     * @return
     */
    @POST
    @Path("/generateInvoice")
    public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto);

    /**
     * Finds an invoice and return it as xml string
     * 
     * @param invoiceNumber Invoice number
     * @return
     */
    @POST
    @Path("/getXMLInvoice")
    public GetXmlInvoiceResponseDto findXMLInvoice(String invoiceNumber);

    /**
     * Finds an invoice and return it as xml string
     * 
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return
     */
    @POST
    @Path("/getXMLInvoiceWithType")
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(@FormParam("invoiceNumber") String invoiceNumber, @FormParam("invoiceType") String invoiceType);

    /**
     * Finds an invoice and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * 
     * @param invoiceNumber Invoice number
     * @return
     */
    @POST
    @Path("/getPdfInvoice")
    public GetPdfInvoiceResponseDto findPdfInvoice(String invoiceNumber);

    /**
     * Finds an invoice and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * 
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return
     */
    @POST
    @Path("/getPdfInvoiceWithType")
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(@FormParam("invoiceNumber") String invoiceNumber, @FormParam("invoiceType") String invoiceType);
}