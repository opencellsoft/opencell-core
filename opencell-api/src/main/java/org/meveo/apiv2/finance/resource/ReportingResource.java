package org.meveo.apiv2.finance.resource;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.finance.ReportingPeriodEnum;
import org.meveo.model.report.query.SortOrderEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/standardReports")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ReportingResource {

	@GET
	@Path("/trialBalances")
	@Operation(summary = "This endpoint allows to get trial balance according to the given filters", tags = {
			"TrialBalance" }, description = "Get trial balance report", responses = { @ApiResponse(responseCode = "200", description = "return trial balance report"),
					@ApiResponse(responseCode = "204", description = "Empty trial balance report") })
	Response getTrialBalances(@Parameter(description = "The trial balance report period") @DefaultValue("CURRENT_QUARTER") @QueryParam("period") ReportingPeriodEnum period,
			@Parameter(description = "code or label") @QueryParam("codeOrLabel") String codeOrLabel,
			@Parameter(description = "The trial balance report start date") @QueryParam("startDate") Date startDate,
			@Parameter(description = "The trial balance report end date") @QueryParam("endDate") Date endDate,
			@Parameter(description = "The sorting by field") @QueryParam("sortBy") String sortBy,
			@Parameter(description = "The sort order") @QueryParam("sortOrder") SortOrderEnum sortOrder,
			@Parameter(description = "The list offset") @DefaultValue("0") @QueryParam("offset") Long offset,
			@Parameter(description = "The record list size") @DefaultValue("100") @QueryParam("limit") Long limit, @Context Request request);

}