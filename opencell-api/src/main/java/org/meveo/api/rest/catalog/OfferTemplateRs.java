package org.meveo.api.rest.catalog;

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
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.OfferTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/offerTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OfferTemplateRs extends IBaseRs {

    /**
     * Create offer template.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(OfferTemplateDto postData);

    /**
     * Update offer template.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(OfferTemplateDto postData);

    /**
     * Search offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be returned.
     * 
     * @param offerTemplateCode The offer template's code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Return offerTemplateDto containing offerTemplate
     */
    @Path("/")
    @GET
    GetOfferTemplateResponseDto find(@QueryParam("offerTemplateCode") String offerTemplateCode, @QueryParam("validFrom") Date validFrom, @QueryParam("validTo") Date validTo);

    /**
     * Remove offer template with a given code and validity dates. If no validity dates are provided, an offer template valid on a current date will be deleted.
     * 
     * @param offerTemplateCode The offer template's code
     * @param validFrom Offer template validity range - from date
     * @param validTo Offer template validity range - to date
     * @return Request processing status
     */
    @Path("/{offerTemplateCode}")
    @DELETE
    ActionStatus remove(@PathParam("offerTemplateCode") String offerTemplateCode, @QueryParam("validFrom") Date validFrom, @QueryParam("validTo") Date validTo);

    /**
     * Create or update offer template based on a given code.
     * 
     * @param postData The offer template's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(OfferTemplateDto postData);

}
