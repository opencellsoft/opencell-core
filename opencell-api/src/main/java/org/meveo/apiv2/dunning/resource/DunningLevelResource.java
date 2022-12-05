package org.meveo.apiv2.dunning.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.meveo.apiv2.dunning.DunningLevel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/dunning/dunningLevel")
@Produces({ "application/json" })
@Consumes({ "application/json" })
public interface DunningLevelResource {

	@POST
	@Operation(summary = "Create new Dunning Level", tags = { "Dunning" }, description = "Create new dunning level", responses = {
			@ApiResponse(responseCode = "200", description = "dunning level successfully created"),
			@ApiResponse(responseCode = "400", description = "dunning level creation is failed") })
	Response create(@Parameter(required = true) DunningLevel dunningLevel);

	@PUT
	@Path("/{dunningLevelId}")
	@Operation(summary = "Update an existing Dunning Level", tags = { "Dunning" }, description = "Update a dunning level", responses = {
			@ApiResponse(responseCode = "200", description = "dunning level successfully updated"),
			@ApiResponse(responseCode = "404", description = "dunning level successfully updated"),
			@ApiResponse(responseCode = "400", description = "dunning level with given code does not exist") })
	Response update(@Parameter(required = true) DunningLevel dunningLevel, @PathParam("dunningLevelId") Long dunningLevelCode);

	@DELETE
	@Path("/{dunningLevelId}")
	@Operation(summary = "Delete existing Dunning level", tags = { "Dunning" }, description = "Delete Existing dunning level", responses = {
			@ApiResponse(responseCode = "200", description = "dunning level successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Dunning level with id in the path doesn't exist") })
	Response delete(@Parameter(required = true, description = "Dunning level id to delete") @PathParam("dunningLevelId") Long dunningLevelId);

	@GET
	@Path("/{dunningLevelCode}")
	@Operation(summary = "Get existing Dunning Level",
    tags = {"Dunning"},
    description = "Get Existing dunning Level",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning level successfully retrivied"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning level with given code does not exist")
    }) 
	Response findByCode(@Parameter(required = true, description = "code dunning level")  @PathParam("dunningLevelCode") String dunningLevelCode);

}
