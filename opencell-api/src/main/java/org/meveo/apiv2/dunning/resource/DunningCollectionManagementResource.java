package org.meveo.apiv2.dunning.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.DunningCollectionManagement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/dunning/collectionManagement")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningCollectionManagementResource {

	@POST
	@Operation(summary = "Create new Dunning Collection Management",
    tags = {"Dunning Collection Management"},
    description = "Create new Dunning Collection Management",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Collection Management successfully created"),
    }) 
	
	Response create(@Parameter(required = true) DunningCollectionManagement dunningCollectionManagement);


	@PUT
	@Path("/{dunningSettingsCode}/{agentEmailItem}")
	@Operation(summary = "update an existing Dunning Collection Management",
    tags = {"Dunning Collection Management"},
    description = "Update an existing Dunning Collection Management",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Collection Management successfully updated"),
    }) 
	
	Response update(@Parameter(required = true) DunningCollectionManagement dunningCollectionManagement, 
					@PathParam("dunningSettingsCode") String dunningSettingsCode,
					@PathParam("agentEmailItem") String agentEmailItem);

	@DELETE
	@Path("/{dunningSettingsCode}/{agentEmailItem}")
	@Operation(summary = "delete an existing Dunning Collection Management",
    tags = {"Dunning Collection Management"},
    description = "Update an existing Dunning Collection Management",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning Collection Management successfully updated"),
    }) 
	
	Response delete(@PathParam("dunningSettingsCode") String dunningSettingsCode, @PathParam("agentEmailItem") String agentEmailItem);
	
}
