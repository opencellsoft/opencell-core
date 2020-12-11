package org.meveo.apiv2.article.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.ArticleMappingLine;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/article/mappingLine")
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
}
