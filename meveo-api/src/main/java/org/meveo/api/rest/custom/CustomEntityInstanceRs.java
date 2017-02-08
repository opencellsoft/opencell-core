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
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Andrius Karpavicius
 **/
@Path("/customEntityInstance")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CustomEntityInstanceRs extends IBaseRs {

    @POST
    @Path("/{customEntityTemplateCode}")
    ActionStatus create(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    @PUT
    @Path("/{customEntityTemplateCode}")
    ActionStatus update(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);

    @DELETE
    @Path("/{customEntityTemplateCode}/{code}")
    ActionStatus remove(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    @GET
    @Path("/{customEntityTemplateCode}/{code}")
    CustomEntityInstanceResponseDto find(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, @PathParam("code") String code);

    @GET
    @Path("/list/{customEntityTemplateCode}")
    CustomEntityInstancesResponseDto list(@PathParam("customEntityTemplateCode") String customEntityTemplateCode);

    @POST
    @Path("/{customEntityTemplateCode}/createOrUpdate")
    ActionStatus createOrUpdate(@PathParam("customEntityTemplateCode") String customEntityTemplateCode, CustomEntityInstanceDto dto);
}