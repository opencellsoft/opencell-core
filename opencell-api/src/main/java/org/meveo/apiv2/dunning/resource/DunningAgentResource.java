package org.meveo.apiv2.dunning.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.DunningAgentInput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/dunning/dunningAgent")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningAgentResource {

	@POST
	@Operation(summary = "Create new Dunning Agent",
    tags = {"Dunning Agent"},
    description = "Create new Dunning Agent",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Agent successfully created"),
    }) 
	
	Response create(@Parameter(required = true) DunningAgentInput dunningAgentInput);


	@PUT
	@Path("/{dunningSettingsCode}/{agentEmailItem}")
	@Operation(summary = "update an existing Dunning Agent",
    tags = {"Dunning Agent"},
    description = "Update an existing Dunning Agent",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Agent successfully updated"),
    }) 
	
	Response update(@Parameter(required = true) DunningAgentInput dunningAgentInput, 
					@PathParam("dunningSettingsCode") String dunningSettingsCode,
					@PathParam("agentEmailItem") String agentEmailItem);

	@DELETE
	@Path("/{dunningSettingsCode}/{agentEmailItem}")
	@Operation(summary = "delete an existing Dunning Agent",
    tags = {"Dunning Agent"},
    description = "Update an existing Dunning Agent",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Agent successfully updated"),
    }) 
	
	Response delete(@PathParam("dunningSettingsCode") String dunningSettingsCode, @PathParam("agentEmailItem") String agentEmailItem);
	
}
