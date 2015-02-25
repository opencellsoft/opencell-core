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
import org.meveo.api.dto.catalog.PricePlanDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.catalog.PricePlanMatrix}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/pricePlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface PricePlanRs extends IBaseRs {

	/**
	 * Create price plan.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	ActionStatus create(PricePlanDto postData);

	/**
	 * Update price plan.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	ActionStatus update(PricePlanDto postData);

	/**
	 * Search price plan with a given id.
	 * 
	 * @param id
	 * @return
	 */
	@Path("/")
	@GET
	GetPricePlanResponse find(@QueryParam("pricePlanCode") String pricePlanCode);

	/**
	 * Remove price plan with a given id.
	 * 
	 * @param id
	 * @return
	 */
	@Path("/{pricePlanCode}")
	@DELETE
	ActionStatus remove(@PathParam("pricePlanCode") String pricePlanCode);

}
