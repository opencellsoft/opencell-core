package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductLineDtoResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.ProductStatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/catalog/product")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ProductRs extends IBaseRs{

	/**
	 * create new product
	 * @param productDto
	 * @return
	 */
	@POST
	@Path("/")
    @Operation(summary = "This endpoint allows to create a new product",
    tags = { "Product" },
    description ="creation of the product",
    responses = {
            @ApiResponse(responseCode="200", description = "the product successfully created",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "the product with code is missing"),
            @ApiResponse(responseCode = "500", description = "the product already existe with the given code")
    })
	ActionStatus addNewProduct(ProductDto productDto);
	
	/**
	 * 
	 * @param productDto
	 * @return
	 */
	@PATCH
	@Path("/")
    @Operation(summary = "This endpoint allows to update an existing product ",
    tags = { "Product" },
    description ="to update the product the status must be DRAFT otherwise exception will be thrown",
    responses = {
            @ApiResponse(responseCode="200", description = "the product successfully updated",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "500", description = "the status of the product is different to DRAFT")
    })
	ActionStatus updateProduct(ProductDto productDto);
	
	/**
	 * 
	 * @param codeProduct
	 * @param status
	 * @return
	 */
	@POST
	@Path("/{codeProduct}/update/status")
    @Operation(summary = "This endpoint allows to update status of existing product  ",
    tags = { "Product" },
    description ="the product with status DRAFT can be change to ACTIVE or CLOSED, if the product status is ACTIVE then the only value possible is CLOSED otherwise it will throw exception",
    responses = {
            @ApiResponse(responseCode="200", description = "the product successfully updated",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "500", description = "the status of the product is already closed")
    })
	ActionStatus updateStatus(@PathParam("codeProduct") String codeProduct,@QueryParam("status") ProductStatusEnum status);
	
	/**
	 * 
	 * @param codeProduct
	 * @return
	 */
	@GET
	@Path("/")
    @Operation(summary = "This endpoint allows to find an existing product  ",
    tags = { "Product" },
    description ="retrieving a product with its code",
    responses = {
            @ApiResponse(responseCode="200", description = "the product successfully updated",
                    content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class))),
            @ApiResponse(responseCode = "500", description = "the product with code in param does not exist")
    })
	GetProductDtoResponse findByCode(@QueryParam("codeProduct") String codeProduct);

	/**
	 * 
	 * @param id
	 * @return
	 */
    @DELETE
    @Path("/{idProductLine}")
    @Operation(summary = "This endpoint allows to remove an existing product line",
    tags = { "ProductLine" },
    description ="remove a product line with its code",
    responses = {
            @ApiResponse(responseCode="200", description = "the product line successfully deleted",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "500", description = "the product with code in param does not exist or the product line is attached to a product")
    })
    ActionStatus removeProductLine(@PathParam("idProductLine") Long id);

    /**
     * 
     * @param dto
     * @return
     */
	@POST
	@Path("/productLine")
    @Operation(summary = "This endpoint allows to create or update a product line",
    tags = { "ProductLine" },
    description ="create a product line if it doesn't exist or update an existing product line",
    responses = {
            @ApiResponse(responseCode="200", description = "the product line successfully created or updated",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "500", description = "the product with code in param does exist for a new product line ")
    })
    ActionStatus createOrUpdateProductLine(ProductLineDto dto);

	/**
	 * 
	 * @param code
	 * @return
	 */
	@GET
	@Path("/productLine")
    @Operation(summary = "This endpoint allows to retrieve a product line",
    tags = { "ProductLine" },
    description ="retrieving a product line with its code",
    responses = {
            @ApiResponse(responseCode="200", description = "the product line successfully created or updated",
                    content = @Content(schema = @Schema(implementation = GetProductLineDtoResponse.class))),
            @ApiResponse(responseCode = "500", description = "the product line with code in param does not exist")
    })
	GetProductLineDtoResponse findProductLineByCode(String code);
}
