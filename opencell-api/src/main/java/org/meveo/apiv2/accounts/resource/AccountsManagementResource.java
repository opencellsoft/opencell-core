package org.meveo.apiv2.accounts.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.accounts.ConsumerInput;
import org.meveo.apiv2.accounts.OpenTransactionsActionEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/accountsManagement")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountsManagementResource {

    @POST
    @Path("/subscriptions/{subscriptionCode}/transfer")
    @Operation(summary = "This endpoint allows to transfer a subscription to another account", tags = { "Subscription",
            "Transfer" }, description = "API to transfer the subscription from a consumer to an other consumer (UA)", responses = {
                    @ApiResponse(responseCode = "204", description = "Success, no return data"),
                    @ApiResponse(responseCode = "200", description = "Success (in case of moved WO/RT) Returns the ids of moved WO/RT"),
                    @ApiResponse(responseCode = "404", description = "Either Customer Account or Customer not found"),
                    @ApiResponse(responseCode = "403", description = "Cannot move subscription") })
    Response transferSubscription(@Parameter(description = "The subscription code", required = true) @PathParam("subscriptionCode") String subscriptionCode,
            @Parameter(description = "The new parent", required = true) ConsumerInput consumerInput,
            @Parameter(description = "Open transactions action ") @DefaultValue("NONE") @QueryParam("openTransactionsAction") OpenTransactionsActionEnum action);
}
