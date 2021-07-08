package org.meveo.apiv2.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.dto.ActionStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accountManagement/customerAccounts")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface AccountManagementResource {
    /**
     *  Change a payer account's parent using API to attach it to an other account.
     *
     * @param customerAccountCodeOrId The customer account's code or id
     * @param id The parent customer id
     * @param code The parent customer code
     * @return counter instances.
     */
    @PUT
    @Path("/{customerAccountCodeOrId}/customer")
    @Operation(summary = "The Customer Account will be moved immediately under the provided Customer.\\n\" +\n" +
            "                    \"All open wallet operation will be rerated.",
            tags = { "Generic" },
            description ="The Customer Account will be moved immediately under the provided Customer.\n" +
                    "All open wallet operation will be rerated.\n" +
                    "\n" +
                    "No consistency check is performed, no other data is modified.\n" +
                    "Especially:\n" +
                    "\n" +
                    "counters (accumulators) set on the origin or target Customer will not be updated to reflect the move\n" +
                    "\n" +
                    "custom fields referencing entities in the origin hierarchy will not be updated\n" +
                    "\n" +
                    "Unless specifically developed to use field history, reports will use the customer at the time of execution",
            responses = {
                    @ApiResponse(responseCode="204", description = "resource successfully updated but not content exposed except the hypermedia"),
                    @ApiResponse(responseCode = "404", description = "entity not found"),
                    @ApiResponse(responseCode = "400", description = "bad request when input not well formed")
            })
    Response changeCustomerAccountParentAccount(@PathParam("customerAccountCodeOrId") String customerAccountCodeOrId,
                                                @Parameter(description = "parentIdOrCode") String parentIdOrCode) throws JsonProcessingException;

}
