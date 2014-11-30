package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.dto.response.GetLanguageResponse;

/**
 * * Web service for managing {@link org.meveo.model.billing.Language} and
 * {@link org.meveo.model.billing.TradingLanguage}.
 * 
 * @author Edward P. Legaspi
 **/
@WebService
public interface LanguageWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(LanguageDto postData);

	@WebMethod
	public GetLanguageResponse find(String languageCode);

	@WebMethod
	public ActionStatus remove(String languageCode);

	@WebMethod
	public ActionStatus update(LanguageDto postData);

}
