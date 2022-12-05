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
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.job.JobInstanceListResponseDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.rest.IBaseRs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

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
     * Search for list of jobInstances.
     *
     * @return list of jobInstances
     */
    @GET
    @Path("/list")
    @Operation(summary = "Search for list of jobInstances",
    tags = { "Jobs management" },
    responses = {
            @ApiResponse(responseCode="200", description = "Return the list of jobInsttance successfully",
                    content = @Content(schema = @Schema(implementation = JobInstanceListResponseDto.class)))
    })
    JobInstanceListResponseDto list();

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
     * Update an existing job instance
     *
     * @param putData The job instance's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    @Operation(summary = "Update an existing job",
            tags = { "Jobs management" })
    ActionStatus updatePut(JobInstanceDto putData);

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
    JobInstanceResponseDto find(@Parameter(description = "The job instance's code", required = true) @QueryParam("jobInstanceCode") String jobInstanceCode);

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
    ActionStatus remove(@Parameter(description = "The job instance's code", required = true) @PathParam("jobInstanceCode") String jobInstanceCode);

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
    ActionStatus enable(@Parameter(description = "The job instance's code", required = true) @PathParam("code") String code);

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
    ActionStatus disable(@Parameter(description = "The job instance's code", required = true) @PathParam("code") String code);

}
