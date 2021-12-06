package org.meveo.api.rest.catalog;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.ProductChargeTemplateMappingDto;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetCpqOfferResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductVersionsResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductLineDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@Path("/catalog/products")
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
                    content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "the product with code is missing"),
            @ApiResponse(responseCode = "302", description = "the product already existe with the given code"),
            @ApiResponse(responseCode = "400", description = "Internat error"),
            @ApiResponse(responseCode = "404", description = "one of the entites doesn't exist : ")
    })
	Response createProduct(@Parameter(description = "product dto for a new insertion", required = true) ProductDto productDto);
	

	/**
	 * @param productCode
	 * @param duplicateHierarchy
	 * @param preserveCode
	 * @return
	 */
	@POST
	@Path("/{productCode}/duplication")
	@Operation(summary = "This endpoint allows to duplicate a product",
	tags = { "Product" },
	description ="duplicate a product with the published its version or a recent version",
	responses = {
	        @ApiResponse(responseCode="200", description = "the product successfully duplicated", content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class))),
	        @ApiResponse(responseCode = "404", description = "the product with product code in param does not exist ", 
	        	content = @Content(schema = @Schema(implementation = EntityDoesNotExistsException.class)) )
	})
	Response duplicateProduct(@Parameter(description = "code of the product that will be duplicate", required = true) @PathParam("productCode") String productCode,
								@Parameter(description = "copy the hierarchy of the product") @QueryParam("duplicateHierarchy") boolean duplicateHierarchy, 
								@Parameter(description = "preserve code of product") @QueryParam("preserveCode") boolean preserveCode);
	
	/**
	 * 
	 * @param productDto
	 * @return
	 */
	@PUT
	@Path("/{productCode}")
    @Operation(summary = "This endpoint allows to update a product ",
    tags = { "Product" },
    description ="to update the product the status must be DRAFT otherwise exception will be thrown",
    responses = {
            @ApiResponse(responseCode="200", description = "the product successfully updated",
                    content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unknown producth"),
            @ApiResponse(responseCode = "400", description = "the status of the product is different to DRAFT")
    })
	Response updateProduct(@Parameter(description = "code of the product to be updated", required = true) @PathParam("productCode") String productCode, 
							@Parameter(description = "product dto for updating a product", required = true) ProductDto productDto);
	
	/**
	 * 
	 * @param productCode
	 * @param status
	 * @return
	 */
	@PUT
	@Path("/{productCode}/status/{status}")
    @Operation(summary = "This endpoint allows to update product status  ",
    tags = { "Product" },
    description ="the product with status DRAFT can be change to ACTIVE or CLOSED, if the product status is ACTIVE then the only value possible is CLOSED otherwise it will throw exception",
    responses = {
            @ApiResponse(responseCode="200", description = "the product successfully updated",
                    content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "the status of the product is already closed")
    })
	Response updateProductStatus(@Parameter @PathParam("productCode") String productCode,@Parameter @PathParam("status") ProductStatusEnum status);
	
	/**
	 * 
	 * @param productCode
	 * @return
	 */
	@GET
	@Path("/{productCode}")
    @Operation(summary = "This endpoint allows to find an existing product  ",
    tags = { "Product" },
    description ="retrieving a product with its code",
    responses = {
            @ApiResponse(responseCode="200", description = "The search operation is succefully executed",
                    content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "the product with code in param does not exist")
    })
	Response findProductByCode(@Parameter(description = "code product for searching an existing product", required = true) @PathParam("productCode") String productCode);
	
	
    /**
     * List offerTemplates matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of offer templates
     */
    @GET
    @Path("/")
    @Operation(summary = "Get products matching the given criteria",
    tags = { "Product" },
    description ="Get products matching the given criteria",
    responses = {
            @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetListProductsResponseDto.class)))
    })
    public Response listProducts(PagingAndFiltering pagingAndFiltering);
    

    
	/**
	 * 
	 * @param productCode
	 * @return
	 */
    @DELETE
    @Path("/{productCode}")
    @Operation(summary = "This endpoint allows to remove an existing product",
    tags = { "Product" },
    description ="remove a product by its code",
    responses = {
            @ApiResponse(responseCode="200", description = "the product is successfully deleted",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "unknown product line"),
            @ApiResponse(responseCode = "400", description = "the product is attached to an offer")
    })
    Response removeProduct(@Parameter(description = "product code", required = true) @PathParam("productCode") String productCode);
    
	/**
	 * 
	 * @param productLineCode
	 * @return
	 */
    @DELETE
    @Path("/productLines/{productLineCode}")
    @Operation(summary = "This endpoint allows to remove an existing product line",
    tags = { "Product" },
    description ="remove a product line by its code",
    responses = {
            @ApiResponse(responseCode="200", description = "the product line is successfully deleted",
                    content = @Content(schema = @Schema(implementation = ActionStatus.class))),
            @ApiResponse(responseCode = "404", description = "unknown product line"),
            @ApiResponse(responseCode = "400", description = "the product line is attached to a product")
    })
    Response removeProductLine(@Parameter(description = "productLine code", required = true) @PathParam("productLineCode") String productLineCode);

    /**
     * 
     * @param dto
     * @return
     */
	@POST
	@Path("/productLines")
    @Operation(summary = "This endpoint allows to create or update a product line",
    tags = { "Product" },
    description ="create a product line if it doesn't exist or update an existing product line",
    responses = {
            @ApiResponse(responseCode="200", description = "the product line successfully created or updated",
                    content = @Content(schema = @Schema(implementation = GetProductLineDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "the product with code in param does exist for a new product line ")
    })
	Response createOrUpdateProductLine(@Parameter(description = "create new product line or update an existing product line", required = true) ProductLineDto dto);

	/**
	 * 
	 * @param code
	 * @return
	 */
	@GET
	@Path("/productLines/{productLineCode}")
    @Operation(summary = "This endpoint allows to retrieve a product line",
    tags = { "Product" },
    description ="retrieving a product line with its code",
    responses = {
            @ApiResponse(responseCode="200", description = "The search operation is succefully executed",
                    content = @Content(schema = @Schema(implementation = GetProductLineDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "productLineCode parameter is missing"),
            @ApiResponse(responseCode = "404", description = "Unkonw product line"),
            @ApiResponse(responseCode = "400", description = "the product line with code in param does not exist")
    })
	Response findProductLineByCode(@Parameter(description = "find an existing product line", required = true) @QueryParam("productLineCode") String code);
	
	
	 /**
     * 
     * @param ProductVersionDto
     * @return
     */
	@POST
	@Path("/productVersions")
    @Operation(summary = "This endpoint allows to create or update a product version",
    tags = { "Product" },
    description ="create a product version if it doesn't exist or update an existing product line",
    responses = {
            @ApiResponse(responseCode="200", description = "the product version successfully created or updated",
                    content = @Content(schema = @Schema(implementation = GetProductVersionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unkonw product to attach to product version"),
            @ApiResponse(responseCode = "400", description = "the product verion with product code and current version in param does not exist ")
    })
	Response createOrUpdateProductVersion(@Parameter(description = "create new product version or update an existing product version", required = true) ProductVersionDto postData);
 


	/**
	 * 
	 * @param productCode
	 * @param productVersion
	 * @return
	 */
	@DELETE
	@Path("/{productCode}/productVersions/{productVersion}")
	@Operation(summary = "This endpoint allows to remove a product version",
	tags = { "Product"},
	description ="remove a product version with product code and current version",
	responses = {
	        @ApiResponse(responseCode="200", description = "the product version successfully deleted",
	        content = @Content(schema = @Schema(implementation = ActionStatus.class))),
	    	@ApiResponse(responseCode = "404", description = "Unknown product version")
	        ,
	    	@ApiResponse(responseCode = "400", description = "the product version with product code and current version in param does not exist or the product version is attached to a product")
	    	})
	Response removeProductVersion(@Parameter @PathParam("productCode") String productCode,@Parameter @PathParam("productVersion") int productVersion);
		
		
	
	/**
	 * 
	 * @param productCode
	 * @param status
	 * @param productVersion
	 * @return
	 */
	@PUT
	@Path("/{productCode}/productVersions/{productVersion}/status/{status}")
    @Operation(summary = "This endpoint allows to update the product version status",
    tags = { "Product" },
    description ="the product with status DRAFT can be change to PUBLIED or CLOSED ",
    responses = {
            @ApiResponse(responseCode="200", description = "the product version successfully updated",  content = @Content(schema = @Schema(implementation = GetProductVersionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unknown product version"),
            @ApiResponse(responseCode = "400", description = "the status of the product is already closed")
    })
	Response updateProductVersionStatus(@Parameter @PathParam("productCode") String productCode,
											@Parameter @PathParam("productVersion") int productVersion,
											@Parameter @PathParam("status") VersionStatusEnum status);



	/**
	 * 
	 * @param productCode
	 * @param productVersion
	 * @return
	 */
	@POST
	@Path("/{productCode}/productVersions/{productVersion}/duplication")
	@Operation(summary = "This endpoint allows to duplicate a product version",
	tags = { "Product" },
	description ="duplicate a product version",
	responses = {
	        @ApiResponse(responseCode="200", description = "the product version successfully duplicated"),
	        @ApiResponse(responseCode = "404", description = "the product verion with product code and current version in param does not exist ")
	})
	Response duplicateProductVersion(@Parameter @PathParam("productCode") String productCode,
										@Parameter @PathParam("productVersion") int productVersion);
	
	/**
	 * 
	 * @param productCode
	 * @param productVersion
	 * @return
	 */
	@GET
	@Path("/{productCode}/productVersions/{productVersion}")
	@Operation(summary = "This endpoint allows to find a product version",
	tags = { "Product" },
	description ="find a product version",
	responses = {
	        @ApiResponse(responseCode="200", description = "The search operation is succefully executed",  content = @Content(schema = @Schema(implementation = GetProductVersionResponse.class))),
	        @ApiResponse(responseCode = "404", description = "the product verion with product code and current version in param does not exist ")
	})
	Response findProductVersion(@Parameter @PathParam("productCode") String productCode,
										@Parameter @PathParam("productVersion") int productVersion);
	
	/**
	 * 
	 * @param productCode
	 * @param productVersion
	 * @return
	 */
	@GET
	@Path("/{productCode}/productVersions")
	@Operation(summary = "This endpoint allows to find all product versions related to a product",
	tags = { "Product" },
	description ="find product versions by product code",
	responses = {
	        @ApiResponse(responseCode="200", description = "The search operation is succefully executed",  content = @Content(schema = @Schema(implementation = GetListProductVersionsResponseDto.class)))
	})
	Response findProductVersions(@Parameter @PathParam("productCode") String productCode);
	

    @POST
    @Path("/cpq/offers")
    @Operation(summary = "Get products with their attributes that match the quote offer context",
    tags = { "Catalog browsing" },
    description ="Get products with their attributes that match the offer context",
    responses = {
            @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetCpqOfferResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "offerCode does not exist")
    })
    public Response listPost(@Parameter(description = "The Offer context", required = false) OfferContextDTO quoteContext);
    
    @POST
    @Path("/{productCode}/addCharges")
    @Operation(summary = "Allow to add existing charges from existing product",
    tags = {"Catalog browsing"},
    description = "Allow to add existing charges to an existing product",
    responses = {
    		@ApiResponse(responseCode = "200", description = "All charges was added", content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class ))),
            @ApiResponse(responseCode = "404", description = "product or one of the charges doesn't exist")
    		
    })
    public Response addCharges(@PathParam("productCode") String productCode, List<ProductChargeTemplateMappingDto> productChargeTemplateMapping);
    
    @POST
    @Path("/{productCode}/removeCharges")
    @Operation(summary = "Allow to remove existing charges from existing product",
    tags = {"Catalog browsing"},
    description = "remove to add existing charges from existing product",
    responses = {
    		@ApiResponse(responseCode = "200", description = "All charges was removed", content = @Content(schema = @Schema(implementation = GetProductDtoResponse.class ))),
            @ApiResponse(responseCode = "404", description = "product or one of the charges doesn't exist")
    		
    })
    public Response removeCharge(@PathParam("productCode") String productCode, List<String> chargeCodes);

	
}
