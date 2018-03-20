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
import org.meveo.api.dto.invoice.Invoice4_2Dto;
import org.meveo.api.dto.response.CustomerInvoices4_2Response;
import org.meveo.api.dto.response.InvoiceCreationResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.billing.Invoice}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoice4_2")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface Invoice4_2Rs extends IBaseRs {

    /**
     * Create invoice. Invoice number depends on invoice type
     * 
     * @param invoiceDto object that contains Invoice information
     * @return the Invoice object matching the given criteria
     */
    @POST
    @Path("/")
    public InvoiceCreationResponse create(Invoice4_2Dto invoiceDto);

    /**
     * Search for a list of invoice given a customer account's code.
     * 
     * @param customerAccountCode Customer account's code
     * @return the Invoice object matching the given criteria
     */
    @GET
    @Path("/")
    public CustomerInvoices4_2Response find(@QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * This operation generates rated transaction given a billing account and invoicing date, updates billing account amounts and generates aggregates and invoice.
     * 
     * @param generateInvoiceRequestDto Contains the code of the billing account, invoicing and last transaction date
     * @return the Invoice object matching the given criteria
     */
    @POST
    @Path("/generateInvoice")
    public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto);

    /**
     * Finds an invoice and return it as xml string
     * 
     * @param invoiceNumber Invoice number
     * @return the Invoice object matching the given criteria
     */
    @POST
    @Path("/getXMLInvoice")
    public GetXmlInvoiceResponseDto findXMLInvoice(String invoiceNumber);

    /**
     * Finds an invoice and return it as xml string
     * 
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return the Invoice object matching the given criteria
     */
    @POST
    @Path("/getXMLInvoiceWithType")
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(@FormParam("invoiceNumber") String invoiceNumber, @FormParam("invoiceType") String invoiceType);

    /**
     * Finds an invoice and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * 
     * @param invoiceNumber Invoice number
     * @return Invoice object in PDF format matching the given search criteria
     */
    @POST
    @Path("/getPdfInvoice")
    public GetPdfInvoiceResponseDto findPdfInvoice(String invoiceNumber);

    /**
     * Finds an invoice and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * 
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return Invoice object in PDF format matching the given search criteria
     */
    @POST
    @Path("/getPdfInvoiceWithType")
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(@FormParam("invoiceNumber") String invoiceNumber, @FormParam("invoiceType") String invoiceType);
}