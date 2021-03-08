package org.meveo.apiv2.article.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.ArticleMappingLine;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

@Path("/articleMappingLine")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ArticleMappingLineResource {

    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to create an article mapping line resource",
            tags = { "articleMappingLine" },
            description ="create new article mapping line resource",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article mapping line resource successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article mapping line information contains an error")
            })
    Response createArticleMappingLine(@Parameter(description = "the article mapping line object", required = true) ArticleMappingLine articleMappingLine);

    @PUT
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to updating an existing article mapping line resource",
            tags = { "articleMappingLine" },
            description ="update an existing article mapping line resource",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article mapping line resource successfully updated, and the object is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article mapping line information contains an error")
            })
    Response updateArticleMappingLine(@Parameter(description = "id of the article mapping line", required = true) @PathParam("id") Long id,
    								  @Parameter(description = "the article mapping line object", required = true) ArticleMappingLine articleMappingLine);
    

    @GET
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to find an existing article mapping line resource",
            tags = { "articleMappingLine" },
            description ="find an existing article mapping line resource",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article mapping line resource retrieved, and the object is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article mapping line information doesn't exist")
            })
    Response findById(@Parameter(description = "id of the article mapping line", required = true) @PathParam("id") Long id, @Context Request request);
    

    @DELETE
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to delete an existing article mapping line resource",
            tags = { "articleMappingLine" },
            description ="delete an existing article mapping line resource",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article mapping line resource deleted, and the object is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article mapping line information doesn't exist")
            })
    Response deleteById(@Parameter(description = "id of the article mapping line", required = true) @PathParam("id") Long id);
}
