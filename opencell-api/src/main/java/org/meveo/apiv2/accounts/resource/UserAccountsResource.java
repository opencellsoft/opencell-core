package org.meveo.apiv2.accounts.resource;

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

@Path("/userAccounts")
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
            @ApiResponse(responseCode = "400", description = "bad request userAccountCode contains an error")
    })
    Response allowedUserAccountParents(@Parameter(description = "user Account code", required = true) @PathParam("userAccountCode") String userAccountCode);

}
