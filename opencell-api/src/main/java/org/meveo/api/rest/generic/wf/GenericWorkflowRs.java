package org.meveo.api.rest.generic.wf;

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
import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowResponseDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowsResponseDto;
import org.meveo.api.dto.response.generic.wf.WorkflowInsHistoryResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/admin/genericWorkflow")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface GenericWorkflowRs extends IBaseRs {

    /**
     * Create a new workflow
     * 
     * @param genericWorkflowDto The workflow's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(GenericWorkflowDto genericWorkflowDto);

    /**
     * Update an existing workflow
     * 
     * @param genericWorkflowDto The workflow's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(GenericWorkflowDto genericWorkflowDto);

    /**
     * Find a workflow with a given code
     * 
     * @param code The workflow's code
     * @return
     */
    @GET
    @Path("/")
    GenericWorkflowResponseDto find(@QueryParam("code") String code);

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
    GenericWorkflowsResponseDto list();

    /**
     * Create new or update an existing workflow with a given code
     * 
     * @param genericWorkflowDto The workflow's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(GenericWorkflowDto genericWorkflowDto);

    /**
     * Enable a Workflow with a given code
     * 
     * @param code Workflow code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Workflow with a given code
     * 
     * @param code Workflow code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    ActionStatus disable(@PathParam("code") String code);

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
    ActionStatus execute(@QueryParam("baseEntityName") String baseEntityName, @QueryParam("entityInstanceCode") String entityInstanceCode,
            @QueryParam("workflowCode") String workflowCode);

    /**
     * Find a workflow by entity
     * 
     * @param baseEntityName
     * @return Request processing status
     */
    @GET
    @Path("/findByEntity")
    GenericWorkflowsResponseDto findByEntity(@QueryParam("baseEntityName") String baseEntityName);

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
    WorkflowInsHistoryResponseDto findHistory(@QueryParam("entityInstanceCode") String entityInstanceCode, @QueryParam("workflowCode") String workflowCode,
            @QueryParam("fromStatus") String fromStatus, @QueryParam("toStatus") String toStatus);

}