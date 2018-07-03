package org.meveo.api.rest.crm;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.crm.GetAddressBookResponseDto;
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
    @Path("/")
    GetAddressBookResponseDto find(@QueryParam("code") String code, @QueryParam("from") String from);
        
    
    @GET
    @Path("/list")
    ActionStatus list();
}
