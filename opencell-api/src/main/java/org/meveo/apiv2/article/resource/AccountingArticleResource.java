package org.meveo.apiv2.article.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.AccountingArticle;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/article")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountingArticleResource {

    @POST
    @Path("/")
    @Operation(summary = "This endpoint allows to create an accounting article resource",
            tags = { "AccountingArticle" },
            description ="create new accounting article",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article information contains an error")
            })
    Response createAccountingArticle(@Parameter(description = "the accounting article object", required = true) AccountingArticle accountingArticle);
}
