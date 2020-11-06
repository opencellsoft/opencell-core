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
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductLineDtoResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            @ApiResponse(responseCode = "412", description = "the product with code is missing"),
            @ApiResponse(responseCode = "302", description = "the product already existe with the given code"),
            @ApiResponse(responseCode = "400", description = "Internat error")
    })
	Response addNewProduct(@Parameter(description = "product dto for a new insertion", required = true) ProductDto productDto);
	
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
            @ApiResponse(responseCode = "404", description = "Unknown producth"),
            @ApiResponse(responseCode = "400", description = "the status of the product is different to DRAFT")
    })
	Response updateProduct(@Parameter(description = "product dto for updating an existing product", required = true) ProductDto productDto);
	
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
            @ApiResponse(responseCode = "400", description = "the status of the product is already closed")
    })
	Response updateStatus(@Parameter @PathParam("codeProduct") String codeProduct,@Parameter @QueryParam("status") ProductStatusEnum status);
	
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
            @ApiResponse(responseCode = "400", description = "the product with code in param does not exist")
    })
	Response findByCode(@Parameter(description = "code product for searching an existing product", required = true) @QueryParam("codeProduct") String codeProduct);

	/**
	 * 
	 * @param codeProductLine
	 * @return
	 */
    @DELETE
    @Path("/{codeProductLine}")
    @Operation(summary = "This endpoint allows to remove an existing product line",
    tags = { "ProductLine" },
    description ="remove a product line with its code",
    responses = {
            @ApiResponse(responseCode="200", description = "the product line successfully deleted",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "unknown product line"),
            @ApiResponse(responseCode = "400", description = "the product line is attached to a product")
    })
    Response removeProductLine(@Parameter(description = "id product for deleting an existing product line", required = true) @PathParam("codeProductLine") String codeProductLine);

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
            @ApiResponse(responseCode = "400", description = "the product with code in param does exist for a new product line ")
    })
	Response createOrUpdateProductLine(@Parameter(description = "create new product line or update an existing product line", required = true) ProductLineDto dto);

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
            @ApiResponse(responseCode = "412", description = "codeProductLine parameter is missing"),
            @ApiResponse(responseCode = "404", description = "Unkonw product line"),
            @ApiResponse(responseCode = "400", description = "the product line with code in param does not exist")
    })
	Response findProductLineByCode(@Parameter(description = "find an existing product line", required = true) @QueryParam("codeProductLine") String code);
	
	
	 /**
     * 
     * @param ProductVersionDto
     * @return
     */
	@POST
	@Path("/productVersion")
    @Operation(summary = "This endpoint allows to create or update a product version",
    tags = { "productVersion" },
    description ="create a product version if it doesn't exist or update an existing product line",
    responses = {
            @ApiResponse(responseCode="200", description = "the product version successfully created or updated",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "Unkonw product to attach to product version"),
            @ApiResponse(responseCode = "400", description = "the product verion with product code and current version in param does not exist ")
    })
	Response createOrUpdateProductVersion(@Parameter(description = "create new product version or update an existing product version", required = true) ProductVersionDto postData);
 


	/**
	 * 
	 * @param productCode
	 * @param currentVersion
	 * @return
	 */
	@DELETE
	@Path("/productVersion/{productCode}/{currentVersion}")
	@Operation(summary = "This endpoint allows to remove an existing product product version",
	tags = { "ProductVersion"},
	description ="remove a product version with product code and current version",
	responses = {
	        @ApiResponse(responseCode="200", description = "the product version successfully deleted",
	        content = @Content(schema = @Schema(implementation = ActionStatus.class))),
	    	@ApiResponse(responseCode = "404", description = "Unknown product version")
	        ,
	    	@ApiResponse(responseCode = "400", description = "the product version with product code and current version in param does not exist or the product version is attached to a product")
	    	})
	Response removeProductVersion(@Parameter @PathParam("productCode") String productCode,@Parameter @PathParam("currentVersion") int currentVersion);
		
		
	
	/**
	 * 
	 * @param codeProduct
	 * @param status
	 * @param currentVersion
	 * @return
	 */
	@POST
	@Path("/productVersion/{productCode}/{currentVersion}")
    @Operation(summary = "This endpoint allows to update status of existing product version ",
    tags = { "ProductVersion" },
    description ="the product with status DRAFT can be change to PUBLIED or CLOSED ",
    responses = {
            @ApiResponse(responseCode="200", description = "the product version successfully updated"),
            @ApiResponse(responseCode = "404", description = "Unknown product version"),
            @ApiResponse(responseCode = "400", description = "the status of the product is already closed")
    })
	Response updateProductVersionStatus(@Parameter @PathParam("productCode") String codeProduct,
											@Parameter @PathParam("currentVersion") int currentVersion,
											@Parameter @QueryParam("status") VersionStatusEnum status);



	/**
	 * 
	 * @param productCode
	 * @param currentVersion
	 * @return
	 */
	@POST
	@Path("/productVersion/duplicate/{productCode}/{currentVersion}")
	@Operation(summary = "This endpoint allows to duplicate a product version",
	tags = { "productVersion" },
	description ="duplicate a product version",
	responses = {
	        @ApiResponse(responseCode="200", description = "the product version successfully duplicated"),
	        @ApiResponse(responseCode = "404", description = "the product verion with product code and current version in param does not exist ")
	})
	Response duplicateProductVersion(@Parameter @PathParam("productCode") String productCode,
										@Parameter @PathParam("currentVersion") int currentVersion);
	

	
}
