package org.meveo.api.rest.tmforum;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.meveo.api.dto.catalog.BomOfferDto;
import org.meveo.api.dto.catalog.ProductChargeTemplateDto;
import org.meveo.api.dto.catalog.ProductTemplateDto;
import org.meveo.api.rest.security.RSSecured;

/**
 * TMForum Product catalog API specification implementation. Note: only READ type methods are implemented.
 * 
 * @author Andrius Karpavicius
 */
@Path("/catalogManagement")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
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
    public Response findProductOfferings(@Context UriInfo info);

    /**
     * Get details of a single product offering
     * 
     * @param id Product offering code
     * @param info Http request context
     * @return Single product offering
     */
    @GET
    @Path("/productOffering/{id}")
    public Response getProductOffering(@PathParam("id") String id, @Context UriInfo info);

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
     * @param info Http request context
     * @return A single product specification
     */
    @GET
    @Path("/productSpecification/{id}")
    public Response getProductSpecification(@PathParam("id") String id, @Context UriInfo info);

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
     * Get a single productTemplate by its code
     * 
     * @param code productTemplate code
     * @return Single productTemplate information
     */
    @GET
    @Path("/productTemplate/{code}")
    public Response getProductTemplate(@PathParam("code") String code);

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
     * Delete a single productTemplate by its code
     * 
     * @param code productTemplate code
     * @return 
     */
    @DELETE
    @Path("/productTemplate/{code}")
    public Response removeProductTemplate(@PathParam("code") String code);
    
    
    /**
     * List all  productTemplates
     * @return 
     */
    @GET
    @Path("/productTemplate/list")
    public Response listProductTemplate();    
    
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
     * List all  productChargeTemplates
     * @return 
     */
    @GET
    @Path("/productChargeTemplate/list")
    public Response listProductChargeTemplate();  

}