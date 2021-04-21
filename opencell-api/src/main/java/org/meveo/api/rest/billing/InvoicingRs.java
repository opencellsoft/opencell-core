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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.billing.CancelBillingRunRequestDto;
import org.meveo.api.dto.billing.CreateBillingRunDto;
import org.meveo.api.dto.billing.ValidateBillingRunRequestDto;
import org.meveo.api.dto.response.billing.GetBillingAccountListInRunResponseDto;
import org.meveo.api.dto.response.billing.GetBillingRunInfoResponseDto;
import org.meveo.api.dto.response.billing.GetPostInvoicingReportsResponseDto;
import org.meveo.api.dto.response.billing.GetPreInvoicingReportsResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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

}
