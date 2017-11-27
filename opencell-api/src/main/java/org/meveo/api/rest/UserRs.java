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
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.UsersDto;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

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
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    public ActionStatus create(UserDto postData);

    /**
     * Update user.
     * 
     * @param postData
     * @return
     */
    @PUT
    @Path("/")
    public ActionStatus update(UserDto postData);

    /**
     * Remove user with a given username.
     * 
     * @param username
     * @return
     */
    @DELETE
    @Path("/{username}")
    public ActionStatus remove(@PathParam("username") String username);

    /**
     * Search user with a given username.
     * 
     * @param username
     * @return
     */
    @GET
    @Path("/")
    public GetUserResponse find(@QueryParam("username") String username);

    /**
     * Create or update user based on the username
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(UserDto postData);
    
    /**
     * Creates a user in keycloak and core.
     * @param postData
     * @return
     */
    @POST
    @Path("/external")
    public ActionStatus createExternalUser(UserDto postData);

    /**
     * Updates a user in keycloak and core given a username.
     * @param postData
     * @return
     */
    @PUT
    @Path("/external/")
    public ActionStatus updateExternalUser(UserDto postData);

    /**
     * Deletes a user in keycloak and core given a username.
     * @param username the username of the user to be deleted.
     * @return
     */
    @DELETE
    @Path("/external/{username}")
    public ActionStatus deleteExternalUser(@PathParam("username") String username);

    /**
     * List users matching a given criteria
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
    public UsersDto listGet(@QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("userName") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * List users matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria. Specify "securedEntities" in fields to include the secured entities.
     * @return A list of users
     */
    @POST
    @Path("/list")
    public UsersDto listPost(PagingAndFiltering pagingAndFiltering);

}