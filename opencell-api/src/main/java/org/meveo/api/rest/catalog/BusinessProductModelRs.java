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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.BusinessProductModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessProductModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessProductModel")
@Tag(name = "BusinessProductModel", description = "@%BusinessProductModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessProductModelRs extends IBaseRs {

    /**
     * Create a new business product model
     * 
     * @param postData The business product model's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new business product model  ",
			description=" Create a new business product model  ",
			operationId="    POST_BusinessProductModel_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(BusinessProductModelDto postData);

    /**
     * Update an existing business product model
     * 
     * @param postData The business product model's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing business product model  ",
			description=" Update an existing business product model  ",
			operationId="    PUT_BusinessProductModel_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(BusinessProductModelDto postData);

    /**
     * Remove an existing business product model with a given code 
     * 
     * @param businessProductModelCode The business product model's code
     * @return A business product model
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Remove an existing business product model with a given code   ",
			description=" Remove an existing business product model with a given code   ",
			operationId="    GET_BusinessProductModel_search",
			responses= {
				@ApiResponse(description=" A business product model ",
						content=@Content(
									schema=@Schema(
											implementation= GetBusinessProductModelResponseDto.class
											)
								)
				)}
	)
    GetBusinessProductModelResponseDto find(@QueryParam("businessProductModelCode") String businessProductModelCode);


    /**
     * Remove an existing business product model with a given code 
     * 
     * @param businessProductModelCode The business product model's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{businessProductModelCode}")
	@Operation(
			summary=" Remove an existing business product model with a given code   ",
			description=" Remove an existing business product model with a given code   ",
			operationId="    DELETE_BusinessProductModel_{businessProductModelCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("businessProductModelCode") String businessProductModelCode);

    /**
     * Create new or update an existing business product model
     * 
     * @param postData The business product model's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing business product model  ",
			description=" Create new or update an existing business product model  ",
			operationId="    POST_BusinessProductModel_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(BusinessProductModelDto postData);

    /**
     * List business product models
     * 
     * @return A list of business product models
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List business product models  ",
			description=" List business product models  ",
			operationId="    GET_BusinessProductModel_list",
			responses= {
				@ApiResponse(description=" A list of business product models ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    public MeveoModuleDtosResponse list();

    /**
     * List Business Product Models matching a given criteria
     *
     * @return List of Business Account Models
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List Business Product Models matching a given criteria ",
			description=" List Business Product Models matching a given criteria ",
			operationId="    GET_BusinessProductModel_listGetAll",
			responses= {
				@ApiResponse(description=" List of Business Account Models ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    MeveoModuleDtosResponse listGetAll();

    /**
     * Install business product model module
     * @param moduleDto business product model
     * @return Request processing status
     */
    @PUT
    @Path("/install")
	@Operation(
			summary=" Install business product model module ",
			description=" Install business product model module ",
			operationId="    PUT_BusinessProductModel_install",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus install(BusinessProductModelDto moduleDto);
}
