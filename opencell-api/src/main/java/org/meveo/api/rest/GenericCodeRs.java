package org.meveo.api.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.custom.GenericCodeDto;
import org.meveo.api.dto.custom.GenericCodeResponseDto;
import org.meveo.api.dto.custom.GetGenericCodeResponseDto;
import org.meveo.api.dto.custom.SequenceDto;

import javax.ws.rs.*;

@Path("/genericCode")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
public interface GenericCodeRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(GenericCodeDto codeDto);

    @PUT
    @Path("/")
    ActionStatus update(GenericCodeDto codeDto);

    @GET
    @Path("/")
    GetGenericCodeResponseDto find(@QueryParam("entityClass") String entityClass);

    @POST
    @Path("/generateCode")
    GenericCodeResponseDto getGenericCode(GenericCodeDto codeDto);

    @POST
    @Path("/sequence/")
    ActionStatus createSequence(SequenceDto sequenceDto);
}
