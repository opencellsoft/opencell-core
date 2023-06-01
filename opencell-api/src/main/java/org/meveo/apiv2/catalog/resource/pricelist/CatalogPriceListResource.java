package org.meveo.apiv2.catalog.resource.pricelist;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

/**
 * PriceList Endpoints
 *
 * @author zelmeliani
 * @since 15.0
 *
 */
@Path("/catalog")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface CatalogPriceListResource {
	
	@GET
	@Path("/{billingAccountCode}/priceList")
	Response getPriceLists(
			@QueryParam("offset") @DefaultValue("0") Long offset,
			@QueryParam("limit") @DefaultValue("50") Long limit,
            @QueryParam("sortOrder") String sortOrder, 
            @QueryParam("sortBy") String orderBy,
            @PathParam("billingAccountCode") String billingAccountCode,
            @Context Request request);
}