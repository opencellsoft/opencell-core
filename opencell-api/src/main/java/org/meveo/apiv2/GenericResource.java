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

package org.meveo.apiv2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.models.ApiException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/generic")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface GenericResource {
    @POST
    @Path("/all/{entityName}")
    @Operation(summary = "Generic single endpoint to retrieve paginated records of an entity",
            tags = { "Generic" },
            description ="specify the entity name, and as body, the configuration of the research."
                    + " also you can define the offset and the limit, you can order by a field and define the sort type"
                    + " see PagingAndFiltering doc for more details. ",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized")
    })
    Response getAll(@Parameter(description ="the entity name", required = true) @PathParam("entityName") String entityName,
            @Parameter(description = "requestDto carries the wanted fields ex: {genericFields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @POST
    @Path("/{entityName}/{id}")
    @Operation(summary = "Generic single endpoint to retrieve resources by ID",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the wanted fields",
            responses = {
                    @ApiResponse(responseCode="200", description = "paginated results successfully retrieved with hypermedia links"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized")
    })
    Response get(@Parameter(description ="the entity name", required = true) @PathParam("entityName") String entityName,
            @Parameter(description ="The id here is the database primary key of the wanted record", required = true) @PathParam("id") Long id,
            @Parameter(description ="requestDto carries the wanted fields ex: {fields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @Operation(summary = "Update a resource by giving it's name and Id",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the fields to update",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
    })
    @PUT
    @Path("/{entityName}/{id}")
    Response update(@Parameter(description ="the entity name", required = true) @PathParam("entityName") String entityName,
            @Parameter(description ="The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id,
            @Parameter(description ="dto the json representation of the object", required = true) String dto);

    @Operation(summary = "Create a resource by giving it's name and Id",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the fields to create",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
    })
    @POST
    @Path("/{entityName}")
    Response create(@Parameter(description ="the entity name", required = true) @PathParam("entityName") String entityName,
            @Parameter(description ="dto the json representation of the object", required = true) String dto);

    @Operation(summary = "Delete a resource by giving it's name and Id",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the fields to delete",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
    })
    @DELETE
    @Path("/{entityName}/{id}")
    Response delete(@Parameter(description ="the entity name", required = true) @PathParam("entityName") String entityName,
            @Parameter(description ="The id here is the database primary key of the record to delete", required = true) @PathParam("id") Long id);
}
