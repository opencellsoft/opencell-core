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

    /**
     * Create All addressbook
     *
     * @return Request processing status
     */   
	@GET
    @Path("/createAll")
    ActionStatus createAll();

    /**
     * Create a new contact address for a contact code
     *
     * @param addrCode The address book code
     * @param ctCode The contact code 
     * @return Request processing status
     */
    @GET
    @Path("/addContact")
    ActionStatus addContact(@QueryParam("addressbookCode") String addrCode, @QueryParam("contactCode") String ctCode);
    
    /**
     * Find a AddressBook with a given code and from
     *
     * @param code The address book code
     * @param from The from information
     * @return GetAddressBookResponse data
     */
    @GET
    @Path("/")
    GetAddressBookResponseDto find(@QueryParam("code") String code, @QueryParam("from") String from);
        
    /**
     * List of address
     *
     * @return Request processing status
     */
    @GET
    @Path("/list")
    ActionStatus list();
}
