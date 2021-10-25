package org.meveo.apiv2.dunning.resource;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningPolicy;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
}