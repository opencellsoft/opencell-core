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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.job.*;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 * @author Adnane Boubia
 * @lastModifiedVersion 5.0
 **/
@Path("/job")
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
    JobExecutionResultResponseDto execute(JobInstanceInfoDto postData);
    
    /**
     * Stop a given job instance info 
     * 
     * @param jobInstanceCode job instance code
     * @return request processing status
     */
    @POST
    @Path("/stop/{jobInstanceCode}")
    ActionStatus stop(@PathParam("jobInstanceCode") String jobInstanceCode);
    
    /**
     * Create a new job instance
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @POST
    @Path("/create")
    ActionStatus create(JobInstanceDto postData);

    /**
     * Update an existing job instance
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(JobInstanceDto postData);

    /**
     * Create new or update an existing job instance with a given code
     * 
     * @param postData The job instance's data
     * @return request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(JobInstanceDto postData);

    /**
     * Find a job instance with a given code 
     * 
     * @param jobInstanceCode string to match the code of JobInstance
     * @return object containing the matched JobInstance
     */
    @GET
    @Path("/")
    JobInstanceResponseDto find(@QueryParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Remove an existing job instance with a given code 
     * 
     * @param jobInstanceCode The job instance's code
     * @return request processing status
     */
    @DELETE
    @Path("/{jobInstanceCode}")
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
    ActionStatus createTimer(TimerEntityDto postData);

    /**
     * Update an existing timer entity
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @PUT
    @Path("/timer/")
    ActionStatus updateTimer(TimerEntityDto postData);

    /**
     * Create new or update an existing timer entity with a given code
     * 
     * @param postData The timer entity's data
     * @return request processing status
     */
    @POST
    @Path("/timer/createOrUpdate/")
    ActionStatus createOrUpdateTimer(TimerEntityDto postData);

    /**
     * Find a timer with a given code 
     * 
     * @param timerCode The timer's code
     * @return request processing status
     */
    @GET
    @Path("/timer/")
    TimerEntityResponseDto findTimer(@QueryParam("timerCode") String timerCode);

    /**
     * Remove an existing timer with a given code 
     * 
     * @param timerCode The timer's code
     * @return request processing status
     */
    @DELETE
    @Path("/timer/{timerCode}")
    ActionStatus removeTimer(@PathParam("timerCode") String timerCode);
    
    /**
     * Find a job execution result with a given id
     * 
     * @param code String to match the code of the JobInstance
     * @param jobExecutionResultId A Job jobExecutionResultId
     * @return object containing the JobExecutionResultImpl
     */
    @GET
    @Path("/jobReport")
    JobExecutionResultResponseDto findJobExecutionResult(@QueryParam("code") String code, @QueryParam("id") Long jobExecutionResultId);

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
    JobExecutionResultsResponseDto list(@QueryParam("query") String query, @QueryParam("fields") String fields,
                                        @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
                                        @DefaultValue("id") @QueryParam("sortBy") String sortBy,
                                        @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Job execution list matching a given criteria
     *
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of JobExecutions
     */
    @POST
    @Path("/jobReport/list")
    JobExecutionResultsResponseDto list(PagingAndFiltering pagingAndFiltering);

    /**
     * List job categories
     * 
     * @return object containing the list of job categories
     */
    @GET
    @Path("/listCategories")
    JobCategoriesResponseDto listCategories();
    
}
