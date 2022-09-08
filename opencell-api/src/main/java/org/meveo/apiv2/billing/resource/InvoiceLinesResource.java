package org.meveo.apiv2.billing.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

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
}