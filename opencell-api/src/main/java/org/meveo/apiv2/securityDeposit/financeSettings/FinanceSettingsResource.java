package org.meveo.apiv2.securityDeposit.financeSettings;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.securityDeposit.FinanceSettings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/accountReceivable/financeSettings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface FinanceSettingsResource {
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
    Response create(@Parameter(description = "Security Deposit Settings", required = true) FinanceSettings financeSettings);

    @PUT
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
                            description = "Following security deposit does not exist : {financeSettings ids}")
            })
    Response update(@Parameter(description = "contain the code of Security deposit settings te be updated by its id", required = true)
                    @PathParam("id") Long id,
                    @Parameter(description = "Security Deposit Settings", required = true) FinanceSettings financeSettings);
}