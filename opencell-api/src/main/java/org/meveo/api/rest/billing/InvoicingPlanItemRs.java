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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.InvoicingPlanItemDto;
import org.meveo.api.dto.response.billing.InvoicingPlanItemResponseDto;
import org.meveo.api.dto.response.billing.InvoicingPlanItemsResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/billing/invoicingPlanItems")
@Tag(name = "InvoicingPlanItem", description = "@%InvoicingPlanItem")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface InvoicingPlanItemRs extends IBaseRs {
	/**
	 * Create a new invoicingPlanItem
	 * 
	 * @param postData The invoicingPlanItem's data
	 * @return Request processing status
	 */
	@POST
	@Path("/")
	@Operation(
			summary="Create a new invoicingPlanItem",
			description="Create a new invoicingPlanItem",
			operationId="POST_InvoicingPlanItem_create",
			responses= {
				@ApiResponse(description="Request processing status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus create(InvoicingPlanItemDto postData);

	/**
	 * Search for a invoicingPlanItem with a given code
	 * 
	 * @param invoicingPlanItemCode The invoicingPlanItem's code
	 * @return A invoicingPlanItem's data
	 */
	@GET
	@Path("/")
	@Operation(
			summary="Search for a invoicingPlanItem with a given code",
			description="Search for a invoicingPlanItem with a given code",
			operationId="GET_InvoicingPlanItem_search",
			responses= {
				@ApiResponse(description="A invoicingPlanItem's data",
						content=@Content(
									schema=@Schema(
											implementation= InvoicingPlanItemResponseDto.class
											)
								)
				)}
	)
	InvoicingPlanItemResponseDto find(@QueryParam("invoicingPlanItemCode") String invoicingPlanItemCode);

	/**
	 * List invoicingPlanItems matching a given criteria
	 * 
	 * @param pagingAndFiltering Pagination and filtering criteria
	 * @return A list of invoicingPlanItems
	 */
	@POST
	@Path("/list")
	@Operation(
			summary="List invoicingPlanItems matching a given criteria",
			description="List invoicingPlanItems matching a given criteria",
			operationId="POST_InvoicingPlanItem_list",
			responses= {
				@ApiResponse(description="A list of invoicingPlanItems",
						content=@Content(
									schema=@Schema(
											implementation= InvoicingPlanItemsResponseDto.class
											)
								)
				)}
	)
	InvoicingPlanItemsResponseDto list(PagingAndFiltering pagingAndFiltering);

	/**
	 * Update an existing invoicingPlanItem
	 * 
	 * @param postData The invoicingPlanItem's data
	 * @return Request processing status
	 */
	@PUT
	@Path("/")
	@Operation(
			summary="Update an existing invoicingPlanItem",
			description="Update an existing invoicingPlanItem",
			operationId="PUT_InvoicingPlanItem_update",
			responses= {
				@ApiResponse(description="Request processing status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus update(InvoicingPlanItemDto postData);

	/**
	 * Remove an existing invoicingPlanItem with a given code
	 * 
	 * @param invoicingPlanItemCode The invoicingPlanItem's code
	 * @return Request processing status
	 */
	@DELETE
	@Path("/{invoicingPlanItemCode}")
	@Operation(
			summary="Remove an existing invoicingPlanItem with a given code",
			description="Remove an existing invoicingPlanItem with a given code",
			operationId="DELETE_InvoicingPlanItem_{invoicingPlanItemCode}",
			responses= {
				@ApiResponse(description="Request processing status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus remove(@PathParam("invoicingPlanItemCode") String invoicingPlanItemCode);

	/**
	 * Create new or update an existing invoicingPlanItem
	 * 
	 * @param postData The invoicingPlanItem's data
	 * @return Request processing status
	 */
	@POST
	@Path("/createOrUpdate")
	@Operation(
			summary="Create new or update an existing invoicingPlanItem",
			description="Create new or update an existing invoicingPlanItem",
			operationId="POST_InvoicingPlanItem_createOrUpdate",
			responses= {
				@ApiResponse(description="Request processing status",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
	ActionStatus createOrUpdate(InvoicingPlanItemDto postData);
}
