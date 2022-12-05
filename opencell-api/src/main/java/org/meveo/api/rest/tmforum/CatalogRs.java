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

import org.meveo.api.dto.catalog.*;
import org.meveo.api.dto.response.ProductChargeTemplatesResponseDto;
import org.meveo.api.dto.response.catalog.GetListProductTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.util.Date;

/**
 * TMForum Product catalog API specification implementation. Note: only READ type methods are implemented.
 * 
 * @author Andrius Karpavicius
 */
@Path("/catalogManagement")
@Tag(name = "Catalog", description = "@%Catalog")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CatalogRs extends IBaseRs {

    /**
     * Get a list of categories
     * 
     * @param info Http request context
     * @return A list of categories
     */
    @GET
    @Path("/category")
	@Operation(
			summary=" Get a list of categories  ",
			description=" Get a list of categories  ",
			operationId="    GET_Catalog_category",
			responses= {
				@ApiResponse(description=" A list of categories ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response findCategories(@Context UriInfo info);

    /**
     * Get a single category by its code
     * 
     * @param code Category code
     * @param info Http request context
     * @return Single category information
     */
    @GET
    @Path("/category/{code}")
	@Operation(
			summary=" Get a single category by its code  ",
			description=" Get a single category by its code  ",
			operationId="    GET_Catalog_category_{code}",
			responses= {
				@ApiResponse(description=" Single category information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response getCategory(@PathParam("code") String code, @Context UriInfo info);

    /**
     * Get a list of product offerings optionally filtering by some criteria
     * 
     * @param validFrom valid From option criteria date
     * @param validTo valid To option criteria date
     * @param info Http request context
     * @return A list of product offerings matching search criteria
     */
    @GET
    @Path("/productOffering")
	@Operation(
			summary=" Get a list of product offerings optionally filtering by some criteria  ",
			description=" Get a list of product offerings optionally filtering by some criteria  ",
			operationId="    GET_Catalog_productOffering",
			responses= {
				@ApiResponse(description=" A list of product offerings matching search criteria ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response findProductOfferings(@QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo, @Context UriInfo info);

    /**
     * Get details of a single Product template and validity dates. If no validity dates are provided, an Product template valid on a current date will be returned.
     * 
     * @param id Product offering code
     * @param validFrom Product template validity range - from date
     * @param validTo Product template validity range - to date
     * @param info Http request context
     * @return Single product offering
     */
    @GET
    @Path("/productOffering/{id}")
	@Operation(
			summary=" Get details of a single Product template and validity dates",
			description=" Get details of a single Product template and validity dates. If no validity dates are provided, an Product template valid on a current date will be returned.  ",
			operationId="    GET_Catalog_productOffering_{id}",
			responses= {
				@ApiResponse(description=" Single product offering ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response getProductOffering(@PathParam("id") String id, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo,
            @Context UriInfo info);

    /**
     * Get a list of product specifications optionally filtering by some criteria
     * 
     * @param info Http request context
     * @return A list of product specifications matching search criteria
     */
    @GET
    @Path("/productSpecification")
	@Operation(
			summary=" Get a list of product specifications optionally filtering by some criteria  ",
			description=" Get a list of product specifications optionally filtering by some criteria  ",
			operationId="    GET_Catalog_productSpecification",
			responses= {
				@ApiResponse(description=" A list of product specifications matching search criteria ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response findProductSpecifications(@Context UriInfo info);

    /**
     * Get details of a single product
     * 
     * @param id Product code
     * @param validFrom Product template validity range - from date
     * @param validTo Product template validity range - to date
     * @param info Http request context
     * @return A single product specification
     */
    @GET
    @Path("/productSpecification/{id}")
	@Operation(
			summary=" Get details of a single product  ",
			description=" Get details of a single product  ",
			operationId="    GET_Catalog_productSpecification_{id}",
			responses= {
				@ApiResponse(description=" A single product specification ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response getProductSpecification(@PathParam("id") String id, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo,
            @Context UriInfo info);

    /**
     * Create offer from BOM definition
     * 
     * @param postData BOM offer information
     * @return Response of the create offer BOM
     */
    @POST
    @Path("/createOfferFromBOM")
	@Operation(
			summary=" Create offer from BOM definition  ",
			description=" Create offer from BOM definition  ",
			operationId="    POST_Catalog_createOfferFromBOM",
			responses= {
				@ApiResponse(description=" Response of the create offer BOM ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createOfferFromBOM(BomOfferDto postData);

    /**
     * Create service from BSM definition
     * 
     * @param postData BSM service information
     * @return Response of the create Service BSM
     */
    @POST
    @Path("/createServiceFromBSM")
	@Operation(
			summary=" Create service from BSM definition  ",
			description=" Create service from BSM definition  ",
			operationId="    POST_Catalog_createServiceFromBSM",
			responses= {
				@ApiResponse(description=" Response of the create Service BSM ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createServiceFromBSM(BsmServiceDto postData);
  
    /**
     * Create product from BPM definition
     * 
     * @param postData BPM service information
     * @return Response of the create Service BPM
     */
    @POST
    @Path("/createProductFromBPM")
	@Operation(
			summary=" Create product from BPM definition  ",
			description=" Create product from BPM definition  ",
			operationId="    POST_Catalog_createProductFromBPM",
			responses= {
				@ApiResponse(description=" Response of the create Service BPM ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createProductFromBPM(BpmProductDto postData);

    /**
     * Get a single productTemplate by its code and validity dates. If no validity dates are provided, a product template valid on a current date will be deleted.
     * 
     * @param code productTemplate code
     * @param validFrom Product template validity range - from date
     * @param validTo Procuct template validity range - to date
     * @return Single productTemplate information
     */
    @GET
    @Path("/productTemplate/{code}")
	@Operation(
			summary=" Get a single productTemplate by its code and validity dates",
			description=" Get a single productTemplate by its code and validity dates. If no validity dates are provided, a product template valid on a current date will be deleted.  ",
			operationId="    GET_Catalog_productTemplate_{code}",
			responses= {
				@ApiResponse(description=" Single productTemplate information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response getProductTemplate(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Create product template
     * 
     * @param postData product template information
     * @return Response of the create Product Template
     */
    @POST
    @Path("/productTemplate")
	@Operation(
			summary=" Create product template  ",
			description=" Create product template  ",
			operationId="    POST_Catalog_productTemplate",
			responses= {
				@ApiResponse(description=" Response of the create Product Template ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createProductTemplate(ProductTemplateDto postData);

    /**
     * Create or update product template
     * 
     * @param postData product template information
     * @return Response of the create Product Template
     */
    @POST
    @Path("/productTemplate/createOrUpdate")
	@Operation(
			summary=" Create or update product template  ",
			description=" Create or update product template  ",
			operationId="    POST_Catalog_productTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Response of the create Product Template ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createOrUpdateProductTemplate(ProductTemplateDto postData);

    /**
     * Update product template
     * 
     * @param postData product template information
     * @return Response of the update Product Template
     */
    @PUT
    @Path("/productTemplate")
	@Operation(
			summary=" Update product template  ",
			description=" Update product template  ",
			operationId="    PUT_Catalog_productTemplate",
			responses= {
				@ApiResponse(description=" Response of the update Product Template ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response updateProductTemplate(ProductTemplateDto postData);

    /**
     * Delete a single productTemplate by its code and validity dates. If no validity dates are provided, a product template valid on a current date will be deleted.
     * 
     * @param code productTemplate code
     * @param validFrom Product template validity range - from date
     * @param validTo Procuct template validity range - to date
     * @return Response of the remove action
     */
    @DELETE
    @Path("/productTemplate/{code}")
	@Operation(
			summary=" Delete a single productTemplate by its code and validity dates",
			description=" Delete a single productTemplate by its code and validity dates. If no validity dates are provided, a product template valid on a current date will be deleted.  ",
			operationId="    DELETE_Catalog_productTemplate_{code}",
			responses= {
				@ApiResponse(description=" Response of the remove action ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response removeProductTemplate(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom,
            @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * List all product templates optionally filtering by code and validity dates. If neither date is provided, validity dates will not be considered. If only validFrom is
     * provided, a search will return products valid on a given date. If only validTo date is provided, a search will return products valid from today to a given date.
     * 
     * @param code Product template code for optional filtering
     * @param validFrom Validity range from date.
     * @param validTo Validity range to date.
     * @return A list of product templates
     */
    @GET
    @Path("/productTemplate/list")
	@Operation(
			summary=" List all product templates optionally filtering by code and validity dates",
			description=" List all product templates optionally filtering by code and validity dates. If neither date is provided, validity dates will not be considered. If only validFrom is provided, a search will return products valid on a given date. If only validTo date is provided, a search will return products valid from today to a given date.  ",
			operationId="    GET_Catalog_productTemplate_list",
			responses= {
				@ApiResponse(description=" A list of product templates ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response listProductTemplate(@QueryParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom,
            @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Gets a productTemplates list.
     *
     * @return Return productTemplates list
     */
    @GET
    @Path("/productTemplate/listGetAll")
	@Operation(
			summary=" Gets a productTemplates list. ",
			description=" Gets a productTemplates list. ",
			operationId="    GET_Catalog_productTemplate_listGetAll",
			responses= {
				@ApiResponse(description=" Return productTemplates list ",
						content=@Content(
									schema=@Schema(
											implementation= GetListProductTemplateResponseDto.class
											)
								)
				)}
	)
    GetListProductTemplateResponseDto listGetAllProductTemplates();

    /**
     * Enable a Product template with a given code
     * 
     * @param code Product template code
     * @param validFrom Product template validity range - from date
     * @param validTo Product template validity range - to date
     * @return Request processing status
     */
    @POST
    @Path("/productTemplate/{code}/enable")
	@Operation(
			summary=" Enable a Product template with a given code  ",
			description=" Enable a Product template with a given code  ",
			operationId="    POST_Catalog_productTemplate_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    Response enableProductTemplate(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Disable a Product template with a given code
     * 
     * @param code Product template code
     * @param validFrom Product template validity range - from date
     * @param validTo Product template validity range - to date
     * @return Request processing status
     */
    @POST
    @Path("/productTemplate/{code}/disable")
	@Operation(
			summary=" Disable a Product template with a given code  ",
			description=" Disable a Product template with a given code  ",
			operationId="    POST_Catalog_productTemplate_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    Response disableProductTemplate(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Get a single productChargeTemplate by its code
     * 
     * @param code productChargeTemplate code
     * @return Single productChargeTemplate information
     */
    @GET
    @Path("/productChargeTemplate/{code}")
	@Operation(
			summary=" Get a single productChargeTemplate by its code  ",
			description=" Get a single productChargeTemplate by its code  ",
			operationId="    GET_Catalog_productChargeTemplate_{code}",
			responses= {
				@ApiResponse(description=" Single productChargeTemplate information ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response getProductChargeTemplate(@PathParam("code") String code);

    /**
     * Create product charge template
     * 
     * @param postData product charge template information
     * @return Response of the create Product Charge Template
     */
    @POST
    @Path("/productChargeTemplate")
	@Operation(
			summary=" Create product charge template  ",
			description=" Create product charge template  ",
			operationId="    POST_Catalog_productChargeTemplate",
			responses= {
				@ApiResponse(description=" Response of the create Product Charge Template ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createProductChargeTemplate(ProductChargeTemplateDto postData);

    /**
     * Create or update product charge template
     * 
     * @param postData product charge template information
     * @return Response of the create or update Product Charge Template
     */
    @POST
    @Path("/productChargeTemplate/createOrUpdate")
	@Operation(
			summary=" Create or update product charge template  ",
			description=" Create or update product charge template  ",
			operationId="    POST_Catalog_productChargeTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" Response of the create or update Product Charge Template ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response createOrUpdateProductChargeTemplate(ProductChargeTemplateDto postData);

    /**
     * Update product charge template
     * 
     * @param postData product charge template information
     * @return Response of the update Product Charge Template
     */
    @PUT
    @Path("/productChargeTemplate")
	@Operation(
			summary=" Update product charge template  ",
			description=" Update product charge template  ",
			operationId="    PUT_Catalog_productChargeTemplate",
			responses= {
				@ApiResponse(description=" Response of the update Product Charge Template ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response updateProductChargeTemplate(ProductChargeTemplateDto postData);

    /**
     * Delete a single productChargeTemplate by its code
     * 
     * @param code productChargeTemplate code
     * @return Response of the delete action
     */
    @DELETE
    @Path("/productChargeTemplate/{code}")
	@Operation(
			summary=" Delete a single productChargeTemplate by its code  ",
			description=" Delete a single productChargeTemplate by its code  ",
			operationId="    DELETE_Catalog_productChargeTemplate_{code}",
			responses= {
				@ApiResponse(description=" Response of the delete action ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response removeProductChargeTemplate(@PathParam("code") String code);

    /**
     * List all productChargeTemplates
     * 
     * @return List of charge template
     */
    @GET
    @Path("/productChargeTemplate/list")
	@Operation(
			summary=" List all productChargeTemplates  ",
			description=" List all productChargeTemplates  ",
			operationId="    GET_Catalog_productChargeTemplate_list",
			responses= {
				@ApiResponse(description=" List of charge template ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    public Response listProductChargeTemplate();

    /**
     * Gets a productChargeTemplate list.
     *
     * @return Return productChargeTemplate list
     */
    @GET
    @Path("/productChargeTemplate/listGetAll")
	@Operation(
			summary=" Gets a productChargeTemplate list. ",
			description=" Gets a productChargeTemplate list. ",
			operationId="    GET_Catalog_productChargeTemplate_listGetAll",
			responses= {
				@ApiResponse(description=" Return productChargeTemplate list ",
						content=@Content(
									schema=@Schema(
											implementation= ProductChargeTemplatesResponseDto.class
											)
								)
				)}
	)
    ProductChargeTemplatesResponseDto listGetAllPCTemplates();

    /**
     * Enable a Product charge template with a given code
     * 
     * @param code Product charge template code
     * @return Request processing status
     */
    @POST
    @Path("/productChargeTemplate/{code}/enable")
	@Operation(
			summary=" Enable a Product charge template with a given code  ",
			description=" Enable a Product charge template with a given code  ",
			operationId="    POST_Catalog_productChargeTemplate_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    Response enableProductChargeTemplate(@PathParam("code") String code);

    /**
     * Disable a Product charge template with a given code
     * 
     * @param code Product charge template code
     * @return Request processing status
     */
    @POST
    @Path("/productChargeTemplate/{code}/disable")
	@Operation(
			summary=" Disable a Product charge template with a given code  ",
			description=" Disable a Product charge template with a given code  ",
			operationId="    POST_Catalog_productChargeTemplate_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= Response.class
											)
								)
				)}
	)
    Response disableProductChargeTemplate(@PathParam("code") String code);
    
}
