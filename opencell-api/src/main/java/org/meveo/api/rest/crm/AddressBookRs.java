package org.meveo.api.rest.crm;

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
import org.meveo.api.dto.crm.ContactDto;
import org.meveo.api.dto.response.crm.GetContactResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/addressbook")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface AddressBookRs extends IBaseRs {
	@GET
    @Path("/")
    ActionStatus createAll();

    @GET
    @Path("/addContact")
    ActionStatus createOrUpdate(@QueryParam("addressbookid") Long id, @QueryParam("contactcode") String code);
    
    @GET
    @Path("/list")
    ActionStatus list();
}
