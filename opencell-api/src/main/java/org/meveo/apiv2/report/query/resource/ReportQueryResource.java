package org.meveo.apiv2.report.query.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.report.ReportQueryInput;
import org.meveo.apiv2.models.ApiException;
import org.meveo.model.report.query.ReportQuery;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("/queryManagement/reportQueries")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface ReportQueryResource {

    @GET
    @Path("/{queryId}")
    @Operation(summary = "This endpoint allows to load a report query resource", tags = {"ReportQuery"},
            description = "Return a report query", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Query successfully loaded"),
            @ApiResponse(responseCode = "404",
                    description = "Query does not exist")})
    Response find(
            @Parameter(description = "Report query id", required = true) @PathParam("queryId") Long id);

    @DELETE
    @Path("/{queryId}")
    @Operation(summary = "This endpoint allows to delete a report query resource", tags = {"ReportQuery"},
            description = "delete report query", responses = {
            @ApiResponse(responseCode = "204",
                    description = "Query successfully deleted"),
            @ApiResponse(responseCode = "404",
                    description = "Query does not exist")})
    Response delete(
            @Parameter(description = "report query id", required = true) @PathParam("queryId") Long id);

    @GET
    @Operation(summary = "Return a list of report queries", tags = {"ReportQuery" },
            description = "Returns a list of report queries",
            responses = {
            @ApiResponse(headers = {
                    @Header(name = "ETag",
                            description = "a pseudo-unique identifier that represents the version of the data sent back.",
                            schema = @Schema(type = "integer", format = "int64")) },
                    description = "list of report queries",
                    content = @Content(schema = @Schema(implementation = ReportQuery.class))),
            @ApiResponse(responseCode = "200", description = "report queries list"),
            @ApiResponse(responseCode = "404", description = "No data found",
                    content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getReportQueries(@DefaultValue("0") @QueryParam("offset") Long offset,
                              @DefaultValue("50") @QueryParam("limit") Long limit,
                              @QueryParam("sort") String sort, @QueryParam("orderBy") String orderBy,
                              @QueryParam("filter") String filter, @Context Request request);

    @POST
    @Operation(summary = "Create a new report query", tags = {"ReportQuery" }, description = "Create a new report query",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Report query successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "204",
                            description = "Report query successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "Target entity does not exist") })
    Response createReportQuery(
            @Parameter(description = "Report query object", required = true) ReportQueryInput reportQuery);
    
    @GET
    @Path(("/queryExecutionResult/{queryexecutionResultId}/results"))
    @Operation( summary = "This API will convert the generate report file to json.", 
    			tags = {"ReportQuery"}, 
    			description = "look for the query result by its id get its path location, and transform csv file to json")
    Response findQueryResult(@PathParam("queryexecutionResultId") Long queryexecutionResultId);

}