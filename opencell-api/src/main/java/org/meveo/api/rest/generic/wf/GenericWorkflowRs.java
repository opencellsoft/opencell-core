/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.rest.generic.wf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowResponseDto;
import org.meveo.api.dto.response.generic.wf.GenericWorkflowsResponseDto;
import org.meveo.api.dto.response.generic.wf.WorkflowInsHistoryResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 *
 */
@Path("/admin/genericWorkflow")
@Tag(name = "GenericWorkflow", description = "@%GenericWorkflow")
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
	@Operation(
			summary=" Create a new workflow  ",
			description=" Create a new workflow  ",
			operationId="    POST_GenericWorkflow_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(GenericWorkflowDto genericWorkflowDto);

    /**
     * Update an existing workflow
     * 
     * @param genericWorkflowDto The workflow's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing workflow  ",
			description=" Update an existing workflow  ",
			operationId="    PUT_GenericWorkflow_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(GenericWorkflowDto genericWorkflowDto);

    /**
     * Find a workflow with a given code
     * 
     * @param code The workflow's code
     * @return Generic Work flow Response data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a workflow with a given code  ",
			description=" Find a workflow with a given code  ",
			operationId="    GET_GenericWorkflow_search",
			responses= {
				@ApiResponse(description=" Generic Work flow Response data ",
						content=@Content(
									schema=@Schema(
											implementation= GenericWorkflowResponseDto.class
											)
								)
				)}
	)
    GenericWorkflowResponseDto find(@QueryParam("code") String code);

    /**
     * Remove an existing workflow with a given code
     * 
     * @param code The workflow's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{code}")
	@Operation(
			summary=" Remove an existing workflow with a given code  ",
			description=" Remove an existing workflow with a given code  ",
			operationId="    DELETE_GenericWorkflow_{code}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("code") String code);

    /**
     * List of workflows.
     * 
     * @return A list of workflow
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List of workflows.  ",
			description=" List of workflows.  ",
			operationId="    GET_GenericWorkflow_list",
			responses= {
				@ApiResponse(description=" A list of workflow ",
						content=@Content(
									schema=@Schema(
											implementation= GenericWorkflowsResponseDto.class
											)
								)
				)}
	)
    GenericWorkflowsResponseDto list();

    /**
     * Create new or update an existing workflow with a given code
     * 
     * @param genericWorkflowDto The workflow's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing workflow with a given code  ",
			description=" Create new or update an existing workflow with a given code  ",
			operationId="    POST_GenericWorkflow_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(GenericWorkflowDto genericWorkflowDto);

    /**
     * Enable a Workflow with a given code
     * 
     * @param code Workflow code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Workflow with a given code  ",
			description=" Enable a Workflow with a given code  ",
			operationId="    POST_GenericWorkflow_{code}_enable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Workflow with a given code
     * 
     * @param code Workflow code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Workflow with a given code  ",
			description=" Disable a Workflow with a given code  ",
			operationId="    POST_GenericWorkflow_{code}_disable",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Execute a workflow  ",
			description=" Execute a workflow  ",
			operationId="    POST_GenericWorkflow_execute",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Find a workflow by entity  ",
			description=" Find a workflow by entity  ",
			operationId="    GET_GenericWorkflow_findByEntity",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= GenericWorkflowsResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Find workflow history  ",
			description=" Find workflow history  ",
			operationId="    GET_GenericWorkflow_history",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= WorkflowInsHistoryResponseDto.class
											)
								)
				)}
	)
    WorkflowInsHistoryResponseDto findHistory(@QueryParam("entityInstanceCode") String entityInstanceCode, @QueryParam("workflowCode") String workflowCode,
            @QueryParam("fromStatus") String fromStatus, @QueryParam("toStatus") String toStatus);

    /**
     * execute transition
     * 
     * @param baseEntityName
     * @param entityInstanceCode
     * @param workflowCode
     * @param transitionUUID
     * @param ignoreConditionEL
     * @return
     */
    @POST
    @Path("/executeTransition")
	@Operation(
			summary=" execute transition  ",
			description=" execute transition  ",
			operationId="    POST_GenericWorkflow_executeTransition",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus executeTransition(@QueryParam("baseEntityName") String baseEntityName,
                                   @QueryParam("entityInstanceCode") String entityInstanceCode, @QueryParam("workflowCode") String workflowCode,
                                   @QueryParam("transition") String transitionUUID, @QueryParam("force") boolean ignoreConditionEL);
}
