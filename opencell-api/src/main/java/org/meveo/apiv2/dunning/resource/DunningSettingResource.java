package org.meveo.apiv2.dunning.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.DunningSettings;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/payment/dunning")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningSettingResource {

	@POST
	@Operation(summary = "Create new Dunning Setting",
    tags = {"Dunning"},
    description = "Create exceptional billing run",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning settings successfully created"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning with the same code exist")
    }) 
	Response create(@Parameter(required = true) DunningSettings dunningSettings);
	
	@PUT
	@Path("/{dunningId}")
	@Operation(summary = "Update an existing Dunning Setting",
    tags = {"Dunning"},
    description = "Update an existing Dunning settings",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning settings successfully updated"),
            @ApiResponse(responseCode = "404",
                    description = "new code for dunning settings already exist")
    }) 
	Response update(@Parameter(required = true) DunningSettings dunningSettings, @PathParam("dunningId") Long dunningCode);
	
	@DELETE
	@Path("/{dunningId}")
	@Operation(summary = "Delete existing Dunning Setting",
    tags = {"Dunning"},
    description = "Delete Existing dunning settings",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning settings successfully deleted"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning setting with id in the path doesn't exist")
    }) 
	Response delete(@Parameter(required = true, description = "id of removed dunning settings")  @PathParam("dunningId") Long dunningCode);
	
	@GET
	@Path("/{dunningCode}")
	@Operation(summary = "Get existing Dunning Setting",
    tags = {"Dunning"},
    description = "Get Existing dunning settings",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning settings successfully retrivied"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning setting with code in the path doesn't exist")
    }) 
	Response findByCode(@Parameter(required = true, description = "code dunning settings")  @PathParam("dunningCode") String dunningCode);

	@POST
	@Path("/{dunningCode}/duplication")
	@Operation(summary = "Duplicate an existing Dunning Setting",
    tags = {"Dunning"},
    description = "Duplicate Existing dunning settings",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "dunning settings successfully retrivied"),
            @ApiResponse(responseCode = "404",
                    description = "Dunning setting with code in the path doesn't exist")
    }) 
	Response duplicate(@Parameter(required = true, description = "code dunning settings")  @PathParam("dunningCode") String dunningCode);
	
	
}
