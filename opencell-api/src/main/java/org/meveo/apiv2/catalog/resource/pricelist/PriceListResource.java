package org.meveo.apiv2.catalog.resource.pricelist;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.pricelist.PriceListStatusEnum;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * PriceList Endpoints
 *
 * @author a.rouaguebe
 * @since 15.0
 *
 */
@Path("/catalog/priceList")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PriceListResource {

    @POST
    @Path("/{priceListCode}/status/{newStatus}")
    @Operation(summary = "Update PriceList status",
            tags = {"Catalog", "Price List"},
            description = "Update the price list status identified by the 'priceListCode' parameter",
            responses = {
                    @ApiResponse(responseCode = "200", description = "The price list status has been successfully udated"),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The price list with 'priceListCode' has not been found",
                            content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))
                    )
            })
    Response updateStatus(@PathParam("priceListCode") String priceListCode, @PathParam("newStatus") PriceListStatusEnum newStatus);
}
