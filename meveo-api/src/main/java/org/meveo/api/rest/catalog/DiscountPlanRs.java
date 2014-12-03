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
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.catalog.DiscountPlanMatrix}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/discountPlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface DiscountPlanRs extends IBaseRs {

	/**
	 * Create discount plan.
	 * 
	 * @param postData
	 * @return The id of the newly create discount plan.
	 */
	@Path("/")
	@POST
	ActionStatus create(DiscountPlanDto postData);

	/**
	 * Update discount plan.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	ActionStatus update(DiscountPlanDto postData);

	/**
	 * Search discount plan with a given id.
	 * 
	 * @param id
	 * @return
	 */
	@Path("/")
	@GET
	GetDiscountPlanResponse find(@QueryParam("id") Long id);

	/**
	 * Remove discount plan with a given id.
	 * 
	 * @param id
	 * @return
	 */
	@Path("/{id}")
	@DELETE
	ActionStatus remove(@PathParam("id") Long id);

}
