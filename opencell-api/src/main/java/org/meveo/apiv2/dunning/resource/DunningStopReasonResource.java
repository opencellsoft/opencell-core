package org.meveo.apiv2.dunning.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningStopReason;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/dunning/stopReason")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningStopReasonResource {

	@POST
	@Operation(summary = "Create new Dunning Stop Reason", tags = { "Dunning" }, description = "Create new Dunning Stop Reason", responses = {
			@ApiResponse(responseCode = "200", description = "dunning Stop Reason successfully created"),
			@ApiResponse(responseCode = "404", description = "Dunning Stop Reason with the same code exist") })
	Response create(@Parameter(required = true) DunningStopReason dunningStopReason);

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing Dunning Stop Reason", tags = { "Dunning" }, description = "Update an existing Dunning Stop Reasons", responses = {
			@ApiResponse(responseCode = "200", description = "dunning Stop Reasons successfully updated"),
			@ApiResponse(responseCode = "404", description = "new code for dunning Stop Reasons already exist") })
	Response update(@Parameter(required = true) DunningStopReason dunningStopReason, @Parameter(required = true, description = "Entity's id to update") @PathParam("id") Long id);

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete existing Dunning Stop Reason",
    tags = {"Dunning"},
    description = "Delete Existing dunning Stop Reasons",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Stop Reasons successfully deleted"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Stop Reason with id in the path doesn't exist")
    })
	Response delete(@Parameter(required = true, description = "Id of removed dunning Stop Reasons") @PathParam("id") Long id);
	
	
}
