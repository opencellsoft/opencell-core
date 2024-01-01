package org.meveo.apiv2.payments.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.payments.ImportRejectionCodeInput;
import org.meveo.apiv2.payments.PaymentGatewayInput;
import org.meveo.apiv2.payments.RejectionCode;
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
    
    @POST
    @Path("/paymentBySepa")
    @Operation(summary = "Payment By Sepa",
            tags = {"Payment"},
            description = "create and validate a payment by Sepa api",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "success"),
                    @ApiResponse(responseCode = "404",
                            description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Payment by sepa is failed")
            })
    Response paymentBySepa(@Parameter(required = true) CardRefund cardPayment);

    @POST
    @Path("/rejectionCodes")
    @Operation(summary = "Create a new RejectionCode",
            tags = {"PaymentRejectionCode"},
            description = "Create a new rejection code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "RejectionCodes successfully created"),
                    @ApiResponse(responseCode = "404", description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412", description = "Missing parameters"),
                    @ApiResponse(responseCode = "400", description = "RejectionCode creation failed")
            })
    Response createRejectionCode(@Parameter(required = true) RejectionCode rejectionCode);

    @PUT
    @Path("/rejectionCodes/{id}")
    @Operation(summary = "Update RejectionCode",
            tags = {"PaymentRejectionCode"},
            description = "Update an existing rejection code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "RejectionCode successfully updated"),
                    @ApiResponse(responseCode = "404", description = "RejectionCode does not exist"),
                    @ApiResponse(responseCode = "412", description = "Missing parameters"),
                    @ApiResponse(responseCode = "400", description = "RejectionCode modification failed")
            })
    Response updateRejectionCode(@Parameter(description = "Rejection code id", required = true)
                                 @PathParam("id") Long id,
                                 @Parameter(required = true) RejectionCode rejectionCode);

    @DELETE
    @Path("/rejectionCodes/{id}")
    @Operation(summary = "remove PaymentRejectionCode",
            tags = {"PaymentRejectionCode"},
            description = "remove payment rejection code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "RejectionCodes successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Entity does not exist"),
                    @ApiResponse(responseCode = "412", description = "Missing parameters"),
                    @ApiResponse(responseCode = "400", description = "RejectionCode deletion failed")
            })
    Response removeRejectionCode(@Parameter(description = "Rejection code id", required = true) @PathParam("id") Long id);

    @DELETE
    @Path("/rejectionCodes/clearAll")
    @Operation(summary = "Clear rejectionCodes by gateway",
            tags = {"PaymentRejectionCode"},
            description = "Clear rejectionCodes by gateway",
            responses = {
                    @ApiResponse(responseCode = "200", description = "RejectionCode successfully cleared"),
                    @ApiResponse(responseCode = "404", description = "Payment gateway does not exist"),
                    @ApiResponse(responseCode = "400", description = "RejectionCode clearing failed")
            })
    Response clearAll(@Parameter(required = true) PaymentGatewayInput paymentGatewayInput);

    @POST
    @Path("/rejectionCodes/import")
    @Operation(summary = "Import rejectionsCodes by gateway",
            tags = {"Payment"},
            description = "Import rejectionsCodes by gateway",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "404", description = "Gateway does not exist"),
                    @ApiResponse(responseCode = "400", description = "Error occurred during import")
            })
    Response importRejectionCodes(@Parameter(required = true) ImportRejectionCodeInput importRejectionCodeInput);

    @POST
    @Path("/rejectionCodes/export")
    @Operation(summary = "Export rejectionsCodes by gateway",
            tags = {"Payment"},
            description = "Export rejectionsCodes by gateway",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "404", description = "Gateway does not exist"),
                    @ApiResponse(responseCode = "400", description = "Error occurred during export")
            })
    Response export(@Parameter(required = true) PaymentGatewayInput paymentGateway);
}
