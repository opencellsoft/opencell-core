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

package org.meveo.apiv2.ordering.resource.openorder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.rest.PATCH;
import org.meveo.apiv2.models.ApiException;
import org.meveo.apiv2.ordering.resource.order.Order;
import org.meveo.apiv2.ordering.resource.order.Orders;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("ordering/openOrderTemplates")
@Produces({ "application/json"})
@Consumes({ "application/json"})
public interface OpenOrderTemplateResource {
    @GET
    @Operation(summary = "Return a list of orders",
            tags = { "Orders" },
            description = "Returns a list of orders with pagination feature or non integers will simulate API error conditions",
            responses = {
                    @ApiResponse(
                            headers = {
                                    @Header(name = "ETag",
                                            description = "a pseudo-unique identifier that represents the version of the data sent back.",
                                            schema = @Schema(type = "integer", format = "int64")
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
                                            schema = @Schema(type = "integer", format = "int64")
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
    @Operation(summary = "Returns the created open order template",
            tags = { "Open Orders Templates" },
            description = "Returns the newly created open order template",
            responses = {
                    @ApiResponse(
                            description = "the created open order template", content = @Content(schema = @Schema(implementation = Order.class)),
                            responseCode = "201"
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response createOrder(@Parameter(description = "order object to be created", required = true) Order order);


}
