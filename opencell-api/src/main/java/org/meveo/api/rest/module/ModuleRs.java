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

package org.meveo.api.rest.module;

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
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

@Path("/module")
@Tag(name = "Module", description = "@%Module")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ModuleRs extends IBaseRs {

    /**
     * Create a new meveo module
     * 
     * @param moduleDto The meveo module's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new meveo module  ",
			description=" Create a new meveo module  ",
			operationId="    POST_Module_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus create(MeveoModuleDto moduleDto);

    /**
     * Update an existing Meveo module
     * 
     * @param moduleDto The Meveo module's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing Meveo module  ",
			description=" Update an existing Meveo module  ",
			operationId="    PUT_Module_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus update(MeveoModuleDto moduleDto);

    /**
     * Create new or update an existing Meveo module
     * 
     * @param moduleDto The Meveo module's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing Meveo module  ",
			description=" Create new or update an existing Meveo module  ",
			operationId="    POST_Module_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus createOrUpdate(MeveoModuleDto moduleDto);

    /**
     * Remove an existing module with a given code 
     * 
     * @param code The module's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" Remove an existing module with a given code   ",
			description=" Remove an existing module with a given code   ",
			operationId="    DELETE_Module_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus delete(@PathParam("code") String code);

    /**
     * List all Meveo's modules
     * 
     * @return A list of Meveo's modules
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List all Meveo's modules  ",
			description=" List all Meveo's modules  ",
			operationId="    GET_Module_list",
			responses= {
				@ApiResponse(description=" A list of Meveo's modules ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    public MeveoModuleDtosResponse list();

    /**
     * Install Meveo module
     * 
     * @param moduleDto the Meveo's module
     * @return Request processing status
     */
    @PUT
    @Path("/install")
	@Operation(
			summary=" Install Meveo module  ",
			description=" Install Meveo module  ",
			operationId="    PUT_Module_install",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus install(MeveoModuleDto moduleDto);

    /**
     * Find a Meveo's module with a given code 
     * 
     * @param code The Meveo module's code
     * @return Meveo module DTO Response.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a Meveo's module with a given code   ",
			description=" Find a Meveo's module with a given code   ",
			operationId="    GET_Module_search",
			responses= {
				@ApiResponse(description=" Meveo module DTO Response. ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtoResponse.class
											)
								)
				)}
	)
    public MeveoModuleDtoResponse get(@QueryParam("code") String code);

    /**
     * uninstall a Meveo's module with a given code
     * 
     * @param code The Meveo module's code
     * @return Request processing status
     */
    @GET
    @Path("/uninstall")
	@Operation(
			summary=" uninstall a Meveo's module with a given code  ",
			description=" uninstall a Meveo's module with a given code  ",
			operationId="    GET_Module_uninstall",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus uninstall(@QueryParam("code") String code);

    /**
     * Enable a Meveo's module with a given code
     * 
     * @param code The Meveo module's code
     * @return Request processing status
     */
    @GET
    @Path("/enable")
	@Operation(
			summary=" Enable a Meveo's module with a given code  ",
			description=" Enable a Meveo's module with a given code  ",
			operationId="    GET_Module_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus enableGet(@QueryParam("code") String code);

    /**
     * Disable a Meveo's module with a given code
     * 
     * @param code The Meveo module's code
     * @return Request processing status
     */
    @GET
    @Path("/disable")
	@Operation(
			summary=" Disable a Meveo's module with a given code  ",
			description=" Disable a Meveo's module with a given code  ",
			operationId="    GET_Module_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus disableGet(@QueryParam("code") String code);

    /**
     * Enable a Opencell module with a given code
     * 
     * @param code Opencell module code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Opencell module with a given code  ",
			description=" Enable a Opencell module with a given code  ",
			operationId="    POST_Module_{code}_enable",
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
     * Disable a Opencell module with a given code
     * 
     * @param code Opencell module code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Opencell module with a given code  ",
			description=" Disable a Opencell module with a given code  ",
			operationId="    POST_Module_{code}_disable",
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
