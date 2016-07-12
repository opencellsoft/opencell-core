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
import org.meveo.api.dto.payment.DunningPlanTransitionDto;
import org.meveo.api.dto.response.payment.DunningPlanTransitionResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/payment/dunningPlan/transition")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface DunningPlanTransitionRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(DunningPlanTransitionDto dunningPlanTransitionDto);
    
    @PUT
    @Path("/")
    ActionStatus update(DunningPlanTransitionDto dunningPlanTransitionDto);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(DunningPlanTransitionDto dunningPlanTransitionDto);
    
    @GET
    @Path("/")
    DunningPlanTransitionResponseDto find(@QueryParam("dunningPlanCode") String dunningPlanCode, 
    		@QueryParam("dunningLevelFrom") String dunningLevelFrom, 
    		@QueryParam("dunningLevelTo") String dunningLevelTo);
    
    @DELETE
    @Path("/{dunningPlanCode}/{dunningLevelFrom}/{dunningLevelTo}")
    ActionStatus remove(@PathParam("dunningPlanCode") String dunningPlanCode, 
    		@PathParam("dunningLevelFrom") String dunningLevelFrom, 
    		@PathParam("dunningLevelTo") String dunningLevelTo);

}

