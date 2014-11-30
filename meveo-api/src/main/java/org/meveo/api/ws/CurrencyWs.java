package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CurrencyDto;
import org.meveo.api.dto.response.GetCurrencyResponse;

/**
 * Web service for managing {@link org.meveo.model.billing.Currency} and
 * {@link org.meveo.model.billing.TradingCurrency}.
 * 
 * @author Edward P. Legaspi
 **/
@WebService
public interface CurrencyWs extends IBaseWs {

	@WebMethod
	ActionStatus create(CurrencyDto postData);

	@WebMethod
	GetCurrencyResponse find(String currencyCode);

	@WebMethod
	ActionStatus remove(String currencyCode);

	@WebMethod
	ActionStatus update(CurrencyDto postData);

}
