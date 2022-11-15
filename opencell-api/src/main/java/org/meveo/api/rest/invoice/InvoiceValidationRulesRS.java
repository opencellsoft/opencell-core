package org.meveo.api.rest.invoice;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.invoice.InvoiceValidationRuleDto;
import org.meveo.api.rest.IBaseRs;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/billing")
@Tag(name = "Invoice", description = "@%Invoice")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface InvoiceValidationRulesRS extends IBaseRs {


    @POST
    @Path("/invoiceValidationRules")
    @Operation(
            summary = " Create invoice validation rule ",
            description = " Create invoice validation rule ",
            operationId = "    POST_Invoice_validation_rule_create",
            responses = {
                    @ApiResponse(description = " created invoice validation rule ",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus create(InvoiceValidationRuleDto invoiceValidationRuleDto);


    @PUT
    @Path("/invoiceValidationRules/{id}")
    @Operation(
            summary = " Update an invoice validation rule. ",
            description = " Update an invoice validatio rule. ",
            operationId = "    PUT_Invoice_validation_rule",
            responses = {
                    @ApiResponse(description = " action status. ",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ActionStatus.class
                                    )
                            )
                    )}
    )
    ActionStatus update(@Parameter(description = "id of the InvoiceValidation Rule", required = true) @PathParam("id") @NotNull Long id,
                        @Parameter(description = "Validation Rule Dto", required = true) @NotNull InvoiceValidationRuleDto invoiceValidationRuleDto);
}
