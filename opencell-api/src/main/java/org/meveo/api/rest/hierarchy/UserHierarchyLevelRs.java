package org.meveo.api.rest.hierarchy;

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
import org.meveo.api.dto.hierarchy.UserHierarchyLevelDto;
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
     * @param hierarchyLevelCode
     * @return
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

}
