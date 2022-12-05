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

package org.meveo.api.rest.account;

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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.account.AccessesResponseDto;
import org.meveo.api.dto.response.account.GetAccessResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;

import java.util.Date;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/access")
@Tag(name = "Access", description = "@%Access")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccessRs extends IBaseRs {

    /**
     * Create a new access
     * 
     * @param postData Access data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new access  ",
			description=" Create a new access  ",
			operationId="    POST_Access_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(AccessDto postData);

    /**
     * Update an existing access
     *
     * @param postData Access data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing access ",
			description=" Update an existing access ",
			operationId="    PUT_Access_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(AccessDto postData);

    /**
     * Search for an access with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @param usageDate a usage date
     * @return Access
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for an access with a given access code and subscription code.  ",
			description=" Search for an access with a given access code and subscription code.  ",
			operationId="    GET_Access_search",
			responses= {
				@ApiResponse(description=" Access ",
						content=@Content(
									schema=@Schema(
											implementation= GetAccessResponseDto.class
											)
								)
				)}
	)
    GetAccessResponseDto find(@QueryParam("accessCode") String accessCode, @QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") Date subscriptionValidityDate, @QueryParam("startDate") Date startDate, @QueryParam("endDate") Date endDate, @QueryParam("usageDate") Date usageDate);

    /**
     * Remove an access with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @return Request processing status
     */
    @DELETE
    @Path("/{accessCode}/{subscriptionCode}/{startDate}/{endDate}")
	@Operation(
			summary=" Remove an access with a given access code and subscription code.  ",
			description=" Remove an access with a given access code and subscription code.  ",
			operationId="    DELETE_Access_{accessCode}_{subscriptionCode}_{startDate}_{endDate}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("accessCode") String accessCode, @PathParam("subscriptionCode") String subscriptionCode, @PathParam("startDate") @RestDateParam Date startDate, @PathParam("endDate") @RestDateParam Date endDate);

    /**
     * List Access filtered by subscriptionCode.
     * 
     * @param subscriptionCode Subscription code
     * @return A list of accesses
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List Access filtered by subscriptionCode.  ",
			description=" List Access filtered by subscriptionCode.  ",
			operationId="    GET_Access_list",
			responses= {
				@ApiResponse(description=" A list of accesses ",
						content=@Content(
									schema=@Schema(
											implementation= AccessesResponseDto.class
											)
								)
				)}
	)
    AccessesResponseDto listBySubscription(@QueryParam("subscriptionCode") String subscriptionCode);

    /**
     * Create new or update an existing access
     * 
     * @param postData data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing access  ",
			description=" Create new or update an existing access  ",
			operationId="    POST_Access_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(AccessDto postData);

    /**
     * Enable an Access point with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @return Request processing status
     */
    @POST
    @Path("/{accessCode}/{subscriptionCode}/{startDate}/{endDate}/enable")
	@Operation(
			summary=" Enable an Access point with a given access code and subscription code.  ",
			description=" Enable an Access point with a given access code and subscription code.  ",
			operationId="    POST_Access_{accessCode}_{subscriptionCode}_{startDate}_{endDate}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("accessCode") String accessCode, @PathParam("subscriptionCode") String subscriptionCode, @PathParam("startDate") Date startDate, @PathParam("endDate") Date endDate);

    /**
     * Disable an Access point with a given access code and subscription code.
     * 
     * @param accessCode Access code
     * @param subscriptionCode Subscription code
     * @param startDate Access startDate
     * @param endDate Access endDate
     * @return Request processing status
     */
    @POST
    @Path("/{accessCode}/{subscriptionCode}/{startDate}/{endDate}/disable")
	@Operation(
			summary=" Disable an Access point with a given access code and subscription code.  ",
			description=" Disable an Access point with a given access code and subscription code.  ",
			operationId="    POST_Access_{accessCode}_{subscriptionCode}_{startDate}_{endDate}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("accessCode") String accessCode, @PathParam("subscriptionCode") String subscriptionCode, @PathParam("startDate") Date startDate, @PathParam("endDate") Date endDate);
}
