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

package org.meveo.api.rest.hierarchy;

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
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
import org.meveo.api.dto.hierarchy.UserHierarchyLevelsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.UserHierarchyLevelResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Phu Bach
 **/
@Path("/hierarchy/userGroupLevel")
@Tag(name = "UserHierarchyLevel", description = "@%UserHierarchyLevel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UserHierarchyLevelRs extends IBaseRs {

    /**
     * Create a new user hierarchy level
     * 
     * @param postData The user hierarchy level's data
     * @return Request processing status
     */
    @POST
    @Path("/")
	@Operation(
			summary=" Create a new user hierarchy level  ",
			description=" Create a new user hierarchy level  ",
			operationId="    POST_UserHierarchyLevel_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus create(UserHierarchyLevelDto postData);

    /**
     * Update an existing user hierarchy level
     * 
     * @param postData The user hierarchy level's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Update an existing user hierarchy level  ",
			description=" Update an existing user hierarchy level  ",
			operationId="    PUT_UserHierarchyLevel_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus update(UserHierarchyLevelDto postData);

    /**
     * Search for a user group level with a given code.
     * 
     * @param hierarchyLevelCode the code to string
     * @return the UserHierarchyLevel given the hierarchyCode
     */
    @GET
    @Path("/")
	@Operation(
			summary=" Search for a user group level with a given code.  ",
			description=" Search for a user group level with a given code.  ",
			operationId="    GET_UserHierarchyLevel_search",
			responses= {
				@ApiResponse(description=" the UserHierarchyLevel given the hierarchyCode ",
						content=@Content(
									schema=@Schema(
											implementation= UserHierarchyLevelResponseDto.class
											)
								)
				)}
	)
    UserHierarchyLevelResponseDto find(@QueryParam("hierarchyLevelCode") String hierarchyLevelCode);

    /**
     * Remove an existing hierarchy level with a given code
     * 
     * @param hierarchyLevelCode The hierarchy level's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{hierarchyLevelCode}")
	@Operation(
			summary=" Remove an existing hierarchy level with a given code  ",
			description=" Remove an existing hierarchy level with a given code  ",
			operationId="    DELETE_UserHierarchyLevel_{hierarchyLevelCode}",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus remove(@PathParam("hierarchyLevelCode") String hierarchyLevelCode);

    /**
     * Create new or update an existing user hierarchy level with a given code
     * 
     * @param postData The user hierarchy level's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create new or update an existing user hierarchy level with a given code  ",
			description=" Create new or update an existing user hierarchy level with a given code  ",
			operationId="    POST_UserHierarchyLevel_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdate(UserHierarchyLevelDto postData);

    /**
     * List user hierarchy levels matching a given criteria
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "childLevels" in fields to include the child levels of user hierarchy level.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of user hierarchy levels
     */
    @GET
    @Path("/list")
	@Operation(
			summary=" List user hierarchy levels matching a given criteria  ",
			description=" List user hierarchy levels matching a given criteria  ",
			operationId="    GET_UserHierarchyLevel_list",
			responses= {
				@ApiResponse(description=" A list of user hierarchy levels ",
						content=@Content(
									schema=@Schema(
											implementation= UserHierarchyLevelsDto.class
											)
								)
				)}
	)
    public UserHierarchyLevelsDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset,
            @QueryParam("limit") Integer limit, @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List user hierarchy levels matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "childLevels" in fields to include the child levels of user hierarchy level.
     * @return A list of user hierarchy levels
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List user hierarchy levels matching a given criteria  ",
			description=" List user hierarchy levels matching a given criteria  ",
			operationId="    POST_UserHierarchyLevel_list",
			responses= {
				@ApiResponse(description=" A list of user hierarchy levels ",
						content=@Content(
									schema=@Schema(
											implementation= UserHierarchyLevelsDto.class
											)
								)
				)}
	)
    public UserHierarchyLevelsDto listPost(PagingAndFiltering pagingAndFiltering);

}
