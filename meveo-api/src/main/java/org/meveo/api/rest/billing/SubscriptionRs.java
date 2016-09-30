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
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsListResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.rest.security.RSSecured;

/**
 * @author Edward P. Legaspi
 **/
@Path("/billing/subscription")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface SubscriptionRs extends IBaseRs {

    /**
     * Create a subscription. It does not activate it
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    ActionStatus create(SubscriptionDto postData);

    /**
     * Updates a subscription. It cannot update a subscription with status=RESILIATED
     * 
     * @param postData
     * @return
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
     * @param postData
     * @return
     */
    @POST
    @Path("/activateServices")
    ActionStatus activateServices(ActivateServicesRequestDto postData);

    /**
     * Apply one shot charge. Subscription should not be in status (RESILIATED OR CANCELLED).
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/applyOneShotChargeInstance")
    ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData);

    /**
     * Terminate a subscription. If subscription status is RESILIATED, an error is thrown
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/terminate")
    ActionStatus terminateSubscription(TerminateSubscriptionRequestDto postData);

    /**
     * Terminate a list of services. If a service is already TERMINATED, an error is thrown.
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/terminateServices")
    ActionStatus terminateServices(TerminateSubscriptionServicesRequestDto postData);

    /**
     * List Subscription filter by userAccountCode.
     * 
     * @param userAccountCode
     * @return
     */
    @GET
    @Path("/list")
    SubscriptionsResponseDto listByUserAccount(@QueryParam("userAccountCode") String userAccountCode);

    /**
     * List All Subscriptions with pagination
     * 
     * @param pageSize
     * @param pageNumber
     * @return
     */
    @GET
    @Path("/listAll")
    SubscriptionsListResponseDto listAll(@QueryParam("pageSize") int pageSize, @QueryParam("pageNumber") int pageNumber);

    @GET
    @Path("/")
    GetSubscriptionResponseDto findSubscription(@QueryParam("subscriptionCode") String subscriptionCode);

    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(SubscriptionDto postData);

    /**
     * Apply a product on a subscription.
     * @param ApplyProductRequestDto subscription field must be set
     * @return
     */
    @POST
    @Path("/applyProduct")
    ActionStatus applyProduct(ApplyProductRequestDto postData);
}
