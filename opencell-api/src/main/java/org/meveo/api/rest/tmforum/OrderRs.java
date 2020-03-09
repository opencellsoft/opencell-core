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
     * @return Response of the request
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
     * @return Response of the delete request
     */
    @DELETE
    @Path("/{orderId}")
    public Response deleteProductOrder(@PathParam("orderId") String id, @Context UriInfo info);
    
    /**
     * Evaluate and return the dueDateDelayEL. It checks the EL in this order: Order, CustomerAccount, BillingCycle.
     * 
     * @param orderId The order id 
     * @param info the URI info 
     * @return Response of the due date delay 
     */
    @GET
    @Path("/{orderId}/applicableDueDateDelay")
    public Response applicableDueDateDelay(@PathParam("orderId") String orderId, @Context UriInfo info);
    
    /**
     * Updates the dueDateDelayEL of an Order. 
     * 
     * @param orderId The order id
     * @param postData Applicable Due Date Delay data
     * @param info The Uri information
     * @return Response of the update request
     */
    @PUT
    @Path("/{orderId}/simpleDueDateDelay")
    public Response simpleDueDateDelay(@PathParam("orderId") String orderId, ApplicableDueDateDelayDto postData, @Context UriInfo info);

    /**
     * validate a product order
     *
     * @param productOrder Product order information
     * @param info Http request context
     * @return Product order information
     */
    @GET
    @Path("/validate")
    public Response validateProductOrder(ProductOrder productOrder, @Context UriInfo info);
}
