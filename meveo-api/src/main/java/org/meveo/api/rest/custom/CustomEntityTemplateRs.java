package org.meveo.api.rest.custom;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.response.GetCustomEntityTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Andrius Karpavicius
 **/
@Path("/customEntityTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CustomEntityTemplateRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(CustomEntityTemplateDto postData);

    @PUT
    @Path("/")
    ActionStatus update(CustomEntityTemplateDto postData);

    @DELETE
    @Path("/{customEntityTemplateCode}")
    ActionStatus remove(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    @GET
    @Path("/{customEntityTemplateCode}")
    GetCustomEntityTemplateResponseDto find(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(CustomEntityTemplateDto postData);
}