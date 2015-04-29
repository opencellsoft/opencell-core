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
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.admin.User}. User has a
 * unique username that is use for update, search and remove operation.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/user")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
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

}
