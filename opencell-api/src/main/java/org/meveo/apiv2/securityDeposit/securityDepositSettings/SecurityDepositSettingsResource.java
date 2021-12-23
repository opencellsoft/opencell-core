package org.meveo.apiv2.securityDeposit.securityDepositSettings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.securityDeposit.SecurityDepositSettings;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/securityDeposit/securityDepositSettings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SecurityDepositSettingsResource {
    @POST
    @Operation(summary = "Create Security Deposit settings",
            tags = {"Post"},
            description = "Create Security Deposit settings",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Security deposit settings was successfully created"),
                    @ApiResponse(responseCode = "400",
                            description = "Bad Request")
            })
    Response create(@Parameter(description = "Security Deposit Settings", required = true) SecurityDepositSettings securityDepositSettings);

    @POST
    @Path("/{id}")
    @Operation(summary = "Update Security Deposit settings",
            tags = {"Post"},
            description = "Update Security Deposit settings",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Security deposit settings was successfully updated"),
					@ApiResponse(responseCode = "400",
							description = "Bad Request"),
                    @ApiResponse(responseCode = "404",
                            description = "Following security deposit does not exist : {securityDepositSettings ids}")
            })
    Response update(@Parameter(description = "contain the code of Security deposit settings te be updated by its id", required = true) @PathParam("id") Long id,
            @Parameter(description = "Security Deposit Settings", required = true) SecurityDepositSettings securityDepositSettings);
    

}
