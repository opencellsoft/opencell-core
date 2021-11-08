package org.meveo.apiv2.dunning.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningCollectionPlanInput;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/dunning/collectionPlan")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface DunningCollectionPlanResource {

    @POST
    @Path("renew/{id}")
    @Operation(summary = "Renew collection plan",
            tags = {"CollectionPlan"},
            description = "Renew collection plan",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Collection plan successfully renewed"),
                    @ApiResponse(responseCode = "404",
                            description = "Collection plan does not exits")
            })
    Response renew(@Parameter(description = "Collection plan id", required = true)
                   @PathParam("id") Long id,
                   @Parameter(description = "Dunning collection plan", required = true)
                           DunningCollectionPlanInput dunningCollectionPlan);
}