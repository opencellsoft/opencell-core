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
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.response.GetRoleResponse;

@Path("/role")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface RoleRs extends IBaseRs {

    /**
     * Create role.
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    public ActionStatus create(RoleDto postData);

    /**
     * Update role.
     * 
     * @param postData
     * @return
     */
    @PUT
    @Path("/")
    public ActionStatus update(RoleDto postData);

    /**
     * Remove role.
     * 
     * @param rolename Role name
     * @return
     */
    @DELETE
    @Path("/{roleName}/{provider}")
    public ActionStatus remove(@PathParam("roleName") String roleName);

    /**
     * Search role.
     * 
     * @param rolename Role name
     * @return
     */
    @GET
    @Path("/")
    public GetRoleResponse find(@QueryParam("roleName") String roleName);

    /**
     * Create or update role
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(RoleDto postData);
}
