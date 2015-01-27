package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ActivateServicesDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface SubscriptionWs extends IBaseWs {

	@WebMethod
	ActionStatus create(SubscriptionDto postData);

	@WebMethod
	ActionStatus update(SubscriptionDto postData);

	@WebMethod
	ActionStatus activateServices(ActivateServicesDto postData);

	@WebMethod
	ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceDto postData);

}
