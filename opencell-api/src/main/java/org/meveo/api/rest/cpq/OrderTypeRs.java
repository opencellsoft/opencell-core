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

import org.meveo.api.dto.cpq.order.OrderTypeDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetListOrderTypeResponseDto;
import org.meveo.api.dto.response.cpq.GetOrderTypeDtoResponse;
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
@Path("/orderManagement/orderTypes")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface OrderTypeRs {

	@POST
	@Operation(summary = "Create a order type", tags = { "Order management" }, description = "", responses = {
			@ApiResponse(responseCode = "200", description = "The order type is succeffully created", content = @Content(schema = @Schema(implementation = GetOrderTypeDtoResponse.class))),
			@ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
			@ApiResponse(responseCode = "302", description = "The order type already exist", content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))) })
	public Response create(OrderTypeDto orderTypeDto);

	@PUT
	@Operation(summary = "update an existing order type", tags = { "Order management" }, description = "", responses = {
			@ApiResponse(responseCode = "200", description = "The order is succeffully updated", content = @Content(schema = @Schema(implementation = GetOrderTypeDtoResponse.class))),
			@ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
			@ApiResponse(responseCode = "404", description = "current code of order type doesn't exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))) })
	public Response update(OrderTypeDto orderTypeDto);

	@DELETE
	@Path("/{orderTypeCode}")
	@Operation(summary = "delete an existing order", tags = { "Order management" }, description = "", responses = {
			@ApiResponse(responseCode = "200", description = "The order is succeffully deleted", content = @Content(schema = @Schema(implementation = GetOrderTypeDtoResponse.class))),
			@ApiResponse(responseCode = "412", description = "code of order type is missing", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
			@ApiResponse(responseCode = "404", description = "current code of order type doesn't exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))) })
	public Response delete(@Parameter(required = true) @PathParam("orderTypeCode") String orderTypeCode);

	@POST
	@Path("/list")
	@Operation(summary = "Get order type matching the given criteria", tags = {
			"Order type" }, description = "Get order type matching the given criteria", responses = {
					@ApiResponse(responseCode = "200", description = "The search operation is succefully executed", content = @Content(schema = @Schema(implementation = GetListOrderTypeResponseDto.class))) })
	public Response list(PagingAndFiltering pagingAndFiltering);

	@GET
	@Path("/{orderTypeCode}")
	@Operation(summary = "Get order type matching the given order number", tags = {
			"Order type" }, description = "Get order type matching the given order number", responses = {
					@ApiResponse(responseCode = "200", description = "The order type is succefully retrieved", content = @Content(schema = @Schema(implementation = GetOrderTypeDtoResponse.class))),
					@ApiResponse(responseCode = "404", description = "Order type Does not exist", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class))) })
	public Response find(
			@Parameter(description = "The code of the order type", required = true) @PathParam("orderTypeCode") String orderTypeCode);
}
