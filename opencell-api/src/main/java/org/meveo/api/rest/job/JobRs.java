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
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.job.*;
import org.meveo.api.rest.IBaseRs;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 * @author Adnane Boubia
 * @lastModifiedVersion 5.0
 **/
@Path("/job")
@Tag(name = "Job", description = "@%Job")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface JobRs extends IBaseRs {

    /**
     * Execute a given job instance info 
     * 
     * @param postData job instance info's data
     * @return request processing status
     */
    @POST
    @Path("/execute")
	@Operation(
			summary=" Execute a given job instance info   ",
			description=" Execute a given job instance info   ",
			operationId="    POST_Job_execute",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= JobExecutionResultResponseDto.class
											)
								)
				)}
	)
    @Deprecated
    JobExecutionResultResponseDto execute(JobInstanceInfoDto postData);

    /**
     * Execute a given job instance info
     *
     * @param postData job instance info's data
     * @return request processing status
     */
    @POST
    @Path("/execution")
	@Operation(
			summary=" Execute a given job instance info ",
			description=" Execute a given job instance info ",
			operationId="    POST_Job_execution",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= JobExecutionResultResponseDto.class
											)
								)
				)}
	)
    JobExecutionResultResponseDto execution(JobInstanceInfoDto postData);
    
    /**
     * Stop a given job instance info 
     * 
     * @param jobInstanceCode job instance code
     * @return request processing status
     */
    @POST
    @Path("/stop/{jobInstanceCode}")
	@Operation(
			summary=" Stop a given job instance info   ",
			description=" Stop a given job instance info   ",
			operationId="    POST_Job_stop_{jobInstanceCode}",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @Deprecated
    ActionStatus stop(@PathParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Stop a given job instance info for put endpoint
     *
     * @param jobInstanceCode job instance code
     * @return request processing status
     */
    @PUT
    @Path("/stop/{jobInstanceCode}")
	@Operation(
			summary=" Stop a given job instance info for put endpoint ",
			description=" Stop a given job instance info for put endpoint ",
			operationId="    PUT_Job_stop_{jobInstanceCode}",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus stopForPut(@PathParam("jobInstanceCode") String jobInstanceCode);
    
    /**
     * Create a new job instance
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @POST
    @Path("/create")
	@Operation(
			summary=" Create a new job instance  ",
			description=" Create a new job instance  ",
			operationId="    POST_Job_create",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(JobInstanceDto postData);

    /**
     * Create a new job instance
     *
     * @param postData The job instance's data
     * @return request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new job instance ",
			description=" Create a new job instance ",
			operationId="    POST_Job_create",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createV2(JobInstanceDto postData);

    /**
     * Update an existing job instance
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing job instance  ",
			description=" Update an existing job instance  ",
			operationId="    PUT_Job_update",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(JobInstanceDto postData);

    /**
     * Create new or update an existing job instance with a given code
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing job instance with a given code  ",
			description=" Create new or update an existing job instance with a given code  ",
			operationId="    POST_Job_createOrUpdate",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @Deprecated
    ActionStatus createOrUpdate(JobInstanceDto postData);

    /**
     * Find a job instance with a given code 
     * 
     * @param jobInstanceCode string to match the code of JobInstance
     * @return object containing the matched JobInstance
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Find a job instance with a given code   ",
			description=" Find a job instance with a given code   ",
			operationId="    GET_Job_search",
			responses= {
				@ApiResponse(description=" object containing the matched JobInstance ",
						content=@Content(
									schema=@Schema(
											implementation= JobInstanceResponseDto.class
											)
								)
				)}
	)
    @Deprecated
    JobInstanceResponseDto find(@QueryParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Find a job instance with a given code
     *
     * @param jobInstanceCode string to match the code of JobInstance
     * @return object containing the matched JobInstance
     */
    @GET
    @Path("/{jobInstanceCode}")
	@Operation(
			summary=" Find a job instance with a given code ",
			description=" Find a job instance with a given code ",
			operationId="    GET_Job_{jobInstanceCode}",
			responses= {
				@ApiResponse(description=" object containing the matched JobInstance ",
						content=@Content(
									schema=@Schema(
											implementation= JobInstanceResponseDto.class
											)
								)
				)}
	)
    JobInstanceResponseDto findV2(@PathParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Remove an existing job instance with a given code 
     * 
     * @param jobInstanceCode The job instance's code
     * @return request processing status
     */
    @DELETE
    @Path("/{jobInstanceCode}")
	@Operation(
			summary=" Remove an existing job instance with a given code   ",
			description=" Remove an existing job instance with a given code   ",
			operationId="    DELETE_Job_{jobInstanceCode}",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Deprecated in v.4.7.2 Use /list instead.
     * 
     * @param offset offset
     * @param limit number of elements in response
     * @param mergedCF true if return
     * @param sortBy sortby field
     * @param sortOrder ASC/DESC
     * @return list of all subscriptions.
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Deprecated in v.4.7.2 Use /list instead.  ",
			description=" Deprecated in v.4.7.2 Use /list instead.  ",
			operationId="    GET_Job_list",
			responses= {
				@ApiResponse(description=" list of all subscriptions. ",
						content=@Content(
									schema=@Schema(
											implementation= JobInstanceListResponseDto.class
											)
								)
				)}
	)
    JobInstanceListResponseDto list(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @DefaultValue("false") @QueryParam("mergedCF") boolean mergedCF,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);
    
    // timer

    /**
     * Create a new timer entity
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @POST
    @Path("/timer/")
	@Operation(
			summary=" Create a new timer entity  ",
			description=" Create a new timer entity  ",
			operationId="    POST_Job_timer_",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createTimer(TimerEntityDto postData);

    /**
     * Update an existing timer entity
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @PUT
    @Path("/timer/")
	@Operation(
			summary=" Update an existing timer entity  ",
			description=" Update an existing timer entity  ",
			operationId="    PUT_Job_timer_",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus updateTimer(TimerEntityDto postData);

    /**
     * Create new or update an existing timer entity with a given code
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @POST
    @Path("/timer/createOrUpdate/")
	@Operation(
			summary=" Create new or update an existing timer entity with a given code  ",
			description=" Create new or update an existing timer entity with a given code  ",
			operationId="    POST_Job_timer_createOrUpdate_",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    @Deprecated
    ActionStatus createOrUpdateTimer(TimerEntityDto postData);

    /**
     * Find a timer with a given code 
     * 
     * @param timerCode The timer's code
     * @return request processing status
     */
    @GET
    @Path("/timer/")
	@Operation(
			summary=" Find a timer with a given code   ",
			description=" Find a timer with a given code   ",
			operationId="    GET_Job_timer_",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= TimerEntityResponseDto.class
											)
								)
				)}
	)
    @Deprecated
    TimerEntityResponseDto findTimer(@QueryParam("timerCode") String timerCode);

    /**
     * Find a timer with a given code
     *
     * @param timerCode The timer's code
     * @return request processing status
     */
    @GET
    @Path("/timers/{timerCode}")
	@Operation(
			summary=" Find a timer with a given code ",
			description=" Find a timer with a given code ",
			operationId="    GET_Job_timers_{timerCode}",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= TimerEntityResponseDto.class
											)
								)
				)}
	)
    TimerEntityResponseDto findTimerV2(@PathParam("timerCode") String timerCode);

    /**
     * Remove an existing timer with a given code 
     * 
     * @param timerCode The timer's code
     * @return request processing status
     */
    @DELETE
    @Path("/timer/{timerCode}")
	@Operation(
			summary=" Remove an existing timer with a given code   ",
			description=" Remove an existing timer with a given code   ",
			operationId="    DELETE_Job_timer_{timerCode}",
			responses= {
				@ApiResponse(description=" request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus removeTimer(@PathParam("timerCode") String timerCode);
    
    /**
     * Find a job execution result.
     * 
     * @param code string to match the code of the JobInstance
     * @param jobExecutionResultId a JobExcutionResultId
     * @return object containing the JobExecutionResultImpl
     */
    @GET
    @Path("/jobReport")
	@Operation(
			summary=" Find a job execution result.  ",
			description=" Find a job execution result.  ",
			operationId="    GET_Job_jobReport",
			responses= {
				@ApiResponse(description=" object containing the JobExecutionResultImpl ",
						content=@Content(
									schema=@Schema(
											implementation= JobExecutionResultResponseDto.class
											)
								)
				)}
	)
    @Deprecated
    JobExecutionResultResponseDto findJobExecutionResult(@QueryParam("code") String code, @QueryParam("id") Long jobExecutionResultId);

    /**
     * Find a job execution result.
     *
     * @param code string to match the code of the JobInstance
     * @param jobExecutionResultId a JobExcutionResultId
     * @return object containing the JobExecutionResultImpl
     */
    @GET
    @Path("{code}/jobExecution/{id}/jobReport")
	@Operation(
			summary=" Find a job execution result. ",
			description=" Find a job execution result. ",
			operationId="    GET_Job{code}_jobExecution_{id}_jobReport",
			responses= {
				@ApiResponse(description=" object containing the JobExecutionResultImpl ",
						content=@Content(
									schema=@Schema(
											implementation= JobExecutionResultResponseDto.class
											)
								)
				)}
	)
    JobExecutionResultResponseDto findJobExecutionResultV2(@PathParam("code") String code, @PathParam("id") Long jobExecutionResultId);

    /**
     * Job execution list matching a given criteria
     *
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return List of JobExecutions
     */
    @GET
    @Path("/jobReport/list")
	@Operation(
			summary=" Job execution list matching a given criteria ",
			description=" Job execution list matching a given criteria ",
			operationId="    GET_Job_jobReport_list",
			responses= {
				@ApiResponse(description=" List of JobExecutions ",
						content=@Content(
									schema=@Schema(
											implementation= JobExecutionResultsResponseDto.class
											)
								)
				)}
	)
    JobExecutionResultsResponseDto list(@QueryParam("query") String query, @QueryParam("fields") String fields,
                                        @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
                                        @DefaultValue("id") @QueryParam("sortBy") String sortBy,
                                        @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List jobExecutions matching a given criteria
     *
     * @return List of jobExecutions
     */
    @GET
    @Path("/jobReport/listGetAll")
	@Operation(
			summary=" List jobExecutions matching a given criteria ",
			description=" List jobExecutions matching a given criteria ",
			operationId="    GET_Job_jobReport_listGetAll",
			responses= {
				@ApiResponse(description=" List of jobExecutions ",
						content=@Content(
									schema=@Schema(
											implementation= JobExecutionResultsResponseDto.class
											)
								)
				)}
	)
    JobExecutionResultsResponseDto list();

    /**
     * Job execution list matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of JobExecutions
     */
    @POST
    @Path("/jobReport/list")
	@Operation(
			summary=" Job execution list matching a given criteria ",
			description=" Job execution list matching a given criteria ",
			operationId="    POST_Job_jobReport_list",
			responses= {
				@ApiResponse(description=" List of JobExecutions ",
						content=@Content(
									schema=@Schema(
											implementation= JobExecutionResultsResponseDto.class
											)
								)
				)}
	)
    JobExecutionResultsResponseDto list(PagingAndFiltering pagingAndFiltering);

    /**
     * List job categories
     * 
     * @return object containing the list of job categories
     */
    @GET
    @Path("/listCategories")
	@Operation(
			summary=" List job categories  ",
			description=" List job categories  ",
			operationId="    GET_Job_listCategories",
			responses= {
				@ApiResponse(description=" object containing the list of job categories ",
						content=@Content(
									schema=@Schema(
											implementation= JobCategoriesResponseDto.class
											)
								)
				)}
	)
    JobCategoriesResponseDto listCategories();
    
}
