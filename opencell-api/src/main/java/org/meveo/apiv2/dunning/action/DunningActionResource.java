package org.meveo.apiv2.dunning.action;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningAction;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/dunning/dunningaction")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningActionResource {

    @GET
    @Path("/{code}")
    @Operation(summary = "Retrieve a Dunning Action by code",
            tags = {"DunningAction"},
            description = "Retrieve a Dunning Action by code",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "success retrieve of dunning action"),
                    @ApiResponse(responseCode = "404",
                            description = "Dunning Action with code in the path doesn't exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters")
            })
    Response getDunningAction(@Parameter(required = true, description = "dunning Action code") @PathParam("code") String code);

    @POST
    @Path("/")
    @Operation(summary = "Create a new Dunning Action",
            tags = {"DunningAction"},
            description = "Create a new Dunning Action",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Action successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "DunningAction creation is failed")
            })
    Response createDunningAction(@Parameter(required = true, description = "dunning Action") DunningAction dunningAction);

    @PUT
    @Path("/{dunningActionId}")
    @Operation(summary = "Update a Dunning Action",
            tags = {"DunningAction"},
            description = "Update a Dunning Action",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Action successfully updated"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "DunningAction creation is failed")
            })
    Response updateDunningAction(@Parameter(required = true, description = "dunning Action id") @PathParam("dunningActionId") Long dunningActionId,
                                 @Parameter(required = true, description = "dunning Action") DunningAction dunningAction);

    @DELETE
    @Path("/{dunningActionId}")
    @Operation(summary = "Delete a Dunning Action",
            tags = {"DunningAction"},
            description = "Delete a Dunning Action",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Action successfully deleted"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "DunningAction creation is failed")
            })
    Response deleteDunningAction(@Parameter(required = true, description = "dunning Action id") @PathParam("dunningActionId") Long dunningActionId);
}
