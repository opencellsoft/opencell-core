package org.meveo.apiv2.catalog.resource;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.apiv2.catalog.ImportPricePlanVersionsDto;
import org.meveo.apiv2.catalog.PricePlanMLinesDTO;

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
            tags = { "Import", "Price Plan" },
            description = "Import grid data in price versions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The price plan line successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internal error"),
                    @ApiResponse(responseCode = "404", description = "PricePlanMatrixColumn with code={columnCode} user1 does not exists."),
                    @ApiResponse(responseCode = "409", description = "A line having similar values already exists!")
            })
    Response importPricePlanMatrixLines(@Parameter(description = "input data", required = true) PricePlanMLinesDTO pricePlanMLinesDTO);

	@POST
    @Path("/pricePlanMatrixVersions/import")
    @Operation(summary = "Import price plan versions",
            tags = { "Import", "Price Plan" },
            description = "This API will import the zip file containing the list of price plan versions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The price plan versions successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "The following parameters are required or contain invalid values: fileToImport"),
                    @ApiResponse(responseCode = "500", description = "Error occured while importing price plan versions"),
            })
    Response importPricePlanMatrixVersions(@Parameter(description = "input data", required = true) ImportPricePlanVersionsDto importPricePlanVersionsDto);

	@POST
    @Path("/pricePlanMatrixVersions/export")
    @Operation(summary = "Export price plan matrix versions",
            tags = { "Price Plan Matrix" },
            description ="Export price plan matrix versions",
            responses = {
                    @ApiResponse(responseCode="200", description = "The price plan matrix versions successfully loaded"),
                    @ApiResponse(responseCode = "400", description = "Internal error"),
                    @ApiResponse(responseCode = "404", description = "PricePlanMatrixVersion with provided ids does not exists."),
                    @ApiResponse(responseCode = "409", description = "A line having similar values already exists!")
            })
    Response exportPricePlanMatrixVersions(@Parameter(description = "input data", required = true) Map<String, Object> payload);

    @POST
    @Path("/search")
    @Operation(
            summary = "search price plan using attributes information",
            description = "search price plan using attributes information",
            responses = {
                    @ApiResponse(description = "list of price plan and meta data information"
                    )}
    )
    Response search(Map<String, Object> searchInfo);
}