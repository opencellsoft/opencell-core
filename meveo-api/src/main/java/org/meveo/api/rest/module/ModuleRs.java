package org.meveo.api.rest.module;

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
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.response.module.MeveoModuleDtoResponse;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/module")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ModuleRs extends IBaseRs {

    @POST
    @Path("/")
    public ActionStatus create(MeveoModuleDto moduleDto);

    @PUT
    @Path("/")
    public ActionStatus update(MeveoModuleDto moduleDto);

    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(MeveoModuleDto moduleDto);

    @DELETE
    @Path("/{code}")
    public ActionStatus delete(@PathParam("code") String code);

    @GET
    @Path("/list")
    public MeveoModuleDtosResponse list();

    @PUT
    @Path("/install")
    public ActionStatus install(MeveoModuleDto moduleDto);

    @GET
    @Path("/")
    public MeveoModuleDtoResponse get(@QueryParam("code") String code);

    @GET
    @Path("/uninstall")
    public ActionStatus uninstall(@QueryParam("code") String code);

    @GET
    @Path("/enable")
    public ActionStatus enable(@QueryParam("code") String code);

    @GET
    @Path("/disable")
    public ActionStatus disable(@QueryParam("code") String code);
}