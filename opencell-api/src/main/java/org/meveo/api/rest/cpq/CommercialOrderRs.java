package org.meveo.api.rest.cpq;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.cpq.order.CommercialOrderDto;
import org.meveo.api.dto.response.cpq.GetQuoteDtoResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;

import io.swagger.v3.oas.annotations.Operation;
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
	 @Operation(summary = "Create a quote",
	    tags = { "Order management" },
	    description ="",
	    responses = {
	            @ApiResponse(responseCode="200", description = "The order is succeffully created",content = @Content(schema = @Schema(implementation = GetQuoteDtoResponse.class))),
	            @ApiResponse(responseCode = "412", description = "Missing required parameters", content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
	            @ApiResponse(responseCode = "302", description = "The order already exist", content = @Content(schema = @Schema(implementation = EntityAlreadyExistsException.class))),
	            @ApiResponse(responseCode = "404", description = "One of attached fields is unknown", content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)))
	    })
	public Response create(CommercialOrderDto orderDto);
}
