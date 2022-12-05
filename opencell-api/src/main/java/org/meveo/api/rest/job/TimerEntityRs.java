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

package org.meveo.api.rest.job;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.GetTimerEntityResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * 
 * @author Manu Liwanag
 * 
 */
@Path("/timerEntity")
@Tag(name = "TimerEntity", description = "@%TimerEntity")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TimerEntityRs extends IBaseRs {

    /**
     * Create a new timer schedule
     * 
     * @param postData The timer schedule's data
     * @return Request processing status
     */
    @POST
    @Path("/create")
	@Operation(
			summary=" Create a new timer schedule  ",
			description=" Create a new timer schedule  ",
			operationId="    POST_TimerEntity_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(TimerEntityDto postData);

    /**
     * Update an existing timer schedule
     * 
     * @param postData The timer schedule's data
     * @return Request processing status
     */
    @POST
    @Path("/update")
	@Operation(
			summary=" Update an existing timer schedule  ",
			description=" Update an existing timer schedule  ",
			operationId="    POST_TimerEntity_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(TimerEntityDto postData);

    /**
     * Create new or update an existing timer schedule with a given code
     * 
     * @param postData The timer schedule's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing timer schedule with a given code  ",
			description=" Create new or update an existing timer schedule with a given code  ",
			operationId="    POST_TimerEntity_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(TimerEntityDto postData);

    /**
     * Find a timer schedule with a given code
     * 
     * @param timerEntityCode The timer schedule's code
     * @return Return timerEntity
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a timer schedule with a given code  ",
			description=" Find a timer schedule with a given code  ",
			operationId="    GET_TimerEntity_search",
			responses= {
				@ApiResponse(description=" Return timerEntity ",
						content=@Content(
									schema=@Schema(
											implementation= GetTimerEntityResponseDto.class
											)
								)
				)}
	)
    GetTimerEntityResponseDto find(@QueryParam("timerEntityCode") String timerEntityCode);

    /**
     * Enable a Timer schedule with a given code
     * 
     * @param code Timer schedule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Timer schedule with a given code  ",
			description=" Enable a Timer schedule with a given code  ",
			operationId="    POST_TimerEntity_{code}_enable",
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
     * Disable a Timer schedule with a given code
     * 
     * @param code Timer schedule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Timer schedule with a given code  ",
			description=" Disable a Timer schedule with a given code  ",
			operationId="    POST_TimerEntity_{code}_disable",
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
