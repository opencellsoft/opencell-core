package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;

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
	ActionStatus instantiateServices(@WebParam(name = "instantiateServices") InstantiateServicesRequestDto postData);

	@WebMethod
	ActionStatus activateServices(@WebParam(name = "activateServices") ActivateServicesRequestDto postData);

	@WebMethod
	ActionStatus applyOneShotChargeInstance(@WebParam(name = "applyOneShotChargeInstance") ApplyOneShotChargeInstanceRequestDto postData);

	@WebMethod
	ActionStatus terminateSubscription(@WebParam(name = "terminateSubscription") TerminateSubscriptionRequestDto postData);

	@WebMethod
	ActionStatus terminateServices(@WebParam(name = "terminateSubscriptionServices") TerminateSubscriptionServicesRequestDto postData);

	@WebMethod
	SubscriptionsResponseDto listSubscriptionByUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);

	@WebMethod
	GetSubscriptionResponseDto findSubscription(@WebParam(name = "subscriptionCode") String subscriptionCode);
	
	@WebMethod
	ActionStatus createOrUpdateSubscription(@WebParam(name = "subscription") SubscriptionDto postData);

}
