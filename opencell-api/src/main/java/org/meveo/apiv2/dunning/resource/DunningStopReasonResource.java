package org.meveo.apiv2.dunning.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningStopReasons;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/dunning/dunningStopReason")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningStopReasonResource {

	@POST
	@Operation(summary = "Create new Dunning Stop Reason",
    tags = {"Dunning"},
    description = "Create new Dunning Stop Reason",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Stop Reason successfully created"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Stop Reason with the same code exist")
    })
	Response create(@Parameter(required = true) DunningStopReasons dunningStopReasons);

	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing Dunning Stop Reason",
    tags = {"Dunning"},
    description = "Update an existing Dunning Stop Reasons",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Stop Reasons successfully updated"),
            @ApiResponse(responseCode = "404",
                    description = "new code for dunning Stop Reasons already exist")
    })
	Response update(@Parameter(required = true) DunningStopReasons dunningStopReasons,
			@Parameter(required = true, description = "Entity's id to update") @PathParam("id") Long id);

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
