package org.meveo.apiv2.dunning.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningPauseReasons;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/dunning/pauseReason")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningPauseReasonResource {

	@POST
	@Operation(summary = "Create new Dunning Pause Reason",
    tags = {"Dunning"},
    description = "Create new Dunning Pause Reason",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Pause Reason successfully created"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Pause Reason with the same code exist")
    })
	Response create(@Parameter(required = true) DunningPauseReasons dunningPauseReasons);

	@PUT
	@Path("/{dunningSettingsCode }/{pauseReason}")
	@Operation(summary = "Update an existing Dunning Pause Reason",
    tags = {"Dunning"},
    description = "Update an existing Dunning Pause Reasons",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Pause Reasons successfully updated"),
            @ApiResponse(responseCode = "404",
                    description = "new code for dunning Pause Reasons already exist")
    })
	Response update(@Parameter(required = true) DunningPauseReasons dunningPauseReasons,
            @Parameter(required = true, description = "The dunning setting related to the dunning pause reason") @PathParam("dunningSettingsCode") String dunningSettingsCode,
            @Parameter(required = true, description = "Entity's id to update") @PathParam("pauseReason") Long pauseReason);

	@DELETE
	@Path("/{dunningSettingsCode }/{pauseReason}")
	@Operation(summary = "Delete existing Dunning Pause Reason",
    tags = {"Dunning"},
    description = "Delete Existing dunning Pause Reasons",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Pause Reasons successfully deleted"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning Pause Reason with id in the path doesn't exist")
    })
	Response delete(
            @Parameter(required = true, description = "The dunning setting related to the dunning pause reason") @PathParam("dunningSettingsCode") String dunningSettingsCode,
            @Parameter(required = true, description = "id of removed dunning Pause Reasons") @PathParam("pauseReason") Long pauseReason);
	
	
}
