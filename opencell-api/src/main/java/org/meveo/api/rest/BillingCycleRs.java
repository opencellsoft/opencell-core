package org.meveo.api.rest;

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
import org.meveo.api.dto.BillingCycleDto;
import org.meveo.api.dto.response.GetBillingCycleResponse;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billingCycle")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BillingCycleRs extends IBaseRs {

    /**
     * Create a new billing cycle.
     * 
     * @param postData billing cycle dto
     * @return action status
     */
    @Path("/")
    @POST
    public ActionStatus create(BillingCycleDto postData);

    /**
     * Update an existing billing cycle.
     * 
     * @param postData billing cycle
     * @return actioon result
     */
    @Path("/")
    @PUT
    public ActionStatus update(BillingCycleDto postData);

    /**
     * Search for billing cycle with a given code.
     * 
     * @param billingCycleCode The billing cycle's code
     * @return billing cycle if exists
     */
    @Path("/")
    @GET
    public GetBillingCycleResponse find(@QueryParam("billingCycleCode") String billingCycleCode);

    /**
     * Remove an existing billing cycle with a given code.
     * 
     * @param billingCycleCode The billing cycle's code
     * @return action result
     */
    @Path("/{billingCycleCode}")
    @DELETE
    public ActionStatus remove(@PathParam("billingCycleCode") String billingCycleCode);

    /**
     * Create new or update an existing billing cycle with a given code
     * 
     * @param postData The billing cycle's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(BillingCycleDto postData);

}
