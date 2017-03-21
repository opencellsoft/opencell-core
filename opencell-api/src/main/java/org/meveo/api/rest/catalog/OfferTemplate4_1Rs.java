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
import org.meveo.api.dto.catalog.OfferTemplate4_1Dto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.OfferTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/offerTemplate4_1")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OfferTemplate4_1Rs extends IBaseRs {

    /**
     * Create offer template.
     * 
     * @param postData
     * @return
     */
    @Path("/")
    @POST
    ActionStatus create(OfferTemplate4_1Dto postData);

    /**
     * Update offer template.
     * 
     * @param postData
     * @return
     */
    @Path("/")
    @PUT
    ActionStatus update(OfferTemplate4_1Dto postData);

    /**
     * Search offer template with a given code.
     * 
     * @param offerTemplateCode
     * @return
     */
    @Path("/")
    @GET
    GetOfferTemplateResponseDto find(@QueryParam("offerTemplateCode") String offerTemplateCode);

    /**
     * Remove offer template with a given code.
     * 
     * @param offerTemplateCode
     * @return
     */
    @Path("/{offerTemplateCode}")
    @DELETE
    ActionStatus remove(@PathParam("offerTemplateCode") String offerTemplateCode);

    /**
     * Create or update offer template based on a given code.
     * 
     * @param postData
     * @return
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(OfferTemplate4_1Dto postData);

}
