package org.meveo.api.rest.payment;

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
import org.meveo.api.dto.payment.DunningPlanDto;
import org.meveo.api.dto.response.payment.DunningPlanResponseDto;
import org.meveo.api.dto.response.payment.DunningPlansResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

@Path("/payment/dunningPlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface DunningPlanRs extends IBaseRs {

    @POST
    @Path("/")
    ActionStatus create(DunningPlanDto dunningPlanDto);
    
    @PUT
    @Path("/")
    ActionStatus update(DunningPlanDto dunningPlanDto);

    @GET
    @Path("/")
    DunningPlanResponseDto find(@QueryParam("code") String code);

    @DELETE
    @Path("/{code}")
    ActionStatus remove(@PathParam("code") String code);

    @GET
    @Path("/list")
    DunningPlansResponseDto list();

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(DunningPlanDto dunningPlanDto);
}

