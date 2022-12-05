package org.meveo.apiv2.generic;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/version")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Version {
	
    @Operation(summary = "Get versions information about OpenCell components",
            tags = { "Generic" },
            description ="return a list of OpenCell's components version information",
            responses = {
                    @ApiResponse(responseCode="200", description = "resource successfully updated but not content exposed except the hypermedia")
            })
    @GET
    @Path("/")
    Response getVersions();
}
