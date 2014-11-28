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
 * @author Edward P. Legaspi
 **/
@Path("/catalog/oneShotChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface OneShotChargeTemplateRs extends IBaseRs {

	@POST
	@Path("/")
	public ActionStatus create(OneShotChargeTemplateDto postData);

	@PUT
	@Path("/")
	public ActionStatus update(OneShotChargeTemplateDto postData);

	@GET
	@Path("/")
	public GetOneShotChargeTemplateResponse find(
			@QueryParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

	@GET
	@Path("/")
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(
			@QueryParam("languageCode") String languageCode,
			@QueryParam("countryCode") String countryCode,
			@QueryParam("currencyCode") String currencyCode,
			@QueryParam("sellerCode") String sellerCode,
			@QueryParam("date") String date);

	@DELETE
	@Path("/{oneShotChargeTemplateCode}")
	public ActionStatus remove(
			@PathParam("oneShotChargeTemplateCode") String oneShotChargeTemplateCode);

}
