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

package org.meveo.api.rest.payment;

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
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

@Path("/revenueRecognitionRule")
@Tag(name = "RevenueRecognitionRules", description = "@%RevenueRecognitionRules")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RevenueRecognitionRulesRs extends IBaseRs {

    /**
     * Create a new revenue recognition rule
     * 
     * @param postData The revenue recognition rule's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new revenue recognition rule  ",
			description=" Create a new revenue recognition rule  ",
			operationId="    POST_RevenueRecognitionRules_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(RevenueRecognitionRuleDto postData);

    /**
     * Update an existing revenue recognition rule
     * 
     * @param postData The revenue recognition rule's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing revenue recognition rule  ",
			description=" Update an existing revenue recognition rule  ",
			operationId="    PUT_RevenueRecognitionRules_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(RevenueRecognitionRuleDto postData);

    /**
     * Find a revenue recognition rule with a given code
     * 
     * @param revenueRecognitionRuleCode The revenue recognition rule's code
     * @return Revenue recognition rules results
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a revenue recognition rule with a given code  ",
			description=" Find a revenue recognition rule with a given code  ",
			operationId="    GET_RevenueRecognitionRules_search",
			responses= {
				@ApiResponse(description=" Revenue recognition rules results ",
						content=@Content(
									schema=@Schema(
											implementation= RevenueRecognitionRuleDtoResponse.class
											)
								)
				)}
	)
    RevenueRecognitionRuleDtoResponse find(@QueryParam("revenueRecognitionRuleCode") String revenueRecognitionRuleCode);

    /**
     * Create new or update an existing revenue recognition rule with a given code
     * 
     * @param postData The revenue recognition rule's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing revenue recognition rule with a given code  ",
			description=" Create new or update an existing revenue recognition rule with a given code  ",
			operationId="    POST_RevenueRecognitionRules_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(RevenueRecognitionRuleDto postData);

    /**
     * Remove an existing revenue recognition rule with a given code
     * 
     * @param revenueRecognitionRuleCode The revenue recognition rule's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{revenueRecognitionRuleCode}")
	@Operation(
			summary=" Remove an existing revenue recognition rule with a given code  ",
			description=" Remove an existing revenue recognition rule with a given code  ",
			operationId="    DELETE_RevenueRecognitionRules_{revenueRecognitionRuleCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("revenueRecognitionRuleCode") String revenueRecognitionRuleCode);

    /**
     * List of revenue recognition rules.
     * 
     * @return A list of revenue recognition rules
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List of revenue recognition rules.  ",
			description=" List of revenue recognition rules.  ",
			operationId="    POST_RevenueRecognitionRules_list",
			responses= {
				@ApiResponse(description=" A list of revenue recognition rules ",
						content=@Content(
									schema=@Schema(
											implementation= RevenueRecognitionRuleDtosResponse.class
											)
								)
				)}
	)
    RevenueRecognitionRuleDtosResponse list();

    /**
     * Enable a Revenue recognition rule with a given code
     * 
     * @param code Revenue recognition rule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Revenue recognition rule with a given code  ",
			description=" Enable a Revenue recognition rule with a given code  ",
			operationId="    POST_RevenueRecognitionRules_{code}_enable",
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
     * Disable a Revenue recognition rule with a given code
     * 
     * @param code Revenue recognition rule code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Revenue recognition rule with a given code  ",
			description=" Disable a Revenue recognition rule with a given code  ",
			operationId="    POST_RevenueRecognitionRules_{code}_disable",
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
