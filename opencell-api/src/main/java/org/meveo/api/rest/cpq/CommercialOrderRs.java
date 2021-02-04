package org.meveo.api.rest.cpq;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetCommercialOrderDtoResponse;
import org.meveo.api.dto.response.cpq.GetListCommercialOrderDtoResponse;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
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
@Path("/commercialOrder")
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

	@PATCH
	@Path("/{commercialOrderId}/userAccount/{userAccountCode}")
	@Operation(summary = "update commercial order user account",
			tags = { "Order management" },
			description ="",
			responses = {
					@ApiResponse(responseCode="200", description = "The order is succeffully updated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
					@ApiResponse(responseCode = "404", description = "One of attached fields is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
			})
	Response updateUserAccount(@Parameter(required = true) @PathParam("commercialOrderId") Long commercialOrderId, @Parameter(required = true) @PathParam("userAccountCode") String userAccountCode);

	@PATCH
	@Path("/{commercialOrderId}/invoicingPlan/{invoicingPlanCode}")
	@Operation(summary = "update commercial order invoicing plan",
			tags = { "Order management" },
			description ="",
			responses = {
					@ApiResponse(responseCode="200", description = "The order is succeffully updated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
					@ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
					@ApiResponse(responseCode = "404", description = "One of attached fields is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
			})
	Response updateOrderInvoicingPlan(@Parameter(required = true) @PathParam("commercialOrderId") Long commercialOrderId, @Parameter(required = true) @PathParam("invoicingPlanCode") String invoicingPlanCode);
	
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
	public Response delete(@Parameter(required = true) @PathParam("orderId") Long orderId);
	

	@PATCH
	@Path("/{orderId}/status/update")
	@Operation(summary = "update status for order",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order's status is succeffully updated ",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "id of order is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response updateStatus(@Parameter(required = true) @PathParam("orderId") Long orderId, @Parameter(required = true) @QueryParam("statusTarget") String statusTarget);
	

	@POST
	@Path("/duplicate/{orderId}")
	@Operation(summary = "duplicate an order",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order is succeffully duplicated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "id of order is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response duplicate(@Parameter(required = true) @PathParam("orderId") Long orderId);
	

	@PATCH
	@Path("/validate/{commercialOrderId}")
	@Operation(summary = "validate an order",
	    tags = { "Order management" },
	    description ="valide a complete order, and create a new order number",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order is succeffully validated",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "id of order is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response validate(@Parameter(required = true) @PathParam("commercialOrderId") Long orderId);
	
	@POST
    @Path("/list")
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
    public Response findByOrderNumber(@Parameter(required = true) @PathParam("orderNumber") String orderNumber);

	@POST
	@Path("/validateOrder/{orderId}")
	@Operation(summary = "Launch the order validation process",
			tags = { "Order management" },
			description ="Launch the order validation process",
			responses = {
					@ApiResponse(responseCode="200", description = "The order is successfully validated",content = @Content(schema = @Schema(implementation = GetCommercialOrderDtoResponse.class))),
					@ApiResponse(responseCode = "404", description = "Order Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
			})
	public Response orderValidationProcess(@Parameter(required = true) @PathParam("orderId") Long orderId);
}
