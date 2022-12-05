package org.meveo.apiv2.billing.resource;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

import org.meveo.apiv2.billing.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/billing/invoiceLines")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface InvoiceLinesResource {

    @GET
    @Path("/{id}/taxes")
    @Operation(summary = "Return invoice line tax details", tags = {"InvoiceLines" },
            description = "Return invoice line tax details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tax details successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Invoice line not found") }
    )
    Response getTaxDetails(@Parameter(description = "Invoice line id", required = true)
                           @PathParam("id") Long invoiceLineId, @Context Request request);
    
    @POST
    @Path("/markForAdjustment")
    @Operation(
            summary = "This API will allow mark adjustment for invoice lines",
            description = "This API will allow mark adjustment for invoice lines.<br>",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Adjustment successfully created"),
                    @ApiResponse(responseCode = "403", description = "Only NOT_ADJUSTED invoice lines can be marked TO_ADJUST"),
                    @ApiResponse(responseCode = "500", description = "Error marking for adjustment")
                }
            )
    Response markForAdjustment(@Parameter(description = "InvoiceLines to mark for adjustment", required = true) @NotNull InvoiceLinesToMarkAdjustment invoiceLinesToMark);
    
    @POST
    @Path("/unmarkForAdjustment")
    @Operation(
            summary = "This API will allow creating adjustment based on an existing validated invoice.",
            description = "This API will allow creating adjustment based on an existing validated invoice.<br>",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Adjustment successfully created"),
                    @ApiResponse(responseCode = "403", description = "Only invoice lines marked TO_ADJUST can be unmarked as NOT_ADJUSTED"),
                    @ApiResponse(responseCode = "500", description = "Error marking for adjustment")
                }
            )
    Response unmarkForAdjustment(@Parameter(description = "InvoiceLines to unmark for adjustment", required = true) @NotNull InvoiceLinesToMarkAdjustment invoiceLinesToMark);
    
}