package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import org.meveo.api.dto.response.GetUserResponse;

/**
 * Web service for managing {@link org.meveo.model.admin.User}. 
 * User has a unique username that is use for update, search and remove operation.
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
    @Path("/keycloak")
    public ActionStatus createKeycloakUser(UserDto postData);

    /**
     * Updates a user in keycloak and core given a username.
     * @param postData
     * @return
     */
    @PUT
    @Path("/keycloak/")
    public ActionStatus updateKeycloakUser(UserDto postData);

    /**
     * Deletes a user in keycloak and core given a username.
     * @param username the username of the user to be deleted.
     * @return
     */
    @DELETE
    @Path("/keycloak/{username}")
    public ActionStatus deleteKeycloakUser(@PathParam("username") String username);

}
