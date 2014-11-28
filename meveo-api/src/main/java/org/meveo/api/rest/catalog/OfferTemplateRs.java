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
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/offerTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface OfferTemplateRs extends IBaseRs {

	@Path("/")
	@POST
	ActionStatus create(OfferTemplateDto postData);

	@Path("/")
	@PUT
	ActionStatus update(OfferTemplateDto postData);

	@Path("/")
	@GET
	GetOfferTemplateResponse find(
			@QueryParam("offerTemplateCode") String offerTemplateCode);

	@Path("/{offerTemplateCode}")
	@DELETE
	ActionStatus remove(@PathParam("offerTemplateCode") String offerTemplateCode);

}
