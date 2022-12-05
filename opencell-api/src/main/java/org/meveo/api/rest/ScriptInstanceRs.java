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
import jakarta.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

import java.util.List;
import java.util.Map;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 * **/
@Path("/scriptInstance")
@Tag(name = "ScriptInstance", description = "@%ScriptInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ScriptInstanceRs extends IBaseRs {

    /**
     * Create a new script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new script instance.  ",
			description=" Create a new script instance.  ",
			operationId="    POST_ScriptInstance_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ScriptInstanceReponseDto.class
											)
								)
				)}
	)
    ScriptInstanceReponseDto create(ScriptInstanceDto postData);

    /**
     * Update an existing script instance.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing script instance.  ",
			description=" Update an existing script instance.  ",
			operationId="    PUT_ScriptInstance_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ScriptInstanceReponseDto.class
											)
								)
				)}
	)
    ScriptInstanceReponseDto update(ScriptInstanceDto postData);

    /**
     * Remove an existing script instance with a given code .
     * 
     * @param scriptInstanceCode The script instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{scriptInstanceCode}")
	@Operation(
			summary=" Remove an existing script instance with a given code .  ",
			description=" Remove an existing script instance with a given code .  ",
			operationId="    DELETE_ScriptInstance_{scriptInstanceCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Find a script instance with a given code.
     *
     * @param scriptInstanceCode The script instance's code
     * @return script instance
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a script instance with a given code. ",
			description=" Find a script instance with a given code. ",
			operationId="    GET_ScriptInstance_search",
			responses= {
				@ApiResponse(description=" script instance ",
						content=@Content(
									schema=@Schema(
											implementation= GetScriptInstanceResponseDto.class
											)
								)
				)}
	)
    GetScriptInstanceResponseDto find(@QueryParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Execute a script instance with a given code and list of parameters for the context of the script
     *
     * @param scriptInstanceCode The script instance's code
     * @return response of the script
     */
    @GET
    @Path("/execute")
	@Operation(
			summary=" Execute a script instance with a given code and list of parameters for the context of the script ",
			description=" Execute a script instance with a given code and list of parameters for the context of the script ",
			operationId="    GET_ScriptInstance_execute",
			responses= {
				@ApiResponse(description=" response of the script ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    Response execute(@QueryParam("scriptInstanceCode") String scriptInstanceCode);

    /**
     * Create new or update an existing script instance with a given code.
     * 
     * @param postData The script instance's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing script instance with a given code.  ",
			description=" Create new or update an existing script instance with a given code.  ",
			operationId="    POST_ScriptInstance_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ScriptInstanceReponseDto.class
											)
								)
				)}
	)
    ScriptInstanceReponseDto createOrUpdate(ScriptInstanceDto postData);

    /**
     * Enable a Script instance with a given code
     * 
     * @param code Script instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Script instance with a given code  ",
			description=" Enable a Script instance with a given code  ",
			operationId="    POST_ScriptInstance_{code}_enable",
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
     * Disable a Script instance with a given code
     * 
     * @param code Script instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Script instance with a given code  ",
			description=" Disable a Script instance with a given code  ",
			operationId="    POST_ScriptInstance_{code}_disable",
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
