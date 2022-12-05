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

import java.util.Date;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.ActivateSubscriptionRequestDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.OfferRollbackDto;
import org.meveo.api.dto.billing.OperationServicesRequestDto;
import org.meveo.api.dto.billing.OperationSubscriptionRequestDto;
import org.meveo.api.dto.billing.RateSubscriptionRequestDto;
import org.meveo.api.dto.billing.SubscriptionAndProductsToInstantiateDto;
import org.meveo.api.dto.billing.SubscriptionAndServicesToActivateRequestDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionForCustomerRequestDto;
import org.meveo.api.dto.billing.SubscriptionForCustomerResponseDto;
import org.meveo.api.dto.billing.SubscriptionPatchDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.billing.UpdateServicesRequestDto;
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
import org.meveo.apiv2.billing.ServiceInstanceToDelete;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billing/subscription")
@Tag(name = "Subscription", description = "@%Subscription")
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
	@Operation(
			summary=" Create a subscription. It does not activate it  ",
			description=" Create a subscription. It does not activate it  ",
			operationId="    POST_Subscription_create",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    Response create(SubscriptionDto postData);

    /**
     * Updates a subscription. It cannot update a subscription with status=RESILIATED
     * 
     * @param postData Subscription's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
	@Operation(
			summary=" Updates a subscription. It cannot update a subscription with status=RESILIATED  ",
			description=" Updates a subscription. It cannot update a subscription with status=RESILIATED  ",
			operationId="    PUT_Subscription_update",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    Response update(SubscriptionDto postData);

    /**
     * Instantiate a Service subscription 
     * 
     * @param postData Subscription's data
     * @return Request processing status
     */
    @POST
    @Path("/instantiateServices")
	@Operation(
			summary=" Instantiate a Service subscription   ",
			description=" Instantiate a Service subscription   ",
			operationId="    POST_Subscription_instantiateServices",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Activate services",
			description=" Activate services. Subscription should not be in status (RESILIATED OR CANCELLED). This service allows to override the charge instance price before activation. This service is actually a 2 step process: service instantiation then activation. If service.subscriptionDate is not set a service is only instantiated else it's instantiated then activated.  ",
			operationId="    POST_Subscription_activateServices",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus activateServices(ActivateServicesRequestDto postData);

    /**
     * Apply one shot charge. Subscription should not be in status (RESILIATED OR CANCELLED).
     * 
     * @param postData ApplyOneShotChargeInstanceRequestDto's data
     * @return Request processing status
     */
    @POST
    @Path("/applyOneShotChargeInstance")
	@Operation(
			summary=" Apply one shot charge. Subscription should not be in status (RESILIATED OR CANCELLED).  ",
			description=" Apply one shot charge. Subscription should not be in status (RESILIATED OR CANCELLED).  ",
			operationId="    POST_Subscription_applyOneShotChargeInstance",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData);

    /**
     * Terminate a subscription. If subscription status is RESILIATED, an error is thrown
     * 
     * @param postData Terminate subscription request's data
     * @return Request processing status
     */
    @POST
    @Path("/terminate")
	@Operation(
			summary=" Terminate a subscription. If subscription status is RESILIATED, an error is thrown  ",
			description=" Terminate a subscription. If subscription status is RESILIATED, an error is thrown  ",
			operationId="    POST_Subscription_terminate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus terminateSubscription(TerminateSubscriptionRequestDto postData);

    /**
     * Terminate a subscription. If subscription status is RESILIATED, an error is thrown
     *
     * @param postData Terminate subscription request's data
     * @return Request processing status
     */
    @PUT
    @Path("/terminate")
	@Operation(
			summary=" Terminate a subscription. If subscription status is RESILIATED, an error is thrown ",
			description=" Terminate a subscription. If subscription status is RESILIATED, an error is thrown ",
			operationId="    PUT_Subscription_terminate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus terminateSubscriptionPut(TerminateSubscriptionRequestDto postData);

    /**
     * Terminate a list of services. If a service is already TERMINATED, an error is thrown.
     * 
     * @param postData Terminate subscription services request's data
     * @return Request processing status
     */
    @POST
    @Path("/terminateServices")
	@Operation(
			summary=" Terminate a list of services. If a service is already TERMINATED, an error is thrown.  ",
			description=" Terminate a list of services. If a service is already TERMINATED, an error is thrown.  ",
			operationId="    POST_Subscription_terminateServices",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" List subscriptions matching a given criteria  ",
			description=" List subscriptions matching a given criteria  ",
			operationId="    GET_Subscription_list",
			responses= {
				@ApiResponse(description=" List of subscriptions ",
						content=@Content(
									schema=@Schema(
											implementation= SubscriptionsListResponseDto.class
											)
								)
				)}
	)
    SubscriptionsListResponseDto listGet(@Deprecated @QueryParam("userAccountCode") String userAccountCode, @QueryParam("mergedCF") Boolean mergedCF,
            @QueryParam("query") String query, @QueryParam("fields") String fields, @QueryParam("offset") Integer offset, @QueryParam("limit") Integer limit,
            @DefaultValue("code") @QueryParam("sortBy") String sortBy, @DefaultValue("ASCENDING") @QueryParam("sortOrder") SortOrder sortOrder,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * List subscriptions matching a given criteria
     *
     * @return List of subscriptions
     */
    @GET
    @Path("/listGetAll")
	@Operation(
			summary=" List subscriptions matching a given criteria ",
			description=" List subscriptions matching a given criteria ",
			operationId="    GET_Subscription_listGetAll",
			responses= {
				@ApiResponse(description=" List of subscriptions ",
						content=@Content(
									schema=@Schema(
											implementation= SubscriptionsListResponseDto.class
											)
								)
				)}
	)
    SubscriptionsListResponseDto list();
    
    /**
     * List subscriptions matching a given criteria
     * 
     * @param customerCode The customer's code.
     * @return List of subscriptions
     */
    @GET
    @Path("/findByCustomer")
	@Operation(
			summary=" List subscriptions matching a given criteria  ",
			description=" List subscriptions matching a given criteria  ",
			operationId="    GET_Subscription_findByCustomer",
			responses= {
				@ApiResponse(description=" List of subscriptions ",
						content=@Content(
									schema=@Schema(
											implementation= SubscriptionsListResponseDto.class
											)
								)
				)}
	)
    SubscriptionsListResponseDto findByCustomer(@QueryParam("customerCode") String customerCode);

    /**
     * List subscriptions matching a given criteria
     * 
     * @param pagingAndFiltering Pagination and filtering criteria
     * @return List of subscriptions
     */
    @POST
    @Path("/list")
	@Operation(
			summary=" List subscriptions matching a given criteria  ",
			description=" List subscriptions matching a given criteria  ",
			operationId="    POST_Subscription_list",
			responses= {
				@ApiResponse(description=" List of subscriptions ",
						content=@Content(
									schema=@Schema(
											implementation= SubscriptionsListResponseDto.class
											)
								)
				)}
	)
    SubscriptionsListResponseDto listPost(PagingAndFiltering pagingAndFiltering);

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
	@Operation(
			summary=" Deprecated in v.4.7.2 Use /list instead.  ",
			description=" Deprecated in v.4.7.2 Use /list instead.  ",
			deprecated=true,
			operationId="    GET_Subscription_listAll",
			responses= {
				@ApiResponse(description=" list of all subscriptions. ",
						content=@Content(
									schema=@Schema(
											implementation= SubscriptionsListResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Search for a subscription with a given code.  ",
			description=" Search for a subscription with a given code.  ",
			operationId="    GET_Subscription_search",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= GetSubscriptionResponseDto.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Search for a subscription with a given code.  ",
			description=" Search for a subscription with a given code.  ",
			operationId="    DELETE_Subscription_oneShotCharge_{subscriptionCode}_{oneshotChargeCode}",
			responses= {
				@ApiResponse(description=" A subscription ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus terminateOneShotCharge(@PathParam("subscriptionCode") String subscriptionCode, @PathParam("oneshotChargeCode") String oneshotChargeCode, @QueryParam("validityDate") Date validityDate);


    /**
     * Search for a subscription with a given code.
     * 
     * 
     * @return list of one-shot other charges.
     */
    @GET
    @Path("/listOneshotChargeOthers")
	@Operation(
			summary=" Search for a subscription with a given code.   ",
			description=" Search for a subscription with a given code.   ",
			operationId="    GET_Subscription_listOneshotChargeOthers",
			responses= {
				@ApiResponse(description=" list of one-shot other charges. ",
						content=@Content(
									schema=@Schema(
											implementation= GetOneShotChargesResponseDto.class
											)
								)
				)}
	)
    GetOneShotChargesResponseDto getOneShotChargeOthers(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("validityDate") Date validityDate);

    /**
     * Create or update subscription information ONLY. Does not include access, services nor products
     * 
     * @param subscriptionDto Subscription information
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
	@Operation(
			summary=" Create or update subscription information ONLY. Does not include access, services nor products  ",
			description=" Create or update subscription information ONLY. Does not include access, services nor products  ",
			operationId="    POST_Subscription_createOrUpdate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    Response createOrUpdate(SubscriptionDto subscriptionDto);

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
	@Operation(
			summary=" Create or update subscription information WITH access, services and products",
			description=" Create or update subscription information WITH access, services and products. Terminates subscription if termination date is provided on subscription. Terminates service if termination date is provided on service. Activates inactive service if service subscription date is provided. Instantiates service if no matching service found. Updates service if matching service found. Only those services, access and products passed will be afected.   ",
			operationId="    POST_Subscription_createOrUpdatePartial",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus createOrUpdateSubscriptionPartial(SubscriptionDto subscriptionDto);

    /**
     * Apply a product on a subscription.
     *
     * @param postData ApplyProductRequestDto subscription field must be set
     * @return Request processing status
     */
    @POST
    @Path("/applyProduct")
	@Operation(
			summary=" Apply a product on a subscription. ",
			description=" Apply a product on a subscription. ",
			operationId="    POST_Subscription_applyProduct",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus applyProduct(ApplyProductRequestDto postData);

    /**
     * Suspend an existing subscription
     * 
     * @param postData Operation subscription request's data (contains actionDate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("suspend")
	@Operation(
			summary=" Suspend an existing subscription  ",
			description=" Suspend an existing subscription  ",
			operationId="    PUT_Subscriptionsuspend",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus suspendSubscription(OperationSubscriptionRequestDto postData);

    /**
     * Resume an existing subscription
     * 
     * @param postData Operation subscription request's data (contains actionDate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("resume")
	@Operation(
			summary=" Resume an existing subscription  ",
			description=" Resume an existing subscription  ",
			operationId="    PUT_Subscriptionresume",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus resumeSubscription(OperationSubscriptionRequestDto postData);

    /**
     * Suspend an existing services
     * 
     * @param postData Operation services request's data (contains serviceToUpdate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("suspendServices")
	@Operation(
			summary=" Suspend an existing services  ",
			description=" Suspend an existing services  ",
			operationId="    PUT_SubscriptionsuspendServices",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus suspendServices(OperationServicesRequestDto postData);

    /**
     * Resume an existing services
     * 
     * @param postData Operation services request's data (contains serviceToUpdate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
    @Path("resumeServices")
	@Operation(
			summary=" Resume an existing services  ",
			description=" Resume an existing services  ",
			operationId="    PUT_SubscriptionresumeServices",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus resumeServices(OperationServicesRequestDto postData);

    /**
     * Update existing services
     * 
     * @param postData Service information data
     * @return Request processing status
     */
    @PUT
    @Path("updateServices")
	@Operation(
			summary=" Update existing services  ",
			description=" Update existing services  ",
			operationId="    PUT_SubscriptionupdateServices",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
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
	@Operation(
			summary=" Find service instance.  ",
			description=" Find service instance.  ",
			operationId="    GET_SubscriptionserviceInstance",
			responses= {
				@ApiResponse(description=" Service instance ",
						content=@Content(
									schema=@Schema(
											implementation= GetServiceInstanceResponseDto.class
											)
								)
				)}
	)
    GetServiceInstanceResponseDto findServiceInstance(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("serviceInstanceId") Long serviceInstanceId,
            @QueryParam("serviceInstanceCode") String serviceInstanceCode, @QueryParam("subscriptionValidityDate") @RestDateParam Date subscriptionValidityDate);

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
	@Operation(
			summary=" Returns a list of service instances.  ",
			description=" Returns a list of service instances.  ",
			operationId="    GET_SubscriptionserviceInstances",
			responses= {
				@ApiResponse(description=" list of service instances ",
						content=@Content(
									schema=@Schema(
											implementation= GetListServiceInstanceResponseDto.class
											)
								)
				)}
	)
    GetListServiceInstanceResponseDto listServiceInstance(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") @RestDateParam Date subscriptionValidityDate, @QueryParam("serviceInstanceCode") String serviceInstanceCode);

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
	@Operation(
			summary=" Returns the due date delay information.  ",
			description=" Returns the due date delay information.  ",
			operationId="    GET_Subscription_dueDateDelay",
			responses= {
				@ApiResponse(description=" list of due date delay ",
						content=@Content(
									schema=@Schema(
											implementation= GetDueDateDelayResponseDto.class
											)
								)
				)}
	)
    GetDueDateDelayResponseDto findDueDateDelay(@QueryParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") @RestDateParam Date subscriptionValidityDate ,@QueryParam("invoiceNumber") String invoiceNumber,
            @QueryParam("invoiceTypeCode") String invoiceTypeCode, @QueryParam("orderCode") String orderCode);
    
    /**
     * Give a rate data for subscription
     * 
     * @param postData post data
     * @return list of service instances
     */
    @POST
    @Path("/rate")
	@Operation(
			summary=" Give a rate data for subscription  ",
			description=" Give a rate data for subscription  ",
			operationId="    POST_Subscription_rate",
			responses= {
				@ApiResponse(description=" list of service instances ",
						content=@Content(
									schema=@Schema(
											implementation= RateSubscriptionResponseDto.class
											)
								)
				)}
	)
    RateSubscriptionResponseDto rate(RateSubscriptionRequestDto postData);

    /**
     * Activate a given Subscription.
     *
     * @param subscriptionCode subscription code
     * @param subscriptionValidityDate
     * @return Request processing status
     */
    @POST
    @Path("/activate")
	@Operation(
			summary=" Activate a given Subscription. ",
			description=" Activate a given Subscription. ",
			operationId="    POST_Subscription_activate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus activate(String subscriptionCode, @QueryParam("subscriptionValidityDate") @RestDateParam Date subscriptionValidityDate);

	/**
	 * Activate the patched version of a given Subscription.
	 *
	 * @param subscriptionCode subscription code
	 * @param updateEffectiveDate should update effective date or not
	 * @param newEffectiveDate new effective date
	 * @return Request processing status
	 */
	@POST
	@Path("/activatePatchedSubscription")
	@Operation(
			summary=" Activate the patched version of a given Subscription. ",
			description=" Activate the patched version of a given Subscription. ",
			operationId="    POST_Patched_Subscription_activate",
			responses= {
					@ApiResponse(description=" Request processing status ",
							content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
									)
							)
					)}
	)
	ActionStatus activatePatchedSubscription(String subscriptionCode, @QueryParam("updateEffectiveDate") Boolean updateEffectiveDate, @QueryParam("newEffectiveDate") @RestDateParam Date newEffectiveDate);

	/**
     * Activate a given Subscription.
     * 
     * @param putData containing subscription code
     * @return Request processing status
     */
    @PUT
    @Path("/activate")
	@Operation(
			summary=" Activate a given Subscription.  ",
			description=" Activate a given Subscription.  ",
			operationId="    PUT_Subscription_activate",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus activate(ActivateSubscriptionRequestDto putData);
    
    /**
     * Activate a given Subscription for a customer.
     *
     * @param postData the post data
     * @return the raw result holding the Subscription EndAgreementDate in its response.
     */
    @POST
    @Path("/activateForCustomer")
	@Operation(
			summary=" Activate a given Subscription for a customer. ",
			description=" Activate a given Subscription for a customer. ",
			operationId="    POST_Subscription_activateForCustomer",
			responses= {
				@ApiResponse(description=" the raw result holding the Subscription EndAgreementDate in its response. ",
						content=@Content(
									schema=@Schema(
											implementation= SubscriptionForCustomerResponseDto.class
											)
								)
				)}
	)
    SubscriptionForCustomerResponseDto activateForCustomer(SubscriptionForCustomerRequestDto postData);

    /**
     * Cancels the renewal term of an active subscription.
     * @param subscriptionCode code of the subscription
     * @return status of the request
     */
    @POST
    @Path("/cancelSubscriptionRenewal/{subscriptionCode}")
	@Operation(
			summary=" Cancels the renewal term of an active subscription. ",
			description=" Cancels the renewal term of an active subscription. ",
			operationId="    POST_Subscription_cancelSubscriptionRenewal_{subscriptionCode}",
			responses= {
				@ApiResponse(description=" status of the request ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus cancelSubscriptionRenewal(@PathParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") @RestDateParam Date subscriptionValidityDate);

	/**
	 * Cancels the programed termination of a subscription.
	 * @param subscriptionCode code of the subscription
	 * @return status of the request
	 */
	@POST
	@Path("/cancelSubscriptionTermination/{subscriptionCode}")
	@Operation(
			summary=" Cancels the programed termination of a subscription. ",
			description=" Cancels the programed termination of a subscription. ",
			operationId="    POST_Subscription_cancelSubscriptionRenewal_{subscriptionCode}",
			responses= {
					@ApiResponse(description=" status of the request ",
							content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
									)
							)
					)}
	)
	ActionStatus cancelSubscriptionTermination(@PathParam("subscriptionCode") String subscriptionCode, @QueryParam("subscriptionValidityDate") @RestDateParam Date subscriptionValidityDate);

	/**
	 * For V11 catalog use : subscribeAndInstantiateProducts API, then activateServices API
     * Create a subscription and activate services in a single transaction.
     * 
     * @param postData Subscription and services to activate data
     * @return Request processing status
     */
	@Deprecated(since = "V11.1.0")
    @POST
    @Path("/subscribeAndActivateServices")
	@Operation(
			summary=" Create a subscription and activate services in a single transaction.  ",
			description=" Create a subscription and activate services in a single transaction.  ",
			operationId="    POST_Subscription_subscribeAndActivateServices",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus subscribeAndActivateServices(SubscriptionAndServicesToActivateRequestDto postData);
    
    
    /**
     * Create a subscription and instanciate product in a single transaction.
     * 
     * @param postData Subscription and products to i
     * @return Request processing status
     */
    @POST
    @Path("/subscribeAndInstantiateProducts")
	@Operation(
			summary=" Create a subscription and instanciate product in a single transaction.  ",
			description=" Create a subscription and instanciate product in a single transaction.  ",
			operationId="    POST_Subscription_subscribeAndInstantiateProducts",
			responses= {
				@ApiResponse(description=" Request processing status ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus subscribeAndInstantiateProducts(SubscriptionAndProductsToInstantiateDto postData);

    /**
     * patch subscription
     * 
     * @param code
     * @param subscriptionPatchDto
     * @return
     */
    @PATCH
    @Path("/{code}/offer")
	@Operation(
			summary=" patch subscription  ",
			description=" patch subscription  ",
			operationId="    PATCH_Subscription_{code}_offer",
			responses= {
				@ApiResponse(description=" ",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus patchSubscription(@PathParam("code") String code, SubscriptionPatchDto subscriptionPatchDto);

    /**
     * rollback offer
     * 
     * @param code
     * @param offerRollbackDto
     * @return
     */
    @PATCH
    @Path("{code}/offer/rollback")
	@Operation(
			summary=" rollback offer  ",
			description=" rollback offer  ",
			operationId="    PATCH_Subscription{code}_offer_rollback",
			responses= {
				@ApiResponse(description="ActionStatus response",
						content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
											)
								)
				)}
	)
    ActionStatus rollbackOffer(@PathParam("code") String code, OfferRollbackDto offerRollbackDto);
    
    /**
     * Create a subscription and instanciate product in a single transaction.
     * 
     * @param postData Subscription and products to i
     * @return Request processing status
     */
    @POST
    @Path("/subscribeAndActivateProducts")
    @Operation(
            summary=" subscribe And Activate Products ",
            description=" Create a subscribe And Activate Products  ",
            operationId="POST_Subscription_subscribeAndActivateProducts",
            responses= {
                @ApiResponse(description=" Request processing status ",
                        content=@Content(
                                    schema=@Schema(
                                            implementation= ActionStatus.class
                                            )
                                )
                )}
    )
    ActionStatus subscribeAndActivateProducts(SubscriptionAndProductsToInstantiateDto postData);

	@DELETE
	@Path("/{subscriptionId}/delete-si")
	@Operation(
			summary="API to delete 'INACTIVE' and 'PENDING' serviceInstance from subscription",
			description="API to delete 'INACTIVE' and 'PENDING' serviceInstance from subscription",
			operationId="DELETE_Subscription_serviceInstance",
			responses= {
					@ApiResponse(description=" A subscription ",
							content=@Content(
									schema=@Schema(
											implementation= ActionStatus.class
									)
							)
					)}
	)
	Response deleteInactiveServiceInstance(@PathParam("subscriptionId") Long subscriptionId, ServiceInstanceToDelete toDelete);

}
