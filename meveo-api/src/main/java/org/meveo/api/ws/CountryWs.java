package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.dto.response.GetCountryResponse;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface CountryWs {

	@WebMethod
	ActionStatus create(CountryDto countryDto);

	@WebMethod
	GetCountryResponse find(String countryCode);

	@WebMethod
	ActionStatus remove(String countryCode, String currencyCode);

	@WebMethod
	ActionStatus update(CountryDto countryDto);

}
