package org.meveo.apiv2.dunning.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningPolicy;
import org.meveo.apiv2.dunning.DunningPolicyInput;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/dunning/dunningPolicy")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface DunningPolicyResource {

    @POST
    @Operation(summary = "Create new Dunning policy",
            tags = {"Dunning"},
            description = "Create new Dunning policy",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning policy successfully created"),
                    @ApiResponse(responseCode = "404",
                            description = "Dunning policy does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "DunningLevel creation is failed")
            })
    Response create(@Parameter(description = "Dunning policy to create", required = true) DunningPolicy dunningPolicy);

    @PUT
    @Path("/{dunningPolicyId}")
    @Operation(summary = "update Dunning policy",
            tags = {"Dunning"},
            description = "update Dunning policy",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Dunning policy successfully updated"),
                    @ApiResponse(responseCode = "404",
                            description = "Dunning policy does not exits")
            })
    Response update(@Parameter(description = "Dunning policy id", required = true)
                    @PathParam("dunningPolicyId") Long dunningPolicyId,
    @Parameter(description = "dunning policy to update", required = true) DunningPolicyInput dunningPolicy);

    @DELETE
    @Path("/{dunningPolicyId}")
    @Operation(summary = "delete a dunning policy",
            tags = {"Dunning"},
            description = "delete dunning policy",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Dunning policy successfully deleted"),
                    @ApiResponse(responseCode = "404",
                            description = "Dunning policy does not exits")
            })
    Response delete(@Parameter(description = "Dunning policy id", required = true)
                    @PathParam("dunningPolicyId") Long dunningPolicyId);


    @GET
    @Path("/{dunningPolicyName}")
    @Operation(summary = "Find dunning policy",
            tags = {"Dunning"},
            description = "Find dunning policy",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Dunning policy successfully retrieved"),
                    @ApiResponse(responseCode = "404",
                            description = "Dunning policy does not exits")
            })
    Response findByName(@Parameter(description = "Dunning policy name", required = true)
                    @PathParam("dunningPolicyName") String dunningPolicyName);

    @PUT
    @Path("/archive/{dunningPolicyId}")
    @Operation(summary = "Archive dunning policy",
            tags = {"Dunning"},
            description = "Archive dunning policy",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "dunning policy successfully archived"),
                    @ApiResponse(responseCode = "404",
                            description = "Dunning policy does not exist")
            })
    Response archive(@Parameter(description = "Dunning policy id", required = true)
                     @PathParam("dunningPolicyId") Long dunningPolicyId);
    @DELETE
    @Path("/policyRule/{policyRuleID}")
    @Operation(summary = "Remove policy rule",
            tags = {"PolicyRule"},
            description = "Remove policy rule",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Policy rule successfully removed"),
                    @ApiResponse(responseCode = "404",
                            description = "Policy rule does not exits")
            })
    Response removePolicyRule(@Parameter(description = "Policy rule id", required = true)
                        @PathParam("policyRuleID") Long policyRuleID);
}