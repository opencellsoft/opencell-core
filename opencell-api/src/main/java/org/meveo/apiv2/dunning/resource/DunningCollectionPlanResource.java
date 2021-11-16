package org.meveo.apiv2.dunning.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.SwitchDunningCollectionPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/dunning/collectionPlan")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface DunningCollectionPlanResource {

    @POST
    @Path("/switch/{collectionPlanId}")
    @Operation(summary = "Switch collection plan",
                tags = { "CollectionPlan" },
                description = "Switch collection plan",
                responses = {
                        @ApiResponse(responseCode = "200", description = "Collection plan successfully switched"),
                        @ApiResponse(responseCode = "404", description = "Entity does not exist") })
    Response switchCollectionPlan(@Parameter(description = "Collection plan id", required = true) @PathParam("collectionPlanId") Long collectionPlanId,
            @Parameter(description = "SwitchDunningCollectionPlan input", required = true) SwitchDunningCollectionPlan switchDunningCollectionPlan);

    @POST
    @Path("/massSwitch")
    @Operation(summary = "Mass switch collection plan",
                tags = { "CollectionPlan" },
                description = "Mass switch collection plan",
                responses = {
                        @ApiResponse(responseCode = "200", description = "Mass switch success"),
                        @ApiResponse(responseCode = "404", description = "Entity does not exist") })
    Response massSwitchCollectionPlan(@Parameter(description = "MassSwitchDunningCollectionPlan input", required = true) MassSwitchDunningCollectionPlan massSwitchDunningCollectionPlan);
}