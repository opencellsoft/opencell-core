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
import org.meveo.apiv2.accounts.ParentInput;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/accountsManagement")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountsManagementResource {

    @POST
    @Path("/subscriptions/{subscriptionCode}/transfer")
    @Operation(summary = "This endpoint allows to transfer a subscription to another account", tags = { "Generic", "Subscription",
            "Transfer" }, description = "API to transfer the subscription from a consumer to an other consumer (UA)", responses = {
                    @ApiResponse(responseCode = "204", description = "Success, no return data"),
                    @ApiResponse(responseCode = "200", description = "Success (in case of moved WO/RT) Returns the ids of moved WO/RT"),
                    @ApiResponse(responseCode = "404", description = "Either Customer Account or Customer not found"),
                    @ApiResponse(responseCode = "409", description = "Cannot move subscription") })
    Response transferSubscription(@Parameter(description = "The subscription code", required = true) @PathParam("subscriptionCode") String subscriptionCode,
            @Parameter(description = "Consumer id or code", required = true) ConsumerInput consumerInput,
            @Parameter(description = "Open transactions action ") @DefaultValue("NONE") @QueryParam("openTransactionsAction") OpenTransactionsActionEnum action);

    /**
     * Change a payer account's parent using API to attach it to an other account.
     *
     * @param customerAccountCode The customer account's code or id
     * @param parentInput The parent customer code or id
     */
    @POST
    @Path("/customerAccounts/{customerAccountCode}/moving")
    @Operation(summary = "The Customer Account will be moved immediately under the provided Customer.\\n\" +\n"
            + "                    \"All open wallet operation will be rerated.", tags = { "Generic", "Move",
                    "Customer Account" }, description = "The Customer Account will be moved immediately under the provided Customer.\n"
                            + "All open wallet operation will be rerated.\n" + "\n" + "No consistency check is performed, no other data is modified.\n" + "Especially:\n" + "\n"
                            + "counters (accumulators) set on the origin or target Customer will not be updated to reflect the move\n" + "\n"
                            + "custom fields referencing entities in the origin hierarchy will not be updated\n" + "\n"
                            + "Unless specifically developed to use field history, reports will use the customer at the time of execution", responses = {
                                    @ApiResponse(responseCode = "204", description = "resource successfully updated but not content exposed except the hypermedia"),
                                    @ApiResponse(responseCode = "404", description = "entity not found"),
                                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response changeCustomerAccountParentAccount(@PathParam("customerAccountCode") String customerAccountCode,
            @Parameter(description = "Parent id or code", required = true) ParentInput parentInput) throws JsonProcessingException;
}
