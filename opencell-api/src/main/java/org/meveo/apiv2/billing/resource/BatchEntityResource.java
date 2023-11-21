package org.meveo.apiv2.billing.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.billing.BatchEntity;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * A definition of batch entity resource.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Path("/jobs/BatchEntity")
@Produces({APPLICATION_JSON})
@Consumes({APPLICATION_JSON})
public interface BatchEntityResource {

    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to create an batch entity resource",
            tags = {"BatchEntity"},
            description = "create new batch entity",
            responses = {
                    @ApiResponse(responseCode = "200", description = "the batch entity successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when batch entity information contains an error")
            })
    Response create(@Parameter(description = "the batch entity object", required = true) BatchEntity batchEntity);

    @PUT
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to update an existing batch entity resource",
            tags = {"BatchEntity"},
            description = "update an existing batch entity",
            responses = {
                    @ApiResponse(responseCode = "200", description = "the batch entity successfully updated, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when batch entity information contains an error")
            })
    Response update(@Parameter(description = "id of batch entity", required = true) @PathParam("id") Long id, @Parameter(description = "the batch entity object", required = true) BatchEntity batchEntity);

    @DELETE
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to delete an existing batch entity resource",
            tags = {"BatchEntity"},
            description = "delete an existing batch entity",
            responses = {
                    @ApiResponse(responseCode = "200", description = "the batch entity successfully deleted"),
                    @ApiResponse(responseCode = "400", description = "bad request when batch entity is not found")
            })
    Response delete(@Parameter(description = "batch entity code", required = true) @PathParam("id") Long id, @Context Request request);

    @PUT
    @Path("/{id}/cancel")
    @Operation(summary = "This endpoint allows to cancel an existing batch entity resource", tags = {
            "BatchEntity"}, description = "cancel an existing batch entity", responses = {
            @ApiResponse(responseCode = "200", description = "the batch entity successfully canceled"),
            @ApiResponse(responseCode = "403", description = "bad request, batch entity is not eligible for update"),
            @ApiResponse(responseCode = "404", description = "bad request, batch entity is not found")})
    Response cancel(
            @Parameter(description = " batch entity id", required = true) @PathParam("id") Long id);
}