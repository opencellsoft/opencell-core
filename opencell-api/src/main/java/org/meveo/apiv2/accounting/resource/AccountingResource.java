package org.meveo.apiv2.accounting.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/accounting")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface AccountingResource {

    @GET
    @Path("/auxiliaryAccounts/{customerAccountCode}")
    @Operation(summary = "Get the auxiliary account information corresponding to the giver customer account",
            tags = {"AuxiliaryCode" },
            description = "Returns auxiliary account information corresponding to the giver customer account",
            responses = {
            @ApiResponse(responseCode = "200", description = "Auxiliary account information are successfully evaluated"),
            @ApiResponse(responseCode = "404", description = "Customer account not fount"),
            @ApiResponse(responseCode = "500", description = "Auxiliary account information not correctly evaluated")
    })
    Response getAuxiliaryAccount(@PathParam("customerAccountCode") String customerAccountCode);
}