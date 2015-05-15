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
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.dto.response.catalog.GetTriggeredEdrResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/catalog/triggeredEdr")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface TriggeredEdrRs extends IBaseRs {

	/**
	 * Create triggered edr.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@POST
	ActionStatus create(TriggeredEdrTemplateDto postData);

	/**
	 * Update triggered edr.
	 * 
	 * @param postData
	 * @return
	 */
	@Path("/")
	@PUT
	ActionStatus update(TriggeredEdrTemplateDto postData);

	/**
	 * Search triggered edr with a given code.
	 * 
	 * @param triggeredEdrCode
	 * @return
	 */
	@Path("/")
	@GET
	GetTriggeredEdrResponseDto find(@QueryParam("triggeredEdrCode") String triggeredEdrCode);

	/**
	 * Remove triggered edr with a given code.
	 * 
	 * @param triggeredEdrCode
	 * @return
	 */
	@Path("/{triggeredEdrCode}")
	@DELETE
	ActionStatus remove(@PathParam("triggeredEdrCode") String triggeredEdrCode);

}
