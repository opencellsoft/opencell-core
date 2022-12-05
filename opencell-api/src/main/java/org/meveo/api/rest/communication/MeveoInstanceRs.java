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

package org.meveo.api.rest.communication;

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
import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.communication.MeveoInstanceResponseDto;
import org.meveo.api.dto.response.communication.MeveoInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 4, 2016 4:05:47 AM
 *
 */
@Path("/communication/meveoInstance")
@Tag(name = "MeveoInstance", description = "@%MeveoInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface MeveoInstanceRs extends IBaseRs {

	/**
	 * Create a meveoInstance by dto.
     *
	 * @param meveoInstanceDto meveo instance
	 * @return action status
	 */
	@POST
    @Path("/")
	@Operation(
			summary="	  Create a meveoInstance by dto.	  ",
			description="	  Create a meveoInstance by dto.	  ",
			operationId="POST_MeveoInstance_create",
			responses= {
				@ApiResponse(description=" action status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(MeveoInstanceDto meveoInstanceDto);

	/**
	 * Update a meveoInstance by dto
     *
	 * @param meveoInstanceDto
	 * @return Request processing status
	 */
    @PUT
    @Path("/")
	@Operation(
			summary="	  Update a meveoInstance by dto	  ",
			description="	  Update a meveoInstance by dto	  ",
			operationId="    PUT_MeveoInstance_update",
			responses= {
				@ApiResponse(description=" Request processing status	  ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(MeveoInstanceDto meveoInstanceDto);

    /**
     * Find a meveoInstance by code
     *
     * @param code the code of the meveo instance
     * @return Request processing status
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a meveoInstance by code ",
			description=" Find a meveoInstance by code ",
			operationId="    GET_MeveoInstance_search",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoInstanceResponseDto.class
											)
								)
				)}
	)
    MeveoInstanceResponseDto find(@QueryParam("code") String code);

    /**
     * Remove a meveoInstance by code
     *
     * @param code the code of the meveo instance
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" Remove a meveoInstance by code ",
			description=" Remove a meveoInstance by code ",
			operationId="    DELETE_MeveoInstance_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List meveoInstances
     *
     * @return List of Meveo Instances 
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List meveoInstances ",
			description=" List meveoInstances ",
			operationId="    GET_MeveoInstance_list",
			responses= {
				@ApiResponse(description=" List of Meveo Instances  ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoInstancesResponseDto.class
											)
								)
				)}
	)
    MeveoInstancesResponseDto list();

    /**
     * CreateOrUpdate a meveoInstance by dto
     *
     * @param meveoInstanceDto meveo Instance data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" CreateOrUpdate a meveoInstance by dto ",
			description=" CreateOrUpdate a meveoInstance by dto ",
			operationId="    POST_MeveoInstance_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(MeveoInstanceDto meveoInstanceDto);
}

