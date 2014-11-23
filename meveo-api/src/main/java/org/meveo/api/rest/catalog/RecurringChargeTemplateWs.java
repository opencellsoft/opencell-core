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
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponse;
import org.meveo.api.rest.IBaseWs;
import org.meveo.api.rest.security.WSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/recurringChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@WSSecured
public interface RecurringChargeTemplateWs extends IBaseWs {

	@POST
	@Path("/")
	public ActionStatus create(RecurringChargeTemplateDto postData);

	@GET
	@Path("/")
	public GetRecurringChargeTemplateResponse find(
			@QueryParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

	@PUT
	@Path("/")
	public ActionStatus update(RecurringChargeTemplateDto postData);

	@DELETE
	@Path("/{recurringChargeTemplateCode}")
	public ActionStatus remove(
			@PathParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

}
