package org.meveo.api.rest.catalog;

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

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/catalog/offerTemplateCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OfferTemplateCategoryRs extends IBaseRs {

    /**
     * Create a new offer template category
     * 
     * @param postData The offer template category's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(OfferTemplateCategoryDto postData);

    /**
     * Update an existing offer template category
     * 
     * @param postData The offer template category's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(OfferTemplateCategoryDto postData);

    /**
     * Find a offer template category with a given code
     * 
     * @param offerTemplateCategoryCode The offer template category's code
     * @return Return offerTemplateCategoryCodeDto containing offerTemplateCategoryCode
     */
    @Path("/")
    @GET
    GetOfferTemplateCategoryResponseDto find(@QueryParam("offerTemplateCategoryCode") String offerTemplateCategoryCode);

    /**
     * Remove an existing offer template category with a given code
     * 
     * @param offerTemplateCategoryCode The offer template category's code
     * @return Request processing status
     */
    @Path("/")
    @DELETE
    ActionStatus delete(@QueryParam("offerTemplateCategoryCode") String offerTemplateCategoryCode);

    /**
     * Create new or update an existing offer template category
     * 
     * @param postData The offer template category's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(OfferTemplateCategoryDto postData);

    /**
     * Enable a Offer template category with a given code
     * 
     * @param code Offer template category code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Offer template category with a given code
     * 
     * @param code Offer template category code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}