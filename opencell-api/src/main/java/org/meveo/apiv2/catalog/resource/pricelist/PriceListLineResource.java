package org.meveo.apiv2.catalog.resource.pricelist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.catalog.PriceListLineDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/catalog/priceListLine")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PriceListLineResource {

    @POST
    @Path("")
    @Operation(summary = "Create a PriceList Line",
            tags = {"Catalog", "Price List"},
            description = "Create a new PriceList Line and link it to a Price List",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The price list line has been successfully created"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Some Linked resources are not found",
                            content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Required parameters to create a PriceList line are missing",
                            content = @Content(schema = @Schema(implementation = MissingParameterException.class))
                    )
            })
    Response create(PriceListLineDto postDto);

    @PUT
    @Path("/{priceListLineId}")
    @Operation(summary = "Update a PriceList Line",
            tags = {"Catalog", "Price List"},
            description = "Update a new PriceList Line based on its entityID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The price list line has been successfully updated"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Some Linked resources are not found",
                            content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Required parameters to create a PriceList line are missing",
                            content = @Content(schema = @Schema(implementation = MissingParameterException.class))
                    )
            })
    Response update(@PathParam("priceListLineId") Long priceListLineId, PriceListLineDto postDto);

    @DELETE
    @Path("/{priceListLineId}")
    @Operation(summary = "Delete a PriceList Line",
            tags = {"Catalog", "Price List"},
            description = "Delete a PriceList Line based on its EntityID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The price list line has been successfully deleted"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The PriceList line identified with entityID is not found",
                            content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))
                    )
            })
    Response delete(@PathParam("priceListLineId") Long priceListLineId);

}
