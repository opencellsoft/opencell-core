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
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponseDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlansResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/catalog/discountPlan")
@Tag(name = "DiscountPlan", description = "@%DiscountPlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DiscountPlanRs extends IBaseRs {

    /**
     * Create a new discount plan
     * 
     * @param postData The discount plan's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new discount plan  ",
			description=" Create a new discount plan  ",
			operationId="    POST_DiscountPlan_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(DiscountPlanDto postData);

    /**
     * Update an existing discount plan
     * 
     * @param postData The discount plan's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing discount plan  ",
			description=" Update an existing discount plan  ",
			operationId="    PUT_DiscountPlan_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(DiscountPlanDto postData);

    /**
     * Find a discount plan with a given code 
     * 
     * @param discountPlanCode The discount plan's code
     * @return Return discount plan
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a discount plan with a given code   ",
			description=" Find a discount plan with a given code   ",
			operationId="    GET_DiscountPlan_search",
			responses= {
				@ApiResponse(description=" Return discount plan ",
						content=@Content(
									schema=@Schema(
											implementation= GetDiscountPlanResponseDto.class
											)
								)
				)}
	)
    GetDiscountPlanResponseDto find(@QueryParam("discountPlanCode") String discountPlanCode);

    /**
     * Remove an existing discount plan with a given code 
     * 
     * @param discountPlanCode The discount plan's code
     * @return Request processing status
     */
    @DELETE
    @Path("/")
	@Operation(
			summary=" Remove an existing discount plan with a given code   ",
			description=" Remove an existing discount plan with a given code   ",
			operationId="    DELETE_DiscountPlan_delete",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@QueryParam("discountPlanCode") String discountPlanCode);

    /**
     * Create new or update an existing discount plan
     * 
     * @param postData The discount plan's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing discount plan  ",
			description=" Create new or update an existing discount plan  ",
			operationId="    POST_DiscountPlan_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(DiscountPlanDto postData);

    /**
     * List discount plan
     * 
     * @return A list of discount plans
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List discount plan  ",
			description=" List discount plan  ",
			operationId="    GET_DiscountPlan_list",
			responses= {
				@ApiResponse(description=" A list of discount plans ",
						content=@Content(
									schema=@Schema(
											implementation= GetDiscountPlansResponseDto.class
											)
								)
				)}
	)
    GetDiscountPlansResponseDto list();

    /**
     * Enable a Discount plan with a given code
     * 
     * @param code Discount plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Discount plan with a given code  ",
			description=" Enable a Discount plan with a given code  ",
			operationId="    POST_DiscountPlan_{code}_enable",
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
     * Disable a Discount plan with a given code
     * 
     * @param code Discount plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Discount plan with a given code  ",
			description=" Disable a Discount plan with a given code  ",
			operationId="    POST_DiscountPlan_{code}_disable",
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

}
