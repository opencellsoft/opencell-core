package org.meveo.apiv2.article.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.ArticleMapping;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/article/mapping")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ArticleMappingResource {

    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to create an article mapping resource",
            tags = { "articleMapping" },
            description ="create new article mapping resource",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article mapping resource successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article mapping information contains an error")
            })
    Response createArticleMappingLine(@Parameter(description = "the article mapping object", required = true) ArticleMapping articleMapping);
}
