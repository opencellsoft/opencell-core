package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.response.PermissionResponseDto;

@Path("/permission")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PermissionRs extends IBaseRs {

	/**
     * List of permissions
     * 
     * @return A list of permissions
     */
    @Path("/list")
    @GET
    PermissionResponseDto list();
}
