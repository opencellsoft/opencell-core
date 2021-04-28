package org.meveo.apiv2.generic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.models.ApiException;

import javax.ws.rs.*;
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
    Response getAll(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
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
    Response get(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                 @Parameter(description = "The id here is the database primary key of the wanted record", required = true) @PathParam("id") Long id,
                 @Parameter(description = "requestDto carries the wanted fields ex: {fields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

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
    Response update(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                    @Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id,
                    @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @Operation(summary = "Create a resource by giving it's name and Id",
            tags = { "Generic" },
            description ="specify the entity name, the record id, and as body, the list of the fields to create",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
    })
    @POST
    @Path("/{entityName}")
    Response create(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                    @Parameter(description = "dto the json representation of the object", required = true) String dto);

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
    Response delete(@Parameter(description = "the entity name", required = true) @PathParam("entityName") String entityName,
                    @Parameter(description = "The id here is the database primary key of the record to delete", required = true) @PathParam("id") Long id);

    @Operation(summary = "Get versions information about OpenCell components",
            tags = { "Generic" },
            description ="return a list of OpenCell's components version information",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia")
            })
    @GET
    @Path("/version")
    Response getVersions();
}
