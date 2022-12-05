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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.InvoicingPlanDto;
import org.meveo.api.dto.response.billing.InvoicingPlanResponseDto;
import org.meveo.api.dto.response.billing.InvoicingPlansResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/billing/invoicingPlans")
@Tag(name = "InvoicingPlan", description = "@%InvoicingPlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface InvoicingPlanRs extends IBaseRs {
	/**
	 * Create a new invoicingPlan
	 * 
	 * @param postData The invoicingPlan's data
	 * @return Request processing status
	 */
	@POST
	@Path("/")
	@Operation(
			summary="	  Create a new invoicingPlan	  	  ",
			description="	  Create a new invoicingPlan	  	  ",
			operationId="POST_InvoicingPlan_create",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus create(InvoicingPlanDto postData);

	/**
	 * Search for a invoicingPlan with a given code
	 * 
	 * @param invoicingPlanCode The invoicingPlan's code
	 * @return A invoicingPlan's data
	 */
	@GET
	@Path("/")
	@Operation(
			summary="	  Search for a invoicingPlan with a given code	  	  ",
			description="	  Search for a invoicingPlan with a given code	  	  ",
			operationId="GET_InvoicingPlan_search",
			responses= {
				@ApiResponse(description=" A invoicingPlan's data	  ",
						content=@Content(
									schema=@Schema(
											implementation= InvoicingPlanResponseDto.class
											)
								)
				)}
	)
	InvoicingPlanResponseDto find(@QueryParam("invoicingPlanCode") String invoicingPlanCode);

	/**
	 * List invoicingPlans matching a given criteria
	 * 
	 * @param pagingAndFiltering Pagination and filtering criteria
	 * @return A list of invoicingPlans
	 */
	@POST
	@Path("/list")
	@Operation(
			summary="	  List invoicingPlans matching a given criteria	  	  ",
			description="	  List invoicingPlans matching a given criteria	  	  ",
			operationId="POST_InvoicingPlan_list",
			responses= {
				@ApiResponse(description=" A list of invoicingPlans	  ",
						content=@Content(
									schema=@Schema(
											implementation= InvoicingPlansResponseDto.class
											)
								)
				)}
	)
	InvoicingPlansResponseDto list(PagingAndFiltering pagingAndFiltering);

	/**
	 * Update an existing invoicingPlan
	 * 
	 * @param postData The invoicingPlan's data
	 * @return Request processing status
	 */
	@PUT
	@Path("/")
	@Operation(
			summary="	  Update an existing invoicingPlan	  	  ",
			description="	  Update an existing invoicingPlan	  	  ",
			operationId="PUT_InvoicingPlan_update",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
	Response update(InvoicingPlanDto postData);

	/**
	 * Remove an existing invoicingPlan with a given code
	 * 
	 * @param invoicingPlanCode The invoicingPlan's code
	 * @return Request processing status
	 */
	@DELETE
	@Path("/{invoicingPlanCode}")
	@Operation(
			summary="	  Remove an existing invoicingPlan with a given code	  	  ",
			description="	  Remove an existing invoicingPlan with a given code	  	  ",
			operationId="DELETE_InvoicingPlan_{invoicingPlanCode}",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	public ActionStatus remove(@PathParam("invoicingPlanCode") String invoicingPlanCode);

}
