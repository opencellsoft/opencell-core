package org.meveo.apiv2.billing.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/billing")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DiscountPlanInstanceResource {

    @GET
    @Path("billingAccounts/{billingAccountId}/discountPlanInstances")
    @Operation(summary = "Return an discount plan instance", tags = { "Discount Plan instances" }, description = "Returns the discount plan instance data", responses = {
            @ApiResponse(headers = {
                    @Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched discount Plan"),
            @ApiResponse(responseCode = "404", description = "Discount plan instance not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getDiscountPlanInstances(@Parameter(description = "id of the billing Account", required = true) @PathParam("billingAccountId") Long billingAccountId,
            @Parameter(description = "requestDto carries the wanted fields ex: {genericFields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @GET
    @Path("billingAccounts/{billingAccountId}/discountPlanInstances/{id}")
    @Operation(summary = "Return an discount plan instance", tags = { "Discount Plan instances" }, description = "Returns the discount plan instance data", responses = {
            @ApiResponse(headers = {
                    @Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched discount Plan"),
            @ApiResponse(responseCode = "404", description = "Discount plan instance not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getDiscountPlanInstance(@Parameter(description = "id of the billing Account", required = true) @PathParam("billingAccountId") Long billingAccountId,
            @Parameter(description = "id of the discount plan instance", required = true) @PathParam("id") Long id);

    @POST
    @Path("billingAccounts/{billingAccountId}/discountPlanInstances")
    @Operation(summary = "This endpoint allows to instantiate a discount plan instance resource", tags = {
            "Discount Plan instances" }, description = "Instantiate new discount plan Instance", responses = {
            @ApiResponse(responseCode = "200", description = "the entity successfully created, and the id is returned in the response"),
            @ApiResponse(responseCode = "400", description = "bad request when entity information contains an error") })
    Response create(@Parameter(description = "id of the billing Account", required = true) @PathParam("billingAccountId") Long billingAccountId,
            @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @PUT
    @Path("/billingAccounts/{billingAccountId}/discountPlanInstances/{id}")
    @Operation(summary = "Update a resource by giving it's Id", tags = {
            "Discount Plan instances" }, description = "specify record id, and as body, the list of the fields to update", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response update(@Parameter(description = "The billing account id", required = true) @PathParam("billingAccountId") Long billingAccountId,
            @Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @DELETE
    @Path("/billingAccounts/{billingAccountId}/discountPlanInstances/{id}")
    @Operation(summary = "Delete a resource by giving it's Id", tags = {
            "Discount Plan instances" }, description = "specify the entity name, the record id, and as body, the list of the fields to delete", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response delete(@Parameter(description = "The billing account id", required = true) @PathParam("billingAccountId") Long billingAccountId,
            @Parameter(description = "The id here is the database primary key of the record to delete", required = true) @PathParam("id") Long id);

    @PUT
    @Path("/billingAccounts/{billingAccountId}/discountPlanInstances/{id}/expiration")
    @Operation(summary = "Expire a discount plan Instance", tags = {
            "Discount Plan instances" }, description = "specify record id, and as body, the list of the fields to update", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully expired"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response expire(@Parameter(description = "The billing account id", required = true) @PathParam("billingAccountId") Long billingAccountId,
            @Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id);

    @GET
    @Path("/subscriptions/{subscriptionId}/discountPlanInstances")
    @Operation(summary = "Return discount plan instances by subscription", tags = {
            "Discount Plan instances" }, description = "Returns the discount plan instances data by subscription", responses = { @ApiResponse(headers = {
            @Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched discount Plan"),
            @ApiResponse(responseCode = "404", description = "Discount plan instance not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getDiscountPlanInstancesBySubscription(@Parameter(description = "id of the subscription", required = true) @PathParam("subscriptionId") Long subscriptionId,
            @Parameter(description = "requestDto carries the wanted fields ex: {genericFields = [code, description]}", required = true) GenericPagingAndFiltering searchConfig);

    @GET
    @Path("/subscriptions/{subscriptionId}/discountPlanInstances/{id}")
    @Operation(summary = "Return an discount plan instance by subscription", tags = {
            "Discount Plan instances" }, description = "Returns the discount plan instance data by subscription", responses = { @ApiResponse(headers = {
            @Header(name = "ETag", description = "a pseudo-unique identifier that represents the version of the data sent back", schema = @Schema(type = "integer", format = "int64")) }, description = "the searched discount Plan"),
            @ApiResponse(responseCode = "404", description = "Discount plan instance not found", content = @Content(schema = @Schema(implementation = ApiException.class))) })
    Response getDiscountPlanInstanceBySubscription(@Parameter(description = "id of the subscription", required = true) @PathParam("subscriptionId") Long subscriptionId,
            @Parameter(description = "id of the discount plan instance", required = true) @PathParam("id") Long id);

    @POST
    @Path("/subscriptions/{subscriptionId}/discountPlanInstances")
    @Operation(summary = "This endpoint allows to instantiate a discount plan instance resource for a subscription", tags = {
            "Discount Plan instances" }, description = "Instantiate new discount plan Instance for a subscription", responses = {
            @ApiResponse(responseCode = "200", description = "the entity successfully created, and the id is returned in the response"),
            @ApiResponse(responseCode = "400", description = "bad request when entity information contains an error") })
    Response createBySubscription(@Parameter(description = "id of the subscription", required = true) @PathParam("subscriptionId") Long subscriptionId,
            @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @PUT
    @Path("/subscriptions/{subscriptionId}/discountPlanInstances/{id}")
    @Operation(summary = "Update a resource by giving it's Id", tags = {
            "Discount Plan instances" }, description = "specify record id, and as body, the list of the fields to update", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response updateBySubscription(@Parameter(description = "The subscription id", required = true) @PathParam("subscriptionId") Long subscriptionId,
            @Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id,
            @Parameter(description = "dto the json representation of the object", required = true) String dto);

    @DELETE
    @Path("/subscriptions/{subscriptionId}/discountPlanInstances/{id}")
    @Operation(summary = "Delete a resource by giving it's Id", tags = {
            "Discount Plan instances" }, description = "specify the entity name, the record id, and as body, the list of the fields to delete", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully updated but not content exposed except the hypermedia"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response deleteBySubscription(@Parameter(description = "The subscription id", required = true) @PathParam("subscriptionId") Long subscriptionId,
            @Parameter(description = "The id here is the database primary key of the record to delete", required = true) @PathParam("id") Long id);

    @PUT
    @Path("/subscriptions/{subscriptionId}/discountPlanInstances/{id}/expiration")
    @Operation(summary = "Expire a discount plan Instance", tags = {
            "Discount Plan instances" }, description = "specify record id, and as body, the list of the fields to update", responses = {
            @ApiResponse(responseCode = "200", description = "resource successfully expired"),
            @ApiResponse(responseCode = "404", description = "baseEntityObject not found", content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "bad request when input not well formed") })
    Response expireBySubscription(@Parameter(description = "The subscription id", required = true) @PathParam("subscriptionId") Long subscriptionId,
            @Parameter(description = "The id here is the database primary key of the record to update", required = true) @PathParam("id") Long id);

}