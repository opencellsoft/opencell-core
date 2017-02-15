package org.meveo.api.rest.catalog;

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
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateCategoryResponseDto;
import org.meveo.api.rest.IBaseRs;

@Path("/catalog/offerTemplateCategory")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface OfferTemplateCategoryRs extends IBaseRs {

    @Path("/")
    @POST
    ActionStatus create(OfferTemplateCategoryDto postData);

    @Path("/")
    @PUT
    ActionStatus update(OfferTemplateCategoryDto postData);

    @Path("/")
    @GET
    GetOfferTemplateCategoryResponseDto find(@QueryParam("offerTemplateCategoryCode") String offerTemplateCategoryCode);

    @Path("/")
    @DELETE
    ActionStatus delete(@QueryParam("offerTemplateCategoryCode") String offerTemplateCategoryCode);

    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(OfferTemplateCategoryDto postData);

}
