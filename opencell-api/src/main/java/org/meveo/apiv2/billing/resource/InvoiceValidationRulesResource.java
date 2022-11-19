package org.meveo.apiv2.billing.resource;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.meveo.apiv2.billing.InvoiceValidationRuleDto;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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
