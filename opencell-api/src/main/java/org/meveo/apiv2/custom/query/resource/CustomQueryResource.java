package org.meveo.apiv2.custom.query.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.custom.CustomQueryInput;
import org.meveo.apiv2.models.ApiException;
import org.meveo.model.custom.query.CustomQuery;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
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

    @GET
    @Operation(summary = "Return a list of custom queries", tags = {"CustomQuery" },
            description = "Returns a list of  custom queries",
            responses = {
            @ApiResponse(headers = {
                    @Header(name = "ETag",
                            description = "a pseudo-unique identifier that represents the version of the data sent back.",
                            schema = @Schema(type = "integer", format = "int64")) },
                    description = "list of custom queries",
                    content = @Content(schema = @Schema(implementation = CustomQuery.class))),
            @ApiResponse(responseCode = "200", description = "Custom queries list"),
            @ApiResponse(responseCode = "404", description = "No data found",
                    content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getCustomQueries(@DefaultValue("0") @QueryParam("offset") Long offset,
                              @DefaultValue("50") @QueryParam("limit") Long limit,
                              @QueryParam("sort") String sort, @QueryParam("orderBy") String orderBy,
                              @QueryParam("filter") String filter, @Context Request request);

    @POST
    @Operation(summary = "Create a new custom query", tags = {"CustomQuery" }, description = "Create a new custom query",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Custom query successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "204",
                            description = "Custom query successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "Target entity does not exist") })
    Response createCustomQuery(
            @Parameter(description = "Custom query object", required = true) CustomQueryInput customQuery);

}