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
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.catalog.UsageChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/usageChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface UsageChargeTemplateRs extends IBaseRs {

	/**
	 * Create usage charge template.
	 * 
	 * @param postData
	 * @return
	 */
	@POST
	@Path("/")
	public ActionStatus create(UsageChargeTemplateDto postData);

	/**
	 * Update usage charge template.
	 * 
	 * @param postData
	 * @return
	 */
	@PUT
	@Path("/")
	public ActionStatus update(UsageChargeTemplateDto postData);

	/**
	 * Search usage charge template with a given code.
	 * 
	 * @param usageChargeTemplateCode
	 * @return
	 */
	@GET
	@Path("/")
	public GetUsageChargeTemplateResponse find(
			@QueryParam("usageChargeTemplateCode") String usageChargeTemplateCode);

	/**
	 * Remove usage charge template with a given code.
	 * 
	 * @param usageChargeTemplateCode
	 * @return
	 */
	@DELETE
	@Path("/{usageChargeTemplateCode}")
	public ActionStatus remove(
			@PathParam("usageChargeTemplateCode") String usageChargeTemplateCode);

}
