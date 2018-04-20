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
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponseDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlansResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/catalog/discountPlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface DiscountPlanRs extends IBaseRs {

    /**
     * Create a new discount plan
     * 
     * @param postData The discount plan's data
     * @return Request processing status
     */
    @Path("/")
    @POST
    ActionStatus create(DiscountPlanDto postData);

    /**
     * Update an existing discount plan
     * 
     * @param postData The discount plan's data
     * @return Request processing status
     */
    @Path("/")
    @PUT
    ActionStatus update(DiscountPlanDto postData);

    /**
     * Find a discount plan with a given code 
     * 
     * @param discountPlanCode The discount plan's code
     * @return Return discount plan
     */
    @Path("/")
    @GET
    GetDiscountPlanResponseDto find(@QueryParam("discountPlanCode") String discountPlanCode);

    /**
     * Remove an existing discount plan with a given code 
     * 
     * @param discountPlanCode The discount plan's code
     * @return Request processing status
     */
    @Path("/")
    @DELETE
    ActionStatus remove(@QueryParam("discountPlanCode") String discountPlanCode);

    /**
     * Create new or update an existing discount plan
     * 
     * @param postData The discount plan's data
     * @return Request processing status
     */
    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(DiscountPlanDto postData);

    /**
     * List discount plan
     * 
     * @return A list of discount plans
     */
    @Path("/list")
    @GET
    GetDiscountPlansResponseDto list();

    /**
     * Enable a Discount plan with a given code
     * 
     * @param code Discount plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Discount plan with a given code
     * 
     * @param code Discount plan code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

}
