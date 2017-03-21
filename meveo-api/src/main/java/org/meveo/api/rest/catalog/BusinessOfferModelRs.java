package org.meveo.api.rest.catalog;

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
import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.response.catalog.GetBusinessOfferModelResponseDto;
import org.meveo.api.dto.response.module.MeveoModuleDtosResponse;
import org.meveo.api.rest.IBaseRs;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/businessOfferModel")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface BusinessOfferModelRs extends IBaseRs {

    @Path("/")
    @POST
    ActionStatus create(BusinessOfferModelDto postData);

    @Path("/")
    @PUT
    ActionStatus update(BusinessOfferModelDto postData);

    @Path("/")
    @GET
    GetBusinessOfferModelResponseDto find(@QueryParam("businessOfferModelCode") String businessOfferModelCode);

    @Path("/{businessOfferModelCode}")
    @DELETE
    ActionStatus remove(@PathParam("businessOfferModelCode") String businessOfferModelCode);

    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(BusinessOfferModelDto postData);

    @GET
    @Path("/list")
    public MeveoModuleDtosResponse list();

    @PUT
    @Path("/install")
    public ActionStatus install(BusinessOfferModelDto moduleDto);
}
