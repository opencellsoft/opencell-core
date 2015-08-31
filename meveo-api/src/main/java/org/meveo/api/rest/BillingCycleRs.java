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
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billingCycle")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface BillingCycleRs extends IBaseRs {

	/**
	 * Create billingCycle.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	public ActionStatus create(BillingCycleDto postData);

	/**
	 * Update billingCycle.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	public ActionStatus update(BillingCycleDto postData);

	/**
	 * Search for billingCycle with a given code.
	 * 
	 * @param billingCycleCode
	 * @return
	 */
	@Path("/")
	@GET
	public GetBillingCycleResponse find(@QueryParam("billingCycleCode") String billingCycleCode);

	/**
	 * Remove billingCycle with a given code.
	 * 
	 * @param billingCycleCode
	 * @return
	 */
	@Path("/{billingCycleCode}")
	@DELETE
	public ActionStatus remove(@PathParam("billingCycleCode") String billingCycleCode);
	
	@POST
	@Path("/createOrUpdate")
	public ActionStatus createOrUpdate(BillingCycleDto postData);

}
