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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.TitleDto;
import org.meveo.api.dto.response.account.TitleResponseDto;
import org.meveo.api.dto.response.account.TitlesResponseDto;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/account/title")
@Tag(name = "Title", description = "@%Title")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TitleRs extends IBaseRs {

    /**
     * Create a new title
     * 
     * @param postData The title's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new title  ",
			description=" Create a new title  ",
			operationId="    POST_Title_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(TitleDto postData);

    /**
     * Search for a title with a given code 
     * 
     * @param titleCode The title's code
     * @return A title's data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a title with a given code   ",
			description=" Search for a title with a given code   ",
			operationId="    GET_Title_search",
			responses= {
				@ApiResponse(description=" A title's data ",
						content=@Content(
									schema=@Schema(
											implementation= TitleResponseDto.class
											)
								)
				)}
	)
    TitleResponseDto find(@QueryParam("titleCode") String titleCode);

    /**
     * List titles 
     * 
     * @return A list of titles
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List titles   ",
			description=" List titles   ",
			operationId="    GET_Title_list",
			responses= {
				@ApiResponse(description=" A list of titles ",
						content=@Content(
									schema=@Schema(
											implementation= TitlesResponseDto.class
											)
								)
				)}
	)
    TitlesResponseDto list();

    /**
     * List titles matching a given criteria
     *
     * @return List of titles
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List titles matching a given criteria ",
			description=" List titles matching a given criteria ",
			operationId="    GET_Title_listGetAll",
			responses= {
				@ApiResponse(description=" List of titles ",
						content=@Content(
									schema=@Schema(
											implementation= TitlesResponseDto.class
											)
								)
				)}
	)
    TitlesResponseDto listGetAll();

    /**
     * Update an existing title
     * 
     * @param postData The title's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing title  ",
			description=" Update an existing title  ",
			operationId="    PUT_Title_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(TitleDto postData);

    /**
     * Remove an existing title with a given code 
     * 
     * @param titleCode The title's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{titleCode}")
	@Operation(
			summary=" Remove an existing title with a given code   ",
			description=" Remove an existing title with a given code   ",
			operationId="    DELETE_Title_{titleCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("titleCode") String titleCode);

    /**
     * Create new or update an existing title
     * 
     * @param postData The title's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing title  ",
			description=" Create new or update an existing title  ",
			operationId="    POST_Title_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(TitleDto postData);
}
