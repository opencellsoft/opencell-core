package org.meveo.api.rest.cpq;

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

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.cpq.GetCommercialRuleDtoResponse;
import org.meveo.api.dto.response.cpq.GetListCommercialRulesResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


/**
 * @author Rachid.AITYAAZZA
 * @version 11.0
 *
 */
@Path("/commercialRule")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CommercialRuleRs extends IBaseRs {

 
	@POST
	@Path("/")
    @Operation(summary = "This endpoint allows to create new commercialRule",
    tags = { "CommercialRules" },
    description ="Creating a new commercialRule",
    responses = {
            @ApiResponse(responseCode="200", description = "the CommercialRule successfully added",
            		content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for CommercialRuleDto.The required params are : code",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class)))
            
    })
	
	Response create(	@Parameter( name = "commercialRuleDto",
									description = "CommercialRule dto for a new insertion")CommercialRuleHeaderDTO commercialRuleDTO);
	
	@PUT
	@Path("/")
    @Operation(summary = "This endpoint allows to update an existing CommercialRule",
    description ="Updating an existing CommercialRule",
    tags = { "CommercialRules" },
    responses = {
            @ApiResponse(responseCode="200", description = "the CommercialRule successfully updated",
            		content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))),
            @ApiResponse(responseCode = "412", description = "missing required paramter for CommercialRuleDTO.The required params are : code",
            		content = @Content(schema = @Schema(implementation = MissingParameterException.class)))
    })
	Response update(@Parameter(description = "commercialRule dto for updating an existing commercialRule", required = true) CommercialRuleHeaderDTO commercialRuleDTO);
	
	@DELETE
	@Path("/{code}")
    @Operation(summary = "This endpoint allows to  delete an existing CommercialRule",
    description ="Deleting an existing CommercialRule with its code",
    tags = { "CommercialRules" },
    responses = {
            @ApiResponse(responseCode="200", description = "The CommercialRule successfully deleted",
            		content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "No CommercialRule found for the code parameter", 
    		content = @Content(schema = @Schema(implementation = BusinessException.class)))
    })
	Response delete(@Parameter(description = "contain the code of CommercialRule te be deleted by its code", required = true) @PathParam("code") String code);

	@GET
	@Path("/")
    @Operation(summary = "This endpoint allows to find a commercialRule by its code",
    description ="Finding a commercialRule by its code ",
    tags = { "CommercialRules" },
    responses = {
            @ApiResponse(responseCode="200", description = "The CommercialRule successfully retrieved",
            		content = @Content(schema = @Schema(implementation = GetCommercialRuleDtoResponse.class))),
    })
	Response findByCode(@Parameter(description = "retrieving a commercialRule with its code") @QueryParam("code") String code);
	
	   /**
     * List Commercial Rules matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of offer templates
     */
    @POST
    @Path("/list")
    @Operation(summary = "Get commercial rules matching the given criteria",
    tags = { "CommercialRules" },
    description ="Get commercial rules matching the given criteria",
    responses = {
            @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class)))
    })
    public Response list (PagingAndFiltering pagingAndFiltering);
    

 @GET
 @Path("/productRules")
 @Operation(summary = "Get product commercial rules",
 tags = { "CommercialRules" },
 description ="Get product commercial rules",
 responses = {
         @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class)))
 })
 public Response findProductRules (@Parameter(description = "offer code", required = false) @QueryParam("offerCode") String offerCode,
		 @Parameter(description = "product code", required = true) @QueryParam("productCode") String productCode,
		 @Parameter(description = "product version", required = false) @QueryParam("productVersion") Integer productVersion);
 
 @GET
 @Path("/attributeRules")
 @Operation(summary = "Get attribute commercial rules",
 tags = { "CommercialRules" },
 description ="Get attribute commercial rules",
 responses = {
         @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class)))
 })
 public Response findAttributeRules (@Parameter(description = "attribute code", required = false) @QueryParam("attributeCode") String attributeCode,
		 @Parameter(description = "product code", required = true) @QueryParam("productCode") String productCode);
 
 
 @GET
 @Path("/tagRules")
 @Operation(summary = "Get tag commercial rules",
 tags = { "CommercialRules" },
 description ="Get tag commercial rules",
 responses = {
         @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class)))
 })
 public Response findTagRules ( @Parameter(description = "tag code", required = true) @QueryParam("tagCode") String tagCode);
	
 @GET
 @Path("/offerRules")
 @Operation(summary = "Get offer commercial rules",
 tags = { "CommercialRules" },
 description ="Get offer commercial rules",
 responses = {
         @ApiResponse(responseCode="200", description = "The search operation is succefully executed",content = @Content(schema = @Schema(implementation = GetListCommercialRulesResponseDto.class)))
 })
 public Response findOfferRules ( @Parameter(description = "offer code", required = true) @QueryParam("offerCode") String offerCode);
	
	 
}
