package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.OperationServicesRequestDto;
import org.meveo.api.dto.billing.OperationSubscriptionRequestDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.api.dto.response.Paging;
import org.meveo.api.dto.response.billing.GetDueDateDelayResponseDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceInstanceResponseDto;

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
	ActionStatus applyProduct(@WebParam(name = "applyProduct") ApplyProductRequestDto postData);
	
	@WebMethod
	ActionStatus terminateSubscription(@WebParam(name = "terminateSubscription") TerminateSubscriptionRequestDto postData);

	@WebMethod
	ActionStatus terminateServices(@WebParam(name = "terminateSubscriptionServices") TerminateSubscriptionServicesRequestDto postData);

	@WebMethod
	SubscriptionsResponseDto listSubscriptionByUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);
	
	@WebMethod
	SubscriptionsListResponseDto listAll(@WebParam(name = "mergedCF") boolean mergedCF, @WebParam(name = "paging") Paging paging);

	@WebMethod
	GetSubscriptionResponseDto findSubscription(@WebParam(name = "subscriptionCode") String subscriptionCode);
	
	@WebMethod
	ActionStatus createOrUpdateSubscription(@WebParam(name = "subscription") SubscriptionDto postData);
	
	@WebMethod
	ActionStatus suspendSubscription(@WebParam(name = "suspendSubscriptionRequestDto") OperationSubscriptionRequestDto postData);
	
	@WebMethod
	ActionStatus resumeSubscription(@WebParam(name = "suspendSubscriptionRequestDto") OperationSubscriptionRequestDto postData);
	
	@WebMethod
	ActionStatus suspendServices(@WebParam(name = "operationServicesRequestDto") OperationServicesRequestDto postData);
	
	@WebMethod
	ActionStatus resumeServices(@WebParam(name = "operationServicesRequestDto") OperationServicesRequestDto postData);
	
	@WebMethod
	ActionStatus updateServices(@WebParam(name = "updateServicesRequest") UpdateServicesRequestDto postData);
	
	/**
     * Returns the due date delay information.
     * 
     * @param subscriptionCode - required
     * @param invoiceNumber - invoice number, can be null
     * @param invoiceTypeCode - can be null
     * @param orderCode - can be null
     * @return
     */
	@WebMethod
	GetDueDateDelayResponseDto findDueDateDelay(@WebParam(name = "subscriptionCode") String subscriptionCode,
			@WebParam(name = "invoiceNumber") String invoiceNumber,
			@WebParam(name = "invoiceTypeCode") String invoiceTypeCode, @WebParam(name = "orderCode") String orderCode);

	@WebMethod
	GetServiceInstanceResponseDto findServiceInstance(@WebParam(name = "subscriptionCode") String subscriptionCode,
			@WebParam(name = "serviceInstanceCode") String serviceInstanceCode);
	
}
