package org.meveo.apiv2.cpq.resource;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.apiv2.cpq.contracts.BillingRuleDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/cpq/contracts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CpqContractResource {

	@POST
	@Path("/{contractCode}/billingRule")
	@Operation(
			summary = "Create a billingRule linked to an existing contract",
			tags = { "BillingRule" },
			responses = {
	            @ApiResponse(responseCode="200", description = "the billing rule is successfully added"),
	            @ApiResponse(responseCode = "412", description = "missing required paramter for billingRule.The required params are : contractCode, criteriaEl, InvoicedBACodeEL"),
	            @ApiResponse(responseCode = "404", description = "the contract code does not exist")
			}
	)
	public Response createBillingRule(@PathParam("contractCode") @NotNull String contractCode, BillingRuleDto billingRule);

	@PUT
	@Path("/{contractCode}/billingRule/{id}")
	@Operation(
			summary = "Update a billingRule linked to an existing contract",
			tags = { "BillingRule" },
			responses = {
            @ApiResponse(responseCode="200", description = "the billing rule is successfully added"),
            @ApiResponse(responseCode = "412", description = "missing required paramter for billingRule.The required params are : contractCode, criteriaEl, InvoicedBACodeEL"),
            @ApiResponse(responseCode = "404", description = "the contract code does not exist")
    })
	public Response updateBillingRule(@PathParam("contractCode") @NotNull String contractCode, @PathParam("id") @NotNull Long billingRuleId, BillingRuleDto billingRuleDto);

	@DELETE
	@Path("/{contractCode}/billingRule/{id}")
	@Operation(
			summary = "Delete a billingRule",
			tags = { "BillingRule" },
			responses = {
	            @ApiResponse(responseCode="200", description = "the billing rule is successfully added"),
	            @ApiResponse(responseCode = "412", description = "missing required paramter for billingRule.The required params are : contractCode, criteriaEl, InvoicedBACodeEL"),
	            @ApiResponse(responseCode = "404", description = "the contract code does not exist")
			}
	)
	public Response deleteBillingRule(@PathParam("contractCode") @NotNull String contractCode, @PathParam("id") @NotNull Long billingRuleId);
	
}
