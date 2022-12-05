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

package org.meveo.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.OccTemplateDto;
import org.meveo.api.dto.response.GetOccTemplateResponseDto;
import org.meveo.api.dto.response.GetOccTemplatesResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Path("/occTemplate")
@Tag(name = "OccTemplate", description = "@%OccTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OccTemplateRs extends IBaseRs {

    /**
     * Create OccTemplate.
     * 
     * @param postData posted data to API (account operation template)
     * @return action status.
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create OccTemplate.  ",
			description=" Create OccTemplate.  ",
			operationId="    POST_OccTemplate_create",
			responses= {
				@ApiResponse(description=" account operation template ",
						content=@Content(
									schema=@Schema(
											implementation= GetOccTemplateResponseDto.class
											)
								)
				)}
	)
    GetOccTemplateResponseDto create(OccTemplateDto postData);

    /**
     * Update OccTemplate.
     * 
     * @param postData posted data to API
     * @return action status.
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update OccTemplate.  ",
			description=" Update OccTemplate.  ",
			operationId="    PUT_OccTemplate_update",
			responses= {
				@ApiResponse(description=" account operation template ",
						content=@Content(
									schema=@Schema(
											implementation= GetOccTemplateResponseDto.class
											)
								)
				)}
	)
    GetOccTemplateResponseDto update(OccTemplateDto postData);

    /**
     * Search OccTemplate with a given code.
     * 
     * @param occtemplateCode  code of account operation template
     * @return account operation template
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search OccTemplate with a given code.  ",
			description=" Search OccTemplate with a given code.  ",
			operationId="    GET_OccTemplate_search",
			responses= {
				@ApiResponse(description=" account operation template ",
						content=@Content(
									schema=@Schema(
											implementation= GetOccTemplateResponseDto.class
											)
								)
				)}
	)
    GetOccTemplateResponseDto find(@QueryParam("occTemplateCode") String occtemplateCode);

    /**
     * Remove OccTemplate with a given code.
     * 
     * @param occTemplateCode code of account operation template
     * @return action status.
     */
    @DELETE
    @Path("/{occTemplateCode}")
	@Operation(
			summary=" Remove OccTemplate with a given code.  ",
			description=" Remove OccTemplate with a given code.  ",
			operationId="    DELETE_OccTemplate_{occTemplateCode}",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("occTemplateCode") String occTemplateCode);

    /**
     * Create or update OccTemplate.
     * 
     * @param postData posted data
     * @return action status.
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update OccTemplate.  ",
			description=" Create or update OccTemplate.  ",
			operationId="    POST_OccTemplate_createOrUpdate",
			responses= {
				@ApiResponse(description=" action status. ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(OccTemplateDto postData);
  
    /**
     * Get List of OccTemplates matching a given criteria
     * 
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of account operations
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" Get List of OccTemplates matching a given criteria  ",
			description=" Get List of OccTemplates matching a given criteria  ",
			operationId="    GET_OccTemplate_list",
			responses= {
				@ApiResponse(description=" A list of account operations ",
						content=@Content(
									schema=@Schema(
											implementation= GetOccTemplatesResponseDto.class
											)
								)
				)}
	)
    GetOccTemplatesResponseDto listGet(@QueryParam("query") String query,
            @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Get List of OccTemplates matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of account operations
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" Get List of OccTemplates matching a given criteria  ",
			description=" Get List of OccTemplates matching a given criteria  ",
			operationId="    POST_OccTemplate_list",
			responses= {
				@ApiResponse(description=" List of account operations ",
						content=@Content(
									schema=@Schema(
											implementation= GetOccTemplatesResponseDto.class
											)
								)
				)}
	)
    GetOccTemplatesResponseDto listPost(PagingAndFiltering pagingAndFiltering);
}