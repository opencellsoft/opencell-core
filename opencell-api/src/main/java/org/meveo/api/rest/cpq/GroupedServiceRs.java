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

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.GroupedServiceDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.cpq.GroupedService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * @author Tarik FA.
 **/
@Path("/cpq/groupedService")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface GroupedServiceRs extends IBaseRs {

    /**
     * Create a new Grouped Service
     * 
     * @param groupedServiceDto
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(GroupedServiceDto groupedServiceDto);

    /**
     * Update an existing Grouped Service
     *
     * @param groupedServiceDto
     * @return Request processing status
     */
    @PUT
    @Path("/")
    @Operation(summary = "updating a existing grouped service by its code",
    description ="check if the code is not null for updating a existing grouped service",
    responses = {
            @ApiResponse(responseCode="200", description = "the grouped service successfully updated"),
            @ApiResponse(responseCode = "404", description = "the grouped service with groupedServiceDto in param does not exist or null"),
            @ApiResponse(responseCode = "500", description = "the code of grouped service is missing or null", 
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "500", description = "No grouped service is found for the groupedServiceCode param", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    ActionStatus update(GroupedServiceDto groupedServiceDto);

   
    /**
     * Remove an Grouped Service with a given code.
     * 
     * @param groupedServiceCode
     * @return Request processing status
     */
    @DELETE
    @Path("/{groupedServiceCode}")
    @Operation(summary = "remove a grouped service",
    tags = { "GroupedService" },
    description ="Remove an Grouped Service with a given grouped service code",
    responses = {
            @ApiResponse(responseCode="200", description = "the grouped service successfully removed"),
            @ApiResponse(responseCode = "404", description = "the grouped service with groupedServiceCode in param does not exist"),
            @ApiResponse(responseCode = "500", description = "No grouped service is found for the groupedServiceCode param", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    ActionStatus remove(@PathParam("groupedServiceCode") String groupedServiceCode);
    
    @GET
    @Path("/")
    @Operation(summary = "This endpoint allows to retrieve a Grouped service information by its code",
    tags = { "GroupedService" },
    description ="retrieve and return an existing grouped service",
    responses = {
            @ApiResponse(responseCode="200", description = "the grouped service successfully retrieved",
                    content = @Content(schema = @Schema(implementation = GroupedService.class))),
            @ApiResponse(responseCode = "404", description = "the grouped service with groupedServiceCode in param does not exist"),
            @ApiResponse(responseCode = "500", description = "No grouped service is found for the groupedServiceCode param", 
            		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
    ActionStatus find(@QueryParam("groupedServiceCode") String groupedServiceCode);


}
