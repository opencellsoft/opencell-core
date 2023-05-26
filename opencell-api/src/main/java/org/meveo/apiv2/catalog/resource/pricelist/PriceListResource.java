package org.meveo.apiv2.catalog.resource.pricelist;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.catalog.PriceList;
import org.meveo.model.pricelist.PriceListStatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
	@Operation(summary = "Create new PriceList", tags = { "PriceList" }, description = "Create new Price List", responses = {
			@ApiResponse(responseCode = "200", description = "Price List successfully created"),
			@ApiResponse(responseCode = "400", description = "Price List creation is failed") })
	Response create(@Parameter(required = true) PriceList priceList);

	@PUT
	@Path("/{priceListCode}")
	@Operation(summary = "Update an existing Price List", tags = { "PriceList" }, description = "Update a Price List", responses = {
			@ApiResponse(responseCode = "200", description = "Price List successfully updated"),
			@ApiResponse(responseCode = "404", description = "Price List successfully updated"),
			@ApiResponse(responseCode = "400", description = "Price List with given code does not exist") })
	Response update(@Parameter(required = true) PriceList priceList, @Parameter(required = true, description = "Price List code to update") @PathParam("priceListCode") String priceListCode); 

	@DELETE
	@Path("/{priceListCode}")
	@Operation(summary = "Delete existing Price List", tags = { "PriceList" }, description = "Delete Existing Price List", responses = {
			@ApiResponse(responseCode = "200", description = "Price List successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Price List with id in the path doesn't exist") })
	Response delete(@Parameter(required = true, description = "Price List code to delete") @PathParam("priceListCode") String priceListCode);

    @PUT
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
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "PriceList without lines cannot be activated",
                            content = @Content(schema = @Schema(implementation = BusinessApiException.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Cannot activate PriceList without lines having a price or active PricePlan",
                            content = @Content(schema = @Schema(implementation = BusinessApiException.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Cannot activate PriceList without application rules",
                            content = @Content(schema = @Schema(implementation = BusinessApiException.class))
                    )
            })
    Response updateStatus(@PathParam("priceListCode") String priceListCode, @PathParam("newStatus") PriceListStatusEnum newStatus);

	@POST
	@Path("/{priceListCode}/duplicate")
	Response duplicate(@PathParam("priceListCode") String priceListCode);
}
