package org.meveo.apiv2.securityDeposit.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.securityDeposit.SecurityDepositInput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/securityDeposit")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SecurityDepositResource {

    @POST
    @Path("/instantiateSecurityDeposit")
    @Operation(summary = "Instantiate Security Deposit",
        tags = { "SecurityDeposit", "Post", "Instantiate" },
        description = "Instantiate Security Deposit",
        responses = {
            @ApiResponse(responseCode = "200", description = "Security deposit was successfully instantiated"),
            @ApiResponse(responseCode = "400", description = "Bad Request") })
    Response instantiate(@Parameter(description = "Security Deposit input", required = true) SecurityDepositInput SecurityDepositInput);

}
