package org.meveo.apiv2.rating.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.dto.ActionStatus;
import org.meveo.apiv2.billing.WalletOperationRerate;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/rating/walletOperation")
@Produces({APPLICATION_JSON})
@Consumes({APPLICATION_JSON})
public interface WalletOperationResource {

    /**
     * Mark WalletOperations to rerate
     */
    @POST
    @Path("/markToRerate")
    @Operation(
            summary = "Mark WalletOperations to rerate",
            description = "Mark WalletOperations to rerate",
            operationId = "POST_WalletOperation_markToRerate",
            responses = {@ApiResponse(description = "Number of updated WO ", content = @Content(schema = @Schema(implementation = ActionStatus.class)))}
    )
    ActionStatus markWOToRerate(@Parameter(description = "WO advanced filter for rerate", required = true) WalletOperationRerate rerate);
}