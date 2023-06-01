package org.meveo.apiv2.report.query.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.report.QuerySchedulerInput;
import org.meveo.apiv2.report.ReportQueryInput;
import org.meveo.apiv2.report.VerifyQueryInput;
import org.meveo.model.report.query.QueryExecutionResultFormatEnum;
import org.meveo.model.report.query.ReportQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
                              @QueryParam("sortOrder") String sort, @QueryParam("sortBy") String orderBy,
                              @QueryParam("filter") String filter, @QueryParam("query") String query,
                              @QueryParam("fields") String fields, @Context Request request);

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
    @Path("/queryExecutionResult/{queryexecutionResultId}/results")
    @Operation( summary = "This API will convert the generate report file to json.", 
    			tags = {"ReportQuery"}, 
    			description = "look for the query result by its id get its path location, and transform csv file to json",
    		            responses = {
    		                    @ApiResponse(responseCode = "200",
    		                            description = "query execution result successfully generated"),
    		                    @ApiResponse(responseCode = "404",
    		                            description = "the Report query execution does not exist / the file path is missing / file path doesn't exist / file extension is not CSV format ") })
    Response findQueryResult(@PathParam("queryexecutionResultId") Long queryExecutionResultId);
    
    @GET
    @Path("/{queryId}/download")
    @Operation( summary = "This API will download result query as csv or excel format.", 
    			tags = {"ReportQuery"}, 
    			description = "download result query execution as scv or excel format",
    		            responses = {
    		                    @ApiResponse(responseCode = "200",
    		                            description = "query execution result is downloaded"),
    		                    @ApiResponse(responseCode = "404",
    		                            description = "the Report query execution does not exist") })
    Response downloadQueryExecutionResult(@PathParam("queryId" ) Long queryExecutionResultId, 
    									 @Parameter(description = "format of the file to be downloaded, by default it CSV format", required = false) @QueryParam("format") @DefaultValue("CSV") QueryExecutionResultFormatEnum format) throws IOException;

    @POST
    @Path("/{reportQueryId}/schedule")
    @Operation(summary = "Create a new query scheduler", tags = {"QueryScheduler" }, description = "Create a new query scheduler",
            responses = {
                    @ApiResponse(responseCode = "204",
                            description = "Query scheduler successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "Target entity does not exist") })
    Response createQueryScheduler(
    		@Parameter(description = "report query id", required = true) @PathParam("reportQueryId") Long id,
            @Parameter(description = "Query scheduler object", required = true) QuerySchedulerInput queryScheduler);

    @POST
    @Path("/{queryId}/execute")
    @Operation(summary = "execute report query", tags = {"ReportQuery"}, description = "Execute report query",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Query successfully executed"),
                    @ApiResponse(responseCode = "404",
                            description = "Query does not exists")})
    Response execute(@Parameter(description = "Query id", required = true) @PathParam("queryId") Long id,
                     @Parameter(description = "Execution type Synchronously or asynchronously")
                     @QueryParam("async") boolean async, @QueryParam("sendNotification") @DefaultValue("true") boolean sendNotification, 
                     @Parameter(description = "Report query object", required = true) ReportQueryInput reportQuery, @Context UriInfo uriInfo);
    
    @POST
    @Path("/verify")
    @Operation(summary = "Verify report query",
            tags = {"ReportQuery" },
            description = "Verify the existing of the report query according to its visibility and creator",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "The query not exists with the visibility"),
                    @ApiResponse(responseCode = "409",
                            description = "The query already exists and belong another user"),
                    @ApiResponse(responseCode = "409",
                            description = "The query already exists and belong you"),
                    @ApiResponse(responseCode = "422",
                            description = "The query already exists and belong to another user") })
    Response verifyReportQuery(
            @Parameter(description = "Verify report query request", required = true) VerifyQueryInput verifyQueryInput);

    @PUT
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to update an report query", tags = {
            "ReportQuery" }, description = "update an existing report query", responses = {
            @ApiResponse(responseCode = "200",
                    description = "the report query successfully updated, and the id is returned in the response"),
            @ApiResponse(responseCode = "404", description = "bad request, report query is not found") })
    Response update(
            @Parameter(description = "Report query id", required = true) @PathParam("id") Long id,
            @Parameter(description = "Report query object", required = true) ReportQueryInput reportQuery);
}