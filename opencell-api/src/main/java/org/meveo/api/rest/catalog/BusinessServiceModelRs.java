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
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessServiceModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessServiceModel")
@Tag(name = "BusinessServiceModel", description = "@%BusinessServiceModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessServiceModelRs extends IBaseRs {

    /**
     * Create a new business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new business service model  ",
			description=" Create a new business service model  ",
			operationId="    POST_BusinessServiceModel_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(BusinessServiceModelDto postData);

    /**
     * Update an existing business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing business service model  ",
			description=" Update an existing business service model  ",
			operationId="    PUT_BusinessServiceModel_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(BusinessServiceModelDto postData);

    /**
     * Search for a business service model with a given code 
     * 
     * @param businessServiceModelCode The business service model's code
     * @return A business servie model
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a business service model with a given code   ",
			description=" Search for a business service model with a given code   ",
			operationId="    GET_BusinessServiceModel_search",
			responses= {
				@ApiResponse(description=" A business servie model ",
						content=@Content(
									schema=@Schema(
											implementation= GetBusinessServiceModelResponseDto.class
											)
								)
				)}
	)
    GetBusinessServiceModelResponseDto find(@QueryParam("businessServiceModelCode") String businessServiceModelCode);

    /**
     * Remove an existing business service model with a given code 
     * 
     * @param businessServiceModelCode The business service model's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{businessServiceModelCode}")
	@Operation(
			summary=" Remove an existing business service model with a given code   ",
			description=" Remove an existing business service model with a given code   ",
			operationId="    DELETE_BusinessServiceModel_{businessServiceModelCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("businessServiceModelCode") String businessServiceModelCode);

    /**
     * Create new or update an existing business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing business service model  ",
			description=" Create new or update an existing business service model  ",
			operationId="    POST_BusinessServiceModel_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(BusinessServiceModelDto postData);

    /**
     * List business service model
     * 
     * @return A list of business service models
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List business service model  ",
			description=" List business service model  ",
			operationId="    GET_BusinessServiceModel_list",
			responses= {
				@ApiResponse(description=" A list of business service models ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    public MeveoModuleDtosResponse list();

    /**
     * List Business Service Models matching a given criteria
     *
     * @return List of Business Service Models
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List Business Service Models matching a given criteria ",
			description=" List Business Service Models matching a given criteria ",
			operationId="    GET_BusinessServiceModel_listGetAll",
			responses= {
				@ApiResponse(description=" List of Business Service Models ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    MeveoModuleDtosResponse listGetAll();

    /**
     * Install business offer model module
     * 
     * @param moduleDto The business service model's data
     * @return Request processing status
     */
    @PUT
    @Path("/install")
	@Operation(
			summary=" Install business offer model module  ",
			description=" Install business offer model module  ",
			operationId="    PUT_BusinessServiceModel_install",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus install(BusinessServiceModelDto moduleDto);
}
