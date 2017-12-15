package org.meveo.api.rest.tmforum;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.BpmProductDto;
import org.meveo.api.dto.catalog.BsmServiceDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.serialize.RestDateParam;

/**
 * TMForum Product catalog API specification implementation. Note: only READ type methods are implemented.
 * 
 * @author Andrius Karpavicius
 */
@Path("/catalogManagement")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface CatalogRs {

    /**
     * Get a list of categories
     * 
     * @param info Http request context
     * @return A list of categories
     */
    @GET
    @Path("/category")
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
    public Response getCategory(@PathParam("code") String code, @Context UriInfo info);

    /**
     * Get a list of product offerings optionally filtering by some criteria
     * 
     * @param info Http request context
     * @return A list of product offerings matching search criteria
     */
    @GET
    @Path("/productOffering")
	public Response findProductOfferings(@QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo, @Context UriInfo info);

    /**
     * Get details of a single Offer template and validity dates. If no validity dates are provided, an offer template valid on a current date will be returned.
     * 
     * @param id Product offering code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @param info Http request context
     * @return Single product offering
     */
    @GET
    @Path("/productOffering/{id}")
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
    public Response getProductSpecification(@PathParam("id") String id, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo,
            @Context UriInfo info);

    /**
     * Create offer from BOM definition
     * 
     * @param postData BOM offer information
     * @return
     */
    @POST
    @Path("/createOfferFromBOM")
    public Response createOfferFromBOM(BomOfferDto postData);
    
    /**
     * Create service from BSM definition
     * 
     * @param postData BSM service information
     * @return
    */
    
    @POST
    @Path("/createServiceFromBSM")
    public Response createServiceFromBSM(BsmServiceDto postData); 
    
    @POST
    @Path("/createProductFromBPM")
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
    public Response getProductTemplate(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Create product template
     * 
     * @param postData product template information
     * @return
     */
    @POST
    @Path("/productTemplate")
    public Response createProductTemplate(ProductTemplateDto postData);

    /**
     * Create or update product template
     * 
     * @param postData product template information
     * @return
     */
    @POST
    @Path("/productTemplate/createOrUpdate")
    public Response createOrUpdateProductTemplate(ProductTemplateDto postData);

    /**
     * Update product template
     * 
     * @param postData product template information
     * @return
     */
    @PUT
    @Path("/productTemplate")
    public Response updateProductTemplate(ProductTemplateDto postData);

    /**
     * Delete a single productTemplate by its code and validity dates. If no validity dates are provided, a product template valid on a current date will be deleted.
     * 
     * @param code productTemplate code
     * @param validFrom Product template validity range - from date
     * @param validTo Procuct template validity range - to date
     * @return
     */
    @DELETE
    @Path("/productTemplate/{code}")
    public Response removeProductTemplate(@PathParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom, @QueryParam("validTo") @RestDateParam Date validTo);

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
    public Response listProductTemplate(@QueryParam("code") String code, @QueryParam("validFrom") @RestDateParam Date validFrom,
            @QueryParam("validTo") @RestDateParam Date validTo);

    /**
     * Get a single productChargeTemplate by its code
     * 
     * @param code productChargeTemplate code
     * @return Single productChargeTemplate information
     */
    @GET
    @Path("/productChargeTemplate/{code}")
    public Response getProductChargeTemplate(@PathParam("code") String code);

    /**
     * Create product charge template
     * 
     * @param postData product charge template information
     * @return
     */
    @POST
    @Path("/productChargeTemplate")
    public Response createProductChargeTemplate(ProductChargeTemplateDto postData);

    /**
     * Create or update product charge template
     * 
     * @param postData product charge template information
     * @return
     */
    @POST
    @Path("/productChargeTemplate/createOrUpdate")
    public Response createOrUpdateProductChargeTemplate(ProductChargeTemplateDto postData);

    /**
     * Update product charge template
     * 
     * @param postData product charge template information
     * @return
     */
    @PUT
    @Path("/productChargeTemplate")
    public Response updateProductChargeTemplate(ProductChargeTemplateDto postData);

    /**
     * Delete a single productChargeTemplate by its code
     * 
     * @param code productChargeTemplate code
     * @return
     */
    @DELETE
    @Path("/productChargeTemplate/{code}")
    public Response removeProductChargeTemplate(@PathParam("code") String code);

    /**
     * List all productChargeTemplates
     * 
     * @return
     */
    @GET
    @Path("/productChargeTemplate/list")
    public Response listProductChargeTemplate();

}