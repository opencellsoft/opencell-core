package org.meveo.rest.api;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.OneShotChargeTemplateServiceApi;
import org.meveo.api.dto.OneShotChargeTemplateDto;
import org.meveo.rest.ActionStatus;
import org.meveo.rest.ActionStatusEnum;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
@Path("/oneShotChargeTemplate")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class OneShotChargeTemplateWS {

	@Inject
	private OneShotChargeTemplateServiceApi oneShotChargeTemplateServiceApi;

	@GET
	@Path("/index")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service");

		return result;
	}

	@GET
	@Path("/")
	public List<OneShotChargeTemplateDto> getOneShotChargeTemplates(
			@QueryParam("languageCode") String languageCode,
			@QueryParam("countryCode") String countryCode,
			@QueryParam("currencyCode") String currencyCode,
			@QueryParam("providerCode") String providerCode,
			@QueryParam("sellerCode") String sellerCode,
			@QueryParam("date") Date date) {

		return oneShotChargeTemplateServiceApi.getOneShotChargeTemplates(
				languageCode, countryCode, currencyCode, providerCode,
				sellerCode, date);

	}

}
