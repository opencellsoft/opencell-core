package org.meveo.apiv2.catalog.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.apiv2.models.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/pricePlans")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PricePlanResource {

	@GET
	@Path("/{pricePlanMatrixCode}/pricePlanVersions/{pricePlanMatrixVersion}/checkIfUsed")
	@Operation(summary = "Check if the current price plan version is used", 
	tags = { "Price Plan" }, description = "Check if the current price plan version is used in a draft quote, not completed/validated order, or in a subscription", 
	responses = {	@ApiResponse(responseCode = "200", description = "The price plan version use successfully loaded"),
					@ApiResponse(responseCode = "400", description = "Internal error"),
					@ApiResponse(responseCode = "404", description = "Price plan version not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })

	Response getDiscountPlanItem(
			@Parameter(description = "code of the price plan ", required = true) @PathParam("pricePlanMatrixCode") String pricePlanMatrixCode,
			@Parameter(description = "version of the pricePlanVersion", required = true) @PathParam("pricePlanMatrixVersion") int pricePlanMatrixVersion);

}