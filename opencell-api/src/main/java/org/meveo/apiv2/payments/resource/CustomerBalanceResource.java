package org.meveo.apiv2.payments.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.payments.CustomerBalance;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/customerBalance")
@Produces({APPLICATION_JSON})
@Consumes({APPLICATION_JSON})
public interface CustomerBalanceResource {

    @POST
    @Operation(summary = "Create new customer balance",
            tags = {"Customer balance"},
            description = "Create new customer balance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer balance successfully created"),
                    @ApiResponse(responseCode = "403", description = "One default balance already exists"),
                    @ApiResponse(responseCode = "403", description = "Can not add more than maxCustomerBalance"),
                    @ApiResponse(responseCode = "403", description = "Debit or Credit line should not be empty"),
            })
    Response create(@Parameter(required = true) CustomerBalance resource);

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing customer balance", tags = {"Customer balance" },
            description = "Update an existing customer balance", responses = {
            @ApiResponse(responseCode = "200", description = "Customer balance successfully updated"),
            @ApiResponse(responseCode = "403", description = "Debit or credit line should not be empty"),
            @ApiResponse(responseCode = "404", description = "Customer balance doesn't exist") })
    Response update(@PathParam("id") Long id, @Parameter(required = true) CustomerBalance resource);

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete an existing customer balance", tags = {"Customer balance" },
            description = "Delete an existing customer balance", responses = {
            @ApiResponse(responseCode = "200", description = "Customer balance successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Default customer balance can not be deleted"),
            @ApiResponse(responseCode = "404", description = "Customer balance doesn't exist") })
    Response delete(@PathParam("id") Long id);
}