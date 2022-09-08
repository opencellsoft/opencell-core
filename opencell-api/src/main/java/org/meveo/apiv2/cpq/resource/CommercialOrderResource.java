package org.meveo.apiv2.cpq.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
