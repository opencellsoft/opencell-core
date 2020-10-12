package org.meveo.apiv2.generic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.models.Resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/notImplemented")
public class NotYetImplementedResource {
    @GET
    @Operation(summary = "This service represent a non implemented resource",
            tags = { "NotImplementedResource" },
            responses = {
                    @ApiResponse(
                            description = "a generic resource", content = @Content(schema = @Schema(implementation = Resource.class))
                    )
            })
    public Response getResource(){
        return Response.seeOther(URI.create("/openapi.json")).entity("not yet implemented resource !").build();
    }
}
