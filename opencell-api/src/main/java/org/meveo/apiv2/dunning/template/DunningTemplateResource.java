package org.meveo.apiv2.dunning.template;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningTemplate;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/dunning/dunningtemplate")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface DunningTemplateResource {
    @POST
    @Path("/")
    @Operation(summary = "Create a new Dunning Template",
            tags = {"DunningTemplate"},
            description = "Create a new Dunning Template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Template successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Dunning Template creation failed")
            })
    Response createDunningTemplate(@Parameter(required = true, description = "dunning Template") DunningTemplate dunningTemplate);

    @DELETE
    @Path("/{dunningTemplateId}")
    @Operation(summary = "Delete a Dunning Template",
            tags = {"DunningTemplate"},
            description = "Delete a Dunning Template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Template successfully deleted"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Dunning Template deletion failed")
            })
    Response deleteDunningTemplate(@Parameter(required = true, description = "dunning Template id") @PathParam("dunningTemplateId") Long dunningTemplateId);

    @POST
    @Path("/{dunningTemplateId}/duplication")
    @Operation(summary = "duplicate a Dunning Template",
            tags = {"DunningTemplate"},
            description = "duplicate a Dunning Template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Template successfully duplicated"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Dunning Template duplication failed")
            })
    Response duplicateDunningTemplate(@Parameter(required = true, description = "dunning Template id") @PathParam("dunningTemplateId") Long dunningTemplateId);

    @PUT
    @Path("/{dunningTemplateId}")
    @Operation(summary = "update a Dunning Template",
            tags = {"DunningTemplate"},
            description = "update a Dunning Template",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning Template successfully updated"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Dunning Template update failed")
            })
    Response updateDunningTemplate(@Parameter(required = true, description = "dunning Template id") @PathParam("dunningTemplateId") Long dunningTemplateId,
                                   @Parameter(required = true, description = "dunning Template") DunningTemplate dunningTemplate);

}
