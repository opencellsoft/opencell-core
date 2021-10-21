package org.meveo.apiv2.dunning.action;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningAction;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/dunning/dunningaction")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningActionResource {

    @POST
    @Path("/")
    @Operation(summary = "Create a new Dunning Action",
            tags = {"DunningAction"},
            description = "Create a new Dunning Action",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Action successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "Dunning Action with code in the path doesn't exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "DunningAction creation is failed")
            })
    Response createDunningAction(@Parameter(required = true, description = "dunning Action") DunningAction dunningAction);
}
