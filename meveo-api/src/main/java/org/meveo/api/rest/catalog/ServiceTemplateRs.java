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
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.catalog.ServiceTemplate}.
 * @author Edward P. Legaspi
 **/
@Path("/catalog/serviceTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface ServiceTemplateRs extends IBaseRs {

	/**
	 * Create service template.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	ActionStatus create(ServiceTemplateDto postData);

	/** 
	 * Update service template.
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	ActionStatus update(ServiceTemplateDto postData);

	/**
	 * Search service template with a given code.
	 * @param serviceTemplateCode
	 * @return
	 */
	@Path("/")
	@GET
	GetServiceTemplateResponseDto find(
			@QueryParam("serviceTemplateCode") String serviceTemplateCode);

	/**
	 * Remove service template with a given code.
	 * @param serviceTemplateCode
	 * @return
	 */
	@Path("/{serviceTemplateCode}")
	@DELETE
	ActionStatus remove(
			@PathParam("serviceTemplateCode") String serviceTemplateCode);
	
	@Path("/createOrUpdate")
	@POST
	ActionStatus createOrUpdate(ServiceTemplateDto postData);

}
