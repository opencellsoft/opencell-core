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
import org.meveo.api.dto.CRMAccountTypeSearchDto;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.response.ParentListResponse;
import org.meveo.api.dto.response.account.BusinessAccountModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/businessAccountModel")
@Tag(name = "BusinessAccountModel", description = "@%BusinessAccountModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessAccountModelRs extends IBaseRs {
    /**
     * Create a new business account model.
     * 
     * @param postData Business account model data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new business account model.  ",
			description=" Create a new business account model.  ",
			operationId="    POST_BusinessAccountModel_ ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(BusinessAccountModelDto postData);

    /**
     * Update an existing business account model.
     * 
     * @param postData Business account model data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing business account model.  ",
			description=" Update an existing business account model.  ",
			operationId="    PUT_BusinessAccountModel_ ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(BusinessAccountModelDto postData);

    /**
     * Search for a business account model.
     * 
     * @param bamCode Business account model code
     * @return business account model response.
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a business account model.  ",
			description=" Search for a business account model.  ",
			operationId="    GET_BusinessAccountModel_ ",
			responses= {
				@ApiResponse(description=" business account model response. ",
						content=@Content(
									schema=@Schema(
											implementation= BusinessAccountModelResponseDto.class
											)
								)
				)}
	)
    BusinessAccountModelResponseDto find(@QueryParam("businessAccountModelCode") String bamCode);

    /**
     * Remove business account model with a given business account model code.
     * 
     * @param bamCode Business account model code
     * @return Request processing status
     */
    @DELETE
    @Path("/{businessAccountModelCode}")
	@Operation(
			summary=" Remove business account model with a given business account model code.  ",
			description=" Remove business account model with a given business account model code.  ",
			operationId="    DELETE_BusinessAccountModel_{businessAccountModelCode} ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("businessAccountModelCode") String bamCode);

    /**
     * Return meveo's modules.
     * 
     * @return meveo module response
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Return meveo's modules.  ",
			description=" Return meveo's modules.  ",
			operationId="    GET_BusinessAccountModel_list",
			responses= {
				@ApiResponse(description=" meveo module response ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    MeveoModuleDtosResponse list();

    /**
     * List MeveoModuleDtos matching a given criteria
     *
     * @return List of MeveoModuleDtos
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List MeveoModuleDtos matching a given criteria ",
			description=" List MeveoModuleDtos matching a given criteria ",
			operationId="    GET_BusinessAccountModel_listGetAll",
			responses= {
				@ApiResponse(description=" List of MeveoModuleDtos ",
						content=@Content(
									schema=@Schema(
											implementation= MeveoModuleDtosResponse.class
											)
								)
				)}
	)
    MeveoModuleDtosResponse listGetAll();

    
    /**
     * Install business account module.
     * 
     * @param moduleDto The module
     * @return Request processing status
     */
    @PUT
    @Path("/install")
	@Operation(
			summary=" Install business account module.  ",
			description=" Install business account module.  ",
			operationId="    PUT_BusinessAccountModel_install ",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus install(BusinessAccountModelDto moduleDto);

    /**
     * Find parent entities based on account hierarchy code.
     *
     * @param searchDto CRM type search dto/
     * @return parent list reponse
     */
    @POST
    @Path("/findParents")
	@Operation(
			summary=" Find parent entities based on account hierarchy code. ",
			description=" Find parent entities based on account hierarchy code. ",
			operationId="    POST_BusinessAccountModel_findParents",
			responses= {
				@ApiResponse(description=" parent list reponse ",
						content=@Content(
									schema=@Schema(
											implementation= ParentListResponse.class
											)
								)
				)}
	)
    ParentListResponse findParents(CRMAccountTypeSearchDto searchDto);
}
