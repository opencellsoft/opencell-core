/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
 * @author Said Ramli
 * @author Khalid HORRI
 * @lastModifiedVersion 7.0
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
     * @param includePdf true if we want to generate/include pdf
     * @param includeXml true if we want to generate/include xml
     * @return instance of GetInvoiceResponseDto
     */
    @GET
    @Path("/")
    GetInvoiceResponseDto findInvoiceByIdOrType(@QueryParam("id") Long id, @QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("invoiceType") String invoiceType,
            @QueryParam("includeTransactions") boolean includeTransactions, @QueryParam("includePdf") Boolean includePdf,  @QueryParam("includeXml") Boolean includeXml);

    /**
     * Create invoice. Invoice number depends on invoice type
     * 
     * @param invoiceDto invoice dto
     * @return created invoice
     */
    @POST
    @Path("/")
    CreateInvoiceResponseDto create(InvoiceDto invoiceDto);

    /**
     * Search for a list of invoices given a customer account code.
     * 
     * Deprecated in v.4.7.2, use "list()" instead with criteria "billingAccount.customerAccount.code=xxx"
     * 
     * @param customerAccountCode Customer account code
     * @param returnPdf true if we want to generate pdf
     * @return customer invoice.
     */
    @Deprecated
    @GET
    @Path("/listInvoiceByCustomerAccount")
    CustomerInvoicesResponse find(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("returnPdf") Boolean returnPdf);

    /**
     * Launch all the invoicing process for a given billingAccount, that's mean.
     *  <ul>
     * <li>Create rated transactions
     * <li>Create an exceptional billingRun with given dates
     * <li>Validate the pre-invoicing report
     * <li>Validate the post-invoicing report
     * <li>Validate the BillingRun
     *  </ul>
     * 
     * @param generateInvoiceRequestDto Contains the code of the billing account, invoicing and last transaction date
     * @return invoice response
     */
    @POST
    @Path("/generateInvoice")
    GenerateInvoiceResponseDto generateInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto);

    /**
     * Finds an invoice based on its invoice number and return it as xml string.
     * 
     * @param invoiceId invoice's id
     * @param invoiceNumber Invoice number
     * @return xml invoice
     */
    @GET
    @Path("/getXMLInvoice")
    GetXmlInvoiceResponseDto findXMLInvoice(@QueryParam("invoiceId") Long invoiceId, @QueryParam("invoiceNumber") String invoiceNumber);

    /**
     * Finds an invoice based on its invoice number and optionally an invoice type and return it as xml string.
     *
     * @param xmlInvoiceRequestDto contains invoice number and optionally an invoice type
     * @return xml invoice
     */
    @POST
    @Path("/fetchXMLInvoice")
    GetXmlInvoiceResponseDto findXMLInvoice(GetXmlInvoiceRequestDto xmlInvoiceRequestDto);

    /**
     * Finds an invoice based on invoice number and invoice type. It returns the result as xml string
     * @param invoiceId invoice's id.
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return xml invoice
     */
    @GET
    @Path("/getXMLInvoiceWithType")
    GetXmlInvoiceResponseDto findXMLInvoiceWithType(@QueryParam("invoiceId") Long invoiceId, @QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("invoiceType") String invoiceType);

    /**
     * Finds an invoice based on invoice number and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * 
     * @param invoiceId invoice's id
     * @param invoiceNumber Invoice number
     * @return pdf invoice
     */
    @GET
    @Path("/getPdfInvoice")
    GetPdfInvoiceResponseDto findPdfInvoice(@QueryParam("invoiceId") Long invoiceId, @QueryParam("invoiceNumber") String invoiceNumber);

    /**
     * Finds an invoice based on invoice number and optionally an invoice type and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database
     * is returned.
     *
     * @param pdfInvoiceRequestDto contains an invoice number and optionally an invoice type
     * @return pdf invoice
     */
    @POST
    @Path("/fetchPdfInvoice")
    GetPdfInvoiceResponseDto findPdfInvoice(GetPdfInvoiceRequestDto pdfInvoiceRequestDto);

    /**
     * Finds an invoice based on invoice number and invoice type and return it as pdf as byte []. Invoice is not recreated, instead invoice stored as pdf in database is returned.
     * @param invoiceId invoice's id
     * @param invoiceNumber Invoice number
     * @param invoiceType Invoice type
     * @return pdf invoice
     */
    @GET
    @Path("/getPdfInvoiceWithType")
    GetPdfInvoiceResponseDto findPdfInvoiceWithType(@QueryParam("invoiceId") Long invoiceId, @QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("invoiceType") String invoiceType);

    /**
     * Cancel an invoice based on invoice id.
     * 
     * @param invoiceId Invoice id
     * @return action status.
     */
    @POST
    @Path("/cancel")
    ActionStatus cancel(Long invoiceId);

    /**
     * Validate an invoice based on the invoice id.
     * 
     * @param invoiceId Invoice id
     * @return action status.
     */
    @POST
    @Path("/validate")
    ActionStatus validate(@FormParam("invoiceId") Long invoiceId);

    /**
     * List invoices with account operation for a given customer account
     * 
     * Deprecated in v.4.8. Use list() instead with criteria "recordedInvoice=IS_NOT_NULL and billingAccount.customerAccount.code=xxx"
     * 
     * @param customerAccountCode Customer account code
     * @param includePdf true if we want to generate/include pdf
     * @return List of invoices
     */
    @GET
    @Deprecated
    @Path("/listPresentInAR")
    CustomerInvoicesResponse listPresentInAR(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("includePdf") boolean includePdf);
    
    /**
     * Generate a Draft invoice
     * 
     * @param generateInvoiceRequestDto Post data
     * @return action status.
     */
    @POST
    @Path("/generateDraftInvoice")
    GenerateInvoiceResponseDto generateDraftInvoice(GenerateInvoiceRequestDto generateInvoiceRequestDto);

    /**
     * List invoices matching a given criteria.
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return An invoice list
     */
    @GET
    @Path("/list")
    InvoicesDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("id") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List invoices matching a given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "transactions" in fields to include transactions and "pdf" to generate/include PDF document
     * @return An invoice list
     */
    @POST
    @Path("/list")
    InvoicesDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Send invoice by Email.
     * @param invoiceDto  the invoice DTO
     * @return "SUCCESS" if sent, "FAIL" else
     */
    @POST
    @Path("/sendByEmail")
    ActionStatus sendByEmail(InvoiceDto invoiceDto);

}