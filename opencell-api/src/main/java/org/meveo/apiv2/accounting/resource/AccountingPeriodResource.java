package org.meveo.apiv2.accounting.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
	
	@PUT
	@Path("/{fiscalYear}/subAccountingPeriods/{number}/allUsersStatus/{status}")
	@Operation(summary = "update allUsers status", tags = {
			"AccountingPeriods" }, description = "Update a SubaccountingPeriod", responses = {
					@ApiResponse(responseCode = "200", description = "allUsers status is successfully updated"),
					@ApiResponse(responseCode = "404", description = "target entity does not exist")})
	Response updateAllUserStatus(@Parameter(description = "fiscalYear of the Invoice", required = true) @PathParam("fiscalYear") String fiscalYear,
					@Parameter(description = "subaccounting period number", required = true) @PathParam("number") String number,
					@Parameter(description = "status", required = true) @PathParam("status") String status,
					@Parameter(description = "reason of reopening sub-accounting period", allowEmptyValue = true) @QueryParam("reason") String reason);
	
	@PUT
	@Path("/{fiscalYear}/subAccountingPeriods/{number}/regularUsersStatus/{status}")
	@Operation(summary = "update regularUsers status", tags = {
			"AccountingPeriods" }, description = "Update a SubaccountingPeriod", responses = {
					@ApiResponse(responseCode = "200", description = "regularUsers status is successfully updated"),
					@ApiResponse(responseCode = "404", description = "target entity does not exist")})
	Response updateRegularUserStatus(@Parameter(description = "fiscalYear of the Invoice", required = true) @PathParam("fiscalYear") String fiscalYear,
					@Parameter(description = "subaccounting period number", required = true) @PathParam("number") String number,
					@Parameter(description = "status", required = true) @PathParam("status") String status,
					@Parameter(description = "reason of reopening sub-accounting period" , allowEmptyValue = true) @QueryParam("reason") String reason);
	

}