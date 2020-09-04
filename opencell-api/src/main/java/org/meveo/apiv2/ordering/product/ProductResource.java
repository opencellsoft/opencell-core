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

package org.meveo.apiv2.ordering.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.rest.PATCH;
import org.meveo.apiv2.models.ApiException;

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

@Path("products")
@Produces({ "application/json"})
@Consumes({ "application/json"})
public interface ProductResource {
    @GET
    @Operation(summary = "Return a list of products",
            tags = { "Products" },
            description = "Returns a list of products with pagination feature or non integers will simulate API error conditions",
            responses = {
            @ApiResponse(
                    headers = {
                            @Header (name = "ETag",
                                    description = "a pseudo-unique identifier that represents the version of the data sent back.",
                                    schema = @Schema(type = "integer", format = "int64")
                            )
                    },
                    description = "list of products", content = @Content(schema = @Schema(implementation = Products.class))
            ),
                    @ApiResponse(responseCode = "304",
                    description = "Not Modified, Returned to the client when the cached copy of a particular product is up to date with the server"),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    Response getProducts(@DefaultValue("0") @QueryParam("offset") Long offset, @DefaultValue("50") @QueryParam("limit") Long limit,
            @QueryParam("sort") String sort, @QueryParam("orderBy") String orderBy, @QueryParam("filter") String filter,
            @Context Request request);

    @GET
    @Path("/{id}")
    @Operation(summary = "Return a product",
            tags = { "Products" },
            description = "Returns a single product",
            responses = {
                    @ApiResponse(
                            headers = {
                                    @Header (name = "ETag",
                                            description = "a pseudo-unique identifier that represents the version of the data sent back",
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            description = "the searched product", content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "304",
                            description = "Not Modified, Returned to the client when the cached copy of a particular product is up to date with the server"),
                    @ApiResponse(responseCode = "404", description = "product not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response getProduct(@Parameter(description = "id of the product", required = true) @PathParam("id") Long id,
            @Context Request request);

    @POST
    @Operation(summary = "Returns the created product",
            tags = { "Products" },
            description = "Returns the created product",
            responses = {
                    @ApiResponse(
                            description = "the created product", content = @Content(schema = @Schema(implementation = Product.class)),
                            responseCode = "201"
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response createProduct(@Parameter(description = "product object to be created", required = true) Product product);

    @PUT
    @Path("/{id}")
    @Operation(summary = "update an existing product",
            tags = { "Products" },
            description = "update an existing product, and returns the updated product",
            responses = {
                    @ApiResponse(
                            description = "the updated product", content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "404", description = "product not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response updateProduct(
            @Parameter(description = "id of the product to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "product object to be update", required = true) Product product);

    @PATCH
    @Path("/{id}")
    @Operation(summary = "partially update an existing product",
            tags = { "Products" },
            description = "update an existing product with a set of changes to apply to the product, and returns the updated product",
            responses = {
                    @ApiResponse(
                            description = "the updated product", content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied", content = @Content(schema = @Schema(implementation = ApiException.class))),
                    @ApiResponse(responseCode = "404", description = "product not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    Response patchProduct(
            @Parameter(description = "id of the product to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "product object to be update", required = true) Product product);


    @DELETE
    @Path("/{id}")
    @Operation(summary = "delete an existing product",
            tags = { "Products" },
            description = "delete an existing product, and returns the deleted product",
            responses = {
                    @ApiResponse(
                            description = "the deleted product", content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "product not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response deleteProduct(
            @Parameter(description = "id of the product to delete", required = true) @PathParam("id") Long id);

    @DELETE
    @Operation(summary = "delete more than one product",
            tags = { "Products" },
            description = "delete more than one product, and returns the deleted products",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "the products deletion went ok ", content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "product not found", content = @Content(schema = @Schema(implementation = ApiException.class)))
            })
    Response deleteProducts(
            @Parameter(description = "ids of products to delete", required = true) @QueryParam("id") List<Long> ids);
}
