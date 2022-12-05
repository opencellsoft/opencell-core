package org.meveo.api.rest.cpq;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.cpq.order.OrderOfferDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetCommercialOrderDtoResponse;
import org.meveo.api.dto.response.cpq.GetListCommercialOrderDtoResponse;
import org.meveo.api.dto.response.cpq.GetOrderOfferDtoResponse;
import org.meveo.api.dto.response.cpq.GetQuoteDtoResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * 
 * @author Tarik FA.
 * @version 11.0
 * @LastModified 04-01-2021
 */
@Path("/orderManagement/commercialOrders")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface CommercialOrderRs {

	@POST
	 @Operation(summary = "Create a order",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order is succeffully created",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "302", description = "The order already exist", content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))),
	            @ApiResponse(responseCode = "404", description = "One of attached fields is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response create(CommercialOrderDto orderDto);


	@PUT
	@Operation(summary = "update an existing order",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order is succeffully updated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "404", description = "One of attached fields is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response update(CommercialOrderDto orderDto);

	@PUT
	@Path("/{commercialOrderId}/userAccounts/{userAccountCode}")
	@Operation(summary = "update commercial order user account",
			tags = { "Order management" },
			description ="",
			responses = {
					@ApiResponse(responseCode="200", description = "The order is succeffully updated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
					@ApiResponse(responseCode = "404", description = "One of attached fields is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
			})
	Response updateUserAccount(@Parameter(description = "The id of the commercial order", required = true) @PathParam("commercialOrderId") Long commercialOrderId, 
								@Parameter(description = "The code of the user account", required = true) @PathParam("userAccountCode") String userAccountCode);

	@PUT
	@Path("/{commercialOrderId}/invoicingPlans/{invoicingPlanCode}")
	@Operation(summary = "update commercial order invoicing plan",
			tags = { "Order management" },
			description ="",
			responses = {
					@ApiResponse(responseCode="200", description = "The order is succeffully updated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
					@ApiResponse(responseCode = "404", description = "One of attached fields is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
			})
	Response updateOrderInvoicingPlan(@Parameter(description = "The id of the commercial order", required = true) @PathParam("commercialOrderId") Long commercialOrderId, 
									@Parameter(description = "The code of the invoicing plan", required = true) @PathParam("invoicingPlanCode") String invoicingPlanCode);
	
	@DELETE
	@Path("/{orderId}")
	@Operation(summary = "delete an existing order",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order is succeffully deleted",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "id of order is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response delete(@Parameter(description = "The id of the commercial order", required = true) @PathParam("orderId") Long orderId);
	

	@PUT
	@Path("/{orderId}/status/{statusTarget}")
	@Operation(summary = "update status for order",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order's status is succeffully updated ",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "id of order is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response updateStatus(@Parameter(description = "The id of the commercial order", required = true) @PathParam("orderId") Long orderId, 
								@Parameter(description = "The status to be updated", required = true) @PathParam("statusTarget") String statusTarget);
	

	@POST
	@Path("{orderId}/duplication")
	@Operation(summary = "duplicate an order",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order is succeffully duplicated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "id of order is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response duplicate(@Parameter(description = "The id of the commercial order", required = true) @PathParam("orderId") Long orderId);
	
	@GET
    @Path("/")
    @Operation(summary = "Get commercial orders matching the given criteria",
    tags = { "Order management" },
    description ="Get commercial orders matching the given criteria",
    responses = {
            @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetListCommercialOrderDtoResponse.class)))
    })
    public Response listCommercialOrder(PagingAndFiltering pagingAndFiltering);
	

	@GET
    @Path("/{orderNumber}")
    @Operation(summary = "Get commercial orders matching the given order number",
    tags = { "Order management" },
    description ="Get commercial order matching the given order number",
    responses = {
            @ApiResponse(responseCode="200", description = "The order is succefully retrieved",content = @Content(schema = @Schema(implementation = GetCommercialOrderDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
     	   })
    public Response findByOrderNumber(@Parameter(description = "The order number for an existing commercial order", required = true) @PathParam("orderNumber") String orderNumber);

	@POST
	@Path("/{orderId}/orderValidation")
	@Operation(summary = "Launch the order validation process",
			tags = { "Order management" },
			description ="Launch the order validation process",
			responses = {
					@ApiResponse(responseCode="200", description = "The order is successfully validated",content = @Content(schema = @Schema(implementation = GetCommercialOrderDtoResponse.class))),
					@ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
			})
	public Response orderValidationProcess(@Parameter(description = "The id of the commercial order", required = true) @PathParam("orderId") Long orderId);
	
	
	@POST
	@Path("orderOffers")
	@Operation(summary = "This endpoint allows to create new order offer",
	tags = { "Order management" },
	description ="Creating a new order offer",
	responses = {
			@ApiResponse(responseCode="200", description = "the order offer successfully added",
					content = @Content(schema = @Schema(implementation = GetOrderOfferDtoResponse.class))),
			@ApiResponse(responseCode = "412", description = "missing required paramter for order offer required",
			content = @Content(schema = @Schema(implementation = MissingParameterException.class)))

	})

	Response createOrderOffer(	@Parameter( name = "orderOfferDto",
	description = "order offer dto for a new insertion") OrderOfferDto orderOfferDto);


	@PUT
	@Path("orderOffers")
	@Operation(summary = "This endpoint allows to update an existing order offer",
	description ="Updating an existing order offer",
	tags = { "Order management" },
	responses = {
			@ApiResponse(responseCode="200", description = "the order offer successfully updated",
					content = @Content(schema = @Schema(implementation = GetOrderOfferDtoResponse.class))),
			@ApiResponse(responseCode = "412", description = "missing required paramter for order offer.The required",
			content = @Content(schema = @Schema(implementation = MissingParameterException.class)))
	})
	Response updateOrderOffer(@Parameter(description = "order offer dto for updating an existing order offer", required = true) OrderOfferDto orderOffer);


	@DELETE
	@Path("orderOffers/{id}")
	@Operation(summary = "This endpoint allows to  delete an existing order offer",
	description ="Deleting an existing order offer with its id",
	tags = { "Order management" },
	responses = {
			@ApiResponse(responseCode="200", description = "The order offer successfully deleted",
					content = @Content(schema = @Schema(implementation = GetOrderOfferDtoResponse.class))),
			@ApiResponse(responseCode = "400", description = "No order offer found for the id parameter", 
			content = @Content(schema = @Schema(implementation = BusinessException.class)))
	})
	Response deleteOrderOffer(@Parameter(description = "contain the code of order offer te be deleted by its id", required = true) @PathParam("id") Long id);


	@GET
	@Path("orderOffers/{id}")
	@Operation(summary = "Get order offer matching the given order id",
	tags = { "Order management" },
	description ="Get order offer matching the given order id",
	responses = {
			@ApiResponse(responseCode="200", description = "The order type is succefully retrieved",content = @Content(schema = @Schema(implementation = GetOrderOfferDtoResponse.class))),
			@ApiResponse(responseCode = "404", description = "Order offer Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	})
	public Response findOrderOffer(@Parameter(description = "The id of the order offer", required = true) @PathParam("id") Long id);
	
	
	@PUT
	@Path("/{commercialOrderId}/orderProgress/{progressValue}")
	@Operation(summary = "update order progress",
			tags = { "Order management" },
			description ="",
			responses = {
					@ApiResponse(responseCode="200", description = "The order progress is succeffully updated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class)))
			})
	Response updateOrderProgress(@Parameter(description = "The id of the commercial order", required = true) @PathParam("commercialOrderId") Long commercialOrderId,
								@Parameter(description = "The value of order to be progress", required = true) @PathParam("progressValue") Integer progressValue);
}
	
	
	

