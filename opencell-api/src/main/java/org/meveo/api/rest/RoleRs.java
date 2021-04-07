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
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.RolesDto;
import org.meveo.api.dto.response.GetRoleResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * REST API for managing {@link Role}.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@Path("/role")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface RoleRs extends IBaseRs {

    /**
     * Create role.
     * 
     * @param postData posted data containing role dto.
     * @return action status
     */
    @POST
    @Path("/") 
    @Operation(summary = "Create role",
    tags = { "Roles management" })
    ActionStatus create(RoleDto postData);

    /**
     * Update role.
     * 
     * @param postData posted data
     * @return action status.
     */
    @PUT
    @Path("/") 
    @Operation(summary = "Update role",
    tags = { "Roles management" })
    ActionStatus update(RoleDto postData);

    /**
     * Remove role.
     *
     * @param roleName Role name
     * @return action status.
     */
    @DELETE
    @Path("/{roleName}")
    @Operation(summary = "Remove role.",
    tags = { "Roles management" })
    ActionStatus remove(@Parameter(required = true) @PathParam("roleName") String roleName);

    /**
     * Search role.
     * 
     * @param roleName Role name
     * @param includeSecuredEntities if true returns the secured entities
     * @return found role
     */
    @GET
    @Path("/") 
    @Operation(summary = "Search role.", deprecated = true,
    tags = { "Deprecated" })
    GetRoleResponse find(@Parameter(required = true)  @QueryParam("roleName") String roleName, @Parameter(description = "indicate return of the list of secured entties") @QueryParam("includeSecuredEntities") boolean includeSecuredEntities);

    /**
     * Search role.
     *
     * @param roleName Role name
     * @param includeSecuredEntities if true returns the secured entities
     * @return found role
     */
    @GET
    @Path("/{roleName}")
    @Operation(summary = "Search role.",
            tags = { "Roles management" })
    GetRoleResponse findV2(@Parameter(required = true) @PathParam("roleName") String roleName, @Parameter(description = "indicate return of the list of secured entties") @QueryParam("includeSecuredEntities") boolean includeSecuredEntities);

    /**
     * Create or update role.
     * 
     * @param postData posted data
     * @return action status
     */
    @POST
    @Path("/createOrUpdate") 
    @Operation(summary = "Create or update role.", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus createOrUpdate(RoleDto postData);

    /**
     * List roles matching a given criteria.
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of roles
     */
    @GET
    @Path("/list")
    @Operation(summary = "List roles matching a given criteria.", deprecated = true,
    tags = { "Deprecated" })
    RolesDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("name") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List roles matching a given criteria.
     *
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of roles
     */
    @GET
    @Path("/filtering")
    @Operation(summary = "List roles matching a given criteria.",
            tags = { "Roles management" })
    RolesDto listGetV2(@Parameter(description = "query Search criteria", example = "filterKey1:filterValue1|filterKey2:filterValue2") @QueryParam("query") String query, 
    				  @Parameter(description = "fields Data retrieval options/fieldnames separated by a comma. Specify \"permissions\" in fields to include the permissions. Specify \"roles\" to include child roles") @QueryParam("fields") String fields,
    				  @Parameter(description = "offset Pagination - from record number")  @QueryParam("offset") Integer offset,
    				  @Parameter(description = "limit Pagination - number of records to retrieve") @QueryParam("limit") Integer limit,
    				  @Parameter(description = "sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields") @DefaultValue("name") @QueryParam("sortBy") String sortBy,
    				  @Parameter(description = "sortOrder Sorting - sort order") @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List roles matching a given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @return A list of roles
     */
    @POST
    @Path("/list") 
    @Operation(summary = "List roles matching a given criteria.", deprecated = true,
    tags = { "Deprecated" })
    RolesDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * List roles matching a given criteria.
     *
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "permissions" in fields to include the permissions. Specify "roles" to include child roles.
     * @return A list of roles
     */
    @POST
    @Path("/filtering")
    @Operation(summary = "List roles matching a given criteria.",
            tags = { "Roles management" })
    RolesDto listPostV2(PagingAndFiltering pagingAndFiltering);
    
    /**
     * List external roles.
     * @return list of external roles
     */
    @GET
    @Path("/external")
    @Operation(summary = "List external roles.", deprecated = true,
    tags = { "Deprecated" })
    RolesDto listExternalRoles();

    /**
     * List external roles.
     * @return list of external roles
     */
    @GET
    @Path("/externals")
    @Operation(summary = "List external roles.",
            tags = { "Roles management" })
    RolesDto listExternalRolesV2();

}