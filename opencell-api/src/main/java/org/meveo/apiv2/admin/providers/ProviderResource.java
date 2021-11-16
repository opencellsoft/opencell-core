package org.meveo.apiv2.admin.providers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.dunning.DunningTemplate;
import org.meveo.apiv2.provider.Provider;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path("/admin/providers")
@Produces({"application/json"})
@Consumes({"application/json"})
public interface ProviderResource {
    @PUT
    @Path("/{providerCode}")
    @Operation(summary = "update a Provider",
            tags = {"DunningTemplate"},
            description = "update a Provider",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Provider successfully updated"),
                    @ApiResponse(responseCode = "404",
                            description = "a related entity does not exist"),
                    @ApiResponse(responseCode = "412",
                            description = "Missing parameters"),
                    @ApiResponse(responseCode = "400",
                            description = "Provider update failed")
            })
    Response updateDunningTemplate(@Parameter(required = true, description = "provider Code") @PathParam("providerCode") String providerCode,
                                   @Parameter(required = true, description = "provider") Provider provider);
}
