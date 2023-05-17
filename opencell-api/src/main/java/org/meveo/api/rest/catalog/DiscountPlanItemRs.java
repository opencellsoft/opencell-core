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

package org.meveo.api.rest.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.catalog.TradingDiscountPlanItemDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemResponseDto;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * CRUD/list discountPlanItem via REST API.
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Aug 2, 2016 11:02:01 AM
 *
 */
@Path("/catalog/discountPlanItem")
@Tag(name = "DiscountPlanItem", description = "@%DiscountPlanItem")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DiscountPlanItemRs extends IBaseRs {

    /**
     * Create a new discount plan item.
     *
     * @param postData A discount plan item's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new discount plan item. ",
			description=" Create a new discount plan item. ",
			operationId="    POST_DiscountPlanItem_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(DiscountPlanItemDto postData);

    /**
     * update an existed discount plan item.
     * 
     * @param postData A discount plan item's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" update an existed discount plan item.  ",
			description=" update an existed discount plan item.  ",
			operationId="    PUT_DiscountPlanItem_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(DiscountPlanItemDto postData);

    /**
     * Find a discount plan item with a given code.
     *
     * @param discountPlanItemCode A discount plan item's code
     * @return A discount plan item
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a discount plan item with a given code. ",
			description=" Find a discount plan item with a given code. ",
			operationId="    GET_DiscountPlanItem_search",
			responses= {
				@ApiResponse(description=" A discount plan item ",
						content=@Content(
									schema=@Schema(
											implementation= DiscountPlanItemResponseDto.class
											)
								)
				)}
	)
    DiscountPlanItemResponseDto find(@QueryParam("discountPlanItemCode") String discountPlanItemCode);

    /**
     * remove a discount plan item by code.
     *
     * @param discountPlanItemCode discount plan item
     * @return Request processing status
     */
    @DELETE
    @Path("/{discountPlanItemCode}")
	@Operation(
			summary=" remove a discount plan item by code. ",
			description=" remove a discount plan item by code. ",
			operationId="    DELETE_DiscountPlanItem_{discountPlanItemCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("discountPlanItemCode") String discountPlanItemCode);

    /**
     * create/update a discount plan item.
     *
     * @param postData discount plan item
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" create/update a discount plan item. ",
			description=" create/update a discount plan item. ",
			operationId="    POST_DiscountPlanItem_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(DiscountPlanItemDto postData);

    /**
     * List all discount plan items by current user.
     *
     * @return List of discount plan items
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List all discount plan items by current user. ",
			description=" List all discount plan items by current user. ",
			operationId="    GET_DiscountPlanItem_list",
			responses= {
				@ApiResponse(description=" List of discount plan items ",
						content=@Content(
									schema=@Schema(
											implementation= DiscountPlanItemsResponseDto.class
											)
								)
				)}
	)
    DiscountPlanItemsResponseDto list();

    /**
     * List DiscountPlanItems matching a given criteria
     *
     * @return List of DiscountPlanItems
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List DiscountPlanItems matching a given criteria ",
			description=" List DiscountPlanItems matching a given criteria ",
			operationId="    GET_DiscountPlanItem_listGetAll",
			responses= {
				@ApiResponse(description=" List of DiscountPlanItems ",
						content=@Content(
									schema=@Schema(
											implementation= DiscountPlanItemsResponseDto.class
											)
								)
				)}
	)
    DiscountPlanItemsResponseDto listGetAll();

    /**
     * Enable a Discount plan item with a given code
     * 
     * @param code Discount plan item code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Discount plan item with a given code  ",
			description=" Enable a Discount plan item with a given code  ",
			operationId="    POST_DiscountPlanItem_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Discount plan item with a given code
     * 
     * @param code Discount plan item code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Discount plan item with a given code  ",
			description=" Disable a Discount plan item with a given code  ",
			operationId="    POST_DiscountPlanItem_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("code") String code);
    
    /**
     * Create a new trading discount plan item.
     *
     * @param postData A trading discount plan item's data
     * @return Request processing status
     */
    @POST
    @Path("/tradingDiscountPlanItem")
	@Operation(
			summary=" Create a new trading discount plan item. ",
			description=" Create a new trading discount plan item. ",
			operationId="    POST_TradingDiscountPlanItem_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(TradingDiscountPlanItemDto postData);
    
    /**
     * Update a trading discount plan item.
     *
     * @param postData A trading discount plan item's data
     * @return Request processing status
     */
    @PUT
    @Path("/tradingDiscountPlanItem/{id}")
	@Operation(
			summary=" Update an existing trading discount plan item. ",
			description=" Update an existing new trading discount plan item. ",
			operationId="    POST_TradingDiscountPlanItem_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(@Parameter(description = "ID of trading discount plan item to update", required = true)  @PathParam("id") Long tradingDiscountPlanItemId, TradingDiscountPlanItemDto postData);
    
    /**
     * Delete a trading discount plan item.
     *
     * @param postData A trading discount plan item's data
     * @return Request processing status
     */
    @DELETE
    @Path("/tradingDiscountPlanItem/{id}")
	@Operation(
			summary=" Delete an existing trading discount plan item. ",
			description=" Delete an existing new trading discount plan item. ",
			operationId="    POST_TradingDiscountPlanItem_delete",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus delete(@Parameter(description = "ID of trading discount plan item to delete") @PathParam("id") Long tradingDiscountPlanItemId);
}
