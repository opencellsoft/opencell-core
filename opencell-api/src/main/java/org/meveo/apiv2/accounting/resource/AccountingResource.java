package org.meveo.apiv2.accounting.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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