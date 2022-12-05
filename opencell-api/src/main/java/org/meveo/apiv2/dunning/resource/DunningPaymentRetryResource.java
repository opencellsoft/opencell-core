package org.meveo.apiv2.dunning.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningPaymentRetry;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/dunning/paymentRetry")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningPaymentRetryResource {

	@POST
	@Operation(summary = "Create new Dunning Payment Retry",
    tags = {"Dunning"},
    description = "Create new Dunning Payment Retry",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Payment Retry successfully created"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Payment Retry with the same code exist")
    })
	Response create(@Parameter(required = true) DunningPaymentRetry dunningPaymentRetry);

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing Dunning Payment Retry",
    tags = {"Dunning"},
    description = "Update an existing Dunning Payment Retry",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Payment Retry successfully updated"),
            @ApiResponse(responseCode = "404",
                    description = "new code for dunning Payment Retry already exist")
    })
	Response update(@Parameter(required = true) DunningPaymentRetry dunningPaymentRetry, @Parameter(required = true, description = "Entity's id to update") @PathParam("id") Long id);

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete existing Dunning Payment Retry",
    tags = {"Dunning"},
    description = "Delete Existing dunning Payment Retry",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Payment Retry successfully deleted"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Payment Retry with id in the path doesn't exist")
    })
	Response delete(@Parameter(required = true, description = "Id of removed dunning Payment Retry") @PathParam("id") Long id);
	
	
}
