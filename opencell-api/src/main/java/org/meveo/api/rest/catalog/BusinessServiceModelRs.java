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
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessServiceModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessServiceModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessServiceModelRs extends IBaseRs {

    /**
     * Create a new business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(BusinessServiceModelDto postData);

    /**
     * Update an existing business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(BusinessServiceModelDto postData);

    /**
     * Search for a business service model with a given code 
     * 
     * @param businessServiceModelCode The business service model's code
     * @return A business servie model
     */
    @Path("/")
    @GET
    GetBusinessServiceModelResponseDto find(@QueryParam("businessServiceModelCode") String businessServiceModelCode);

    /**
     * Remove an existing business service model with a given code 
     * 
     * @param businessServiceModelCode The business service model's code
     * @return Request processing status
     */
    @Path("/{businessServiceModelCode}")
    @DELETE
    ActionStatus remove(@PathParam("businessServiceModelCode") String businessServiceModelCode);

    /**
     * Create new or update an existing business service model
     * 
     * @param postData The business service model's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(BusinessServiceModelDto postData);

    /**
     * List business service model
     * 
     * @return A list of business service models
     */
    @GET
    @Path("/list")
    public MeveoModuleDtosResponse list();

    /**
     * Install business offer model module
     * 
     * @param moduleDto The business service model's data
     * @return Request processing status
     */
    @PUT
    @Path("/install")
    public ActionStatus install(BusinessServiceModelDto moduleDto);
}