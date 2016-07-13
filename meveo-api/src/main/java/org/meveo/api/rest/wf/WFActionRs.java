package org.meveo.api.rest.wf;

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
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.response.payment.ActionPlanItemResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/wf/wfAction")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface WFActionRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(WFActionDto actionPlanItemDto);
    
    @PUT
    @Path("/")
    ActionStatus update(WFActionDto actionPlanItemDto);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(WFActionDto actionPlanItemDto);
        
    @GET
    @Path("/")
    ActionPlanItemResponseDto find( WFTransitionDto wfTransitionDto,@QueryParam("priority")Integer priority);
    
    @DELETE
    @Path("/{workflowCode}/{fromStatus}/{toStatus}/{priority}")
    ActionStatus remove(@PathParam("workflowCode") String workflowCode, 
    		@PathParam("fromStatus") String fromStatus, 
    		@PathParam("toStatus") String toStatus,@QueryParam("priority")Integer priority);

    
}

