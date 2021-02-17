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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;

/**
 * 
 * @author Manu Liwanag
 * 
 */
@Path("/jobInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface JobInstanceRs extends IBaseRs {

    /**
     * Create a new job instance
     * 
     * @param postData The job instance's data
     * @return Request processing status
     */
    @POST
    @Path("/create")
    @Operation(summary = "Create a new job",
    tags = { "Jobs management" })
    ActionStatus create(JobInstanceDto postData);

    /**
     * Update an existing job instance
     * 
     * @param postData The job instance's data
     * @return Request processing status
     */
    @POST
    @Path("/update")
    @Operation(summary = "Update an existing job",
    tags = { "Jobs management" })
    ActionStatus update(JobInstanceDto postData);

    /**
     * Create new or update an existing job instance with a given code
     * 
     * @param postData The job instance's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    @Operation(summary = " Create new or update an existing job with a given code",
    tags = { "Jobs management" })
    ActionStatus createOrUpdate(JobInstanceDto postData);

    /**
     * Find a job instance with a given code
     * 
     * @param jobInstanceCode The job instance's code
     * @return Job Instance Response data
     */
    @GET
    @Path("/")
    @Operation(summary = "Find a job with a given code",
    tags = { "Jobs management" })
    JobInstanceResponseDto find(@QueryParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Remove an existing job instance with a given code
     * 
     * @param jobInstanceCode The job instance's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{jobInstanceCode}")
    @Operation(summary = "Remove an existing job with a given code",
    tags = { "Jobs management" })
    ActionStatus remove(@PathParam("jobInstanceCode") String jobInstanceCode);

    /**
     * Enable a Job instance with a given code
     * 
     * @param code Job instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/enable")
    @Operation(summary = "Enable a Job with a given code",
    tags = { "Jobs management" })
    ActionStatus enable(@PathParam("code") String code);

    /**
     * Disable a Job instance with a given code
     * 
     * @param code Job instance code
     * @return Request processing status
     */
    @POST
    @Path("/{code}/disable")
    @Operation(summary = "Disable a Job with a given code",
    tags = { "Jobs management" })
    ActionStatus disable(@PathParam("code") String code);

}