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
import org.meveo.api.rest.IBaseWs;
import org.meveo.api.rest.security.WSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/discountPlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@WSSecured
public interface DiscountPlanWs extends IBaseWs {

	@Path("/")
	@POST
	ActionStatus create(DiscountPlanDto postData);

	@Path("/")
	@PUT
	ActionStatus update(DiscountPlanDto postData);

	@Path("/")
	@GET
	GetDiscountPlanResponse find(@QueryParam("id") Long id);

	@Path("/{id}")
	@DELETE
	ActionStatus remove(@PathParam("id") Long id);

}
