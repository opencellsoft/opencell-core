package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.response.catalog.GetDiscountPlanResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface DiscountPlanWs extends IBaseWs {

	@WebMethod
	ActionStatus create(DiscountPlanDto postData);

	@WebMethod
	ActionStatus update(DiscountPlanDto postData);

	@WebMethod
	GetDiscountPlanResponse find(Long id);

	@WebMethod
	ActionStatus remove(Long id);

}
