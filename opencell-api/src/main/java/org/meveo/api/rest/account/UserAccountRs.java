package org.meveo.api.rest.account;

import java.util.Date;

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
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.account.GetUserAccountResponseDto;
import org.meveo.api.dto.response.account.UserAccountsResponseDto;
import org.meveo.api.dto.response.billing.GetCountersInstancesResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.api.serialize.RestDateParam;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;

/**
 * @author Edward P. Legaspi
 **/
@Path("/account/userAccount")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface UserAccountRs extends IBaseRs {

    /**
     * Create a new user account.
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(UserAccountDto postData);

    /**
     * Update an existing user account
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @PUT
    @Path("/")
    ActionStatus update(UserAccountDto postData);

    /**
     * Search for a user account with a given code.
     * 
     * @param userAccountCode user account code
     * @return found user account if exist
     */
    @GET
    @Path("/")
    GetUserAccountResponseDto find(@QueryParam("userAccountCode") String userAccountCode,
            @DefaultValue("INHERIT_NO_MERGE") @QueryParam("inheritCF") CustomFieldInheritanceEnum inheritCF);

    /**
     * Remove an existing user account with a given code.
     * 
     * @param userAccountCode The user account's code
     * @return Request processing status
     */
    @DELETE
    @Path("/{userAccountCode}")
    ActionStatus remove(@PathParam("userAccountCode") String userAccountCode);

    /**
     * List user accounts filtered by a billing account's code.
     * 
     * @param billingAccountCode The user billing account's code
     * @return list of user accounts.
     */
    @GET
    @Path("/list")
    UserAccountsResponseDto listByBillingAccount(@QueryParam("billingAccountCode") String billingAccountCode);

    /**
     * Create new or update an existing user account.
     * 
     * @param postData The user account's data
     * @return Request processing status
     */
    @POST
    @Path("/createOrUpdate")
    ActionStatus createOrUpdate(UserAccountDto postData);

    /**
     * Filter counters by period date.
     *
     * @param userAccountCode The user account's code
     * @param date The date corresponding to the period
     * @return counter instances.
     */
    @GET
    @Path("/filterCountersByPeriod")
    GetCountersInstancesResponseDto filterUserAccountCountersByPeriod(@QueryParam("userAccountCode") String userAccountCode, @QueryParam("date") @RestDateParam Date date);

    /**
     * Apply a product on a user account.
     *
     * @param postData ApplyProductRequestDto userAccount field must be set
     * @return action status.
     */
    @POST
    @Path("/applyProduct")
    ActionStatus applyProduct(ApplyProductRequestDto postData);

    /**
     * Terminate User account. Status will be changed to Terminated. Action will also terminate related Subscriptions.
     * 
     * @param code User account code
     * @param terminationReasonCode Termination reason code
     * @param terminationDate Termination date
     * @return Request processing status
     */
    @POST
    @Path("/{code}/terminate")
    ActionStatus terminate(@PathParam("code") String code, @QueryParam("terminationReason") String terminationReasonCode,
            @QueryParam("terminationDate") @RestDateParam Date terminationDate);

    /**
     * Cancel User account. Status will be changed to Canceled. Action will also cancel related Subscriptions.
     * 
     * @param code User account code
     * @param cancellationDate Cancellation date
     * @return Request processing status
     */
    @POST
    @Path("/{code}/cancel")
    ActionStatus cancel(@PathParam("code") String code, @QueryParam("cancellationDate") @RestDateParam Date cancellationDate);

    /**
     * Activate previously canceled or terminated User account. Status will be changed to Active.
     * 
     * @param code User account code
     * @param activationDate Activation date
     * @return Request processing status
     */
    @POST
    @Path("/{code}/reactivate")
    ActionStatus reactivate(@PathParam("code") String code, @QueryParam("activationDate") @RestDateParam Date activationDate);

}