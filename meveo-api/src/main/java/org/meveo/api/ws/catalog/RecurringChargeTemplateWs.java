package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.RecurringChargeTemplateDto;
import org.meveo.api.dto.response.catalog.GetRecurringChargeTemplateResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface RecurringChargeTemplateWs extends IBaseWs {

	@WebMethod
	public ActionStatus create(RecurringChargeTemplateDto postData);

	@WebMethod
	public GetRecurringChargeTemplateResponse find(
			String recurringChargeTemplateCode);

	@WebMethod
	public ActionStatus update(RecurringChargeTemplateDto postData);

	@WebMethod
	public ActionStatus remove(String recurringChargeTemplateCode);

}
