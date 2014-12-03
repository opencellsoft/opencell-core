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
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing
 * {@link org.meveo.model.catalog.OneShotChargeTemplate}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/catalog/oneShotChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface OneShotChargeTemplateRs extends IBaseRs {

	/**
	 * Create one shot charge template.
	 * 
	 * @param postData
	 * @return
	 */
	@POST
	@Path("/")
	public ActionStatus create(OneShotChargeTemplateDto postData);

	/**
	 * Update one shot charge template.
	 * 
	 * @param postData
	 * @return
	 */
	@PUT
	@Path("/")
	public ActionStatus update(OneShotChargeTemplateDto postData);

	/**
	 * Search one shot charge templatewith a given code.
	 * 
	 * @param oneShotChargeTemplateCode
	 * @return
	 */
	@GET
	@Path("/")
	public GetOneShotChargeTemplateResponse find(
			@QueryParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

	/**
	 * List one shot charge template with the following filters.
	 * 
	 * @param languageCode
	 * @param countryCode
	 * @param currencyCode
	 * @param sellerCode
	 * @param date
	 * @return
	 */
	@GET
	@Path("/")
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(
			@QueryParam("languageCode") String languageCode,
			@QueryParam("countryCode") String countryCode,
			@QueryParam("currencyCode") String currencyCode,
			@QueryParam("sellerCode") String sellerCode,
			@QueryParam("date") String date);

	/**
	 * Remove one shot charge template with a given code.
	 * 
	 * @param oneShotChargeTemplateCode
	 * @return
	 */
	@DELETE
	@Path("/{oneShotChargeTemplateCode}")
	public ActionStatus remove(
			@PathParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

}
