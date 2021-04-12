package org.meveo.apiv2.billing.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.billing.RatedTransactionInput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/billing/ratedTransaction")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RatedTransactionResource {

	@POST
	@Path("/")
	@Operation(summary = "This endpoint allows to create a ratedTransaction resource", tags = {
			"RatedTransaction" }, description = "create new ratedTransaction", responses = {
					@ApiResponse(responseCode = "200", description = "the ratedTransaction successfully created, and the id is returned in the response"),
					@ApiResponse(responseCode = "400", description = "bad request on ratedTransaction creation") })
	Response createRatedTransaction(
			@Parameter(description = "the ratedTransaction object", required = true) RatedTransactionInput RatedTransaction);

	@PUT
	@Path("/{id}")
	@Operation(summary = "This endpoint allows to update an existing ratedTransaction resource", tags = {
			"RatedTransaction" }, description = "update an existing ratedTransaction", responses = {
					@ApiResponse(responseCode = "200", description = "the ratedTransaction successfully updated, and the id is returned in the response"),
					@ApiResponse(responseCode = "400", description = "bad request, ratedTransaction information contains an error") })
	Response updateRatedTransaction(
			@Parameter(description = "id of ratedTransaction", required = true) @PathParam("id") Long id,
			@Parameter(description = "the ratedTransaction object", required = true) RatedTransactionInput RatedTransaction);

	@PUT
	@Path("/{id}/cancellation")
	@Operation(summary = "This endpoint allows to cancel an existing ratedTransaction resource", tags = {
			"RatedTransaction" }, description = "cancel an existing ratedTransaction", responses = {
					@ApiResponse(responseCode = "200", description = "the ratedTransaction successfully canceled"),
					@ApiResponse(responseCode = "400", description = "bad request, ratedTransaction is not found") })
	Response cancel(
			@Parameter(description = " ratedTransaction id", required = true) @PathParam("id") Long id,
			@Context Request request);

}
