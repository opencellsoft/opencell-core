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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CancelBillingRunRequestDto;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.billing.InvalidateInvoiceDocumentsDto;
import org.meveo.api.dto.billing.InvoiceValidationDto;
import org.meveo.api.dto.billing.ValidateBillingRunRequestDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/billing/invoicing")
@Tag(name = "Invoicing", description = "@%Invoicing")
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
	@Operation(
			summary=" Create a new billing run. The id of the created BillingRun is returned on 'message' field of response object.  ",
			description=" Create a new billing run. The id of the created BillingRun is returned on 'message' field of response object.  ",
			operationId="    POST_Invoicing_createBillingRun",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createBillingRun(CreateBillingRunDto createBillingRunDto);

    /**
     * Create a new billing run. The id of the created BillingRun is returned on 'message' field of response object.
     * 
     * @param createBillingRunDto The billing run's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdateBillingRun")
	@Operation(
			summary=" Create or Update billing run. The id of the created BillingRun is returned on 'message' field of response object.  ",
			description=" Create a new billing run. The id of the created BillingRun is returned on 'message' field of response object.  ",
			operationId="    POST_Invoicing_createBillingRun",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdateBillingRun(CreateBillingRunDto createBillingRunDto);
    
    
    /**
     * Search for a billing run info with a given Id 
     * 
     * @param billingRunId The billing run's Id
     * @return The billing run info
     */
    @POST
    @Path("/getBillingRunInfo")
	@Operation(
			summary=" Search for a billing run info with a given Id   ",
			description=" Search for a billing run info with a given Id   ",
			operationId="    POST_Invoicing_getBillingRunInfo",
			responses= {
				@ApiResponse(description=" The billing run info ",
						content=@Content(
									schema=@Schema(
											implementation= GetBillingRunInfoResponseDto.class
											)
								)
				)}
	)
    GetBillingRunInfoResponseDto getBillingRunInfo(Long billingRunId);

    /**
     * Returns the list of billable billing accounts of a billing run
     * 
     * @param billingRunId The billing run id
     * @return A list of billing accounts
     */
    @POST
    @Path("/getBillingAccountListInRun")
	@Operation(
			summary=" Returns the list of billable billing accounts of a billing run  ",
			description=" Returns the list of billable billing accounts of a billing run  ",
			operationId="    POST_Invoicing_getBillingAccountListInRun",
			responses= {
				@ApiResponse(description=" A list of billing accounts ",
						content=@Content(
									schema=@Schema(
											implementation= GetBillingAccountListInRunResponseDto.class
											)
								)
				)}
	)
    GetBillingAccountListInRunResponseDto getBillingAccountListInRun(Long billingRunId);

    /**
     * Returns the pre-invoicing report for a given billing run Id
     * 
     * @param billingRunId The billing run id
     * @return A pre-invoicing reports
     */
    @POST
    @Path("/getPreInvoicingReport")
	@Operation(
			summary=" Returns the pre-invoicing report for a given billing run Id  ",
			description=" Returns the pre-invoicing report for a given billing run Id  ",
			operationId="    POST_Invoicing_getPreInvoicingReport",
			responses= {
				@ApiResponse(description=" A pre-invoicing reports ",
						content=@Content(
									schema=@Schema(
											implementation= GetPreInvoicingReportsResponseDto.class
											)
								)
				)}
	)
    GetPreInvoicingReportsResponseDto getPreInvoicingReport(Long billingRunId);

    /**
     * Returns the post-invoicing report for a given billing run Id
     * 
     * @param billingRunId The billing run id
     * @return A post-invoicing reports
     */
    @POST
    @Path("/getPostInvoicingReport")
	@Operation(
			summary=" Returns the post-invoicing report for a given billing run Id  ",
			description=" Returns the post-invoicing report for a given billing run Id  ",
			operationId="    POST_Invoicing_getPostInvoicingReport",
			responses= {
				@ApiResponse(description=" A post-invoicing reports ",
						content=@Content(
									schema=@Schema(
											implementation= GetPostInvoicingReportsResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Depending on the status of the billing run, produce the pre-invoicing report, the post-Invoicing report or validates a billing run",
			description=" Depending on the status of the billing run, produce the pre-invoicing report, the post-Invoicing report or validates a billing run. Sets the next invoice date of a billing account to the next calendar date.  ",
			operationId="    POST_Invoicing_validateBillingRun",
			responses= {
				@ApiResponse(description=" Request processing status  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus validateBillingRun(Long billingRunId);

    /**
     * Validate a billing run based on billingRun id.
     *
     * @param billingRunId The billing run id
     * @return Request processing status
     */
    @PUT
    @Path("/validateBillingRun")
	@Operation(
			summary=" Validate a billing run based on billingRun id. ",
			description=" Validate a billing run based on billingRun id. ",
			operationId="    PUT_Invoicing_validateBillingRun",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus validateBillingRun(ValidateBillingRunRequestDto billingRunId);

    /**
     * Cancels a billing run. Sets RatedTransaction.status associated to billing run to OPEN. Remove aggregates and invoice associated to the billing run. Set
     * billingAccount.billingRun to null.
     * 
     * @param billingRunId Billing run id
     * @return Request processing status 
     */
    @POST
    @Path("/cancelBillingRun")
	@Operation(
			summary=" Cancels a billing run",
			description=" Cancels a billing run. Sets RatedTransaction.status associated to billing run to OPEN. Remove aggregates and invoice associated to the billing run. Set billingAccount.billingRun to null.  ",
			operationId="    POST_Invoicing_cancelBillingRun",
			responses= {
				@ApiResponse(description=" Request processing status  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus cancelBillingRun(Long billingRunId);

    /**
     * Cancel a billing run based on billingRun id.
     *
     * @param putData CancelBillingRunRequestDto
     * @return action status.
     */
    @PUT
    @Path("/cancelBillingRun")
	@Operation(
			summary=" Cancel a billing run based on billingRun id. ",
			description=" Cancel a billing run based on billingRun id. ",
			operationId="    PUT_Invoicing_cancelBillingRun",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus cancelBillingRun(CancelBillingRunRequestDto putData);
    
    /**
     * Recompute invoices based on RatedTransactions and re-apply invoiceValidationScript
     *     
     *   
     */
    @PUT
    @Path("/rebuildInvoice")
	@Operation(
			summary=" Recompute invoices based on RatedTransactions and re-apply invoiceValidationScript    ",
			description=" Recompute invoices based on RatedTransactions and re-apply invoiceValidationScript    ",
			operationId="    PUT_Invoicing_rebuildInvoice",
			responses= {
				@ApiResponse(description="type ActionStatus.class Recompute invoices based on RatedTransactions and re-apply invoiceValidationScript ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus rebuildInvoice(InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Reject one or several invoices (change status to REJECTED).
     * Body will contains a list of invoice ids.
     *   
     */
    @PUT
    @Path("/billingRun/{billingRunId}/rejectInvoice")
	@Operation(
			summary=" Reject one or several invoices (change status to REJECTED). Body will contains a list of invoice ids.    ",
			description=" Reject one or several invoices (change status to REJECTED). Body will contains a list of invoice ids.    ",
			operationId="    PUT_Invoicing_billingRun_{billingRunId}_rejectInvoice",
			responses= {
				@ApiResponse(description="type ActionStatus.class Reject one or several invoices (change status to REJECTED). Body will contains a list of invoice ids. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus rejectInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Validate one or several invoices (change status to DRAFT).
     * Body will contain a list of invoice id
     *   
     */
    @PUT
    @Path("/billingRun/{billingRunId}/validateInvoice")
	@Operation(
			summary=" Validate one or several invoices (change status to DRAFT). Body will contain a list of invoice id    ",
			description=" Validate one or several invoices (change status to DRAFT). Body will contain a list of invoice id    ",
			operationId="    PUT_Invoicing_billingRun_{billingRunId}_validateInvoice",
			responses= {
				@ApiResponse(description="type ActionStatus.class Validate one or several invoices (change status to DRAFT). Body will contain a list of invoice id ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus validateInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    
    /**
     * Validate one or several invoices (change status to DRAFT).
     * Body will contain a list of invoice id
     *   
     */
    @PUT
    @Path("/billingRun/{billingRunId}/invalidateInvoiceDocuments")
    @Operation(
            summary="This API will empty xml_filename and pdf_filename from all invoices in the specified billing run.",
            description="This API will empty xml_filename and pdf_filename from all invoices in the specified billing run.",
            operationId="    PUT_Invoicing_billingRun_{billingRunId}_invalidateInvoice",
            responses= {
                @ApiResponse(description="type ActionStatus.class Invalidate billing run invoice files",
                        content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                            )
                                )
                )}
    )
    ActionStatus invalidateInvoiceDocuments(@PathParam("billingRunId") Long billingRunId, InvalidateInvoiceDocumentsDto invalidateInvoiceDocumentsDto);

    /**
     * Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.
     *   
     */
    @PUT
    @Path("/billingRun/{billingRunId}/moveInvoice")
	@Operation(
			summary=" Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.    ",
			description=" Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.    ",
			operationId="    PUT_Invoicing_billingRun_{billingRunId}_moveInvoice",
			responses= {
				@ApiResponse(description="type ActionStatus.class Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus moveInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.
     *   
     */
    @PUT
    @Path("/billingRun/{billingRunId}/cancelInvoice")
	@Operation(
			summary=" Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.    ",
			description=" Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED.    ",
			operationId="    PUT_Invoicing_billingRun_{billingRunId}_cancelInvoice",
			responses= {
				@ApiResponse(description="type ActionStatus.class Move invoices to a new Billing Run with the same parameters as the current one, and also in status REJECTED|POSTINVOICED. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus cancelInvoice(@PathParam("billingRunId") Long billingRunId, InvoiceValidationDto InvoiceValidationDto);
    
    /**
     * Delete canceled invoices for a given billing run.
     *   
     */
    @DELETE
    @Path("/billingRun/{billingRunId}/canceledInvoices")
	@Operation(
			summary=" Delete canceled invoices for a given billing run.    ",
			description=" Delete canceled invoices for a given billing run.    ",
			operationId="    DELETE_Invoicing_billingRun_{billingRunId}_canceledInvoices",
			responses= {
				@ApiResponse(description="type ActionStatus.class Delete canceled invoices for a given billing run. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus canceledInvoices(@PathParam("billingRunId") Long billingRunId);

}
