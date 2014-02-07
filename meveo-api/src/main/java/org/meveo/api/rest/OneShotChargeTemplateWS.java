package org.meveo.api.rest;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.OneShotChargeTemplateServiceApi;
import org.meveo.api.dto.OneShotChargeTemplateListDto;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Path("/oneShotChargeTemplate")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class OneShotChargeTemplateWS {

	@Inject
	private Logger log;

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
	public OneShotChargeTemplateListDto getOneShotChargeTemplates(
			@QueryParam("languageCode") String languageCode,
			@QueryParam("countryCode") String countryCode,
			@QueryParam("currencyCode") String currencyCode,
			@QueryParam("providerCode") String providerCode,
			@QueryParam("sellerCode") String sellerCode,
			@QueryParam("date") Date date) {
		log.debug(
				"oneShotChargeTemplate.getOneShotChargeTemplates languageCode={}, countryCode={}, currencyCode={}, providerCode={}, sellerCode={}, date={}",
				languageCode, countryCode, currencyCode, providerCode,
				sellerCode, date);

		return oneShotChargeTemplateServiceApi.getOneShotChargeTemplates(
				languageCode, countryCode, currencyCode, providerCode,
				sellerCode, date);

	}

}
