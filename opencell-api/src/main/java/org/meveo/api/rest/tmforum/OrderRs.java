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

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.meveo.api.dto.billing.ApplicableDueDateDelayDto;
import org.meveo.api.rest.PATCH;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

/**
 * TMForum Product ordering API specification implementation
 * 
 * @author Andrius Karpavicius
 */
@Path("/orderManagement/productOrder")
@Tag(name = "Order", description = "@%Order")
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
	@Operation(
			summary=" Place a new product order  ",
			description=" Place a new product order  ",
			operationId="    POST_Order_create",
			responses= {
				@ApiResponse(description=" Product order information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Get details of a single product order  ",
			description=" Get details of a single product order  ",
			operationId="    GET_Order_{orderId}",
			responses= {
				@ApiResponse(description=" Response of the request ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response getProductOrder(@PathParam("orderId") String id, @Context UriInfo info);

    /**
     * Get a list of product orders optionaly filtered by some criteria
     * 
     * @param info Http request context
     * @return A list of product orders matching search criteria
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Get a list of product orders optionaly filtered by some criteria  ",
			description=" Get a list of product orders optionaly filtered by some criteria  ",
			operationId="    GET_Order_search",
			responses= {
				@ApiResponse(description=" A list of product orders matching search criteria ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Modify partially a product order  ",
			description=" Modify partially a product order  ",
			operationId="    PATCH_Order_{id}",
			responses= {
				@ApiResponse(description=" An updated product order information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Delete a product order  ",
			description=" Delete a product order  ",
			operationId="    DELETE_Order_{orderId}",
			responses= {
				@ApiResponse(description=" Response of the delete request ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Evaluate and return the dueDateDelayEL. It checks the EL in this order: Order, CustomerAccount, BillingCycle.  ",
			description=" Evaluate and return the dueDateDelayEL. It checks the EL in this order: Order, CustomerAccount, BillingCycle.  ",
			operationId="    GET_Order_{orderId}_applicableDueDateDelay",
			responses= {
				@ApiResponse(description=" Response of the due date delay  ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Updates the dueDateDelayEL of an Order.   ",
			description=" Updates the dueDateDelayEL of an Order.   ",
			operationId="    PUT_Order_{orderId}_simpleDueDateDelay",
			responses= {
				@ApiResponse(description=" Response of the update request ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" validate a product order ",
			description=" validate a product order ",
			operationId="    GET_Order_validate",
			responses= {
				@ApiResponse(description=" Product order information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response validateProductOrder(ProductOrder productOrder, @Context UriInfo info);
}
