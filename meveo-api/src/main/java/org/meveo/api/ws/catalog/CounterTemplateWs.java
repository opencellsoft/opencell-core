package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.response.catalog.GetCounterTemplateResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface CounterTemplateWs extends IBaseWs {

	@WebMethod
	ActionStatus create(CounterTemplateDto postData);

	@WebMethod
	ActionStatus update(CounterTemplateDto postData);

	@WebMethod
	GetCounterTemplateResponse find(String counterTemplateCode);

	@WebMethod
	ActionStatus remove(String counterTemplateCode);

}
