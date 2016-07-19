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
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.wf.WorkflowResponseDto;
import org.meveo.api.dto.wf.WorkflowsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/admin/workflow")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface WorkflowRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(WorkflowDto workflowDto);
    
    @PUT
    @Path("/")
    ActionStatus update(WorkflowDto workflowDto);

    @GET
    @Path("/")
    WorkflowResponseDto find(@QueryParam("code") String code);

    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    @GET
    @Path("/list")
    WorkflowsResponseDto list();

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(WorkflowDto workflowDto);
}

