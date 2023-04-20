package org.meveo.apiv2.customtable;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/customTable")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CustomTableResource {
    @POST
    @Path("/export/{customTableCode}/{fileFormat}")
    @Operation(summary = "Create an export Custom Table",
            tags = {"Post"},
            description = "Create an export Custom Table",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "export Custom Table is successfully created"),
                    @ApiResponse(responseCode = "400",
                    description = "fileFormat is incorrect"),
                    @ApiResponse(responseCode = "403",
                    description = "user has not habilitation to create an export Custom Table"),
                    @ApiResponse(responseCode = "404",
                            description = "The custom table code does not exist")
            })
    Response export(@Parameter(description = "the entity name", required = true) @PathParam("customTableCode") String customTableCode,
            @Parameter(description = "file format", required = true) @PathParam("fileFormat") String fileFormat);

}
