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
 * @author Edward P. Legaspi
 **/
@Path("/catalog/pricePlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface PricePlanRs extends IBaseRs {

	@Path("/")
	@POST
	ActionStatus create(PricePlanDto postData);

	@Path("/")
	@PUT
	ActionStatus update(PricePlanDto postData);

	@Path("/")
	@GET
	GetPricePlanResponse find(@QueryParam("id") Long id);

	@Path("/{id}")
	@DELETE
	ActionStatus remove(@PathParam("id") Long id);

}
