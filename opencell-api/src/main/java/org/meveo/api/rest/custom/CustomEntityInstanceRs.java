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

package org.meveo.api.rest.custom;

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
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityInstancesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.rest.IBaseRs;

/**
 * Rest API for custom entity instance management
 * 
 * @author Andrius Karpavicius
 **/
@Path("/customEntityInstance")
@Tag(name = "CustomEntityInstance", description = "@%CustomEntityInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomEntityInstanceRs extends IBaseRs {

    /**
     * Create a new custom entity instance using a custom entity template.
     *
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode The custom entity template's code
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}")
	@Operation(
			summary=" Create a new custom entity instance using a custom entity template. ",
			description=" Create a new custom entity instance using a custom entity template. ",
			operationId="    POST_CustomEntityInstance_{customEntityTemplateCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Update an existing custom entity instance using a custom entity template
     * 
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode The custom entity template's code
     * @return Request processing status
     */
    @PUT
    @Path("/{customEntityTemplateCode}")
	@Operation(
			summary=" Update an existing custom entity instance using a custom entity template  ",
			description=" Update an existing custom entity instance using a custom entity template  ",
			operationId="    PUT_CustomEntityInstance_{customEntityTemplateCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Remove an existing custom entity instance with a given code from a custom entity template given by code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code The custom entity instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{customEntityTemplateCode}/{code}")
	@Operation(
			summary=" Remove an existing custom entity instance with a given code from a custom entity template given by code  ",
			description=" Remove an existing custom entity instance with a given code from a custom entity template given by code  ",
			operationId="    DELETE_CustomEntityInstance_{customEntityTemplateCode}_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * Find a #### with a given (exemple) code .
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code The custom entity instance's code
     * @return Return a customEntityInstance
     */
    @GET
    @Path("/{customEntityTemplateCode}/{code}")
	@Operation(
			summary=" Find a #### with a given (exemple) code .  ",
			description=" Find a #### with a given (exemple) code .  ",
			operationId="    GET_CustomEntityInstance_{customEntityTemplateCode}_{code}",
			responses= {
				@ApiResponse(description=" Return a customEntityInstance ",
						content=@Content(
									schema=@Schema(
											implementation= CustomEntityInstanceResponseDto.class
											)
								)
				)}
	)
    CustomEntityInstanceResponseDto find(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * List custom entity instances.
     * 
     * @param customEntityTemplateCode The custom entity instance's code
     * @return A list of custom entity instances
     */
    @GET
    @Path("/list/{customEntityTemplateCode}")
	@Operation(
			summary=" List custom entity instances.  ",
			description=" List custom entity instances.  ",
			operationId="    GET_CustomEntityInstance_list_{customEntityTemplateCode}",
			responses= {
				@ApiResponse(description=" A list of custom entity instances ",
						content=@Content(
									schema=@Schema(
											implementation= CustomEntityInstancesResponseDto.class
											)
								)
				)}
	)
    CustomEntityInstancesResponseDto list(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    /**
     * Search in custom entity instances.
     * 
     * @param customEntityTemplateCode The custom entity instance's code
     * @param pagingAndFiltering Paging and search criteria
     * @return Custom table data
     */
    @POST
    @Path("/list/{customEntityTemplateCode}")
	@Operation(
			summary=" Search in custom entity instances.  ",
			description=" Search in custom entity instances.  ",
			operationId="    POST_CustomEntityInstance_list_{customEntityTemplateCode}",
			responses= {
				@ApiResponse(description=" Custom table data ",
						content=@Content(
									schema=@Schema(
											implementation= CustomEntityInstancesResponseDto.class
											)
								)
				)}
	)
    CustomEntityInstancesResponseDto list(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, PagingAndFiltering pagingAndFiltering);

    /**
     * Create new or update an existing custom entity instance with a given code.
     * 
     * @param dto The custom entity instance's data
     * @param customEntityTemplateCode code of custome entity template.
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing custom entity instance with a given code.  ",
			description=" Create new or update an existing custom entity instance with a given code.  ",
			operationId="    POST_CustomEntityInstance_{customEntityTemplateCode}_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    /**
     * Enable a Custom entity instance with a given code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code Custom entity instance code
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}/{code}/enable")
	@Operation(
			summary=" Enable a Custom entity instance with a given code  ",
			description=" Enable a Custom entity instance with a given code  ",
			operationId="    POST_CustomEntityInstance_{customEntityTemplateCode}_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    /**
     * Disable a Custom entity instance with a given code
     * 
     * @param customEntityTemplateCode The custom entity template's code
     * @param code Custom entity instance code
     * @return Request processing status
     */
    @POST
    @Path("/{customEntityTemplateCode}/{code}/disable")
	@Operation(
			summary=" Disable a Custom entity instance with a given code  ",
			description=" Disable a Custom entity instance with a given code  ",
			operationId="    POST_CustomEntityInstance_{customEntityTemplateCode}_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);
}
