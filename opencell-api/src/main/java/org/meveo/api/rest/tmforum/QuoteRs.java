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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.rest.PATCH;
import org.tmf.dsmapi.quote.ProductQuote;

/**
 * TMForum Product quote API specification implementation
 * 
 * @author Andrius Karpavicius
 */
@Path("/quoteManagement/productQuote")
@Tag(name = "Quote", description = "@%Quote")
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
	@Operation(
			summary=" Place a new product quote  ",
			description=" Place a new product quote  ",
			operationId="    POST_Quote_create",
			responses= {
				@ApiResponse(description=" Product quote information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createProductQuote(ProductQuote productQuote, @Context UriInfo info);

    /**
     * Get details of a single product quote
     * 
     * @param id Product code
     * @param info Http request context
     * @return quote response
     */
    @GET
    @Path("/{quoteId}")
	@Operation(
			summary=" Get details of a single product quote  ",
			description=" Get details of a single product quote  ",
			operationId="    GET_Quote_{quoteId}",
			responses= {
				@ApiResponse(description=" quote response ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response getProductQuote(@PathParam("quoteId") String id, @Context UriInfo info);

    /**
     * Get a list of product quotes optionally filtered by some criteria
     * 
     * @param info Http request context
     * @return A list of product quotes matching search criteria
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Get a list of product quotes optionally filtered by some criteria  ",
			description=" Get a list of product quotes optionally filtered by some criteria  ",
			operationId="    GET_Quote_search",
			responses= {
				@ApiResponse(description=" A list of product quotes matching search criteria ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response findProductQuotes(@Context UriInfo info);

    /**
     * Modify partially a product quote
     * 
     * @param id Product quote code
     * @param productQuote Product quote information
     * @param info Http request context
     * @return An updated product quote information
     */
    @PATCH
    @Path("/{id}")
	@Operation(
			summary=" Modify partially a product quote  ",
			description=" Modify partially a product quote  ",
			operationId="    PATCH_Quote_{id}",
			responses= {
				@ApiResponse(description=" An updated product quote information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response updateProductQuote(@PathParam("id") String id, ProductQuote productQuote, @Context UriInfo info);

    /**
     * Delete a product quote.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @DELETE
    @Path("/{quoteId}")
	@Operation(
			summary=" Delete a product quote.  ",
			description=" Delete a product quote.  ",
			operationId="    DELETE_Quote_{quoteId}",
			responses= {
				@ApiResponse(description=" Response status ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response deleteProductQuote(@PathParam("quoteId") String id, @Context UriInfo info);

    /**
     * Place an order based on a product quote.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return Response status
     */
    @POST
    @Path("/placeOrder/{quoteId}")
	@Operation(
			summary=" Place an order based on a product quote.  ",
			description=" Place an order based on a product quote.  ",
			operationId="    POST_Quote_placeOrder_{quoteId}",
			responses= {
				@ApiResponse(description=" Response status ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response placeOrder(@PathParam("quoteId") String id, @Context UriInfo info);
}
