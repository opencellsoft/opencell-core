/**
 * @author Tyshan Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 3:51:34 AM 
 */
package org.meveo.api.rest.account;

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
import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.response.account.ProviderContactResponseDto;
import org.meveo.api.dto.response.account.ProviderContactsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 3:51:34 AM
 *
 */

@Path("/account/providerContact")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ProviderContactRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(ProviderContactDto providerContactDto);

    @PUT
    @Path("/")
    ActionStatus update(ProviderContactDto providerContactDto);

    @GET
    @Path("/")
    ProviderContactResponseDto find(@QueryParam("code") String code);

    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    @GET
    @Path("/list")
    ProviderContactsResponseDto list();
    
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(ProviderContactDto providerContactDto);
}

