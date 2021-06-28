package org.meveo.apiv2.custom.query.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/queryManagement/customQueries")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface CustomQueryResource {

    @GET
    @Path("/{queryId}")
    @Operation(summary = "This endpoint allows to load a custom query resource", tags = {"CustomQuery"},
            description = "Return a custom query", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Query successfully loaded"),
            @ApiResponse(responseCode = "404",
                    description = "Query does not exist")})
    Response find(
            @Parameter(description = "Custom query id", required = true) @PathParam("queryId") Long id);

    @DELETE
    @Path("/{queryId}")
    @Operation(summary = "This endpoint allows to delete a custom query resource", tags = {"CustomQuery"},
            description = "delete custom query", responses = {
            @ApiResponse(responseCode = "204",
                    description = "Query successfully deleted"),
            @ApiResponse(responseCode = "404",
                    description = "Query does not exist")})
    Response delete(
            @Parameter(description = "Custom query id", required = true) @PathParam("queryId") Long id);
}