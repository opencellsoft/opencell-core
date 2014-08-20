package org.meveo.api.rest;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.OneShotChargeTemplateServiceApi;
import org.meveo.api.dto.OneShotChargeTemplateListDto;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.rest.security.WSSecured;
import org.meveo.model.shared.DateUtils;

/**
 * @author Edward P. Legaspi
 **/
@Path("/oneShotChargeTemplate")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Interceptors({ LoggingInterceptor.class })
@WSSecured
public class OneShotChargeTemplateWS extends BaseWS {

	@Inject
	private OneShotChargeTemplateServiceApi oneShotChargeTemplateServiceApi;

	@GET
	@Path("/")
	public OneShotChargeTemplateListDto getOneShotChargeTemplates(
			@QueryParam("languageCode") String languageCode,
			@QueryParam("countryCode") String countryCode,
			@QueryParam("currencyCode") String currencyCode,
			@QueryParam("sellerCode") String sellerCode,
			@QueryParam("date") String date) {

		Date subscriptionDate = DateUtils.parseDateWithPattern(date,
				"yyyy-MM-dd");

		return oneShotChargeTemplateServiceApi.getOneShotChargeTemplates(
				languageCode, countryCode, currencyCode, sellerCode,
				subscriptionDate, currentUser);

	}

}
