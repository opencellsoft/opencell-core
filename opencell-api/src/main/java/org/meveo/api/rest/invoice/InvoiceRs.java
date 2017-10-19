package org.meveo.api.rest.invoice;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.invoice.CreateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.api.dto.invoice.GenerateInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceRequestDto;
import org.meveo.api.dto.invoice.GetPdfInvoiceResponseDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceRequestDto;
import org.meveo.api.dto.invoice.GetXmlInvoiceResponseDto;
import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.api.dto.response.CustomerInvoicesResponse;
import org.meveo.api.dto.response.InvoicesDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.billing.Invoice}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/invoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceRs extends IBaseRs {

    /**
     * Search for an invoice given an invoice id or invoice number and invoice type.
     * 
     * @param id invoice id
     * @param invoiceNumber invoice number
     * @param invoiceType invoice type
     * @param includeTransactions Should transactions, associated to an invoice, be listed
     * @return GetInvoiceResponseDto
     */
    @GET
    @Path("/")
    public GetInvoiceResponseDto findInvoiceByIdOrType(@QueryParam("id") Long id, @QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("invoiceType") String invoiceType,
            @QueryParam("includeTransactions") boolean includeTransactions);

    /**
     * Create invoice. Invoice number depends on invoice type
     * 
     * @param invoiceDto invoice dto
     * @return
     */
    @POST
    @Path("/")
    public CreateInvoiceResponseDto create(InvoiceDto invoiceDto);

    /**
     * Search for a list of invoices given a customer account code.
     * 
     * Deprecated in v.4.7.2, use "list()" instead
     * 
     * @param customerAccountCode Customer account code
     * @return
     */
    @Deprecated
    @GET
    @Path("/listInvoiceByCustomerAccount")
    public CustomerInvoicesResponse find(@QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Launch all the invoicing process for a given billingAccount, that's mean : <lu>
     * <li>Create rated transactions
     * <li>Create an exceptional billingRun with given dates
     * <li>Validate the pre-invoicing report
     * <li>Validate the post-invoicing report
     * <li>Validate the BillingRun </lu>
     * 
     * @param generateInvoiceRequestDto Contains the code of the billing account, invoicing and last transaction date
     * @return
     */
    @POST
    @Path("/generateInvoice")
    public GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto);

    /**
     * Finds an invoice based on its invoice number and return it as xml string
     * 
     * @param invoiceNumber Invoice number
     * @return
     */
    @POST
    @Path("/getXMLInvoice")
    public GetXmlInvoiceResponseDto findXMLInvoice(Long invoiceId, String invoiceNumber);

    /**
     * Finds an invoice based on its invoice number and optionally an invoice type and return it as xml string
     *
     * @param xmlInvoiceRequestDto contains invoice number and optionally an invoice type
     * @return
     */
    @POST
    @Path("/fetchXMLInvoice")
    public GetXmlInvoiceResponseDto findXMLInvoice(GetXmlInvoiceRequestDto xmlInvoiceRequestDto);

    /**
     * Finds an invoice based on invoice number and invoice type. It returns the result as xml string
     * 
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return
     */
    @POST
    @Path("/getXMLInvoiceWithType")
    public GetXmlInvoiceResponseDto findXMLInvoiceWithType(Long invoiceId, String invoiceNumber, String invoiceType);

    /**
     * Finds an invoice based on invoice number and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * 
     * @param invoiceNumber Invoice number
     * @return
     */
    @POST
    @Path("/getPdfInvoice")
    public GetPdfInvoiceResponseDto findPdfInvoice(Long invoiceId, String invoiceNumber);

    /**
     * Finds an invoice based on invoice number and optionally an invoice type and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database
     * is returned.
     *
     * @param pdfInvoiceRequestDto contains an invoice number and optionally an invoice type
     * @return
     */
    @POST
    @Path("/fetchPdfInvoice")
    public GetPdfInvoiceResponseDto findPdfInvoice(GetPdfInvoiceRequestDto pdfInvoiceRequestDto);

    /**
     * Finds an invoice based on invoice number and invoice type and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * 
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return
     */
    @POST
    @Path("/getPdfInvoiceWithType")
    public GetPdfInvoiceResponseDto findPdfInvoiceWithType(Long invoiceId, String invoiceNumber, String invoiceType);

    /**
     * Cancel an invoice based on invoice id
     * 
     * @param invoiceId Invoice id
     * @return
     */
    @POST
    @Path("/cancel")
    public ActionStatus cancel(Long invoiceId);

    /**
     * Validate an invoice based on the invoice id
     * 
     * @param invoiceId Invoice id
     * @return
     */
    @POST
    @Path("/validate")
    public ActionStatus validate(@FormParam("invoiceId") Long invoiceId);

    @GET
    @Path("/listPresentInAR")
    public CustomerInvoicesResponse listPresentInAR(@QueryParam("customerAccountCode") String customerAccountCode);

    @POST
    @Path("/generateDraftInvoice")
    public GenerateInvoiceResponseDto generateDraftInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto);

    /**
     * List invoices matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An invoice list
     */
    @GET
    @Path("/list")
    public InvoicesDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List invoices matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return An invoice list
     */
    @POST
    @Path("/list")
    public InvoicesDto listPost(PagingAndFiltering pagingAndFiltering);

}