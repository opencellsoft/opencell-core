package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
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
import org.meveo.api.dto.payment.AccountOperationDto;
import org.meveo.api.dto.payment.LitigationRequestDto;
import org.meveo.api.dto.payment.MatchOperationRequestDto;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.dto.response.Paging.SortOrder;
import org.meveo.api.dto.response.payment.AccountOperationResponseDto;
import org.meveo.api.dto.response.payment.AccountOperationsResponseDto;
import org.meveo.api.dto.response.payment.MatchedOperationsResponseDto;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author Edward P. Legaspi
 **/
@Path("/accountOperation")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface AccountOperationRs extends IBaseRs {

    /**
     * Create a new account operation
     * 
     * @param postData The account operation's data
     * @return Request processing status
     */
    @POST
    @Path("/")
    ActionStatus create(AccountOperationDto postData);

    /**
     * List of account operations
     * 
     * @param customerAccountCode The customer account's code
     * @return A list of account operations
     */
	@GET
	@Path("/list")
	AccountOperationsResponseDto list(@QueryParam("customerAccountCode") String customerAccountCode, @DefaultValue("0") @QueryParam("offset") Integer offset,
			@DefaultValue("10") @QueryParam("limit") Integer limit, @DefaultValue("id") @QueryParam("sortBy") String sortBy,
			@DefaultValue("DESCENDING") @QueryParam("sortOrder") SortOrder sortOrder);

    /**
     * Match operations
     * 
     * @param postData The matching operation's data
     * @return Request processing status
     */
    @POST
    @Path("/matchOperations")
    ActionStatus matchOperations(MatchOperationRequestDto postData);

    /**
     * Unmatching operations
     * 
     * @param postData The unmatching operations data
     * @return Request processing status
     */
    @POST
    @Path("/unMatchingOperations")
    ActionStatus unMatchingOperations(UnMatchingOperationRequestDto postData);

    /**
     * Add a new litigation
     * 
     * @param postData The litigation's data
     * @return Request processing status
     */
    @POST
    @Path("/addLitigation")
    ActionStatus addLitigation(LitigationRequestDto postData);

    /**
     * Cancel a litigation
     * 
     * @param postData The litigation's data
     * @return Request processing status
     */
    @POST
    @Path("/cancelLitigation")
    ActionStatus cancelLitigation(LitigationRequestDto postData);

    /**
     * Finds an accountOperation given an id.
     * 
     * @param id id of the account operation
     * @return
     */
    @GET
    @Path("/")
    AccountOperationResponseDto find(@QueryParam("id") Long id);

    /**
     * Update payment method for all customerAccount AO's if customerAccountCode is set.Or single AO if aoId is set.
     * 
     * @param customerAccountCode
     * @param aoId
     * @param paymentMethod
     * @return Request processing status
     */
    @PUT
    @Path("/updatePaymentMethod")
    ActionStatus updatePaymentMethod(@QueryParam("customerAccountCode") String customerAccountCode, @QueryParam("aoId") Long aoId,
            @QueryParam("paymentMethod") PaymentMethodEnum paymentMethod);

    /**
     * List matched operations for a given account operation
     * 
     * @param accountOperationId Account operation identifier
     * @return A list of matched operations
     */
    @GET
    @Path("/{accountOperationId}/listMatchedOperations")
    public MatchedOperationsResponseDto listMatchedOperations(@PathParam("accountOperationId") Long accountOperationId);

}