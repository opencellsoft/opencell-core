package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.dto.response.GetTerminationReasonResponse;

@Path("/terminationReason")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface TerminationReasonRs extends IBaseRs {

    @Path("/")
    @POST
    ActionStatus create(TerminationReasonDto postData);

    @Path("/")
    @PUT
    ActionStatus update(TerminationReasonDto postData);

    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(TerminationReasonDto postData);

    @Path("/")
    @DELETE
    ActionStatus remove(@QueryParam("terminationReasonCode") String code);

    @Path("/")
    @GET
    GetTerminationReasonResponse find(@QueryParam("terminationReasonCode") String code);

    @Path("/list")
    @GET
    GetTerminationReasonResponse list();

}
