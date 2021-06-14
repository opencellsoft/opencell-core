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

package org.meveo.api.rest.wf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

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

//@Path("/admin/workflow")
@Tag(name = "Workflow", description = "@%Workflow")
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
	@Operation(
			summary=" Create a new workflow  ",
			description=" Create a new workflow  ",
			operationId="    POST_Workflow_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(WorkflowDto workflowDto);

    /**
     * Update an existing workflow
     * 
     * @param workflowDto The workflow's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing workflow  ",
			description=" Update an existing workflow  ",
			operationId="    PUT_Workflow_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(WorkflowDto workflowDto);

    /**
     * Find a workflow with a given code
     * 
     * @param code The workflow's code
     * @return Work flow Response 
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a workflow with a given code  ",
			description=" Find a workflow with a given code  ",
			operationId="    GET_Workflow_search",
			responses= {
				@ApiResponse(description=" Work flow Response  ",
						content=@Content(
									schema=@Schema(
											implementation= WorkflowResponseDto.class
											)
								)
				)}
	)
    WorkflowResponseDto find(@QueryParam("code") String code);

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
			operationId="    DELETE_Workflow_{code}",
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
			operationId="    GET_Workflow_list",
			responses= {
				@ApiResponse(description=" A list of workflow ",
						content=@Content(
									schema=@Schema(
											implementation= WorkflowsResponseDto.class
											)
								)
				)}
	)
    WorkflowsResponseDto list();

    /**
     * Create new or update an existing workflow with a given code
     * 
     * @param workflowDto The workflow's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing workflow with a given code  ",
			description=" Create new or update an existing workflow with a given code  ",
			operationId="    POST_Workflow_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(WorkflowDto workflowDto);

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
			operationId="    POST_Workflow_{code}_enable",
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
			operationId="    POST_Workflow_{code}_disable",
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
			operationId="    POST_Workflow_execute",
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
			operationId="    GET_Workflow_findByEntity",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= WorkflowsResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Find workflow history  ",
			description=" Find workflow history  ",
			operationId="    GET_Workflow_history",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= WorkflowHistoryResponseDto.class
											)
								)
				)}
	)
    WorkflowHistoryResponseDto findHistory(@QueryParam("entityInstanceCode") String entityInstanceCode, @QueryParam("workflowCode") String workflowCode,
            @QueryParam("fromStatus") String fromStatus, @QueryParam("toStatus") String toStatus);

}
