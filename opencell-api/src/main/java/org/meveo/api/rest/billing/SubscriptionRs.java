package org.meveo.api.rest.billing;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
import org.meveo.api.dto.response.billing.GetDueDateDelayResponseDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;
import org.meveo.api.dto.response.catalog.GetOneShotChargesResponseDto;
import org.meveo.api.dto.response.catalog.GetServiceInstanceResponseDto;
import org.meveo.api.rest.IBaseRs;

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
     * List of subscriptions filter by user account's code.
     * 
     * @param userAccountCode The user account's code
     * @return Subscriptions list
     */
    @GET
    @Path("/list")
    SubscriptionsResponseDto listByUserAccount(@QueryParam("userAccountCode") String userAccountCode);

    /**
     * List All Subscriptions with pagination
     * 
     * @param pageSize Page size integer
     * @param pageNumber Page number integer
     * @return Subscriptions List
     */
    @GET
    @Path("/listAll")
    SubscriptionsListResponseDto listAll(@QueryParam("pageSize") int pageSize, @QueryParam("pageNumber") int pageNumber);

    /**
     * Search for a subscription with a given code 
     * 
     * @param subscriptionCode The subscription's code
     * @return A subscription
     */
    @GET
    @Path("/")
    GetSubscriptionResponseDto findSubscription(@QueryParam("subscriptionCode") String subscriptionCode);
    
    
    
    /**
     * Search for a subscription with a given code 
     * 
     * @param subscriptionCode The subscription's code
     * @return A subscription
     */
    @GET
    @Path("/listOneshotChargeOthers")
    GetOneShotChargesResponseDto getOneShotChargeOthers();

    /**
     * Create new or update an existing subscription
     * 
     * @param postData The subscription's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(SubscriptionDto postData);

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
     * Update an existing services
     * 
     * @param postData Operation services request's data (contains serviceToUpdate and subscriptionCode)
     * @return Request processing status
     */
    @PUT
	@Path("updateServices")
	ActionStatus updateServices(UpdateServicesRequestDto postData);
    
	@GET
	@Path("serviceInstance")
	GetServiceInstanceResponseDto findServiceInstance(@QueryParam("subscriptionCode") String subscriptionCode,
			@QueryParam("serviceInstanceCode") String serviceInstanceCode);

    /**
     * Returns the due date delay information.
     * 
     * @param subscriptionCode - required
     * @param invoiceNumber - invoice number, can be null
     * @param invoiceTypeCode - can be null
     * @param orderCode - can be null
     * @return
     */
	@GET
	@Path("/dueDateDelay")
	GetDueDateDelayResponseDto findDueDateDelay(@QueryParam("subscriptionCode") String subscriptionCode,
			@QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("invoiceTypeCode") String invoiceTypeCode,
			@QueryParam("orderCode") String orderCode);
	
}
