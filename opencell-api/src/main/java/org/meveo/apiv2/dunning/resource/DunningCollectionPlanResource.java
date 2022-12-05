package org.meveo.apiv2.dunning.resource;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import org.meveo.apiv2.dunning.AvailablePoliciesInput;
import org.meveo.apiv2.dunning.DunningActionInstanceInput;
import org.meveo.apiv2.dunning.DunningCollectionPlanPause;
import org.meveo.apiv2.dunning.DunningCollectionPlanStop;
import org.meveo.apiv2.dunning.DunningLevelInstanceInput;
import org.meveo.apiv2.dunning.DunningMassSwitchInput;
import org.meveo.apiv2.dunning.MassStopDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassPauseDunningCollectionPlan;
import org.meveo.apiv2.dunning.MassSwitchDunningCollectionPlan;
import org.meveo.apiv2.dunning.RemoveActionInstanceInput;
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
	@Path("/massPause")
	@Operation(summary = "Mass Pause list of Collection plan",
    tags = {"Collection Plan"},
    description = "Mass Pause list of collection plan",
    responses = {
            @ApiResponse(responseCode = "200", description = "list of collection plan successfully paused"),
            @ApiResponse(responseCode = "404", description = "Entity does not exits") }) 
	Response massPauseCollectionPlan(@Parameter(description = "MassPauseDunningCollectionPlan input", required = true) MassPauseDunningCollectionPlan massPauseDunningCollectionPlan);

    @POST
	@Path("/stop/{id}")
	@Operation(summary = "Stop Collection plan",
    tags = {"Collection Plan"},
    description = "Stop collection plan",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "collection plan successfully stoped"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exits")
    }) 
	Response stopCollectionPlan(@Parameter(required = true) DunningCollectionPlanStop dunningCollectionPlan, @PathParam("id") Long id);

	@POST
	@Path("/massStop")
	@Operation(summary = "Mass Stop list of Collection plan",
    tags = {"Collection Plan"},
    description = "Mass Stop collection plan",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "list of collection plan successfully stoped"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exits")
    }) 
    Response massStopCollectionPlan(@Parameter(description = "MassStopDunningCollectionPlan input", required = true) MassStopDunningCollectionPlan massStopDunningCollectionPlan);

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

	@PUT
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

	@POST
    @Path("/addDunningActionInstance")
    @Operation(summary = "Add DunningActionInstance",
    tags = {"Collection Plan"},
    description = "Add DunningActionInstance",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "Add action success"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exist")
    })
    Response addDunningActionInstance(@Parameter(required = true) DunningActionInstanceInput dunningActionInstanceInput);

	@POST
    @Path("/removeDunningActionInstance")
    @Operation(summary = "Remove DunningActionInstance",
    tags = {"Collection Plan"},
    description = "Remove DunningActionInstance",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "Remove action success"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exist")
    })
    Response removeDunningActionInstance(@Parameter(required = true) RemoveActionInstanceInput removeActionInstanceInput);

	@PUT
    @Path("/updateDunningActionInstance/{actionInstanceId}")
    @Operation(summary = "Update DunningActionInstance",
    tags = {"Collection Plan"},
    description = "Update DunningActionInstance",
    responses = {
            @ApiResponse(responseCode = "200",
                   description = "Update action success"),
            @ApiResponse(responseCode = "404",
                    description = "Entity does not exist")
    })
    Response updateDunningActionInstance(@Parameter(required = true) DunningActionInstanceInput dunningActionInstanceInput, @PathParam("actionInstanceId") Long actionInstanceId);
}