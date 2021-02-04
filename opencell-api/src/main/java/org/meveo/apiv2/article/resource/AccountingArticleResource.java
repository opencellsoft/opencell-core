package org.meveo.apiv2.article.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.AccountingArticle;

import java.util.Map;

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
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "This endpoint allows to update an existing accounting article resource",
            tags = { "AccountingArticle" },
            description ="update an existing accounting article",
            responses = {
                    @ApiResponse(responseCode="200", description = "the article successfully updated, and the id is returned in the response"),
                    @ApiResponse(responseCode = "400", description = "bad request when article information contains an error")
            })
    Response updateAccountingArticle(@Parameter(description = "id of Accounting article", required = true) @PathParam("id") Long id, @Parameter(description = "the accounting article object", required = true) AccountingArticle accountingArticle);
    
    @GET
    @Path("/{accountingArticleCode}")
    @Operation(summary = "This endpoint allows to find an existing accounting article resource",
    tags = { "AccountingArticle" },
    description ="fine an existing accounting article",
    responses = {
            @ApiResponse(responseCode="200", description = "the article successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "bad request when article information contains an error")
    })
    Response find(@Parameter(description = "accounting article code", required = true) @PathParam("accountingArticleCode") String accountingArticleCode, @Context Request request);
    
    
    @DELETE
    @Path("/{accountingArticleCode}")
    @Operation(summary = "This endpoint allows to delete an existing accounting article resource",
    tags = { "AccountingArticle" },
    description ="delete an existing accounting article",
    responses = {
            @ApiResponse(responseCode="200", description = "the article successfully deleted"),
            @ApiResponse(responseCode = "400", description = "bad request when article is not found")
    })
    Response delete(@Parameter(description = "accounting article code", required = true) @PathParam("accountingArticleCode") String accountingArticleCode, @Context Request request);
    
    @POST
    @Path("/list")
    @Operation(summary = "This endpoint allows to find list of accounting article resource",
    tags = { "AccountingArticle" },
    description ="find list of an existing accounting article",
    responses = {
            @ApiResponse(responseCode="200", description = "return list of accounting article"),
            @ApiResponse(responseCode = "400", description = "bad request when article information contains an error")
    })
    Response list(@DefaultValue("0") @QueryParam("offset") Long offset, @DefaultValue("50") @QueryParam("limit") Long limit,
            @QueryParam("sort") String sort, @QueryParam("orderBy") String orderBy, Map<String, Object> filter,
            @Context Request request);
    
    @GET
    @Path("/product/{productCode}")
    @Operation(summary = "This endpoint allows to find accounting article resource with product and list of attributes",
    tags = { "AccountingArticle" },
    description ="find  an existing accounting article",
    responses = {
            @ApiResponse(responseCode="200", description = "return accounting article"),
            @ApiResponse(responseCode = "400", description = "bad request when article information contains an error")
    })
    Response getAccountingArticles(@PathParam("productCode") String productCode, Map<String, Object> attribues, @Context Request request);
    
}
