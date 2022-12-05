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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/customFieldTemplate")
@Tag(name = "CustomFieldTemplate", description = "@%CustomFieldTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CustomFieldTemplateRs extends IBaseRs {

    /**
     * Define a new custom field
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Define a new custom field  ",
			description=" Define a new custom field  ",
			operationId="    POST_CustomFieldTemplate_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CustomFieldTemplateDto postData);

    /**
     * Update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update existing custom field definition  ",
			description=" Update existing custom field definition  ",
			operationId="    PUT_CustomFieldTemplate_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CustomFieldTemplateDto postData);

    /**
     * Remove custom field definition given its code and entity it applies to
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @DELETE
    @Path("/{customFieldTemplateCode}/{appliesTo}")
	@Operation(
			summary=" Remove custom field definition given its code and entity it applies to  ",
			description=" Remove custom field definition given its code and entity it applies to  ",
			operationId="    DELETE_CustomFieldTemplate_{customFieldTemplateCode}_{appliesTo}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Get custom field definition
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return instance of GetCustomFieldTemplateReponseDto
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Get custom field definition  ",
			description=" Get custom field definition  ",
			operationId="    GET_CustomFieldTemplate_search",
			responses= {
				@ApiResponse(description=" instance of GetCustomFieldTemplateReponseDto ",
						content=@Content(
									schema=@Schema(
											implementation= GetCustomFieldTemplateReponseDto.class
											)
								)
				)}
	)
    GetCustomFieldTemplateReponseDto find(@QueryParam("customFieldTemplateCode") String customFieldTemplateCode, @QueryParam("appliesTo") String appliesTo);

    /**
     * Define new or update existing custom field definition
     * 
     * @param postData posted data to API
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Define new or update existing custom field definition  ",
			description=" Define new or update existing custom field definition  ",
			operationId="    POST_CustomFieldTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CustomFieldTemplateDto postData);

    /**
     * Enable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/{customFieldTemplateCode}/{appliesTo}/enable")
	@Operation(
			summary=" Enable a Custom field template with a given code  ",
			description=" Enable a Custom field template with a given code  ",
			operationId="    POST_CustomFieldTemplate_{customFieldTemplateCode}_{appliesTo}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

    /**
     * Disable a Custom field template with a given code
     * 
     * @param customFieldTemplateCode Custom field template code
     * @param appliesTo Entity it applies to
     * @return Request processing status
     */
    @POST
    @Path("/{customFieldTemplateCode}/{appliesTo}/disable")
	@Operation(
			summary=" Disable a Custom field template with a given code  ",
			description=" Disable a Custom field template with a given code  ",
			operationId="    POST_CustomFieldTemplate_{customFieldTemplateCode}_{appliesTo}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus disable(@PathParam("customFieldTemplateCode") String customFieldTemplateCode, @PathParam("appliesTo") String appliesTo);

}
