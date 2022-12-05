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
import jakarta.ws.rs.PUT;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.response.GetFilterResponseDto;

/**
 * @author Tyshan Shi
 **/
@Path("/filter")
@Tag(name = "Filter", description = "@%Filter")
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})

public interface FilterRs extends IBaseRs {

    /**
     * Create new or update an existing filter with a given code
     *
     * @param postData The filter's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing filter with a given code ",
			description=" Create new or update an existing filter with a given code ",
			operationId="    POST_Filter_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus createOrUpdate(FilterDto postData);

    /**
     * Find a filter with a given code
     *
     * @param filterCode The job instance's code
     * @return Dto for FilteredList API
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a filter with a given code ",
			description=" Find a filter with a given code ",
			operationId="    GET_Filter_search",
			responses= {
				@ApiResponse(description=" Dto for FilteredList API ",
						content=@Content(
									schema=@Schema(
											implementation= GetFilterResponseDto.class
											)
								)
				)}
	)
    public GetFilterResponseDto find(@QueryParam("filterCode") String filterCode);

    /**
     * Enable a Filter with a given code
     *
     * @param code Filter code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Filter with a given code ",
			description=" Enable a Filter with a given code ",
			operationId="    POST_Filter_{code}_enable",
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
     * Disable a Filter with a given code
     *
     * @param code Filter code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Filter with a given code ",
			description=" Disable a Filter with a given code ",
			operationId="    POST_Filter_{code}_disable",
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
     * update an existing filter.Same input parameter as create. If the filter code does not exists, a filter record is created. The operation fails if the filter is
     * null
     *
     * @param postData filter to be updated
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" update an existing filter",
			description=" update an existing filter.Same input parameter as create. If the filter code does not exists, a filter record is created. The operation fails if the filter is null ",
			operationId="    PUT_Filter_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(FilterDto postData);

    /**
     * Creates filter based on filter code. If the filter code does not exists, a filter record is created
     *
     * @param postData filter to be created
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Creates filter based on filter code. If the filter code does not exists, a filter record is created ",
			description=" Creates filter based on filter code. If the filter code does not exists, a filter record is created ",
			operationId="    POST_Filter_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(FilterDto postData);
}
