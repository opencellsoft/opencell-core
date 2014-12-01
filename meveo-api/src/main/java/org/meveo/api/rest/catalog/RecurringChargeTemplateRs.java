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
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing
 * {@link org.meveo.model.catalog.RecurringChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/recurringChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface RecurringChargeTemplateRs extends IBaseRs {

	/**
	 * Create recurring charge template.
	 * 
	 * @param postData
	 * @return
	 */
	@POST
	@Path("/")
	public ActionStatus create(RecurringChargeTemplateDto postData);

	/**
	 * Search recurring charge template with a given code.
	 * 
	 * @param recurringChargeTemplateCode
	 * @return
	 */
	@GET
	@Path("/")
	public GetRecurringChargeTemplateResponse find(
			@QueryParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

	/**
	 * Update recurring charge template.
	 * 
	 * @param postData
	 * @return
	 */
	@PUT
	@Path("/")
	public ActionStatus update(RecurringChargeTemplateDto postData);

	/**
	 * Remove recurring charge template with a given code.
	 * 
	 * @param recurringChargeTemplateCode
	 * @return
	 */
	@DELETE
	@Path("/{recurringChargeTemplateCode}")
	public ActionStatus remove(
			@PathParam("recurringChargeTemplateCode") String recurringChargeTemplateCode);

}
