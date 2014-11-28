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
	public ActionStatus create(CountryDto countryDto);

	@WebMethod
	public GetCountryResponse find(String countryCode);

	@WebMethod
	public ActionStatus remove(String countryCode, String currencyCode);

	@WebMethod
	public ActionStatus update(CountryDto countryDto);

}
