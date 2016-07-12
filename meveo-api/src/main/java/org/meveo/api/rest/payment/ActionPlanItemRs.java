package org.meveo.api.rest.payment;

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
import org.meveo.api.dto.payment.ActionPlanItemDto;
import org.meveo.api.dto.response.payment.ActionPlanItemResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/payment/dunningPlan/action")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ActionPlanItemRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(ActionPlanItemDto actionPlanItemDto);
    
    @PUT
    @Path("/")
    ActionStatus update(ActionPlanItemDto actionPlanItemDto);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ActionPlanItemDto actionPlanItemDto);
    
    @GET
    @Path("/")
    ActionPlanItemResponseDto find(@QueryParam("dunningPlanCode") String dunningPlanCode, 
    		@QueryParam("itemOrder") Integer itemOrder, 
    		@QueryParam("dunningLevel") String dunningLevel);
    
    @DELETE
    @Path("/{dunningPlanCode}/{itemOrder}/{dunningLevel}")
    ActionStatus remove(@PathParam("dunningPlanCode") String dunningPlanCode, 
    		@PathParam("itemOrder") Integer itemOrder, 
    		@PathParam("dunningLevel") String dunningLevel);

    
}

