package org.meveo.api.rest.script;

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
import org.meveo.api.dto.response.script.OfferModelScriptResponseDto;
import org.meveo.api.dto.script.OfferModelScriptDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/script/offerModelScript")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface OfferModelScriptRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(OfferModelScriptDto postData);

    @PUT
    @Path("/")
    ActionStatus update(OfferModelScriptDto postData);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(OfferModelScriptDto postData);

    @DELETE
    @Path("/{code}")
    ActionStatus delete(@PathParam("code") String code);

    @GET
    @Path("/")
    OfferModelScriptResponseDto get(@QueryParam("code") String code);

}
