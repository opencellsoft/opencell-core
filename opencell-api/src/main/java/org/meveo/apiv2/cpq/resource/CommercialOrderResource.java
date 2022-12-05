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

@Path("/commercialOrders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CommercialOrderResource {

    @GET
    @Path("/{code}/availableOpenOrders")
    @Operation(summary = "Get available open orders for a commercial order", 
    tags = {""},
    responses = {
    		@ApiResponse(responseCode = "200", description = "The Open Orders avaiblable for commercial order")
    })
    Response findAvailableOpenOrders(@Parameter(description = "", required = true) @PathParam("code") String code);
}
