package org.meveo.apiv2.quote.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
