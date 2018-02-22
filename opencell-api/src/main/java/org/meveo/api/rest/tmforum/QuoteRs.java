package org.meveo.api.rest.tmforum;

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
    public Response getProductQuote(@PathParam("quoteId") String id, @Context UriInfo info);

    /**
     * Get a list of product quotes optionally filtered by some criteria
     * 
     * @param info Http request context
     * @return A list of product quotes matching search criteria
     */
    @GET
    @Path("/")
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
    public Response updateProductQuote(@PathParam("id") String id, ProductQuote productQuote, @Context UriInfo info);

    /**
     * Delete a product quote.
     * 
     * @param info Http request context
     * @return
     */
    @DELETE
    @Path("/{quoteId}")
    public Response deleteProductQuote(@PathParam("quoteId") String id, @Context UriInfo info);

    /**
     * Place an order based on a product quote.
     * 
     * @param id Product quote code
     * @param info Http request context
     * @return
     */
    @POST
    @Path("/placeOrder/{quoteId}")
    public Response placeOrder(@PathParam("quoteId") String id, @Context UriInfo info);
}