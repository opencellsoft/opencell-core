package org.meveo.apiv2.catalog.resource.pricelist;

import org.meveo.apiv2.catalog.PriceListLineDto;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/catalog/priceListLine")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PriceListLineResource {

    @POST
    @Path("")
    Response create(PriceListLineDto postDto);

    @PUT
    @Path("/{priceListLineId}")
    Response update(@PathParam("priceListLineId") Long priceListLineId, PriceListLineDto postDto);

    @DELETE
    @Path("/{priceListLineId}")
    Response delete(@PathParam("priceListLineId") Long priceListLineId);

}
