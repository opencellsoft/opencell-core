package org.meveo.apiv2.securityDeposit.resource;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.securityDeposit.SecurityDepositCreditInput;
import org.meveo.apiv2.securityDeposit.SecurityDepositInput;
import org.meveo.model.securityDeposit.FinanceSettings;

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

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update Security Deposit",
            tags = {"Post"},
            description = "Update Security Deposit",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Security deposit was successfully updated"),
                    @ApiResponse(responseCode = "400",
                            description = "Bad Request"),
                    @ApiResponse(responseCode = "404",
                            description = "Following security deposit does not exist : {securityDeposit ids}")
            })
    Response update(@Parameter(description = "contain the code of Security deposit te be updated by its id", required = true) @PathParam("id") Long id,
            @Parameter(description = "Security Deposit input", required = true) SecurityDepositInput securityDepositInput);

    @PUT
    @Path("/credit/{id}")
    @Operation(summary = "Credit Security Deposit",
            tags = {"Put"},
            description = "Credit Security Deposit",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Security deposit was successfully credited"),
                    @ApiResponse(responseCode = "400",
                            description = "Bad Request"),
                    @ApiResponse(responseCode = "404",
                            description = "Following security deposit does not exist : {securityDeposit ids}")
            })
    Response credit(@Parameter(description = "contain the code of Security deposit te be credited by its id", required = true) @PathParam("id") Long id,
            @Parameter(description = "Security Deposit input", required = true) SecurityDepositCreditInput securityDepositInput);

}
