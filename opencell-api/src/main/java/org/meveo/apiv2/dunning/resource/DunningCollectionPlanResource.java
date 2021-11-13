package org.meveo.apiv2.dunning.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.dunning.DunningCollectionPlanInput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/dunning/collectionPlan")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface DunningCollectionPlanResource {

    @POST
    @Path("/switch/{collectionPlanId}")
    @Operation(summary = "Switch dunning policy",
            tags = { "CollectionPlan" },
            description = "Switch dunning policy",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Collection plan successfully renewed"),
                    @ApiResponse(responseCode = "404", description = "Collection plan does not exits")
            })
    Response switchCollectionPlan(@Parameter(description = "Collection plan id", required = true) @PathParam("collectionPlanId") Long collectionPlanId,
            @Parameter(description = "Dunning collection plan", required = true) DunningCollectionPlanInput dunningCollectionPlanInput);
}