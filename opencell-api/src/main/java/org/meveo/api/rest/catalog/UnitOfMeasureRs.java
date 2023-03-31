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
import org.meveo.api.dto.catalog.UnitOfMeasureDto;
import org.meveo.api.dto.response.catalog.GetListUnitOfMeasureResponseDto;
import org.meveo.api.dto.response.catalog.GetUnitOfMeasureResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
/**
 * @author Mounir Bahije
 **/

@Path("/catalog/unitOfMeasure")
@Tag(name = "UnitOfMeasure", description = "@%UnitOfMeasure")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UnitOfMeasureRs extends IBaseRs {

    /**
     * Create a new unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/")
	@Operation(
			summary=" Create a new unitOfMeasure  ",
			description=" Create a new unitOfMeasure  ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @POST
    ActionStatus create(UnitOfMeasureDto postData);

    /**
     * Update an existing unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/")
	@Operation(
			summary=" Update an existing unitOfMeasure  ",
			description=" Update an existing unitOfMeasure  ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @PUT
    ActionStatus update(UnitOfMeasureDto postData);

    /**
     * Search for a unitOfMeasure with a given code
     * 
     * @param unitOfMeasureCode The unitOfMeasure's code
     * @return A unitOfMeasure
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a unitOfMeasure with a given code  ",
			description=" Search for a unitOfMeasure with a given code  ",
			operationId="    GET_UnitOfMeasure_search",
			responses= {
				@ApiResponse(description=" A unitOfMeasure ",
						content=@Content(
									schema=@Schema(
											implementation= GetUnitOfMeasureResponseDto.class
											)
								)
				)}
	)
    GetUnitOfMeasureResponseDto find(@QueryParam("unitOfMeasureCode") String unitOfMeasureCode);

    /**
     * Remove an existing unitOfMeasure with a given code
     * 
     * @param unitOfMeasureCode The unitOfMeasure's code
     * @return Request processing status
     */
    @Path("/{code}")
	@Operation(
			summary=" Remove an existing unitOfMeasure with a given code  ",
			description=" Remove an existing unitOfMeasure with a given code  ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @DELETE
    ActionStatus delete(@PathParam("code") String unitOfMeasureCode);

    /**
     * Create new or update an existing unitOfMeasure
     * 
     * @param postData The unitOfMeasure's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing unitOfMeasure  ",
			description=" Create new or update an existing unitOfMeasure  ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @POST
    ActionStatus createOrUpdate(UnitOfMeasureDto postData);

    /**
     * List all currencies.
     * @return list of all unitOfMeasure/
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List all currencies. ",
			description=" List all currencies. ",
			operationId="    GET_UnitOfMeasure_list",
			responses= {
				@ApiResponse(description=" list of all unitOfMeasure/ ",
						content=@Content(
									schema=@Schema(
											implementation= GetListUnitOfMeasureResponseDto.class
											)
								)
				)}
	)
    GetListUnitOfMeasureResponseDto list();

    /**
     * List UnitOfMeasures matching a given criteria
     *
     * @return List of UnitOfMeasures
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List UnitOfMeasures matching a given criteria ",
			description=" List UnitOfMeasures matching a given criteria ",
			operationId="    GET_UnitOfMeasure_listGetAll",
			responses= {
				@ApiResponse(description=" List of UnitOfMeasures ",
						content=@Content(
									schema=@Schema(
											implementation= GetListUnitOfMeasureResponseDto.class
											)
								)
				)}
	)
    GetListUnitOfMeasureResponseDto listGetAll();
}
