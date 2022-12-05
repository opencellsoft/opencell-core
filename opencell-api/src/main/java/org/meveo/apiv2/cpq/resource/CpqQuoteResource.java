package org.meveo.apiv2.cpq.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/cpq/quotes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CpqQuoteResource {

    @GET
    @Path("/{quoteCode}/availableOpenOrders")
    @Operation(summary = "Get available open orders for a quote", 
    tags = {""},
    responses = {
    		@ApiResponse(responseCode = "200", description = "The Open Orders avaiblable for quote")
    })
    Response findAvailableOpenOrders(@Parameter(description = "", required = true) @PathParam("quoteCode") String quoteCode);
}
