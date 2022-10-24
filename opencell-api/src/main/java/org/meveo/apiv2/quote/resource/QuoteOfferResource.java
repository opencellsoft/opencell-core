package org.meveo.apiv2.quote.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/quoteItems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface QuoteOfferResource {

		@POST
	    @Path("/{quoteCode}/{quoteVersion}/{quoteItemId}/duplication")
	    @Operation(summary = "This endpoint allows duplicate an existing quote item", 
	    		tags = {"Quote management"}, 
	    		description = "API to duplicate an existing quote item", 
	    		responses = {
	                    @ApiResponse(responseCode = "200", description = "Success Returns new quote item duplicated from existing one"),
	                    @ApiResponse(responseCode = "404", description = "quote item doesn't existe"),
	                    @ApiResponse(responseCode = "403", description = "Cannot move subscription") })
	    Response duplicate(@Parameter(description = "The quote code", required = true) @PathParam("quoteCode") String quoteCode,
	            @Parameter(description = "quote version id", required = true) @PathParam("quoteVersion") Integer quoteVersion,
	            @Parameter(description = "quote item id") @PathParam("quoteItemId") Long quoteItemId);
		
		@POST
	    @Path("/{quoteCode}/{quoteVersion}/{quoteItemId}/duplicate")
	    @Operation(summary = "This endpoint allows duplicate an existing quote item", 
	    		tags = {"Quote management"}, 
	    		description = "API to duplicate an existing quote item", 
	    		responses = {
	                    @ApiResponse(responseCode = "200", description = "Success Returns new quote item duplicated from existing one"),
	                    @ApiResponse(responseCode = "404", description = "quote item doesn't existe"),
	                    @ApiResponse(responseCode = "403", description = "Cannot move subscription") })
	    Response duplicateQuote(@Parameter(description = "The quote code", required = true) @PathParam("quoteCode") String quoteCode,
	            @Parameter(description = "quote version id", required = true) @PathParam("quoteVersion") Integer quoteVersion,
	            @Parameter(description = "quote item id") @PathParam("quoteItemId") Long quoteItemId);
}
