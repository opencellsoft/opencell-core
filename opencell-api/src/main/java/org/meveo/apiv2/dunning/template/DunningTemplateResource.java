package org.meveo.apiv2.dunning.template;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningTemplate;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/dunning/dunningtemplate")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningTemplateResource {
    @POST
    @Path("/")
    @Operation(summary = "Create a new Dunning Template",
            tags = {"DunningAction"},
            description = "Create a new Dunning Template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Template successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Dunning Template creation is failed")
            })
    Response createDunningTemplate(@Parameter(required = true, description = "dunning Template") DunningTemplate dunningTemplate);

    @DELETE
    @Path("/{dunningTemplateId}")
    @Operation(summary = "Delete a Dunning Template",
            tags = {"DunningAction"},
            description = "Delete a Dunning Template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Template successfully deleted"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Dunning Template creation is failed")
            })
    Response deleteDunningTemplate(@Parameter(required = true, description = "dunning Template id") @PathParam("dunningTemplateId") Long dunningTemplateId);

}
