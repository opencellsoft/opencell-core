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

import java.util.Date;

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
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;

/**
 * Web service for managing {@link org.meveo.model.catalog.OneShotChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/oneShotChargeTemplate")
@Tag(name = "OneShotChargeTemplate", description = "@%OneShotChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OneShotChargeTemplateRs extends IBaseRs {

    /**
     * Create one shot charge template.
     * 
     * @param postData The one shot charge template's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create one shot charge template.  ",
			tags = { "ChargeTemplates" },
			description=" Create one shot charge template.  ",
			operationId="    POST_OneShotChargeTemplate_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(OneShotChargeTemplateDto postData);

    /**
     * Update one shot charge template.
     * 
     * @param postData The one shot charge template's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update one shot charge template.  ",
			tags = { "ChargeTemplates" },
			description=" Update one shot charge template.  ",
			operationId="    PUT_OneShotChargeTemplate_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(OneShotChargeTemplateDto postData);

    /**
     * Search one shot charge templatewith a given code.
     * 
     * @param oneShotChargeTemplateCode The one shot charge template's code
     * @return one shot charge template
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search one shot charge templatewith a given code.  ",
            tags = { "ChargeTemplates" },
			description=" Search one shot charge templatewith a given code.  ",
			operationId="    GET_OneShotChargeTemplate_search",
			responses= {
				@ApiResponse(description=" one shot charge template ",
						content=@Content(
									schema=@Schema(
											implementation= GetOneShotChargeTemplateResponseDto.class
											)
								)
				)}
	)
    GetOneShotChargeTemplateResponseDto find(@QueryParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    /**
     * List one shot charge template with the following filters.
     * 
     * @param languageCode language's code
     * @param countryCode country's code
     * @param currencyCode currency's code
     * @param sellerCode seller's code
     * @param date application date
     * @return list of one shot charge template
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List one shot charge template with the following filters.  ",
			tags = { "ChargeTemplates" },
			description=" List one shot charge template with the following filters.  ",
			operationId="    GET_OneShotChargeTemplate_list",
			responses= {
				@ApiResponse(description=" list of one shot charge template ",
						content=@Content(
									schema=@Schema(
											implementation= OneShotChargeTemplateWithPriceListDto.class
											)
								)
				)}
	)
    OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(@QueryParam("languageCode") String languageCode, @QueryParam("countryCode") String countryCode,
            @QueryParam("currencyCode") String currencyCode, @QueryParam("sellerCode") String sellerCode, @QueryParam("date") @RestDateParam Date date);

    /**
     * Remove one shot charge tesmplate with a given code.
     * 
     * @param oneShotChargeTemplateCode The one shot charge template's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{oneShotChargeTemplateCode}")
	@Operation(
			summary=" Remove one shot charge tesmplate with a given code.  ",
			tags = { "ChargeTemplates" },
			description=" Remove one shot charge tesmplate with a given code.  ",
			operationId="    DELETE_OneShotChargeTemplate_{oneShotChargeTemplateCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

    /**
     * Create new or update an existing.
     * 
     * @param postData The exemple's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing.  ",
		    tags = { "ChargeTemplates" },
			description=" Create new or update an existing.  ",
			operationId="    POST_OneShotChargeTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(OneShotChargeTemplateDto postData);

    /**
     * Enable a One shot charge template with a given code
     * 
     * @param code One shot charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a One shot charge template with a given code  ",
		    tags = { "ChargeTemplates" },
			description=" Enable a One shot charge template with a given code  ",
			operationId="    POST_OneShotChargeTemplate_{code}_enable",
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
     * Disable a One shot charge template with a given code
     * 
     * @param code One shot charge template code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a One shot charge template with a given code  ",
			tags = { "ChargeTemplates" },
			description=" Disable a One shot charge template with a given code  ",
			operationId="    POST_OneShotChargeTemplate_{code}_disable",
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
