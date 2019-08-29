package org.meveo.api.rest.hierarchy;

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
    ActionStatus create(UserHierarchyLevelDto postData);

    /**
     * Update an existing user hierarchy level
     * 
     * @param postData The user hierarchy level's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(UserHierarchyLevelDto postData);

    /**
     * Search for a user group level with a given code.
     * 
     * @param hierarchyLevelCode the code to string
     * @return the UserHierarchyLevel given the hierarchyCode
     */
    @GET
    @Path("/")
    UserHierarchyLevelResponseDto find(@QueryParam("hierarchyLevelCode") String hierarchyLevelCode);

    /**
     * Remove an existing hierarchy level with a given code
     * 
     * @param hierarchyLevelCode The hierarchy level's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{hierarchyLevelCode}")
    ActionStatus remove(@PathParam("hierarchyLevelCode") String hierarchyLevelCode);

    /**
     * Create new or update an existing user hierarchy level with a given code
     * 
     * @param postData The user hierarchy level's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
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
    public UserHierarchyLevelsDto listPost(PagingAndFiltering pagingAndFiltering);

}