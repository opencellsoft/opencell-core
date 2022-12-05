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
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.response.cpq.GetAttributeDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


/**
 * @author Mbarek-Ay
 * @version 10.0
 *
 */
@Path("/attributes")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface  AttributeRs extends IBaseRs {

 
	@POST
	@Path("/")
    @Operation(summary = "This endpoint allows to create new attribute",
    tags = { "Attribute" },
    description ="Creating a new attribute",
    responses = {
            @ApiResponse(responseCode="200", description = "the Attribute successfully added",
            		content = @Content(schema = @Schema(implementation = GetAttributeDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for AttributeDto.The required params are : code",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "400", description = "No grouped attribute is found for the parameter GroupedAttributeCode", 
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class)))
            
    })
	
	Response create(	@Parameter( name = "attributeDto",
									description = "Attribute dto for a new insertion")AttributeDTO attributeDTO);
	
	@PUT
	@Path("/")
    @Operation(summary = "This endpoint allows to update an existing Attribute",
    description ="Updating an existing Attribute",
    tags = { "Attribute" },
    responses = {
            @ApiResponse(responseCode="200", description = "the Attribute successfully updated",
            		content = @Content(schema = @Schema(implementation = GetAttributeDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for AttributeDTO.The required params are : code",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class))),
            @ApiResponse(responseCode = "400", description = "No grouped attribute is found for the parameter GroupedAttributeCode", 
    		content = @Content(schema = @Schema(implementation = MissingParameterException.class)))
    })
	Response update(@Parameter(description = "attribute dto for updating an existing attribute", required = true) AttributeDTO attributeDTO);
	
	@DELETE
	@Path("/{code}")
    @Operation(summary = "This endpoint allows to  delete an existing Attribute",
    description ="Deleting an existing Attribute with its code",
    tags = { "Attribute" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Attribute successfully deleted",
            		content = @Content(schema = @Schema(implementation = GetAttributeDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "No Attribute found for the code parameter", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response delete(@Parameter(description = "contain the code of Attribute te be deleted by its code", required = true) @PathParam("code") String code);

	@GET
	@Path("/{code}")
    @Operation(summary = "This endpoint allows to find an attribute by its code",
    description ="Finding an attribute by its code ",
    tags = { "Attribute" },
    responses = {
            @ApiResponse(responseCode="200", description = "The Attribute successfully retrieved",
            		content = @Content(schema = @Schema(implementation = GetAttributeDtoResponse.class))),
    })
	Response findByCode(@Parameter(description = "retrieving a attribute with its code") @PathParam("code") String code);
	
	
    
    @POST
    @Path("/cpq/offers/{productCode}/{productVersion}")
    @Operation(summary = "Get attributes related to the given product that match the quote offer context",
    tags = { "Catalog browsing" },
    description ="Get attributes related to the given product that match the quote offer context",
    responses = {
            @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "billingAccountCode does not exist"),
            @ApiResponse(responseCode = "404", description = "offerCode does not exist"),
            @ApiResponse(responseCode = "404", description = "productCode does not exist"),
            @ApiResponse(responseCode = "404", description = "selected service does not exist")
    })
    public Response listPost(@Parameter(description = "product code", required = true) @PathParam("productCode") String productCode,
    		@Parameter(description = "product version", required = true) @PathParam("productVersion") String productVersion,
    		@Parameter(description = "The Offer context", required = false) OfferContextDTO quoteContext);
	
	 
}
