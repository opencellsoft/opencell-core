package org.meveo.apiv2.dunning.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningInvoiceStatus ;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/dunning/invoiceStatus")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningInvoiceStatusResource {

	@POST
	@Operation(summary = "Create new Dunning Invoice status ",
    tags = {"Dunning"},
    description = "Create new Dunning Invoice status ",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Invoice status  successfully created"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Invoice status  with the same code exist")
    })
	Response create(@Parameter(required = true) DunningInvoiceStatus  dunningInvoiceStatus );

	@PUT
	@Path("/{dunningSettingsCode }/{status}")
	@Operation(summary = "Update an existing Dunning Invoice status ",
    tags = {"Dunning"},
    description = "Update an existing Dunning Invoice status",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Invoice status successfully updated"),
            @ApiResponse(responseCode = "404",
                    description = "new code for dunning Invoice status already exist")
    })
	Response update(@Parameter(required = true) DunningInvoiceStatus  dunningInvoiceStatus ,
            @Parameter(required = true, description = "The dunning setting related to the dunning invoice status ") @PathParam("dunningSettingsCode") String dunningSettingsCode,
            @Parameter(required = true, description = "Entity's id to update") @PathParam("status") Long id);

	@DELETE
	@Path("/{dunningSettingsCode }/{status}")
	@Operation(summary = "Delete existing Dunning Invoice status ",
    tags = {"Dunning"},
    description = "Delete Existing dunning Invoice status",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Invoice status successfully deleted"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Invoice status  with id in the path doesn't exist")
    })
	Response delete(
            @Parameter(required = true, description = "The dunning setting related to the dunning invoice status ") @PathParam("dunningSettingsCode") String dunningSettingsCode,
            @Parameter(required = true, description = "id of removed dunning Invoice status") @PathParam("status") Long id);
	
	
}
