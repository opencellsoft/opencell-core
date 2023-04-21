package org.meveo.apiv2.billing.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.apiv2.billing.ExceptionalBillingRun;
import org.meveo.model.billing.RatedTransactionAction;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/billing/invoicing")
@Produces({APPLICATION_JSON})
@Consumes({APPLICATION_JSON})
public interface InvoicingResource {

    @POST
    @Path("/exceptionalBillingRun")
    @Operation(summary = "Create exceptional billing run",
            tags = {"Invoicing"},
            description = "Create exceptional billing run",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                            description = "exceptional billing run successfully created"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "304",
                            description = "error when creating exceptional billing run")
            })
    Response createExceptionalBillingRuns(
            @Parameter(description = "Billing run to create", required = true) ExceptionalBillingRun billingRun);

    @PUT
    @Path("/{billingRunId}/advanceStatus")
    @Operation(summary = "Advance the billing run status",
            tags = {"Invoicing"},
            description = "Advance the billing run status",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "Status changed successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                            description = "The status should be either NEW, INVOICE_LINES_CREATED , DRAFT_INVOICES , or REJECTED")
            })
    Response advanceStatus(@PathParam("billingRunId") Long billingRunId,
                           @QueryParam("executeInvoicingJob") boolean executeInvoicingJob);
    
    @POST
    @Path("/{billingRunId}/cancelBillingRun")
    @Operation(summary = "cancel the billing run",
            tags = {"Invoicing"},
            description = "Cancel the billing run",
            responses = {
            		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "The billing canceled successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "The billing run does not exists"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                            description = "The billing run cannot be cancelled")
            })
    Response cancelBillingRun(@PathParam("billingRunId") Long billingRunId,
                              @QueryParam("ratedTransactionAction") RatedTransactionAction action);
}