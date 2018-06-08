package org.meveo.api.rest.crm;

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
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.rest.IBaseRs;


@Path("/contact")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })public interface ContactRs extends IBaseRs {
	@POST
    @Path("/")
    ActionStatus create(ContactDto postData);
	
    @PUT
    @Path("/")
    ActionStatus update(ContactDto postData);
    
    @GET
    @Path("/")
    String find(@QueryParam("code") String code);
    
    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ContactDto postData);
    
 }
