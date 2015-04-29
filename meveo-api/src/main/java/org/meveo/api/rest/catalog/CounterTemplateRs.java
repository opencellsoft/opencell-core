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
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.catalog.CounterTemplate}.
 * @author Edward P. Legaspi
 **/
@Path("/catalog/counterTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CounterTemplateRs extends IBaseRs {

	/**
	 * Create counter template.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	ActionStatus create(CounterTemplateDto postData);

	/**
	 * Update counter template.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	ActionStatus update(CounterTemplateDto postData);

	/**
	 * Search counter template with a given code.
	 * @param counterTemplateCode
	 * @return
	 */
	@Path("/")
	@GET
	GetCounterTemplateResponse find(
			@QueryParam("counterTemplateCode") String counterTemplateCode);

	/**
	 * Remove counter template with a given code.
	 * 
	 * @param counterTemplateCode
	 * @return
	 */
	@Path("/{counterTemplateCode}")
	@DELETE
	ActionStatus remove(
			@PathParam("counterTemplateCode") String counterTemplateCode);

}
