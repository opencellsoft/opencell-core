package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.PricePlanDto;
import org.meveo.api.dto.response.catalog.GetPricePlanResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface PricePlanWs extends IBaseWs {

	@WebMethod
	ActionStatus create(PricePlanDto postData);

	@WebMethod
	ActionStatus update(PricePlanDto postData);

	@WebMethod
	GetPricePlanResponse find(Long id);

	@WebMethod
	ActionStatus remove(Long id);

}
