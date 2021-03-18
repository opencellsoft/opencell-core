package org.meveo.apiv2.catalog.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.apiv2.article.AccountingArticle;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.models.ApiException;
import org.meveo.model.catalog.DiscountPlan;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("/catalog/discountPlans")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DiscountPlanResource {

    @GET
    @Operation(summary = "Return a list of discountPlans", tags = {
            "Discount Plans" }, description = "Returns a list of discountPlans with pagination feature or non integers will simulate API error conditions", responses = {
            @ApiResponse(headers = {
                    @Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back.", schema = @Schema(type = "integer", format = "int64")) }, description = "list of discountPlans", content = @Content(schema = @Schema(implementation = DiscountPlan.class))),
            @ApiResponse(responseCode = "304", description = "Lists discountPlans with filtering, sorting, paging."),
            @ApiResponse(responseCode = "400", description = "Invalid parameters supplied", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getAll(
            @Parameter(description = "requestDto carries the wanted fields ex: {genericFields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @GET
    @Path("/{id}")
    @Operation(summary = "Return an discount plan", tags = { "Discount Plans" }, description = "Returns the discount plan data", responses = { @ApiResponse(headers = {
            @Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched discount Plan"),
            @ApiResponse(responseCode = "404", description = "Discount plan not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response get(@Parameter(description = "id of the discount plan", required = true) @PathParam("id") Long id);

    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to create a discount plan resource", tags = { "Discount Plans" }, description = "create new discount plan", responses = {
            @ApiResponse(responseCode = "200", description = "the entity successfully created, and the id is returned in the response"),
            @ApiResponse(responseCode = "400", description = "bad request when entity information contains an error") })
    Response create(@Parameter(description = "dto the json representation of the object", required = true) String dto);

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a resource by giving it's Id", tags = {
            "Discount Plans" }, description = "specify record id, and as body, the list of the fields to update", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response update(@Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a resource by giving it's Id", tags = {
            "Discount Plans" }, description = "specify the entity name, the record id, and as body, the list of the fields to delete", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response delete(@Parameter(description = "The id here is the database primary key of the record to delete", required = true) @PathParam("id") Long id);

    @GET
    @Path("/{id}/discountPlanItems")
    @Operation(summary = "Return an discount plan items", tags = { "Discount Plans" }, description = "Returns the discount plan items data", responses = {
            @ApiResponse(responseCode = "200", description = "paginated results successfully retrieved with hypermedia links"),
            @ApiResponse(responseCode = "400", description = "bad request when entityName not well formed or entity unrecognized") })
    Response getDiscountPlanItems(@Parameter(description = "id of the discount plan ", required = true) @PathParam("id") Long id,
            @Parameter(description = "requestDto carries the wanted fields ex: {genericFields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @GET
    @Path("/{id}/discountPlanItems/{idItem}")
    @Operation(summary = "Return an discount plan", tags = { "Discount Plans" }, description = "Returns the discount plan data", responses = { @ApiResponse(headers = {
            @Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched discount plan item"),
            @ApiResponse(responseCode = "404", description = "Discount plan item not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getDiscountPlanItem(@Parameter(description = "id of the discount plan ", required = true) @PathParam("id") Long id,
            @Parameter(description = "id of the discount plan item", required = true) @PathParam("idItem") Long idItem);

    @POST
    @Path("/{id}/discountPlanItems")
    @Operation(summary = "This endpoint allows to create a discount plan item resource", tags = { "Discount Plans" }, description = "create new discount plan", responses = {
            @ApiResponse(responseCode = "200", description = "the entity successfully created, and the id is returned in the response"),
            @ApiResponse(responseCode = "400", description = "bad request when entity information contains an error") })
    Response createItem(@Parameter(description = "id of the discount plan ", required = true) @PathParam("id") Long id,
            @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @PUT
    @Path("/{id}/discountPlanItems/{idItem}")
    @Operation(summary = "Update a discount plan item resource by giving it's Id", tags = {
            "Discount Plans" }, description = "specify record id, and as body, the list of the fields to update", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response updateItem(@Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("idItem") Long id,
            @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @DELETE
    @Path("/{id}/discountPlanItems/{idItem}")
    @Operation(summary = "Delete a resource by giving it's Id", tags = {
            "Discount Plans" }, description = "specify the entity name, the record id, and as body, the list of the fields to delete", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response deleteItem(@Parameter(description = "The id here is the database primary key of the record to delete", required = true) @PathParam("idItem") Long id);

}