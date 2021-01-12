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

package org.meveo.api.rest.billing;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.billing.InvoiceValidationDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/billing/invoicing")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoicingRs extends IBaseRs {

    /**
     * Create a new billing run. The id of the created BillingRun is returned on 'message' field of response object.
     * 
     * @param createBillingRunDto The billing run's data
     * @return Request processing status
     */
    @POST
    @Path("/createBillingRun")
    ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto);


    /**
     * Search for a billing run info with a given Id 
     * 
     * @param billingRunId The billing run's Id
     * @return The billing run info
     */
    @POST
    @Path("/getBillingRunInfo")
    GetBillingRunInfoResponseDto getBillingRunInfo(Long billingRunId);

    /**
     * Returns the list of billable billing accounts of a billing run
     * 
     * @param billingRunId The billing run id
     * @return A list of billing accounts
     */
    @POST
    @Path("/getBillingAccountListInRun")
    GetBillingAccountListInRunResponseDto getBillingAccountListInRun(Long billingRunId);

    /**
     * Returns the pre-invoicing report for a given billing run Id
     * 
     * @param billingRunId The billing run id
     * @return A pre-invoicing reports
     */
    @POST
    @Path("/getPreInvoicingReport")
    GetPreInvoicingReportsResponseDto getPreInvoicingReport(Long billingRunId);

    /**
     * Returns the post-invoicing report for a given billing run Id
     * 
     * @param billingRunId The billing run id
     * @return A post-invoicing reports
     */
    @POST
    @Path("/getPostInvoicingReport")
    GetPostInvoicingReportsResponseDto getPostInvoicingReport(Long billingRunId);

    /**
     * Depending on the status of the billing run, produce the pre-invoicing report, the post-Invoicing report or validates a billing run. Sets the next invoice date of a billing
     * account to the next calendar date.
     * 
     * @param billingRunId The billing run id
     * @return Request processing status 
     */
    @POST
    @Path("/validateBillingRun")
    ActionStatus validateBillingRun(Long billingRunId);

    /**
     * Cancels a billing run. Sets RatedTransaction.status associated to billing run to OPEN. Remove aggregates and invoice associated to the billing run. Set
     * billingAccount.billingRun to null.
     * 
     * @param billingRunId Billing run id
     * @return Request processing status 
     */
    @POST
    @Path("/cancelBillingRun")
    ActionStatus cancelBillingRun(Long billingRunId);
    
    /**
     * Recompute invoices based on RatedTransactions and re-apply invoiceValidationScript
     *     
     *   
     */
    @PUT
    @Path("/{billingRunId}/rebuildInvoice")
    ActionStatus rebuildInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Reject one or several invoices (change status to REJECTED).
     * Body will contains a list of invoice ids.
     *   
     */
    @PUT
    @Path("/{billingRunId}/rejectInvoice")
    ActionStatus rejectInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Validate one or several invoices (change status to DRAFT).
     * Body will contain a list of invoice id
     *   
     */
    @PUT
    @Path("/{billingRunId}/validateInvoice")
    ActionStatus validateInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.
     *   
     */
    @PUT
    @Path("/{billingRunId}/moveInvoice")
    ActionStatus moveInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.
     *   
     */
    @PUT
    @Path("/{billingRunId}/cancelInvoice")
    ActionStatus cancelInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Delete canceled invoices for a given billing run.
     *   
     */
    @DELETE
    @Path("/{billingRunId}/canceledInvoices")
    ActionStatus canceledInvoices(@PathParam("billingRunId") Long billingRunId);


}
