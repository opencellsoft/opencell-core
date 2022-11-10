package org.meveo.apiv2.billing.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

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
            summary = "This API will allow creating adjustment based on an existing validated invoice.",
            description = "This API will allow creating adjustment based on an existing validated invoice.<br>"
                            + "Either can we choose specific invoice lines from a specific invoice or the whole invoice to be used on the newly created adjustment.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Adjustment successfully created"),
                    @ApiResponse(responseCode = "403", description = "Invoice should be Validated and occCategory equals to DEBIT as an invoice type!"),
                    @ApiResponse(responseCode = "500", description = "Error when creating adjustment"),
                    @ApiResponse(responseCode = "403", description = "IThe following parameters are required or contain invalid values: globalAdjustment")
                }
            )
    Response markForAdjustment(@Parameter(description = "InvoiceLines to mark for adjustment", required = true) @NotNull InvoiceLinesToMarkAdjustment invoiceLinesToMark);
    
}