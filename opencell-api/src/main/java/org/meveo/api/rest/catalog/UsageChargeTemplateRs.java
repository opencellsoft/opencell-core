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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.UsageChargeTemplateResponseDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.catalog.UsageChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/usageChargeTemplate")
@Tag(name = "UsageChargeTemplate", description = "@%UsageChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UsageChargeTemplateRs extends IBaseRs {

    /**
     * Create new usage charge template.
     * 
     * @param postData The usage charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create new usage charge template.  ",
			tags = { "ChargeTemplates" },
			description=" Create new usage charge template.  ",
			operationId="    POST_UsageChargeTemplate_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(UsageChargeTemplateDto postData);

    /**
     * Update usage charge template.
     * 
     * @param postData The usage charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update usage charge template.  ",
		    tags = { "ChargeTemplates" },
			description=" Update usage charge template.  ",
			operationId="    PUT_UsageChargeTemplate_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(UsageChargeTemplateDto postData);

    /**
     * Find an existing usage charge template with a given code.
     * 
     * @param usageChargeTemplateCode The charge template's code
     * @return Returns a usageChargeTemplate
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find an existing usage charge template with a given code.  ",
		    tags = { "ChargeTemplates" },
			description=" Find an existing usage charge template with a given code.  ",
			operationId="    GET_UsageChargeTemplate_search",
			responses= {
				@ApiResponse(description=" Returns a usageChargeTemplate ",
						content=@Content(
									schema=@Schema(
											implementation= GetUsageChargeTemplateResponseDto.class
											)
								)
				)}
	)
    GetUsageChargeTemplateResponseDto find(@Parameter(required = true) @QueryParam("usageChargeTemplateCode") String usageChargeTemplateCode);

    /**
     * Remove usage charge template with a given code.
     * 
     * @param usageChargeTemplateCode The charge template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{usageChargeTemplateCode}")
	@Operation(
			summary=" Remove usage charge template with a given code.  ",
			tags = { "ChargeTemplates" },
			description=" Remove usage charge template with a given code.  ",
			operationId="    DELETE_UsageChargeTemplate_{usageChargeTemplateCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@Parameter(required = true) @PathParam("usageChargeTemplateCode") String usageChargeTemplateCode);

    /**
     * Create new or update an existing charge template with a given code.
     * 
     * @param postData The usage charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing charge template with a given code.  ",
		    tags = { "ChargeTemplates" },
			description=" Create new or update an existing charge template with a given code.  ",
			operationId="    POST_UsageChargeTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(UsageChargeTemplateDto postData);

    /**
     * Enable a Usage charge template with a given code
     * 
     * @param code Usage charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Usage charge template with a given code  ",
		    tags = { "ChargeTemplates" },
			description=" Enable a Usage charge template with a given code  ",
			operationId="    POST_UsageChargeTemplate_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@Parameter(required = true)  @PathParam("code") String code);

    /**
     * Disable a Usage charge template with a given code
     * 
     * @param code Usage charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Usage charge template with a given code  ",
			tags = { "ChargeTemplates" },
			description=" Disable a Usage charge template with a given code  ",
			operationId="    POST_UsageChargeTemplate_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@Parameter(required = true)  @PathParam("code") String code);
    
    /**
     * List UsageChargeTemplate matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of UsageChargeTemplate
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List UsageChargeTemplate matching a given criteria ",
			tags = { "ChargeTemplates" },
			description=" List UsageChargeTemplate matching a given criteria ",
			operationId="    POST_UsageChargeTemplate_list",
			responses= {
				@ApiResponse(description=" List of UsageChargeTemplate ",
						content=@Content(
									schema=@Schema(
											implementation= UsageChargeTemplateResponseDto.class
											)
								)
				)}
	)
    UsageChargeTemplateResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * List UsageChargeTemplate
     *
     * @return List of UsageChargeTemplate
     */
    @GET
    @Path("/listGetAll")
    UsageChargeTemplateResponseDto listGetAll();
}
