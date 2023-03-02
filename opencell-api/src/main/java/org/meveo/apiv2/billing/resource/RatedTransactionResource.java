package org.meveo.apiv2.billing.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.meveo.apiv2.billing.DuplicateRTDto;
import org.meveo.apiv2.billing.Invoice;
import org.meveo.apiv2.billing.RatedTransactionInput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.models.ApiException;

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
					@ApiResponse(responseCode = "400", description = "bad request, ratedTransaction is not eligible for update"),
					@ApiResponse(responseCode = "404", description = "bad request, ratedTransaction is not found") })
	Response updateRatedTransaction(
			@Parameter(description = "id of ratedTransaction", required = true) @PathParam("id") Long id,
			@Parameter(description = "the ratedTransaction object", required = true) RatedTransactionInput RatedTransaction);

	@PUT
	@Path("/{id}/cancellation")
	@Operation(summary = "This endpoint allows to cancel an existing ratedTransaction resource", tags = {
			"RatedTransaction" }, description = "cancel an existing ratedTransaction", responses = {
					@ApiResponse(responseCode = "200", description = "the ratedTransaction successfully canceled"),
					@ApiResponse(responseCode = "400", description = "bad request, ratedTransaction is not eligible for update"),
					@ApiResponse(responseCode = "404", description = "bad request, ratedTransaction is not found") })
	Response cancel(
			@Parameter(description = " ratedTransaction id", required = true) @PathParam("id") Long id);

	@GET
	@Path("/{code}")
	@Operation(summary = "Return a rated transaction", tags = {
			"RatedTransaction" }, description = "Returns rated transaction data", responses = { @ApiResponse(headers = {
			@Header(name = "ETag", description = "code that represents the version of the data sent back",
					schema = @Schema(type = "integer", format = "int64")) }, description = "the searched RatedTransaction",
			content = @Content(schema = @Schema(implementation = Invoice.class))),
			@ApiResponse(responseCode = "404", description = "RatedTransaction not found",
					content = @Content(schema = @Schema(implementation = ApiException.class))) })
	Response find(@Parameter(description = "code of the Rated transaction", required = true) @PathParam("code") String code,
						@Context Request request);
	

	@POST
	@Path("/duplication")
	@Operation(summary = "duplicate list of rated transaction fron their ids", tags = {
			"RatedTransaction" })
	Response duplication(@Parameter(description = "dto contains list of id for rated transaction", required = true) DuplicateRTDto duplicateRTDto);
}
