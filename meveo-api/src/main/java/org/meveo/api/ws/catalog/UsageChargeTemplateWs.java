package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.UsageChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetUsageChargeTemplateResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface UsageChargeTemplateWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(UsageChargeTemplateDto postData);

	@WebMethod
	public ActionStatus update(UsageChargeTemplateDto postData);

	@WebMethod
	public GetUsageChargeTemplateResponse find(String usageChargeTemplateCode);

	@WebMethod
	public ActionStatus remove(String usageChargeTemplateCode);

}
