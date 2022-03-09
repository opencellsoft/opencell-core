package org.meveo.apiv2.settings.openOrderSetting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.settings.OpenOrderSettingInput;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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


}
