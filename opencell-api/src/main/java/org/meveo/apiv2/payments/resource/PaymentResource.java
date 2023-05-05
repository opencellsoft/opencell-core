package org.meveo.apiv2.payments.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.refund.CardRefund;

@Path("/payment")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface PaymentResource {

    @POST
    @Path("/paymentByCard")
    @Operation(summary = "Payment By Card",
            tags = {"Payment"},
            description = "create and validate a payment by Card order",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "success"),
                    @ApiResponse(responseCode = "404",
                            description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Payment by card is failed")
            })
    Response paymentByCard(@Parameter(required = true) CardRefund cardPayment);
}
