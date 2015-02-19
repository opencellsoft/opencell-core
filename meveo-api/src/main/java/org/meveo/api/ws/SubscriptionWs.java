package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ActivateServicesDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesDto;
import org.meveo.api.dto.response.billing.ListSubscriptionResponseDto;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface SubscriptionWs extends IBaseWs {

	@WebMethod
	ActionStatus create(@WebParam(name = "subscription") SubscriptionDto postData);

	@WebMethod
	ActionStatus update(@WebParam(name = "subscription") SubscriptionDto postData);

	@WebMethod
	ActionStatus activateServices(@WebParam(name = "activateServices") ActivateServicesDto postData);

	@WebMethod
	ActionStatus applyOneShotChargeInstance(
			@WebParam(name = "applyOneShotChargeInstance") ApplyOneShotChargeInstanceDto postData);

	@WebMethod
	ActionStatus terminateSubscription(@WebParam(name = "terminateSubscription") TerminateSubscriptionDto postData);

	@WebMethod
	ActionStatus terminateServices(
			@WebParam(name = "terminateSubscriptionServices") TerminateSubscriptionServicesDto postData);

	@WebMethod
	ListSubscriptionResponseDto listSubscriptionByUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);

}
