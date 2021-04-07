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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.RecurringChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/recurringChargeTemplate")
@Tag(name = "RecurringChargeTemplate", description = "@%RecurringChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RecurringChargeTemplateRs extends IBaseRs {

    /**
     * Create a new recurring charge template.
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new recurring charge template.  ",
			tags = { "ChargeTemplates" },
			description=" Create a new recurring charge template.  ",
			operationId="    POST_RecurringChargeTemplate_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus create(RecurringChargeTemplateDto postData);

    /**
     * Find a recurring charge template with a given code.
     * 
     * @param recurringChargeTemplateCode The recurring charge template's code
     * @return Return a recurringChargeTemplate
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a recurring charge template with a given code.  ",
			tags = { "ChargeTemplates" },
			description=" Find a recurring charge template with a given code.  ",
			operationId="    GET_RecurringChargeTemplate_search",
			responses= {
				@ApiResponse(description=" Return a recurringChargeTemplate ",
						content=@Content(
									schema=@Schema(
											implementation= GetRecurringChargeTemplateResponseDto.class
											)
								)
				)}
	)
    public GetRecurringChargeTemplateResponseDto find(@Parameter(required = true, description = "code of recurring charge template") @QueryParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

    /**
     * Update an existing recurring charge template.
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing recurring charge template.  ",
			tags = { "ChargeTemplates" },
			description=" Update an existing recurring charge template.  ",
			operationId="    PUT_RecurringChargeTemplate_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus update(RecurringChargeTemplateDto postData);

    /**
     * Remove an existing recurring charge template with a given code.
     * 
     * @param recurringChargeTemplateCode The recurring charge template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{recurringChargeTemplateCode}")
	@Operation(
			summary=" Remove an existing recurring charge template with a given code.  ",
		    tags = { "ChargeTemplates" },
			description=" Remove an existing recurring charge template with a given code.  ",
			operationId="    DELETE_RecurringChargeTemplate_{recurringChargeTemplateCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus remove(@Parameter(required = true) @PathParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

    /**
     * Create new or update an existing recurring charge template
     * 
     * @param postData The recurring charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing recurring charge template  ",
			tags = { "ChargeTemplates" },
			description=" Create new or update an existing recurring charge template  ",
			operationId="    POST_RecurringChargeTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    public ActionStatus createOrUpdate(RecurringChargeTemplateDto postData);

    /**
     * Enable a Recurring charge template with a given code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Recurring charge template with a given code  ",
			tags = { "ChargeTemplates" },
			description=" Enable a Recurring charge template with a given code  ",
			operationId="    POST_RecurringChargeTemplate_{code}_enable",
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
     * Disable a Recurring charge template with a given code
     * 
     * @param code Recurring charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Recurring charge template with a given code  ",
			tags = { "ChargeTemplates" },
			description=" Disable a Recurring charge template with a given code  ",
			operationId="    POST_RecurringChargeTemplate_{code}_disable",
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
