package org.meveo.apiv2.billing.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.apiv2.billing.CancelBillingRunInput;
import org.meveo.apiv2.billing.ExceptionalBillingRun;

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
    Response cancelBillingRun(@PathParam("billingRunId") Long billingRunId, CancelBillingRunInput input);

    @PUT
    @Path("/{billingRunId}/closeInvoiceLines")
    @Operation(summary = "Set status of billing run to INVOICE_LINES_CREATED while in appending mode",
            tags = {"Invoicing"},
            description = "Set status of billing run to INVOICE_LINES_CREATED, the invoice lines cannot receive new incoming RTs",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "The status of billing run is successfully set to INVOICE_LINES_CREATED"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403",
                            description = "The billing run must be in status OPEN to be updated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "The billing run does not exists"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                            description = "The status of billing run cannot be updated to INVOICE_LINES_CREATED")
            })
    Response closeInvoiceLines(@Parameter(description = "The id of billing run to be updated")
                                @PathParam("billingRunId") Long billingRunId,
                               @Parameter(description = "True to generate invoice immediately, false otherwise") @DefaultValue("false")
                               @QueryParam("executeInvoicingJob") boolean executeInvoicingJob);
    
    @POST
    @Path("/{billingRunId}/enableBillingRun")
    @Operation(summary = "enable the billing run",
            tags = {"Invoicing"},
            description = "enable the billing run",
            responses = {
            		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "The billing enabled successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "The billing run does not exists"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                            description = "The billing run cannot be enabled")
            })
    Response enableBillingRun(@PathParam("billingRunId") Long billingRunId);
    
    @POST
    @Path("/{billingRunId}/disableBillingRun")
    @Operation(summary = "disable the billing run",
            tags = {"Invoicing"},
            description = "Disable the billing run",
            responses = {
            		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                            description = "The billing disabled successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                            description = "The billing run does not exists"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409",
                            description = "The billing run cannot be disabled")
            })
    Response disableBillingRun(@PathParam("billingRunId") Long billingRunId);
}