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
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.response.CounterTemplatesResponseDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.catalog.CounterTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/counterTemplate")
@Tag(name = "CounterTemplate", description = "@%CounterTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CounterTemplateRs extends IBaseRs {

    /**
     * Create counter template.
     * 
     * @param postData counter template
     * @return action status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create counter template.  ",
			description=" Create counter template.  ",
			operationId="    POST_CounterTemplate_create",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(CounterTemplateDto postData);

    /**
     * Update counter template.
     * 
     * @param postData counter template
     * @return action status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update counter template.  ",
			description=" Update counter template.  ",
			operationId="    PUT_CounterTemplate_update",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(CounterTemplateDto postData);

    /**
     * Search counter template with a given code.
     * 
     * @param counterTemplateCode counter temlate's code
     * @return counter template
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search counter template with a given code.  ",
			description=" Search counter template with a given code.  ",
			operationId="    GET_CounterTemplate_search",
			responses= {
				@ApiResponse(description=" counter template ",
						content=@Content(
									schema=@Schema(
											implementation= GetCounterTemplateResponseDto.class
											)
								)
				)}
	)
    GetCounterTemplateResponseDto find(@QueryParam("counterTemplateCode") String counterTemplateCode);

    /**
     * Remove counter template with a given code.
     * 
     * @param counterTemplateCode counter template's code
     * @return action status
     */
    @DELETE
    @Path("/{counterTemplateCode}")
	@Operation(
			summary=" Remove counter template with a given code.  ",
			description=" Remove counter template with a given code.  ",
			operationId="    DELETE_CounterTemplate_{counterTemplateCode}",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("counterTemplateCode") String counterTemplateCode);

    /**
     * Create or update a counter Template.
     *
     * @param postData counter template
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update a counter Template. ",
			description=" Create or update a counter Template. ",
			operationId="    POST_CounterTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(CounterTemplateDto postData);

    /**
     * Enable a Counter template with a given code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Counter template with a given code  ",
			description=" Enable a Counter template with a given code  ",
			operationId="    POST_CounterTemplate_{code}_enable",
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
     * Disable a Counter template with a given code
     * 
     * @param code Counter template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Counter template with a given code  ",
			description=" Disable a Counter template with a given code  ",
			operationId="    POST_CounterTemplate_{code}_disable",
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

    /**
     * List CounterTemplates matching a given criteria
     *
     * @return List of CounterTemplates
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List CounterTemplates matching a given criteria ",
			description=" List CounterTemplates matching a given criteria ",
			operationId="    GET_CounterTemplate_listGetAll",
			responses= {
				@ApiResponse(description=" List of CounterTemplates ",
						content=@Content(
									schema=@Schema(
											implementation= CounterTemplatesResponseDto.class
											)
								)
				)}
	)
    CounterTemplatesResponseDto listGetAll();
}
