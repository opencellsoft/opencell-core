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
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/serviceTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ServiceTemplateRs extends IBaseRs {

	@Path("/")
	@POST
	ActionStatus create(ServiceTemplateDto postData);

	@Path("/")
	@PUT
	ActionStatus update(ServiceTemplateDto postData);

	@Path("/")
	@GET
	GetServiceTemplateResponse find(
			@QueryParam("serviceTemplateCode") String serviceTemplateCode);

	@Path("/{serviceTemplateCode}")
	@DELETE
	ActionStatus remove(
			@PathParam("serviceTemplateCode") String serviceTemplateCode);

}
