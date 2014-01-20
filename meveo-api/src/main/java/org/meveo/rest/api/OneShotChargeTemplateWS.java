package org.meveo.rest.api;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.OneShotChargeTemplateServiceApi;
import org.meveo.api.dto.OneShotChargeDto;

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

	public List<OneShotChargeDto> getOneShotChargeTemplates(
			String languageCode, String countryCode, String currencyCode,
			String providerCode, String sellerCode, Date date) {

		return oneShotChargeTemplateServiceApi.getOneShotChargeTemplates(
				languageCode, countryCode, currencyCode, providerCode,
				sellerCode, date);

	}

}
