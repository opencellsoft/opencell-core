package org.meveo.apiv2.accounting.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounting.AccountingPeriod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/accountingPeriodManagement/accountingPeriods")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountingPeriodResource {
	
	@POST
	@Operation(summary = "Create a new AccountingPeriod", tags = {
			"AccountingPeriods" }, description = "Create a new AccountingPeriod", responses = {
					@ApiResponse(responseCode = "200", description = "the AccountingPeriod is successfully created"),
					@ApiResponse(responseCode = "400", description = "bad request when AccountingPeriod information contains an error") })
	Response create(
			@Parameter(description = "the AccountingPeriod input object", required = true) AccountingPeriod input);

	@PUT
	@Path("/{fiscalYear}")
	@Operation(summary = "update an new AccountingPeriod", tags = {
			"AccountingPeriods" }, description = "Update an AccountingPeriod", responses = {
					@ApiResponse(responseCode = "200", description = "the AccountingPeriod is successfully updated"),
					@ApiResponse(responseCode = "404", description = "the AccountingPeriod does not exist"),
					@ApiResponse(responseCode = "400", description = "bad request, AccountingPeriod informations contains an error") })
	Response update(@Parameter(description = "fiscalYear of the Invoice", required = true) @PathParam("fiscalYear") String fiscalYear,
			@Parameter(description = "the AccountingPeriod input object", required = true) AccountingPeriod input);
	
	@POST
	@Path("/generateNextAP")
	@Operation(summary = "Generate next AccountingPeriod", tags = {
			"AccountingPeriods" }, description = "Generate next AccountingPeriod", responses = {
					@ApiResponse(responseCode = "200", description = "the next AccountingPeriod is successfully generated"),
					@ApiResponse(responseCode = "400", description = "bad request: AccountingPeriod information contains an error") })
	Response generateNextAP();

}