package org.meveo.apiv2.ordering.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.rest.PATCH;
import org.meveo.apiv2.models.ApiException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("ordering/orders")
@Produces({ "application/json"})
@Consumes({ "application/json"})
public interface OrderResource {
    @GET
    @Operation(summary = "Return a list of orders",
            tags = { "Orders" },
            description = "Returns a list of orders with pagination feature or non integers will simulate API error conditions",
            responses = {
                    @ApiResponse(
                            headers = {
                                    @Header(name = "ETag",
                                            description = "a pseudo-unique identifier that represents the version of the data sent back.",
                                            schema = @Schema(implementation = Integer.class)
                                    )
                            },
                            description = "list of orders", content = @Content(schema = @Schema(implementation = Orders.class))
                    ),@ApiResponse(responseCode = "304",
                    description = "Not Modified, Returned to the client when the cached copy of a particular file is up to date with the server"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response getOrders(@DefaultValue("0") @QueryParam("offset") Long offset, @DefaultValue("50") @QueryParam("limit") Long limit,
            @QueryParam("sort") String sort, @QueryParam("orderBy") String orderBy, @QueryParam("filter") String filter,
            @Context Request request);


    @GET
    @Path("/{id}")
    @Operation(summary = "Return an order",
            tags = { "Orders" },
            description = "Returns a single order",
            responses = {
                    @ApiResponse(
                            headers = {
                                    @Header (name = "ETag",
                                            description = "a pseudo-unique identifier that represents the version of the data sent back",
                                            schema = @Schema(implementation = Integer.class)
                                    )
                            },
                            description = "the searched order", content = @Content(schema = @Schema(implementation = Order.class))
                    ),
                    @ApiResponse(responseCode = "304",
                            description = "Not Modified, Returned to the client when the cached copy of a particular resource is up to date with the server"),
                    @ApiResponse(responseCode = "404", description = "order not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response getOrder(@Parameter(description = "id of the order", required = true) @PathParam("id") Long id,
            @Context Request request);


    @POST
    @Operation(summary = "Returns the created order",
            tags = { "Orders" },
            description = "Returns the created order",
            responses = {
                    @ApiResponse(
                            description = "the created order", content = @Content(schema = @Schema(implementation = Order.class)),
                            responseCode = "201"
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response createOrder(@Parameter(description = "order object to be created", required = true) Order order);

    @PUT
    @Path("/{id}")
    @Operation(summary = "update an existing order",
            tags = { "Orders" },
            description = "update an existing order, and returns the updated order",
            responses = {
                    @ApiResponse(
                            description = "the updated order", content = @Content(schema = @Schema(implementation = Order.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "404", description = "order not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response updateOrder(
            @Parameter(description = "id of the order to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "order object to be update", required = true) Order order);

    @PATCH
    @Path("/{id}")
    @Operation(summary = "partially update an existing order",
            tags = { "Orders" },
            description = "update an existing order with a set of changes to apply to the order, and returns the updated order",
            responses = {
                    @ApiResponse(
                            description = "the updated order", content = @Content(schema = @Schema(implementation = Order.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "404", description = "order not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response patchOrder(
            @Parameter(description = "id of the order to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "order object to be update", required = true) Order order);


    @DELETE
    @Path("/{id}")
    @Operation(summary = "delete an existing order",
            tags = { "Orders" },
            description = "delete an existing order, and returns the deleted order",
            responses = {
                    @ApiResponse(
                            description = "the deleted order", content = @Content(schema = @Schema(implementation = Order.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "order not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response deleteOrder(
            @Parameter(description = "id of the order to delete", required = true) @PathParam("id") Long id);

}
