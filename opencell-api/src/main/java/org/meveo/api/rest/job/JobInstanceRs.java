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

package org.meveo.api.rest.job;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.response.job.JobInstanceListResponseDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.rest.IBaseRs;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 
 * @author Manu Liwanag
 * 
 */
@Path("/jobInstance")
@Tag(name = "JobInstance", description = "@%JobInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface JobInstanceRs extends IBaseRs {

    /**
     * Search for list of jobInstances.
     *
     * @return list of jobInstances
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Search for list of jobInstances. ",
			description=" Search for list of jobInstances. ",
			operationId="    GET_JobInstance_list",
			responses= {
				@ApiResponse(description=" list of jobInstances ",
						content=@Content(
									schema=@Schema(
											implementation= JobInstanceListResponseDto.class
											)
								)
				)}
	)
    JobInstanceListResponseDto list();

    /**
     * Create a new job instance
     * 
     * @param postData The job instance's data
     * @return Request processing status
     */
    @POST
    @Path("/create")
	@Operation(
			summary=" Create a new job instance  ",
			description=" Create a new job instance  ",
			operationId="    POST_JobInstance_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(JobInstanceDto postData);

    /**
     * Update an existing job instance
     * 
     * @param postData The job instance's data
     * @return Request processing status
     */
    @POST
    @Path("/update")
	@Operation(
			summary=" Update an existing job instance  ",
			description=" Update an existing job instance  ",
			operationId="    POST_JobInstance_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(JobInstanceDto postData);

    /**
     * Update an existing job instance
     *
     * @param putData The job instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing job instance ",
			description=" Update an existing job instance ",
			operationId="    PUT_JobInstance_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updatePut(JobInstanceDto putData);

    /**
     * Create new or update an existing job instance with a given code
     * 
     * @param postData The job instance's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing job instance with a given code  ",
			description=" Create new or update an existing job instance with a given code  ",
			operationId="    POST_JobInstance_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(JobInstanceDto postData);

    /**
     * Find a job instance with a given code
     * 
     * @param jobInstanceCode The job instance's code
     * @return Job Instance Response data
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a job instance with a given code  ",
			description=" Find a job instance with a given code  ",
			operationId="    GET_JobInstance_search",
			responses= {
				@ApiResponse(description=" Job Instance Response data ",
						content=@Content(
									schema=@Schema(
											implementation= JobInstanceResponseDto.class
											)
								)
				)}
	)
    JobInstanceResponseDto find(@QueryParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Remove an existing job instance with a given code
     * 
     * @param jobInstanceCode The job instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{jobInstanceCode}")
	@Operation(
			summary=" Remove an existing job instance with a given code  ",
			description=" Remove an existing job instance with a given code  ",
			operationId="    DELETE_JobInstance_{jobInstanceCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Enable a Job instance with a given code
     * 
     * @param code Job instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
	@Operation(
			summary=" Enable a Job instance with a given code  ",
			description=" Enable a Job instance with a given code  ",
			operationId="    POST_JobInstance_{code}_enable",
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
     * Disable a Job instance with a given code
     * 
     * @param code Job instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
	@Operation(
			summary=" Disable a Job instance with a given code  ",
			description=" Disable a Job instance with a given code  ",
			operationId="    POST_JobInstance_{code}_disable",
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

}
