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
import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.response.catalog.GetChannelResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/catalog/channel")
@Tag(name = "Channel", description = "@%Channel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ChannelRs extends IBaseRs {

    /**
     * Create a new channel
     * 
     * @param postData The channel's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new channel  ",
			description=" Create a new channel  ",
			operationId="    POST_Channel_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(ChannelDto postData);

    /**
     * Update an existing channel
     * 
     * @param postData The channel's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing channel  ",
			description=" Update an existing channel  ",
			operationId="    PUT_Channel_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(ChannelDto postData);

    /**
     * Search for a channel with a given code
     * 
     * @param channelCode The channel's code
     * @return A channel
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a channel with a given code  ",
			description=" Search for a channel with a given code  ",
			operationId="    GET_Channel_search",
			responses= {
				@ApiResponse(description=" A channel ",
						content=@Content(
									schema=@Schema(
											implementation= GetChannelResponseDto.class
											)
								)
				)}
	)
    GetChannelResponseDto find(@QueryParam("channelCode") String channelCode);

    /**
     * Remove an existing channel with a given code
     * 
     * @param channelCode The channel's code
     * @return Request processing status
     */
    @DELETE
    @Path("/")
	@Operation(
			summary=" Remove an existing channel with a given code  ",
			description=" Remove an existing channel with a given code  ",
			operationId="    DELETE_Channel_delete",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus delete(@QueryParam("channelCode") String channelCode);

    /**
     * Create new or update an existing channel
     * 
     * @param postData The channel's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing channel  ",
			description=" Create new or update an existing channel  ",
			operationId="    POST_Channel_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(ChannelDto postData);

    /**
     * Enable a Channel with a given code
     * 
     * @param code Channel code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Channel with a given code  ",
			description=" Enable a Channel with a given code  ",
			operationId="    POST_Channel_{code}_enable",
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
     * Disable a Channel with a given code
     * 
     * @param code Channel code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Channel with a given code  ",
			description=" Disable a Channel with a given code  ",
			operationId="    POST_Channel_{code}_disable",
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
