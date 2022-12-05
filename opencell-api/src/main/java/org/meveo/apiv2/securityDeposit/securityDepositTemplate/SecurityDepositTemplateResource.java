package org.meveo.apiv2.securityDeposit.securityDepositTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.securityDeposit.SDTemplateListStatus;
import org.meveo.apiv2.securityDeposit.SecurityDepositTemplate;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/securityDeposit/securityDepositTemplate")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SecurityDepositTemplateResource {

    @POST
    @Operation(summary = "Create Security Deposit template",
            tags = {"Post"},
            description = "Create Security Deposit template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Security deposit template was successfully created"),
                    @ApiResponse(responseCode = "400",
                            description = "Bad Request")
            })
    Response create(@Parameter(description = "Security Deposit Template", required = true) SecurityDepositTemplate securityDepositTemplate);

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update Security Deposit template",
            tags = {"Post"},
            description = "Update Security Deposit template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Security deposit template was successfully updated"),
					@ApiResponse(responseCode = "400",
							description = "Bad Request"),
                    @ApiResponse(responseCode = "404",
                            description = "Following security deposit template does not exist : {securityDepositTemplate ids}")
            })
    Response update(@Parameter(description = "contain the code of Security deposit template te be updated by its id", required = true) @PathParam("id") Long id,
            @Parameter(description = "Security Deposit template", required = true) SecurityDepositTemplate securityDepositTemplate);

@POST
    @Path("/changeStatus")
    @Operation(summary = "Change status of Security Deposit Template",
            tags = {"Post"},
            description = "Change status of Security Deposit Template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Security deposit template  status was successfully updated"),
					@ApiResponse(responseCode = "400",
							description = "Bad Request"),
                    @ApiResponse(responseCode = "404",
                            description = "Following security deposit template does not exist : {securityDepositTemplate ids}")
            })
    Response updateStatus(@Parameter(description = "Security Deposit template List", required = true)SDTemplateListStatus securityDepositTemplateListStatus);


}
