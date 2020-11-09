/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.tmforum;

import java.util.List;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.GetListAccountingArticlePricesResponseDto;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.ProductContextDTO;
import org.meveo.api.dto.cpq.QuoteVersionDto;
import org.meveo.api.rest.PATCH;
import org.tmf.dsmapi.quote.ProductQuote;
import org.tmf.dsmapi.quote.ProductQuoteItem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * TMForum Product quote API specification implementation
 * 
 * @author Andrius Karpavicius
 */
@Path("/quoteManagement/productQuote")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface QuoteRs {

    /**
     * Place a new product quote
     * 
     * @param productQuote Product quote information
     * @param info Http request context
     * @return Product quote information
     */
    @POST
    @Path("/")
    @Operation(summary = "Create a product quote",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully created",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response createProductQuote(@Parameter(description = "Product quote information", required = false) ProductQuote productQuote, @Context UriInfo info);

    /**
     * Get details of a single product quote
     * 
     * @param id Product code
     * @param info Http request context
     * @return quote response
     */
    @GET
    @Path("/{quoteCode}")
    @Operation(summary = "Get a product quote by its code",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully retrieved",content = @Content(schema = @Schema(implementation = ProductQuote.class)))
    })
    public Response getProductQuote(@Parameter(description = "Product quote code", required = false) @PathParam("QuoteCode") String code, @Context UriInfo info);

    /**
     * Get a list of product quotes optionally filtered by some criteria
     * 
     * @param info Http request context
     * @return A list of product quotes matching search criteria
     */
    @GET
    @Path("/")
    @Operation(summary = "Get a list of product quotes optionally filtered by some criteria",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quotes are succeffully retrieved",content = @Content(schema = @Schema(implementation = ProductQuote.class)))
    })
    public Response findProductQuotes(@Context UriInfo info);

    /**
     * Modify partially a product quote
     * 
     * @param id Product quote code
     * @param productQuote Product quote information
     * @param info Http request context
     * @return An updated product quote information
     */
    @PUT
    @Path("/{quoteCode}")
    @Operation(summary = "Modify a product quote",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully updated",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response updateProductQuote(@Parameter(description = "Product quote code", required = false) @PathParam("QuoteCode") String code,
    		@Parameter(description = "Product quote information", required = false) ProductQuote productQuote, @Context UriInfo info);
    
    
    /**
     * Modify  a product quote item
     * 
     * @param id Product quote code
     * @param productQuote Product quote information
     * @param info Http request context
     * @return An updated product quote information
     */
    @PUT
    @Path("/{quoteItemCode}")
    @Operation(summary = "Modify a quote item",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote item is succeffully updated",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response updateQuoteItem(@Parameter(description = "Product quote code", required = false) @PathParam("QuoteItemCode") String code,
    		@Parameter(description = "Product quote information", required = false) ProductQuoteItem productQuoteitem, @Context UriInfo info);

    /**
     * Delete a product quote.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @DELETE
    @Path("/{quoteCode}")
    @Operation(summary = "Delete a product quote.",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "The quote is succeffully deleted",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response deleteProductQuote(@Parameter(description = "Product quote code", required = false) @PathParam("QuoteCode") String code, @Context UriInfo info);

    /**
     * Place an order based on a product quote.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @POST
    @Path("/placeOrder/{quoteCode}")
    @Operation(summary = "Place an order based on a product quote",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "order succeffully created from current quote",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response placeOrder(@Parameter(description = "Product quote code", required = false) @PathParam("QuoteCode") String id, @Context UriInfo info);
    
    /**
     * Create a new product quote item
     * 
     * @param productQuote Product quote information
     * @param info Http request context
     * @return Product quote information
     */
    @POST
    @Path("/quoteItem")
    @Operation(summary = "Create a quote item",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quote item is succeffully created",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response createQuoteItem(@Parameter(description = "Product quote item information", required = false) ProductQuoteItem productQuoteItem, @Context UriInfo info);
    
    
    @POST
    @Path("/quoteVersion")
    @Operation(summary = "Create a quote version",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "New quote version is succeffully created",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response createQuoteVersion(@Parameter(description = "Product quote version information", required = false) QuoteVersionDto quoteVersion, @Context UriInfo info);
    
    /**
     * Delete a product quote item.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @DELETE
    @Path("/{quoteItemCode}")
    @Operation(summary = "Delete a quote item",
    tags = { "Quote management" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "quote item is succeffully deleted",content = @Content(schema = @Schema(implementation = ActionStatus.class)))
    })
    public Response deleteQuoteItem(@Parameter(description = "Product quote item code", required = false) @PathParam("quoteItemCode") String code, @Context UriInfo info);
    
    @POST
    @Path("/productQuotation")
    @Operation(summary = "product quotation",
    tags = { "CPQ" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "Product quotation is well done!",content = @Content(schema = @Schema(implementation = GetListAccountingArticlePricesResponseDto.class)))
    })
     Response productQuotation(@Parameter(description = "product context", required = false) ProductContextDTO quoteContext, UriInfo info);
   
    @POST
    @Path("/offerQuotation")
    @Operation(summary = "Offer quotation",
    tags = { "CPQ" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "Offer quotation is well done!",content = @Content(schema = @Schema(implementation = GetListAccountingArticlePricesResponseDto.class)))
    })
	Response offerQuotation(@Parameter(description = "Offer context ", required = false) OfferContextDTO quoteContext, UriInfo info);
    
    @POST
    @Path("/quoteQuotation")
    @Operation(summary = "Quote quotation",
    tags = { "CPQ" },
    description ="",
    responses = {
            @ApiResponse(responseCode="200", description = "Quote quotation is well done!",content = @Content(schema = @Schema(implementation = GetListAccountingArticlePricesResponseDto.class)))
    })
	Response quoteQuotation(@Parameter(description = "quote context", required = false) List<OfferContextDTO> quoteContext, UriInfo info);
    
    
}