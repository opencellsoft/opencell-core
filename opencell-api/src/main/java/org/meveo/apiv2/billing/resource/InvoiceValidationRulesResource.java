package org.meveo.apiv2.billing.resource;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.apiv2.billing.InvoiceValidationRuleDto;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/billing/invoicevalidationrules")
@Produces({APPLICATION_JSON})
@Consumes({APPLICATION_JSON})
public interface InvoiceValidationRulesResource {

    @POST
    @Path("/")
    @Operation(summary = "Create invoice validation rule",
            tags = {"InvoiceValidationRules"},
            description = "Create invoice validation rule",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                            description = "invoice validation rule successfully created"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "304",
                            description = "error when creating invoice validation rule ")
            })
    Response create(InvoiceValidationRuleDto invoiceValidationRuleDto);


    @PUT
    @Path("/{id}")
    @Operation(summary = "Update invoice validation rule",
            tags = {"InvoiceValidationRules"},
            description = "Create invoice validation rule",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                            description = "invoice validation rule successfully updated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "304",
                            description = "error when updating invoice validation rule ")
            })
    Response update(@Parameter(description = "id of the InvoiceValidation Rule", required = true) @PathParam("id") @NotNull Long id,
                    @Parameter(description = "Validation Rule Dto", required = true) @NotNull InvoiceValidationRuleDto invoiceValidationRuleDto);


    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete invoice validation rule",
            tags = {"InvoiceValidationRules"},
            description = "Delete invoice validation rule",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
                            description = "invoice validation rule successfully deleted"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "304",
                            description = "error when deleting invoice validation rule ")
            })
    Response delete(@Parameter(description = "id of the InvoiceValidation Rule to delete", required = true) @PathParam("id") @NotNull Long id);
}
