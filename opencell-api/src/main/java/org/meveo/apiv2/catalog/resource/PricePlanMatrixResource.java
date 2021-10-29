package org.meveo.apiv2.catalog.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/catalog/priceManagement")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PricePlanMatrixResource {

	@POST
    @Path("/pricePlanMatrixLines/import")
    @Operation(summary = "Import grid data in price versions",
            tags = { "Price Plan" },
            description ="Import grid data in price versions",
            responses = {
                    @ApiResponse(responseCode="200", description = "the price plan line successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internal error"),
                    @ApiResponse(responseCode = "404", description = "PricePlanMatrixColumn with code={columnCode} user1 does not exists."),
                    @ApiResponse(responseCode = "409", description = "A line having similar values already exists!")
            })
    Response importPricePlanMatrixLines(@Parameter(description = "input data", required = true) PricePlanMLinesDTO pricePlanMLinesDTO);

}