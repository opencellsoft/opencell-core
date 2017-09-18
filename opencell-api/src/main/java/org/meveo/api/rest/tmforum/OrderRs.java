package org.meveo.api.rest.tmforum;

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

import org.meveo.api.dto.billing.ApplicableDueDateDelayDto;
import org.meveo.api.rest.PATCH;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

/**
 * TMForum Product ordering API specification implementation
 * 
 * @author Andrius Karpavicius
 */
@Path("/orderManagement/productOrder")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OrderRs {

    /**
     * Place a new product order
     * 
     * @param productOrder Product order information
     * @param info Http request context
     * @return Product order information
     */
    @POST
    @Path("/")
    public Response createProductOrder(ProductOrder productOrder, @Context UriInfo info);

    /**
     * Get details of a single product order
     * 
     * @param id Product code
     * @param info Http request context
     * @return
     */
    @GET
    @Path("/{orderId}")
    public Response getProductOrder(@PathParam("orderId") String id, @Context UriInfo info);

    /**
     * Get a list of product orders optionaly filtered by some criteria
     * 
     * @param info Http request context
     * @return A list of product orders matching search criteria
     */
    @GET
    @Path("/")
    public Response findProductOrders(@Context UriInfo info);

    /**
     * Modify partially a product order
     * 
     * @param id Product order code
     * @param productOrder Product order information
     * @param info Http request context
     * @return An updated product order information
     */
    @PATCH
    @Path("/{id}")
    public Response updateProductOrder(@PathParam("id") String id, ProductOrder productOrder, @Context UriInfo info);

    /**
     * Delete a product order
     * 
     * @param id Product order code
     * @param info Http request context
     * @return
     */
    @DELETE
    @Path("/{orderId}")
    public Response deleteProductOrder(@PathParam("orderId") String id, @Context UriInfo info);
    
    /**
     * Evaluate and return the dueDateDelayEL. It checks the EL in this order: Order, CustomerAccount, BillingCycle.
     * 
     * @param orderId
     * @param info
     * @return
     */
    @GET
    @Path("/{orderId}/applicableDueDateDelay")
    public Response applicableDueDateDelay(@PathParam("orderId") String orderId, @Context UriInfo info);
    
    /**
     * Updates the dueDateDelayEL of an Order. 
     * 
     * @param orderId
     * @param postData
     * @param info
     * @return
     */
    @PUT
    @Path("/{orderId}/simpleDueDateDelay")
    public Response simpleDueDateDelay(@PathParam("orderId") String orderId, ApplicableDueDateDelayDto postData, @Context UriInfo info);
}
