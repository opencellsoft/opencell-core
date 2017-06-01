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
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.wf.WorkflowHistoryResponseDto;
import org.meveo.api.dto.wf.WorkflowResponseDto;
import org.meveo.api.dto.wf.WorkflowsResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/admin/workflow")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface WorkflowRs extends IBaseRs {

    /**
     * Create a new workflow
     * 
     * @param workflowDto The workflow's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(WorkflowDto workflowDto);
    
    /**
     * Update an existing workflow
     * 
     * @param workflowDto The workflow's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(WorkflowDto workflowDto);

    /**
     * Find a workflow with a given code 
     * 
     * @param code The workflow's code
     * @return
     */
    @GET
    @Path("/")
    WorkflowResponseDto find(@QueryParam("code") String code);

    /**
     * Remove an existing workflow with a given code 
     * 
     * @param code The workflow's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List of workflows.
     * 
     * @return A list of workflow
     */
    @GET
    @Path("/list")
    WorkflowsResponseDto list();

    /**
     * Create new or update an existing workflow with a given code
     * 
     * @param workflowDto The workflow's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(WorkflowDto workflowDto);
    
    /**
     * Execute a workflow
     * 
     * @param baseEntityName
     * @param entityInstanceCode
     * @param workflowCode
     * @return Request processing status
     */
    @POST
    @Path("/execute")
    ActionStatus execute(@QueryParam("baseEntityName") String baseEntityName, @QueryParam("entityInstanceCode") String entityInstanceCode,@QueryParam("workflowCode") String workflowCode);
    
    /**
     * Find a workflow by entity
     * 
     * @param baseEntityName
     * @return Request processing status
     */
    @GET
    @Path("/findByEntity")
    WorkflowsResponseDto findByEntity(@QueryParam("baseEntityName") String baseEntityName);
    
    /**
     * Find workflow history
     * 
     * @param entityInstanceCode
     * @param workflowCode
     * @param fromStatus
     * @param toStatus
     * @return Request processing status
     */
    @GET
    @Path("/history")
    WorkflowHistoryResponseDto findHistory(@QueryParam("entityInstanceCode") String entityInstanceCode,@QueryParam("workflowCode") String workflowCode,
    		@QueryParam("fromStatus") String fromStatus,@QueryParam("toStatus") String toStatus);

    
}

