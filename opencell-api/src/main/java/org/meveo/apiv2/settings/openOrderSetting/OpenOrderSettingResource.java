package org.meveo.apiv2.settings.openOrderSetting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.settings.OpenOrderSettingInput;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/setting/openOrderSettings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface OpenOrderSettingResource {
    @POST
    @Operation(summary = "Create Open Order settings",
            tags = {"Post"},
            description = "Create Open Order settings",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Open Order settings was successfully created"),
                    @ApiResponse(responseCode = "400",
                            description = "Bad Request")
            })
    Response create(@Parameter(description = "Open Order settings", required = true) OpenOrderSettingInput input);

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update Open Order settings",
            tags = {"Put"},
            description = "Update Open Order settings",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Open Order settings was successfully updated"),
					@ApiResponse(responseCode = "400",
							description = "Bad Request"),
                    @ApiResponse(responseCode = "404",
                            description = "Following Open Order settings does not exist : {OpenOrderSetting ids}")
            })
    Response update(@Parameter(description = "contain the code of Open Order settings te be updated by its id", required = true) @PathParam("id") Long id,
            @Parameter(description = "Open Order settings", required = true) OpenOrderSettingInput input);


}
