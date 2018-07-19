package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessOfferModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessOfferModelRs extends IBaseRs {

    /**
     * Create a new business offer model.
     * 
     * @param postData The business offer model's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(BusinessOfferModelDto postData);

    /**
     * Update an existing business offer model.
     * 
     * @param postData The business offer model's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(BusinessOfferModelDto postData);

    /**
     * Remove an existing business offer model with a given code.
     * 
     * @param businessOfferModelCode The business offer model's code
     * @param loadOfferServiceTemplate if true loads the services
     * @param loadOfferProductTemplate if true loads the products
     * @param loadServiceChargeTemplate if true load the service charges
     * @param loadProductChargeTemplate if true load the product charges
     * @return A business offer model
     */
    @Path("/")
    @GET
    GetBusinessOfferModelResponseDto find(@QueryParam("businessOfferModelCode") String businessOfferModelCode,
            @QueryParam("loadOfferServiceTemplate") @DefaultValue("false") boolean loadOfferServiceTemplate, @QueryParam("loadOfferProductTemplate") @DefaultValue("false") boolean loadOfferProductTemplate,
            @QueryParam("loadServiceChargeTemplate") @DefaultValue("false") boolean loadServiceChargeTemplate, @QueryParam("loadProductChargeTemplate") @DefaultValue("false") boolean loadProductChargeTemplate);


    /**
     * Remove an existing business offer model with a given code.
     * 
     * @param businessOfferModelCode The business offer model's code
     * @return Request processing status
     */
    @Path("/{businessOfferModelCode}")
    @DELETE
    ActionStatus remove(@PathParam("businessOfferModelCode") String businessOfferModelCode);

    /**
     * Create new or update an existing business offer model.
     * 
     * @param postData The business offer model's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(BusinessOfferModelDto postData);

    /**
     * List business offer models.
     * 
     * @return A list of business offer models
     */
    @GET
    @Path("/list")
    MeveoModuleDtosResponse listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);
    
    /**
     * List business offer models.
     * 
     * @return A list of business offer models
     */
    @POST
    @Path("/list")
    MeveoModuleDtosResponse listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Install business offer model module.
     * @param moduleDto business offer model
     * @return Request processing status
     */
    @PUT
    @Path("/install")
    ActionStatus install(BusinessOfferModelDto moduleDto);
}
