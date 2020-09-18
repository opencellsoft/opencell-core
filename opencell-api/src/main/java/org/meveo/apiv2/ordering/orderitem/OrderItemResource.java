/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.ordering.orderitem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.rest.PATCH;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.ordering.orderItem.OrderItem;
import org.meveo.apiv2.ordering.orderItem.OrderItems;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("order-items")
@Produces({ "application/json"})
@Consumes({ "application/json"})
public interface OrderItemResource {
    @GET
    @Operation(summary = "Return a list of order-items",
            tags = { "Order-items" },
            description = "Returns a list of order-items with pagination feature or non integers will simulate API error conditions",
            responses = {
                    @ApiResponse(
                            headers = {
                                    @Header(name = "ETag",
                                            description = "a pseudo-unique identifier that represents the version of the data sent back.",
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            description = "list of order-items", content = @Content(schema = @Schema(implementation = OrderItems.class))
                    ),@ApiResponse(responseCode = "304",
                    description = "Not Modified, Returned to the client when the cached copy of a particular file is up to date with the server"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response getOrderItems(@DefaultValue("0") @QueryParam("offset") Long offset, @DefaultValue("50") @QueryParam("limit") Long limit,
            @QueryParam("sort") String sort, @QueryParam("orderBy") String orderBy, @QueryParam("filter") String filter,
            @Context Request request);


    @GET
    @Path("/{id}")
    @Operation(summary = "Return an order-Item",
            tags = { "Order-items" },
            description = "Returns a single order-Item",
            responses = {
                    @ApiResponse(
                            headers = {
                                    @Header (name = "ETag",
                                            description = "a pseudo-unique identifier that represents the version of the data sent back",
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            description = "the searched order-Item", content = @Content(schema = @Schema(implementation = OrderItem.class))
                    ),
                    @ApiResponse(responseCode = "304",
                            description = "Not Modified, Returned to the client when the cached copy of a particular resource is up to date with the server"),
                    @ApiResponse(responseCode = "404", description = "order-Item not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response getOrderItem(@Parameter(description = "id of the order-Item", required = true) @PathParam("id") Long id,
            @Context Request request);


    @POST
    @Operation(summary = "Returns the created order-item",
            tags = { "Order-items" },
            description = "Returns the created order-item",
            responses = {
                    @ApiResponse(
                            description = "the created order-item", content = @Content(schema = @Schema(implementation = OrderItem.class)),
                            responseCode = "201"
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response createOrderItem(@Parameter(description = "order-item object to be created", required = true) OrderItem orderItem);

    @PUT
    @Path("/{id}")
    @Operation(summary = "update an existing order-item",
            tags = { "Order-items" },
            description = "update an existing order-item, and returns the updated order-item",
            responses = {
                    @ApiResponse(
                            description = "the updated order-item", content = @Content(schema = @Schema(implementation = OrderItem.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "404", description = "order-item not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response updateOrderItem(
            @Parameter(description = "id of the order-item to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "order-item object to be update", required = true) OrderItem orderItem);

    @PATCH
    @Path("/{id}")
    @Operation(summary = "partially update an existing order-item",
            tags = { "Order-items" },
            description = "update an existing order-item with a set of changes to apply to the order-item, and returns the updated order-item",
            responses = {
                    @ApiResponse(
                            description = "the updated order-item", content = @Content(schema = @Schema(implementation = OrderItem.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "404", description = "order-item not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response patchOrderItem(
            @Parameter(description = "id of the order-item to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "order-item object to be update", required = true) OrderItem orderItem);


    @DELETE
    @Path("/{id}")
    @Operation(summary = "delete an existing order-item",
            tags = { "Order-items" },
            description = "delete an existing order-item, and returns the deleted order-item",
            responses = {
                    @ApiResponse(
                            description = "the deleted order-item", content = @Content(schema = @Schema(implementation = OrderItem.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "order-item not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response deleteOrderItem(
            @Parameter(description = "id of the order-item to delete", required = true) @PathParam("id") Long id);

    @DELETE
    @Operation(summary = "delete more than one order-item",
            tags = { "Order-items" },
            description = "delete more than one order-item",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "the order-items deletion went ok ", content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "order-item not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response deleteOrderItem(
            @Parameter(description = "ids of order-items to delete", required = true) @QueryParam("id") List<Long> ids);
}
