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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Hidden;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.response.GetCurrentUserResponse;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Web service for managing {@link org.meveo.model.admin.User}. User has a unique username that is use for update, search and remove operation.
 * 
 * @author Mohamed Hamidi
 **/
@Path("/user")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UserRs extends IBaseRs {

    /**
     * Create user.
     * 
     * @param postData user to be created
     * @return action status
     */
    @POST
    @Path("/")
    @Operation(summary = "Create user",
    tags = { "User management" })
    ActionStatus create(UserDto postData);

    /**
     * Update user.
     * 
     * @param postData user to be updated
     * @return action status
     */
    @PUT
    @Path("/")
    @Operation(summary = "Update user",
    tags = { "User management" })
    ActionStatus update(UserDto postData);

    /**
     * Remove user with a given username.
     * 
     * @param username user name
     * @return action status
     */
    @DELETE
    @Path("/{username}")
    @Operation(summary = "Remove user with a given username",
    tags = { "User management" })
    ActionStatus remove(@Parameter(description = "The usename of user to be deleted", required = true) @PathParam("username") String username);

    /**
     * Search user with a given username.
     *
     * @param username user name
     * @return user
     */
    @GET
    @Path("/")
    @Operation(summary = "Search user with a given username.",
            deprecated = true,
            tags = { "Deprecated" })
    GetUserResponse find(@Parameter(description = "The usename of user", required = true) @QueryParam("username") String username);

    /**
     * Search user with a given username.
     *
     * @param username user name
     * @return user
     */
    @GET
    @Path("/name/{username}")
    @Operation(summary = "Search user with a given username.",
            tags = { "User management" })
    GetUserResponse findV2(@Parameter(description = "The usename of user", required = true) @QueryParam("username") String username);

    /**
     * Get info of currently logged in user
     * 
     * @return user
     */
    @GET
    @Path("/current")
    GetCurrentUserResponse getCurrentUser();

    /**
     * Create or update user based on the username.
     * 
     * @param postData user to be created or updated
     * @return action status
     */
    @POST
    @Path("/createOrUpdate")
    @Operation(summary = "Create or update user based on the username.", deprecated = true,
    tags = { "Deprecated" })
    ActionStatus createOrUpdate(UserDto postData);

    /**
     * Creates a user in keycloak and core.
     * 
     * @param postData user to be created externally
     * @return action status
     */
    @POST
    @Path("/external")
    @Operation(summary = "Creates a user in keycloak and core.",
    tags = { "User management" })
    ActionStatus createExternalUser(UserDto postData);

    /**
     * Updates a user in keycloak and core given a username.
     * 
     * @param postData user to be updated
     * @return action status
     */
    @PUT
    @Path("/external/")
    @Operation(summary = "Updates a user in keycloak and core given a username.",
    tags = { "User management" })
    ActionStatus updateExternalUser(UserDto postData);

    /**
     * Deletes a user in keycloak and core given a username.
     * 
     * @param username the username of the user to be deleted.
     * @return action status
     */
    @DELETE
    @Path("/external/{username}")
    @Operation(summary = " Deletes a user in keycloak and core given a username.",
    tags = { "User management" })
    ActionStatus deleteExternalUser(@Parameter(description = "The usename of user to be deleted in keykloak", required = true) @PathParam("username") String username);

    /**
     * List users matching a given criteria.
     * 
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "securedEntities" in fields to include the secured entities.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of users
     */
    @GET
    @Path("/list")
    @Operation(summary = " List users matching a given criteria.", deprecated = true,
    tags = { "Deprecated" })
    UsersDto listGet(@Parameter(description = "query Search criteria. Query is composed of the following" , example ="filterKey1:filterValue1|filterKey2:filterValue2") @QueryParam("query") String query, 
    				@Parameter(description = "ields Data retrieval options/fieldnames separated by a comma. Specify \"securedEntities\" in fields to include the secured entities") @QueryParam("fields") String fields, 
    				@Parameter(description = "offset Pagination - from record number") @QueryParam("offset") Integer offset, 
    				@Parameter(description = "limit Pagination - number of records to retrieve") @QueryParam("limit") Integer limit,
    				@Parameter(description = "sort by Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields") @DefaultValue("userName") @QueryParam("sortBy") String sortBy, 
    				@Parameter(description = "sort order Sorting") @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);


    /**
     * List users matching a given criteria.
     *
     * @param query Search criteria. Query is composed of the following: filterKey1:filterValue1|filterKey2:filterValue2
     * @param fields Data retrieval options/fieldnames separated by a comma. Specify "securedEntities" in fields to include the secured entities.
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @return A list of users
     */
    @GET
    @Path("/filtering")
    @Operation(summary = " List users matching a given criteria.",
            tags = { "User management" })
    UsersDto listGetV2(@Parameter(description = "query Search criteria. Query is composed of the following" , example ="filterKey1:filterValue1|filterKey2:filterValue2") @QueryParam("query") String query, 
			@Parameter(description = "ields Data retrieval options/fieldnames separated by a comma. Specify \"securedEntities\" in fields to include the secured entities") @QueryParam("fields") String fields, 
			@Parameter(description = "offset Pagination - from record number") @QueryParam("offset") Integer offset, 
			@Parameter(description = "limit Pagination - number of records to retrieve") @QueryParam("limit") Integer limit,
			@Parameter(description = "sort by Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields") @DefaultValue("userName") @QueryParam("sortBy") String sortBy, 
			@Parameter(description = "sort order Sorting") @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List users matching a given criteria
     *
     * @return List of users
     */
    @GET
    @Path("/listGetAll")
    @Operation(summary = "List all users.",
            tags = { "User management" })
    UsersDto list();

    /**
     * List users matching a given criteria.
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "securedEntities" in fields to include the secured entities.
     * @return A list of users
     */
    @POST
    @Path("/list")
    @Operation(summary = "List users matching a given criteria.", deprecated = true,
    tags = { "Deprecated" })
    UsersDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * List users matching a given criteria.
     *
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "securedEntities" in fields to include the secured entities.
     * @return A list of users
     */
    @POST
    @Path("/filtering")
    @Operation(summary = "List users matching a given criteria.",
            tags = { "User management" })
    UsersDto listPostV2(PagingAndFiltering pagingAndFiltering);
}
