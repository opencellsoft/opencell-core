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
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponseDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixesResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Web service for managing {@link org.meveo.model.catalog.PricePlanMatrix}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/pricePlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PricePlanRs extends IBaseRs {

    /**
     * Create a new price plan matrix
     * 
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(PricePlanMatrixDto postData);

    /**
     * Update an existing price plan matrix
     * 
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(PricePlanMatrixDto postData);

    /**
     * Find a price plan matrix with a given code
     * 
     * @param pricePlanCode The price plan's code
     * @return pricePlanMatrixDto Returns pricePlanMatrixDto containing pricePlan
     */
    @Path("/")
    @GET
    GetPricePlanResponseDto find(@QueryParam("pricePlanCode") String pricePlanCode);

    /**
     * Remove an existing price plan matrix with a given code
     * 
     * @param pricePlanCode The price plan's code
     * @return Request processing status
     */
    @Path("/{pricePlanCode}")
    @DELETE
    ActionStatus remove(@PathParam("pricePlanCode") String pricePlanCode);

    /**
     * List price plan matrix.
     * 
     * @param eventCode The charge's code linked to price plan.
     * @return Return pricePlanMatrixes
     */
    @Path("/list")
    @GET
    PricePlanMatrixesResponseDto listPricePlanByEventCode(@QueryParam("eventCode") String eventCode);

    /**
     * Create new or update an existing price plan matrix
     * 
     * @param postData The price plan matrix's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(PricePlanMatrixDto postData);

    /**
     * Enable a Price plan with a given code
     * 
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Price plan with a given code
     * 
     * @param code Price plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}