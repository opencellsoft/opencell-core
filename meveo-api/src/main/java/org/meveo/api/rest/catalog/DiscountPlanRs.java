package org.meveo.api.rest.catalog;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponseDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlansResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/catalog/discountPlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface DiscountPlanRs extends IBaseRs {
	
	@Path("/")
	@POST
	ActionStatus create(DiscountPlanDto postData);
	
	@Path("/")
	@PUT
	ActionStatus update(DiscountPlanDto postData);
	
	@Path("/")
	@GET
	GetDiscountPlanResponseDto find(@QueryParam("discountPlanCode") String discountPlanCode);
	
	@Path("/")
	@DELETE
	ActionStatus remove(@QueryParam("discountPlanCode") String discountPlanCode);
	
	@Path("/createOrUpdate")
	@POST
	ActionStatus createOrUpdate(DiscountPlanDto postData);
	
	@Path("/")
	@GET
	GetDiscountPlansResponseDto list();
}
