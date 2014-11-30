package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.catalog.OneShotChargeTemplateWithPriceListDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargeTemplateResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface OneShotChargeTemplateWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(OneShotChargeTemplateDto postData);

	@WebMethod
	public ActionStatus update(OneShotChargeTemplateDto postData);

	@WebMethod
	public GetOneShotChargeTemplateResponse find(
			String oneShotChargeTemplateCode);

	@WebMethod
	public OneShotChargeTemplateWithPriceListDto listOneShotChargeTemplates(
			String languageCode, String countryCode, String currencyCode,
			String sellerCode, String date);

	@WebMethod
	public ActionStatus remove(String oneShotChargeTemplateCode);

}
