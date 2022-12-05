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
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.response.GetTerminationReasonResponse;

@Path("/terminationReason")
@Tag(name = "TerminationReason", description = "@%TerminationReason")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TerminationReasonRs extends IBaseRs {

    /**
     * Create a new termination reason.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new termination reason.  ",
			description=" Create a new termination reason.  ",
			operationId="    POST_TerminationReason_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(TerminationReasonDto postData);

    /**
     * Update an existing termination reason.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing termination reason.  ",
			description=" Update an existing termination reason.  ",
			operationId="    PUT_TerminationReason_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(TerminationReasonDto postData);

    /**
     * Create new or update an existing termination reason with a given code.
     * 
     * @param postData The termination reason's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing termination reason with a given code.  ",
			description=" Create new or update an existing termination reason with a given code.  ",
			operationId="    POST_TerminationReason_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(TerminationReasonDto postData);

    /**
     * Remove an existing termination reason with a given code.
     * 
     * @param terminationReasonCode The termination reason's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{terminationReasonCode}")
	@Operation(
			summary=" Remove an existing termination reason with a given code.  ",
			description=" Remove an existing termination reason with a given code.  ",
			operationId="    DELETE_TerminationReason_{terminationReasonCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("terminationReasonCode") String terminationReasonCode);

    /**
     * Find a termination reason with a given code.
     * 
     * @param code The termination reason's code
     * @return found termination reason
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a termination reason with a given code.  ",
			description=" Find a termination reason with a given code.  ",
			operationId="    GET_TerminationReason_search",
			responses= {
				@ApiResponse(description=" found termination reason ",
						content=@Content(
									schema=@Schema(
											implementation= GetTerminationReasonResponse.class
											)
								)
				)}
	)
    GetTerminationReasonResponse find(@QueryParam("terminationReasonCode") String code);

    /**
     * List of termination reasons.
     * 
     * @return A list of termination reasons
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List of termination reasons.  ",
			description=" List of termination reasons.  ",
			operationId="    GET_TerminationReason_list",
			responses= {
				@ApiResponse(description=" A list of termination reasons ",
						content=@Content(
									schema=@Schema(
											implementation= GetTerminationReasonResponse.class
											)
								)
				)}
	)
    GetTerminationReasonResponse list();

}
