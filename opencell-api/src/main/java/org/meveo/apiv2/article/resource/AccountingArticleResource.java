package org.meveo.apiv2.article.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.meveo.apiv2.article.*;

import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

@Path("/articles")
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
    
    @GET
    @Path("/")
    @Operation(summary = "This endpoint allows to find list of accounting article resource",
    tags = { "AccountingArticle" },
    description ="find list of an existing accounting article",
    responses = {
            @ApiResponse(responseCode="200", description = "return list of accounting article"),
            @ApiResponse(responseCode = "400", description = "bad request when article information contains an error")
    })
    Response list(@Parameter(description = "The offset of the list") @DefaultValue("0") @QueryParam("offset") Long offset, 
    		@Parameter(description = "The limit element per page") @DefaultValue("50") @QueryParam("limit") Long limit,
    		@Parameter(description = "The sort by field") @QueryParam("sort") String sort, 
    		@Parameter(description = "The ordering by field") @QueryParam("orderBy") String orderBy, 
    		@Parameter(description = "Map of filters") Map<String, Object> filter,
            @Context Request request);
    
    @GET
    @Path("/products/{productCode}")
    @Operation(summary = "This endpoint allows to find accounting article resource with product and list of attributes",
    tags = { "AccountingArticle" },
    description ="find  an existing accounting article",
    responses = {
            @ApiResponse(responseCode="200", description = "return accounting article"),
            @ApiResponse(responseCode = "400", description = "bad request when article information contains an error")
    })
    Response getAccountingArticles(@Parameter(description = "product code", required = true) @PathParam("productCode") String productCode, 
    								@Parameter(description = "Map of attributes") Map<String, Object> attribues, @Context Request request);


    @POST
    @Path("/accountingCodeMapping")
    @Operation(summary = "This endpoint create accounting code mapping",
            tags = { "AccountingCodeMapping" },
            description ="Create accounting code mapping",
            responses = {
                    @ApiResponse(responseCode="200", description = "Accounting code mapping successfully created, and the id is returned in the response"),
                    @ApiResponse(responseCode = "404", description = "Entity not found")
            })
    Response createAccountingCodeMapping(AccountingCodeMappingInput accountingCodeMappingInput);

    @PUT
    @Path("/{accountingArticleCode}/accountingCodeMapping")
    @Operation(summary = "This endpoint update accounting code mapping",
            tags = { "AccountingCodeMapping" },
            description ="Update accounting code mapping",
            responses = {
                    @ApiResponse(responseCode="200", description = "Accounting code mapping successfully updated, and the id is returned in the response"),
                    @ApiResponse(responseCode = "404", description = "Entity not found")
            })
    Response updateAccountingCodeMapping(@Parameter(description = "accounting article code", required = true)
                                         @PathParam("accountingArticleCode") String accountingArticleCode,
                                         AccountingCodeMappingInput accountingCodeMappingInput);
}
