package org.meveo.apiv2.ordering.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.rest.PATCH;
import org.meveo.apiv2.models.Product;
import org.meveo.apiv2.models.Products;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("ordering/products")
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
                                    schema = @Schema(implementation = String.class)
                            )
                    },
                    description = "list of products", content = @Content(schema = @Schema(implementation = Products.class))
            ), @ApiResponse(responseCode = "400", description = "Invalid parameters supplied")
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
                                            schema = @Schema(implementation = String.class)
                                    )
                            },
                            description = "the searched product", content = @Content(schema = @Schema(implementation = Product.class))
                    ),
                    @ApiResponse(responseCode = "304", description = "the resource has not been modified since the last request"),
                    @ApiResponse(responseCode = "404", description = "product not found")
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
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied")
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
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied"),
                    @ApiResponse(responseCode = "404", description = "product not found")
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
                    @ApiResponse(responseCode = "400", description = "Invalid inputs supplied"),
                    @ApiResponse(responseCode = "404", description = "product not found")
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
                    @ApiResponse(responseCode = "404", description = "product not found")
            })
    Response deleteProduct(
            @Parameter(description = "id of the product to delete", required = true) @PathParam("id") Long id);
}
