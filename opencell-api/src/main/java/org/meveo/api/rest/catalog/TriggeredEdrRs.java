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
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.response.TriggeredEdrsResponseDto;
import org.meveo.api.dto.response.catalog.GetTriggeredEdrResponseDto;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/triggeredEdr")
@Tag(name = "TriggeredEdr", description = "@%TriggeredEdr")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TriggeredEdrRs extends IBaseRs {

    /**
     * Create a new triggered edr. template
     * 
     * @param postData The triggered edr. template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new triggered edr. template  ",
			description=" Create a new triggered edr. template  ",
			operationId="    POST_TriggeredEdr_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(TriggeredEdrTemplateDto postData);

    /**
     * Update an existing triggered edr. template
     * 
     * @param postData The triggered edr. template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing triggered edr. template  ",
			description=" Update an existing triggered edr. template  ",
			operationId="    PUT_TriggeredEdr_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(TriggeredEdrTemplateDto postData);

    /**
     * Find triggered edr with a given code.
     * 
     * @param triggeredEdrCode The triggered edr's code
     * @return Returns triggeredEdrTemplate
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find triggered edr with a given code.  ",
			description=" Find triggered edr with a given code.  ",
			operationId="    GET_TriggeredEdr_search",
			responses= {
				@ApiResponse(description=" Returns triggeredEdrTemplate ",
						content=@Content(
									schema=@Schema(
											implementation= GetTriggeredEdrResponseDto.class
											)
								)
				)}
	)
    GetTriggeredEdrResponseDto find(@QueryParam("triggeredEdrCode") String triggeredEdrCode);

    /**
     * Remove an existing triggered edr template with a given code.
     * 
     * @param triggeredEdrCode The triggered edr's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{triggeredEdrCode}")
	@Operation(
			summary=" Remove an existing triggered edr template with a given code.  ",
			description=" Remove an existing triggered edr template with a given code.  ",
			operationId="    DELETE_TriggeredEdr_{triggeredEdrCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("triggeredEdrCode") String triggeredEdrCode);

    /**
     * Create new or update an existing triggered edr template
     * 
     * @param postData The triggered edr template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing triggered edr template  ",
			description=" Create new or update an existing triggered edr template  ",
			operationId="    POST_TriggeredEdr_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(TriggeredEdrTemplateDto postData);

    /**
     * Gets a triggeredEdrs list.
     *
     * @return Return triggeredEdrs list
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" Gets a triggeredEdrs list. ",
			description=" Gets a triggeredEdrs list. ",
			operationId="    GET_TriggeredEdr_listGetAll",
			responses= {
				@ApiResponse(description=" Return triggeredEdrs list ",
						content=@Content(
									schema=@Schema(
											implementation= TriggeredEdrsResponseDto.class
											)
								)
				)}
	)
    TriggeredEdrsResponseDto listGetAll();
}
