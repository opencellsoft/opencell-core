package org.meveo.apiv2.dunning.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningCollectionPlanStatus;

@Path("/dunning/collectionPlanStatus")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface CollectionPlanStatusResource {

	@POST
	@Operation(summary = "Create new Collection plan status",
    tags = {"Collection Plan Status"},
    description = "Create new collection plan status",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "collection plan status successfully created"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning with the same code exist")
    }) 
	Response create(@Parameter(required = true) DunningCollectionPlanStatus collectionPlanStatus);
	
	@PUT
	@Path("/{id}")
	@Operation(summary = "Update an existing Collection plan status", tags = {
			"Collection Plan Status" }, description = "Update new collection plan status without its dunning settings", responses = {
			@ApiResponse(responseCode = "200", description = "collection plan status successfully updated"),
			@ApiResponse(responseCode = "404", description = "Collection with dunning code parameter and status doesn't exist") })
	Response update(@Parameter(required = true) DunningCollectionPlanStatus collectionPlanStatus, @PathParam("id") Long id);

	@DELETE
	@Path("/{id}")
	@Operation(summary = "Delete an existing Collection plan status", tags = {
			"Collection Plan Status" }, description = "Delete new collection plan status without its dunning settings", responses = {
			@ApiResponse(responseCode = "200", description = "collection plan status successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Collection with dunning code parameter and status doesn't exist") })
	Response delete(@PathParam("id") Long id);
	
}
