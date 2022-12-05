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

package org.meveo.api.rest.cpq;

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
import jakarta.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.dto.response.cpq.GetGroupedAttributesResponse;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * @author Tarik FA.
 **/
@Path("/cpq/groupedAttributes")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface GroupedAttributesRs extends IBaseRs {

    /**
     * Create a new Grouped Attribute
     * 
     * @param groupedAttributeDto
     * @return Request processing status
     */
  
    @POST
	@Path("/")
    @Operation(summary = "This endpoint allows to create new groupedAttribute",
    tags = { "GroupedAttribute" },
    description ="Creating a new groupedAttribute",
    responses = {
            @ApiResponse(responseCode="200", description = "the GroupedAttribute successfully added",
            		content = @Content(schema = @Schema(implementation = GetGroupedAttributesResponse.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for GroupedAttributeDto required params are : code",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "400", description = "No GroupedAttribute is found for the parameter code", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    Response create(GroupedAttributeDto groupedAttributeDto);

    /**
     * Update an existing Grouped Attribute
     *
     * @param groupedAttributeDto
     * @return Request processing status
     */
    @PUT
    @Path("/")
    @Operation(summary = "updating a existing grouped attribute by its code",
    description ="check if the code is not null for updating a existing grouped attribute",
    tags = { "GroupedAttribute" },
    responses = {
            @ApiResponse(responseCode="200", description = "the grouped attribute successfully updated"),
            @ApiResponse(responseCode = "404", description = "the grouped attribute with groupedAttributeDto in param does not exist or null"),
            @ApiResponse(responseCode = "500", description = "the code of grouped attribute is missing or null", 
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "500", description = "No grouped attribute is found for the groupedAttributeCode param", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    ActionStatus update(GroupedAttributeDto groupedAttributeDto);

   
    /**
     * Remove an Grouped Attribute with a given code.
     * 
     * @param groupedAttributeCode
     * @return Request processing status
     */
    @DELETE
    @Path("/{groupedAttributeCode}")
    @Operation(summary = "remove a grouped attribute",
    tags = { "GroupedAttribute" },
    description ="Remove an Grouped Attribute with a given grouped attribute code",
    responses = {
            @ApiResponse(responseCode="200", description = "the grouped attribute successfully removed"),
            @ApiResponse(responseCode = "404", description = "the grouped attribute with groupedAttributeCode in param does not exist"),
            @ApiResponse(responseCode = "500", description = "No grouped attribute is found for the groupedAttributeCode param", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    ActionStatus remove(@Parameter(description = "The code of the grouped attribute", required = true) @PathParam("groupedAttributeCode") String groupedAttributeCode);
    
    @GET
    @Path("/")
    @Operation(summary = "This endpoint allows to retrieve a Grouped attribute information by its code",
    tags = { "GroupedAttribute" },
    description ="retrieve and return an existing grouped attribute",
    responses = {
            @ApiResponse(responseCode="200", description = "the grouped attribute successfully retrieved",
                    content = @Content(schema = @Schema(implementation = GetGroupedAttributesResponse.class))),
            @ApiResponse(responseCode = "404", description = "the grouped attribute with groupedAttributeCode in param does not exist"),
            @ApiResponse(responseCode = "500", description = "No grouped attribute is found for the groupedAttributeCode param", 
            		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    Response find(@Parameter(description = "The code of the grouped attribute", required = true) @QueryParam("groupedAttributeCode") String groupedAttributeCode);


}
