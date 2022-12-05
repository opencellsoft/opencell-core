package org.meveo.apiv2.accounts.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.apiv2.models.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/accounts/userAccounts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserAccountsResource {

    @GET
    @Path("/{userAccountCode}/allowedParents")
    @Operation(summary = "List user accounts allowed to be parent of the giving user account",
    tags = { "AllowedParents" },
    description ="fine allowed parents for a giving user account",
    responses = {
            @ApiResponse(responseCode="200", description = "the UserAccounts allowed to be parent for userAccountCode successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "the full list of entities not found",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))            
    })
    Response allowedUserAccountParents(@Parameter(description = "user Account code", required = true) @PathParam("userAccountCode") String userAccountCode);

}
