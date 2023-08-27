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

package org.meveo.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.response.BillingCyclesResponseDto;
import org.meveo.api.dto.response.GetBillingCycleResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billingCycle")
@Tag(name = "BillingCycle", description = "@%BillingCycle")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BillingCycleRs extends IBaseRs {

    /**
     * Search for list of billingCycles.
     *
     * @return list of billingCycles
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Search for list of billingCycles. ",
			description=" Search for list of billingCycles. ",
			operationId="    GET_BillingCycle_list",
			responses= {
				@ApiResponse(description=" list of billingCycles ",
						content=@Content(
									schema=@Schema(
											implementation= BillingCyclesResponseDto.class
											)
								)
				)}
	)
    BillingCyclesResponseDto list();

    /**
     * Create a new billing cycle.
     * 
     * @param postData billing cycle dto
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new billing cycle.  ",
			description=" Create a new billing cycle.  ",
			operationId="    POST_BillingCycle_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(BillingCycleDto postData);

    /**
     * Update an existing billing cycle.
     * 
     * @param postData billing cycle
     * @return actioon result
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing billing cycle.  ",
			description=" Update an existing billing cycle.  ",
			operationId="    PUT_BillingCycle_update",
			responses= {
				@ApiResponse(description=" actioon result ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(BillingCycleDto postData);

    /**
     * Search for billing cycle with a given code.
     * 
     * @param billingCycleCode The billing cycle's code
     * @return billing cycle if exists
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for billing cycle with a given code.  ",
			description=" Search for billing cycle with a given code.  ",
			operationId="    GET_BillingCycle_search",
			responses= {
				@ApiResponse(description=" billing cycle if exists ",
						content=@Content(
									schema=@Schema(
											implementation= GetBillingCycleResponse.class
											)
								)
				)}
	)
    GetBillingCycleResponse find(@QueryParam("billingCycleCode") String billingCycleCode);

    /**
     * Remove an existing billing cycle with a given code.
     * 
     * @param billingCycleCode The billing cycle's code
     * @return action result
     */
    @DELETE
    @Path("/{billingCycleCode}")
	@Operation(
			summary=" Remove an existing billing cycle with a given code.  ",
			description=" Remove an existing billing cycle with a given code.  ",
			operationId="    DELETE_BillingCycle_{billingCycleCode}",
			responses= {
				@ApiResponse(description=" action result ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("billingCycleCode") String billingCycleCode);

    /**
     * Create new or update an existing billing cycle with a given code
     * 
     * @param postData The billing cycle's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing billing cycle with a given code  ",
			description=" Create new or update an existing billing cycle with a given code  ",
			operationId="    POST_BillingCycle_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(BillingCycleDto postData);

}
