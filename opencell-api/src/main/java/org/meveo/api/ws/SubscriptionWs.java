/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.api.dto.billing.SubscriptionAndServicesToActivateRequestDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionForCustomerRequestDto;
import org.meveo.api.dto.billing.SubscriptionForCustomerResponseDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.billing.GetDueDateDelayResponseDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;
import org.meveo.api.dto.response.catalog.GetListServiceInstanceResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceInstanceResponseDto;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

@WebService
@Deprecated
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
    ActionStatus activateSubscription(@WebParam(name = "subscriptionCode") String subscriptionCode);

    /**
     * Activate a given Subscription for a customer.
     *
     * @param postData the post data
     * @return the raw result holding the Subscription EndAgreementDate in its response.
     */
    @WebMethod
    SubscriptionForCustomerResponseDto activateForCustomer(@WebParam(name = "subscription") SubscriptionForCustomerRequestDto postData);

    /**
     * Cancels the renewal term of an active subscription.
     * 
     * @param subscriptionCode code of the subscription
     * @return status of the request
     */
    @WebMethod
    ActionStatus cancelSubscriptionRenewal(@WebParam(name = "subscriptionCode") String subscriptionCode);

    /**
     * List subscriptions by a user account. Deprecated in v.4.7.2. Use listAll() instead.
     * 
     * @param userAccountCode The user account code
     * @return Subscriptions response dto
     */
    @Deprecated
    @WebMethod
    SubscriptionsResponseDto listSubscriptionByUserAccount(@WebParam(name = "userAccountCode") String userAccountCode);

    /**
     * List subscriptions
     * 
     * @param mergedCF Should inherited custom field values be included. Deprecated in v. 4.7.2 Use pagingAndFiltering.fields="inheritedCF" instead
     * @param pagingAndFiltering Paging and filtering criteria
     * @return List of subscriptions
     */
    @WebMethod
    SubscriptionsListResponseDto listAll(@Deprecated @WebParam(name = "mergedCF") Boolean mergedCF, @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

    /**
     * Find subscription by its code
     * 
     * @param subscriptionCode Subscription code
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return Subscription information
     */
    @WebMethod
    GetSubscriptionResponseDto findSubscription(@WebParam(name = "subscriptionCode") String subscriptionCode, @WebParam(name = "inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Create or update subscription information ONLY. Does not include access, services nor products
     * 
     * @param subscriptionDto Subscription information
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createOrUpdateSubscription(@WebParam(name = "subscription") SubscriptionDto subscriptionDto);

    /**
     * Create or update subscription information WITH access, services and products. Terminates subscription if termination date is provided on subscription. Terminates service if
     * termination date is provided on service. Activates inactive service if service subscription date is provided. Instantiates service if no matching service found. Updates
     * service if matching service found. Only those services, access and products passed will be afected.
     * 
     * @param subscriptionDto Subscription information
     * @return Request processing status
     */
    @WebMethod
    ActionStatus createOrUpdateSubscriptionPartial(@WebParam(name = "subscription") SubscriptionDto subscriptionDto);

    @WebMethod
    ActionStatus suspendSubscription(@WebParam(name = "suspendSubscriptionRequestDto") OperationSubscriptionRequestDto postData);

    @WebMethod
    ActionStatus resumeSubscription(@WebParam(name = "suspendSubscriptionRequestDto") OperationSubscriptionRequestDto postData);

    @WebMethod
    ActionStatus suspendServices(@WebParam(name = "operationServicesRequestDto") OperationServicesRequestDto postData);

    @WebMethod
    ActionStatus resumeServices(@WebParam(name = "operationServicesRequestDto") OperationServicesRequestDto postData);

    /**
     * Update existing services
     * 
     * @param postData Service information data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus updateServices(@WebParam(name = "updateServicesRequest") UpdateServicesRequestDto postData);

    /**
     * Returns the due date delay information.
     * 
     * @param subscriptionCode - required
     * @param invoiceNumber - invoice number, can be null
     * @param invoiceTypeCode - can be null
     * @param orderCode - can be null
     * @return The due date delay information
     */
    @WebMethod
    GetDueDateDelayResponseDto findDueDateDelay(@WebParam(name = "subscriptionCode") String subscriptionCode, @WebParam(name = "invoiceNumber") String invoiceNumber,
            @WebParam(name = "invoiceTypeCode") String invoiceTypeCode, @WebParam(name = "orderCode") String orderCode);

    /**
     * Find service instance
     * 
     * @param subscriptionCode Subscription code
     * @param serviceInstanceId Service instance id
     * @param serviceInstanceCode Service instance code
     * @return Service instance
     */
    @WebMethod
    GetServiceInstanceResponseDto findServiceInstance(@WebParam(name = "subscriptionCode") String subscriptionCode, @WebParam(name = "serviceInstanceId") Long serviceInstanceId,
            @WebParam(name = "serviceInstanceCode") String serviceInstanceCode);

    @WebMethod
    GetListServiceInstanceResponseDto listServiceInstance(@WebParam(name = "subscriptionCode") String subscriptionCode,
            @WebParam(name = "serviceInstanceCode") String serviceInstanceCode);

    /**
     * Create a subscription and activate services in a single transaction.
     * 
     * @param postData Subscription and services to activate data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus subscribeAndActivateServices(@WebParam(name = "subscribeAndActivateServices") SubscriptionAndServicesToActivateRequestDto postData);
    
    
    /**
     * Create a subscription and instantiate services in a single transaction.
     * 
     * @param postData Subscription and services to activate data
     * @return Request processing status
     */
    @WebMethod
    ActionStatus subscribeAndInstantiateServices(@WebParam(name = "subscribeAndInstanciateServices") SubscriptionAndServicesToActivateRequestDto postData);

}
