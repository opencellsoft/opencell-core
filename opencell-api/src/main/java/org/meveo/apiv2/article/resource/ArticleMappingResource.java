package org.meveo.apiv2.article.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.ArticleMapping;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

@Path("/articleMapping")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ArticleMappingResource {

    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to create an article mapping resource",
            tags = { "AccountingArticle" },
            description ="create new article mapping resource",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article mapping resource successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article mapping information contains an error")
            })
    @Deprecated
    Response createArticleMappingLine(@Parameter(description = "the article mapping object", required = true) ArticleMapping articleMapping);

    @GET
    @Path("/{code}")
    @Operation(summary = "This endpoint allows to find an existing article mapping resource",
            tags = { "articleMapping" },
            description ="find an existing article mapping resource",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article mapping resource retrieved, and the object is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article mapping information doesn't exist")
            })
    Response find(@Parameter(description = "code of the article mapping", required = true)
                  @PathParam("code") String code, @Context Request request);

}
