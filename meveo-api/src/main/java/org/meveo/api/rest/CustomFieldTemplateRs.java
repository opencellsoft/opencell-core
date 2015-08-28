package org.meveo.api.rest;

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
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/customFieldTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CustomFieldTemplateRs extends IBaseRs {

	@POST
	@Path("/")
	ActionStatus create(CustomFieldTemplateDto postData);

	@PUT
	@Path("/")
	ActionStatus update(CustomFieldTemplateDto postData);

	@DELETE
	@Path("/{customFieldTemplateCode}/{accountLevel}")
	ActionStatus remove(@PathParam("customFieldTemplateCode") String customFieldTemplateCode,
			@PathParam("accountLevel") String accountLevel);

	@GET
	@Path("/")
	GetCustomFieldTemplateReponseDto find(@QueryParam("customFieldTemplateCode") String customFieldTemplateCode,
			@QueryParam("accountLevel") String accountLevel);

}
