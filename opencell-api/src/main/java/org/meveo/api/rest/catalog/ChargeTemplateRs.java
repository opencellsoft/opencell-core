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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.catalog.GetChargeTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog")
@Tag(name = "ChargeTemplate", description = "@%ChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface ChargeTemplateRs extends IBaseRs {

	/**
     * Search for a charge template with a given code 
     * 
     * @param chargeTemplateCode The charge template's code
     * @return A charge template
     */
    @GET
    @Path("/chargeTemplate")
	@Operation(
			summary=" Search for a charge template with a given code   ",
			tags = { "ChargeTemplates" },
			description=" Search for a charge template with a given code   ",
			operationId="    GET_ChargeTemplate_chargeTemplate",
			deprecated = true,
			responses= {
				@ApiResponse(description=" A charge template ",
						content=@Content(
									schema=@Schema(
											implementation= GetChargeTemplateResponseDto.class
											)
								)
				)}
	)
	@Deprecated
    GetChargeTemplateResponseDto find(@QueryParam("chargeTemplateCode") String chargeTemplateCode);

	/**
	 * Search for a charge template with a given code
	 *
	 * @param chargeTemplateCode The charge template's code
	 * @return A charge template
	 */
	@GET
	@Path("/chargeTemplates/{chargeTemplateCode}")
	@Operation(
			summary="	  Search for a charge template with a given code	 	  ",
			tags = { "ChargeTemplates" },
			description="	  Search for a charge template with a given code	 	  ",
			operationId="GET_ChargeTemplate_chargeTemplates_{chargeTemplateCode}",
			responses= {
				@ApiResponse(description=" A charge template	  ",
							content=@Content(
									schema=@Schema(
											implementation= GetChargeTemplateResponseDto.class
									)
							)
					)}
	)
	GetChargeTemplateResponseDto findV2(@PathParam("chargeTemplateCode") String chargeTemplateCode);
	
	/**
	 * Update charge template status
	 *
	 * @param chargeTemplateCode The charge template's code
	 * @param status             The new charge template's status
	 * @return action status
	 */
	@PUT
	@Path("/chargeTemplates/{chargeTemplateCode}/status/{status}")
	@Operation(summary = "update charge template status for a given code", 
		tags = {"ChargeTemplates" }, description = "update charge template status for a given code", operationId = "PUT_ChargeTemplate_chargeTemplates_{chargeTemplateCode}", 
		responses = {@ApiResponse(description = "charge template", content = @Content(schema = @Schema(implementation = GetChargeTemplateResponseDto.class))) })
	ActionStatus updateStatus(@PathParam("chargeTemplateCode") String chargeTemplateCode, @PathParam("status") String status);

	/**
	 * Duplicate charge template
	 *
	 * @param chargeTemplateCode The charge template's code
	 * @return action status
	 */
	@POST
	@Path("/chargeTemplates/{chargeTemplateCode}/duplicate")
	@Operation(summary = "add duplicate charge template for a given code", 
		tags = {"ChargeTemplates" }, description = "add duplicate charge template for a given code", operationId = "POST_ChargeTemplate_chargeTemplates_{chargeTemplateCode}", 
		responses = {@ApiResponse(description = "charge template", content = @Content(schema = @Schema(implementation = GetChargeTemplateResponseDto.class))) })
	GetChargeTemplateResponseDto duplicateCharge(@PathParam("chargeTemplateCode") String chargeTemplateCode);

}
