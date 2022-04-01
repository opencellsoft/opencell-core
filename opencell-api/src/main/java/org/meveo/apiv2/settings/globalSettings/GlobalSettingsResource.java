package org.meveo.apiv2.settings.globalSettings;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
