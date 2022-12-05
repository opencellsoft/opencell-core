package org.meveo.apiv2.settings.globalSettings;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.apiv2.settings.GlobalSettingsInput;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/setting/globalSettings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface GlobalSettingsResource {

    @POST
    @Operation(summary = "Global settings",
            tags = {"Post", "Global settings"},
            description = "Create Global settings",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Global settings was successfully created"),
                    @ApiResponse(responseCode = "400",
                            description = "Bad Request")
            })
    Response create(@Parameter(description = "Global settings", required = true) GlobalSettingsInput input);
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "Global settings",
            tags = {"Post", "Global settings"},
            description = "Update Global settings",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Global settings was successfully updated"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "The QuotesSettings does not exist")
            })
    Response update(@Parameter(description = "Global settings id", required = true) @PathParam("id") Long id,
            @Parameter(description = "Global settings", required = true) GlobalSettingsInput input);
}
