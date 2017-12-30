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
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
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
     * @return A business offer model
     */
    @Path("/")
    @GET
    GetBusinessOfferModelResponseDto find(@QueryParam("businessOfferModelCode") String businessOfferModelCode);


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
    MeveoModuleDtosResponse list();

    /**
     * Install business offer model module.
     * @param moduleDto business offer model
     * @return Request processing status
     */
    @PUT
    @Path("/install")
    ActionStatus install(BusinessOfferModelDto moduleDto);
}
