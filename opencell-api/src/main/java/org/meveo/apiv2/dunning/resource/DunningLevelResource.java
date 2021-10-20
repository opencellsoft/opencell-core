package org.meveo.apiv2.dunning.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
	@Path("/{dunningId}")
	@Operation(summary = "Update an existing Dunning Level", tags = { "Dunning" }, description = "Update a dunning level", responses = {
			@ApiResponse(responseCode = "200", description = "dunning level successfully updated"),
			@ApiResponse(responseCode = "404", description = "dunning level successfully updated"),
			@ApiResponse(responseCode = "400", description = "dunning level with given code does not exist") })
	Response update(@Parameter(required = true) DunningLevel dunningLevel);
}
