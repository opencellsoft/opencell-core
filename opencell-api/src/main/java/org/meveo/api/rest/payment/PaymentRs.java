package org.meveo.api.rest.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.PaymentActionStatus;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.PaymentMethodDto;
import org.meveo.api.dto.payment.PaymentMethodTokenDto;
import org.meveo.api.dto.payment.PaymentMethodTokensDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.rest.IBaseRs;
import org.meveo.model.payments.PaymentMethodEnum;

@Path("/payment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PaymentRs extends IBaseRs {

    /**
     * Creates automated payment. It also process if a payment is matching or not
     * 
     * @param postData
     *            Payment's data
     * @return payment action status
     */
    @POST
    @Path("/create")
    public PaymentActionStatus create(PaymentDto postData);

    /**
     * Returns a list of account operations along with the balance of a customer
     * 
     * @param customerAccountCode
     * @return
     */
    @GET
    @Path("/customerPayment")
    public CustomerPaymentsResponse list(@QueryParam("customerAccountCode") String customerAccountCode);

    /************************************************************************************************/
    /**** Card Payment Method ****/
    /************************************************************************************************/

    /**
     * Add a new card payment method. It will be marked as preferred.
     * 
     * @param cardPaymentMethod
     *            Card payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/cardPaymentMethod")
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Update existing card payment method.
     * 
     * @param cardPaymentMethod
     *            Card payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/cardPaymentMethod")
    public ActionStatus updateCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Remove card payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id
     *            Id
     * @return Action status
     */
    @DELETE
    @Path("/cardPaymentMethod")
    public ActionStatus removeCardPaymentMethod(@QueryParam("id") Long id);

    /**
     * List available card payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId
     *            Customer account id
     * @param customerAccountCode
     *            Customer account code
     * @return A list of card payment methods
     */
    @GET
    @Path("/cardPaymentMethod/list")
    public CardPaymentMethodTokensDto listCardPaymentMethods(@QueryParam("customerAccountId") Long customerAccountId,
	    @QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Retrieve card payment method by its id
     * 
     * @param id
     *            Id
     * @return Card payment DTO
     */
    @GET
    @Path("/cardPaymentMethod")
    public CardPaymentMethodTokenDto findCardPaymentMethod(@QueryParam("id") Long id);

    /************************************************************************************************/
    /**** Payment Methods ****/
    /************************************************************************************************/
    /**
     * Add a new payment method. It will be marked as preferred.
     * 
     * @param paymentMethod
     *            payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/paymentMethod")
    public PaymentMethodTokenDto addPaymentMethod(PaymentMethodDto paymentMethod);

    /**
     * Update existing payment method.
     * 
     * @param paymentMethod
     *            payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/paymentMethod")
    public ActionStatus updatePaymentMethod(PaymentMethodDto ddPaymentMethod);

    /**
     * Remove payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id
     *            Id
     * @return Action status
     */
    @DELETE
    @Path("/paymentMethod")
    public ActionStatus removePaymentMethod(@QueryParam("id") Long id);
    

    /**
     * List available payment methods for a given customer account identified either by id or by code, or paymentMethod type, isPreferred, infos
     * 
     * @param customerAccountId  Customer account id
     * @param customerAccountCode  Customer account code
     * @param type  PaymentMethod type
     * @param isPrefered is the  preferred payment method
     * @param info1 additional info1
     * @param info2 additional info2
     * @param info3 additional info3
     * @param info4 additional info4
     * @param info5 additional info5
     * @return A list of payment methods
     */
    @GET
    @Path("/paymentMethod/list")
    public PaymentMethodTokensDto listPaymentMethods(@QueryParam("customerAccountId") Long customerAccountId, @QueryParam("customerAccountCode") String customerAccountCode,
	    @QueryParam("type") PaymentMethodEnum type, @QueryParam("isPreferred") Boolean isPreferred, @QueryParam("info1") String info1, @QueryParam("info2") String info2,
	    @QueryParam("info3") String info3, @QueryParam("ifo4") String info4, @QueryParam("info5") String info5);

    /**
     * Retrieve payment method by its id
     * 
     * @param id
     *            Id
     * @return payment DTO
     */
    @GET
    @Path("/paymentMethod")
    public PaymentMethodTokenDto findPaymentMethod(@QueryParam("id") Long id);
}