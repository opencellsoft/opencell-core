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

package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.*;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.billing.GetDueDateDelayResponseDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.RateSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.catalog.GetListServiceInstanceResponseDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargesResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceInstanceResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.PATCH;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import java.util.Date;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billing/subscription")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface SubscriptionRs extends IBaseRs {

    /**
     * Create a subscription. It does not activate it
     * 
     * @param postData Subscription's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(SubscriptionDto postData);

    /**
     * Updates a subscription. It cannot update a subscription with status=RESILIATED
     * 
     * @param postData Subscription's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(SubscriptionDto postData);

    /**
     * Instantiate a Service subscription 
     * 
     * @param postData Subscription's data
     * @return Request processing status
     */
    @POST
    @Path("/instantiateServices")
    ActionStatus instantiateServices(InstantiateServicesRequestDto postData);

    /**
     * Activate services. Subscription should not be in status (RESILIATED OR CANCELLED). This service allows to override the charge instance price before activation. This service
     * is actually a 2 step process: service instantiation then activation. If service.subscriptionDate is not set a service is only instantiated else it's instantiated then
     * activated.
     * 
     * @param postData Activate services request's data
     * @return Request processing status
     */
    @POST
    @Path("/activateServices")
    ActionStatus activateServices(ActivateServicesRequestDto postData);

    /**
     * Apply one shot charge. Subscription should not be in status (RESILIATED OR CANCELLED).
     * 
     * @param postData ApplyOneShotChargeInstanceRequestDto's data
     * @return Request processing status
     */
    @POST
    @Path("/applyOneShotChargeInstance")
    ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData);

    /**
     * Terminate a subscription. If subscription status is RESILIATED, an error is thrown
     * 
     * @param postData Terminate subscription request's data
     * @return Request processing status
     */
    @POST
    @Path("/terminate")
    ActionStatus terminateSubscription(TerminateSubscriptionRequestDto postData);

    /**
     * Terminate a list of services. If a service is already TERMINATED, an error is thrown.
     * 
     * @param postData Terminate subscription services request's data
     * @return Request processing status
     */
    @POST
    @Path("/terminateServices")
    ActionStatus terminateServices(TerminateSubscriptionServicesRequestDto postData);

    /**
     * List subscriptions matching a given criteria
     * 
     * @param userAccountCode The user account's code. Deprecated in v. 4.7.2 Use query=userAccount.code:code instead
     * @param mergedCF Should inherited custom field values be included. Deprecated in v. 4.7.2 Use pagingAndFiltering.fields="inheritedCF" instead
     * @param query Search criteria
     * @param fields Data retrieval options/fieldnames separated by a comma
     * @param offset Pagination - from record number
     * @param limit Pagination - number of records to retrieve
     * @param sortBy Sorting - field to sort by - a field from a main entity being searched. See Data model for a list of fields.
     * @param sortOrder Sorting - sort order.
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return List of subscriptions
     */
    @GET
    @Path("/list")
    public SubscriptionsListResponseDto listGet(@Deprecated @QueryParam("userAccountCode") String userAccountCode, @QueryParam("mergedCF") Boolean mergedCF,
            @QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);
    
    /**
     * List subscriptions matching a given criteria
     * 
     * @param customerCode The customer's code.
     * @return List of subscriptions
     */
    @GET
    @Path("/findByCustomer")
    public SubscriptionsListResponseDto findByCustomer(@QueryParam("customerCode") String customerCode);

    /**
     * List subscriptions matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of subscriptions
     */
    @POST
    @Path("/list")
    public SubscriptionsListResponseDto listPost(PagingAndFiltering pagingAndFiltering);

    /**
     * Deprecated in v.4.7.2 Use /list instead.
     * 
     * @param offset offset
     * @param limit number of elements in response
     * @param mergedCF true if return
     * @param sortBy sortby field
     * @param sortOrder ASC/DESC
     * @return list of all subscriptions.
     */
    @GET
    @Deprecated
    @Path("/listAll")
    SubscriptionsListResponseDto listAll(@QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit, @DefaultValue("false") @QueryParam("mergedCF") boolean mergedCF,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Search for a subscription with a given code.
     * 
     * @param subscriptionCode The subscription's code
     * @param mergedCF true if merge inherited custom fields.
     * @param inheritCF Should inherited custom fields be retrieved. Defaults to INHERIT_NO_MERGE.
     * @return Request processing status
     */
    @GET
    @Path("/")
    GetSubscriptionResponseDto findSubscription(@QueryParam("subscriptionCode") String subscriptionCode,
            @Deprecated @DefaultValue("false") @QueryParam("mergedCF") boolean mergedCF,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF,
                                                @QueryParam("validityDate") @RestDateParam Date validityDate);

    /**
     * Search for a subscription with a given code.
     * 
     * @param subscriptionCode The subscription's code
     * @param oneshotChargeCode one shot Charge Code
     * @return A subscription
     */
    @DELETE
    @Path("/oneShotCharge/{subscriptionCode}/{oneshotChargeCode}")
    ActionStatus terminateOneShotCharge(@PathParam("subscriptionCode") String subscriptionCode, @PathParam("oneshotChargeCode") String oneshotChargeCode, @QueryParam("validityDate") Date validityDate);


    /**
     * Search for a subscription with a given code.
     * 
     * 
     * @return list of one-shot other charges.
     */
    @GET
    @Path("/listOneshotChargeOthers")
    GetOneShotChargesResponseDto getOneShotChargeOthers(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("validityDate") Date validityDate);

    /**
     * Create or update subscription information ONLY. Does not include access, services nor products
     * 
     * @param subscriptionDto Subscription information
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(SubscriptionDto subscriptionDto);

    /**
     * Create or update subscription information WITH access, services and products. Terminates subscription if termination date is provided on subscription. Terminates service if
     * termination date is provided on service. Activates inactive service if service subscription date is provided. Instantiates service if no matching service found. Updates
     * service if matching service found. Only those services, access and products passed will be afected. 
     * 
     * @param subscriptionDto Subscription information
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdatePartial")
    ActionStatus createOrUpdateSubscriptionPartial(SubscriptionDto subscriptionDto);

    /**
     * Apply a product on a subscription.
     *
     * @param postData ApplyProductRequestDto subscription field must be set
     * @return Request processing status
     */
    @POST
    @Path("/applyProduct")
    ActionStatus applyProduct(ApplyProductRequestDto postData);

    /**
     * Suspend an existing subscription
     * 
     * @param postData Operation subscription request's data (contains actionDate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("suspend")
    ActionStatus suspendSubscription(OperationSubscriptionRequestDto postData);

    /**
     * Resume an existing subscription
     * 
     * @param postData Operation subscription request's data (contains actionDate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("resume")
    ActionStatus resumeSubscription(OperationSubscriptionRequestDto postData);

    /**
     * Suspend an existing services
     * 
     * @param postData Operation services request's data (contains serviceToUpdate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("suspendServices")
    ActionStatus suspendServices(OperationServicesRequestDto postData);

    /**
     * Resume an existing services
     * 
     * @param postData Operation services request's data (contains serviceToUpdate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("resumeServices")
    ActionStatus resumeServices(OperationServicesRequestDto postData);

    /**
     * Update existing services
     * 
     * @param postData Service information data
     * @return Request processing status
     */
    @PUT
    @Path("updateServices")
    ActionStatus updateServices(UpdateServicesRequestDto postData);

    /**
     * Find service instance.
     * 
     * @param subscriptionCode Subscription code
     * @param serviceInstanceCode Service instance code
     * @param serviceInstanceId service instance id
     * @return Service instance
     */
    @GET
    @Path("serviceInstance")
    GetServiceInstanceResponseDto findServiceInstance(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("serviceInstanceId") Long serviceInstanceId,
            @QueryParam("serviceInstanceCode") String serviceInstanceCode, @QueryParam("subscriptionValidityDate") Date subscriptionValidityDate);

    /**
     * Returns a list of service instances.
     * 
     * @param subscriptionCode subscription code
     * @param subscriptionValidityDate
     * @param serviceInstanceCode service instance code.
     * @return list of service instances
     */
    @GET
    @Path("serviceInstances")
    GetListServiceInstanceResponseDto listServiceInstance(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") Date subscriptionValidityDate, @QueryParam("serviceInstanceCode") String serviceInstanceCode);

    /**
     * Returns the due date delay information.
     * 
     * @param subscriptionCode - required
     * @param invoiceNumber - invoice number, can be null
     * @param invoiceTypeCode - can be null
     * @param orderCode - can be null
     * @return list of due date delay
     */
    @GET
    @Path("/dueDateDelay")
    GetDueDateDelayResponseDto findDueDateDelay(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") Date subscriptionValidityDate ,@QueryParam("invoiceNumber") String invoiceNumber,
            @QueryParam("invoiceTypeCode") String invoiceTypeCode, @QueryParam("orderCode") String orderCode);
    
    /**
     * Give a rate data for subscription
     * 
     * @param postData post data
     * @return list of service instances
     */
    @POST
    @Path("/rate")
    RateSubscriptionResponseDto rate(RateSubscriptionRequestDto postData);
    
    /**
     * Activate a given Subscription.
     * 
     * @param subscriptionCode subscription code
     * @return Request processing status
     */
    @POST
    @Path("/activate")
    ActionStatus activate(String subscriptionCode);
    
    /**
     * Activate a given Subscription for a customer.
     *
     * @param postData the post data
     * @return the raw result holding the Subscription EndAgreementDate in its response.
     */
    @POST
    @Path("/activateForCustomer")
    SubscriptionForCustomerResponseDto activateForCustomer(SubscriptionForCustomerRequestDto postData);

    /**
     * Cancels the renewal term of an active subscription.
     * @param subscriptionCode code of the subscription
     * @return status of the request
     */
    @POST
    @Path("/cancelSubscriptionRenewal/{subscriptionCode}")
    ActionStatus cancelSubscriptionRenewal(@PathParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") Date subscriptionValidityDate);

    /**
     * Create a subscription and activate services in a single transaction.
     * 
     * @param postData Subscription and services to activate data
     * @return Request processing status
     */
    @POST
    @Path("/subscribeAndActivateServices")
    ActionStatus subscribeAndActivateServices(SubscriptionAndServicesToActivateRequestDto postData);

    @PATCH
    @Path("/{code}/offer")
    ActionStatus patchSubscription(@PathParam("code") String code, SubscriptionPatchDto subscriptionPatchDto);

    @PATCH
    @Path("{code}/offer/rollback")
    ActionStatus rollbackOffer(@PathParam("code") String code, OfferRollbackDto offerRollbackDto);
}
