package org.meveo.apiv2.refund;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/refund")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface RefundResource {

    @POST
    @Path("/refundByCard")
    @Operation(summary = "Refund By Card",
            tags = {"Refund"},
            description = "create and validate a refund by Card order",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "success"),
                    @ApiResponse(responseCode = "404",
                            description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Refund by card is failed")
            })
    Response refundByCard(@Parameter(required = true) CardRefund cardRefund);

    @POST
    @Path("/refundBySCT")
    @Operation(summary = "Refund By SCT",
            tags = {"Refund"},
            description = "create and validate a refund by SCT order",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "success"),
                    @ApiResponse(responseCode = "404",
                            description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Refund by SCT is failed")
            })
    Response refundBySCT(@Parameter(required = true) SCTRefund sctRefund);
}
