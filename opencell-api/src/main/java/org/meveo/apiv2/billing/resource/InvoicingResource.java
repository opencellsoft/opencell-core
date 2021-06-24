package org.meveo.apiv2.billing.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.apiv2.billing.ExceptionalBillingRun;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/billing/invoicing")
@Produces({"application/json"})
@Consumes({"application/json"})
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
}