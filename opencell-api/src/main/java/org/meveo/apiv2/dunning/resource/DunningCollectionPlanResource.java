package org.meveo.apiv2.dunning.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.AvailablePoliciesInput;
import org.meveo.apiv2.dunning.DunningCollectionPlanPause;
import org.meveo.apiv2.dunning.DunningCollectionPlanStop;
import org.meveo.apiv2.dunning.DunningLevelInstanceInput;
import org.meveo.apiv2.dunning.DunningMassSwitchInput;
import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.RemoveLevelInstanceInput;
import org.meveo.apiv2.dunning.SwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.UpdateLevelInstanceInput;

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

    @POST
    @Path("/checkMassSwitch")
    @Operation(summary = "Check eligible collection for switch",
            tags = { "CollectionPlan" },
            description = "Check eligible collection for switch",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Check successfully passed"),
                    @ApiResponse(responseCode = "404", description = "Entity does not exits")
            })
    Response checkMassSwitch(@Parameter(description = "Collection plans to check", required = true)
                                     DunningMassSwitchInput massSwitchInput);

    @POST
    @Path("/availableDunningPolicies")
    @Operation(summary = "List of available dunning policies",
            tags = { "CollectionPlan" },
            description = "List of available dunning policies",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List successfully returned"),
                    @ApiResponse(responseCode = "404", description = "Entity does not exits")
            })
    Response availableDunningPolicies(@Parameter(description = "available dunning policies input", required = true)
                                              AvailablePoliciesInput availablePoliciesInput);
    
    @POST
	@Path("/pause/{id}")
	@Operation(summary = "Pause Collection plan",
    tags = {"Collection Plan"},
    description = "Pause collection plan",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "collection plan successfully paused"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exits")
    }) 
	Response pauseCollectionPlan(@Parameter(required = true) DunningCollectionPlanPause dunningCollectionPlan, @PathParam("id") Long id);

	@POST
	@Path("/stop/{id}")
	@Operation(summary = "Stop Collection plan",
    tags = {"Collection Plan"},
    description = "Stop collection plan",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "collection plan successfully paused"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exits")
    }) 
	Response stopCollectionPlan(@Parameter(required = true) DunningCollectionPlanStop dunningCollectionPlan, @PathParam("id") Long id);

	@POST
	@Path("/resume/{id}")
	@Operation(summary = "Resume Collection plan",
    tags = {"Collection Plan"},
    description = "Resume collection plan",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "collection plan successfully paused"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exits")
    })
	Response resumeCollectionPlan(@PathParam("id") Long id);

	@POST
    @Path("/removeDunningLevelInstance")
    @Operation(summary = "Remove DunningLevelInstance",
    tags = {"Collection Plan"},
    description = "Remove DunningLevelInstance",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "Remove action success"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exist")
    })
    Response removeDunningLevelInstance(@Parameter(required = true) RemoveLevelInstanceInput removeLevelInstanceInput);

	@POST
    @Path("/addDunningLevelInstance")
    @Operation(summary = "Add DunningLevelInstance",
    tags = {"Collection Plan"},
    description = "Add DunningLevelInstance",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "Add action success"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exist")
    })
    Response addDunningLevelInstance(@Parameter(required = true) DunningLevelInstanceInput dunningLevelInstanceInput);

	@POST
    @Path("/updateDunningLevelInstance/{levelInstanceId}")
    @Operation(summary = "Update DunningLevelInstance",
    tags = {"Collection Plan"},
    description = "Update DunningLevelInstance",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "Update action success"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exist")
    })
    Response updateDunningLevelInstance(@Parameter(required = true) UpdateLevelInstanceInput updateLevelInstanceInput, @PathParam("levelInstanceId") Long levelInstanceId);
}