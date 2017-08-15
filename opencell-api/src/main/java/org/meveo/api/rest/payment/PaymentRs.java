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
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.CheckPaymentMethodDto;
import org.meveo.api.dto.payment.CheckPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CheckPaymentMethodTokensDto;
import org.meveo.api.dto.payment.DDPaymentMethodDto;
import org.meveo.api.dto.payment.DDPaymentMethodTokenDto;
import org.meveo.api.dto.payment.DDPaymentMethodTokensDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.TipPaymentMethodDto;
import org.meveo.api.dto.payment.TipPaymentMethodTokenDto;
import org.meveo.api.dto.payment.TipPaymentMethodTokensDto;
import org.meveo.api.dto.payment.WirePaymentMethodDto;
import org.meveo.api.dto.payment.WirePaymentMethodTokenDto;
import org.meveo.api.dto.payment.WirePaymentMethodTokensDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.rest.IBaseRs;

@Path("/payment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })

public interface PaymentRs extends IBaseRs {

    /**
     * Creates automated payment. It also process if a payment is matching or not
     * 
     * @param postData Payment's data
     * @return
     */
    @POST
    @Path("/create")
    public ActionStatus create(PaymentDto postData);

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
    /****                                 Card Payment Method                                    ****/
    /************************************************************************************************/
    
    /**
     * Add a new card payment method. It will be marked as preferred.
     * 
     * @param cardPaymentMethod Card payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/cardPaymentMethod")
    public CardPaymentMethodTokenDto addCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Update existing card payment method.
     * 
     * @param cardPaymentMethod Card payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/cardPaymentMethod")
    public ActionStatus updateCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod);

    /**
     * Remove card payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/cardPaymentMethod")
    public ActionStatus removeCardPaymentMethod(@QueryParam("id") Long id);

    /**
     * List available card payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of card payment methods
     */
    @GET
    @Path("/cardPaymentMethod/list")
    public CardPaymentMethodTokensDto listCardPaymentMethods(@QueryParam("customerAccountId") Long customerAccountId, @QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Retrieve card payment method by its id
     * 
     * @param id Id
     * @return Card payment DTO
     */
    @GET
    @Path("/cardPaymentMethod")
    public CardPaymentMethodTokenDto findCardPaymentMethod(@QueryParam("id") Long id);
    
    /************************************************************************************************/
    /****                                 DirectDebit Payment Method                             ****/
    /************************************************************************************************/
    /**
     * Add a new directDebit payment method. It will be marked as preferred.
     * 
     * @param ddPaymentMethod DerictDebit payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/ddPaymentMethod")
    public DDPaymentMethodTokenDto addDDPaymentMethod(DDPaymentMethodDto ddPaymentMethod);

    /**
     * Update existing directDebit payment method.
     * 
     * @param ddPaymentMethod DerictDebit payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/ddPaymentMethod")
    public ActionStatus updateDDPaymentMethod(DDPaymentMethodDto ddPaymentMethod);

    /**
     * Remove directDebit payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/ddPaymentMethod")
    public ActionStatus removeDDPaymentMethod(@QueryParam("id") Long id);

    /**
     * List available directDebit payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of directDebit payment methods
     */
    @GET
    @Path("/ddPaymentMethod/list")
    public DDPaymentMethodTokensDto listDDPaymentMethods(@QueryParam("customerAccountId") Long customerAccountId, @QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Retrieve directDebit payment method by its id
     * 
     * @param id Id
     * @return DerictDebit payment DTO
     */
    @GET
    @Path("/ddPaymentMethod")
    public DDPaymentMethodTokenDto findDDPaymentMethod(@QueryParam("id") Long id);
    /************************************************************************************************/
    /****                                 TIP Payment Method                                     ****/
    /************************************************************************************************/
    /**
     * Add a new Tip payment method. It will be marked as preferred.
     * 
     * @param tipPaymentMethod DerictDebit payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/tipPaymentMethod")
    public TipPaymentMethodTokenDto addTipPaymentMethod(TipPaymentMethodDto tipPaymentMethod);

    /**
     * Update existing tip payment method.
     * 
     * @param tipPaymentMethod DerictDebit payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/tipPaymentMethod")
    public ActionStatus updateTipPaymentMethod(TipPaymentMethodDto tipPaymentMethod);

    /**
     * Remove tip payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/tipPaymentMethod")
    public ActionStatus removeTipPaymentMethod(@QueryParam("id") Long id);

    /**
     * List available tip payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of tip payment methods
     */
    @GET
    @Path("/tipPaymentMethod/list")
    public TipPaymentMethodTokensDto listTipPaymentMethods(@QueryParam("customerAccountId") Long customerAccountId, @QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Retrieve tip payment method by its id
     * 
     * @param id Id
     * @return DerictDebit payment DTO
     */
    @GET
    @Path("/tipPaymentMethod")
    public TipPaymentMethodTokenDto findTipPaymentMethod(@QueryParam("id") Long id);
    /************************************************************************************************/
    /****                                 Check Payment Method                                   ****/
    /************************************************************************************************/
    /**
     * Add a new Check payment method. It will be marked as preferred.
     * 
     * @param checkPaymentMethod DerictDebit payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/checkPaymentMethod")
    public CheckPaymentMethodTokenDto addCheckPaymentMethod(CheckPaymentMethodDto checkPaymentMethod);

    /**
     * Update existing check payment method.
     * 
     * @param checkPaymentMethod DerictDebit payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/checkPaymentMethod")
    public ActionStatus updateCheckPaymentMethod(CheckPaymentMethodDto checkPaymentMethod);

    /**
     * Remove check payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/checkPaymentMethod")
    public ActionStatus removeCheckPaymentMethod(@QueryParam("id") Long id);

    /**
     * List available check payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of check payment methods
     */
    @GET
    @Path("/checkPaymentMethod/list")
    public CheckPaymentMethodTokensDto listCheckPaymentMethods(@QueryParam("customerAccountId") Long customerAccountId, @QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Retrieve check payment method by its id
     * 
     * @param id Id
     * @return DerictDebit payment DTO
     */
    @GET
    @Path("/checkPaymentMethod")
    public CheckPaymentMethodTokenDto findCheckPaymentMethod(@QueryParam("id") Long id);
    /************************************************************************************************/
    /****                                 Wire Payment Method                                    ****/
    /************************************************************************************************/
    /**
     * Add a new Wire payment method. It will be marked as preferred.
     * 
     * @param wirePaymentMethod DerictDebit payment method DTO
     * @return Token id in payment gateway
     */
    @POST
    @Path("/wirePaymentMethod")
    public WirePaymentMethodTokenDto addWirePaymentMethod(WirePaymentMethodDto wirePaymentMethod);

    /**
     * Update existing wire payment method.
     * 
     * @param wirePaymentMethod DerictDebit payment method DTO
     * @return Action status
     */
    @PUT
    @Path("/wirePaymentMethod")
    public ActionStatus updateWirePaymentMethod(WirePaymentMethodDto wirePaymentMethod);

    /**
     * Remove wire payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @DELETE
    @Path("/wirePaymentMethod")
    public ActionStatus removeWirePaymentMethod(@QueryParam("id") Long id);

    /**
     * List available wire payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of wire payment methods
     */
    @GET
    @Path("/wirePaymentMethod/list")
    public WirePaymentMethodTokensDto listWirePaymentMethods(@QueryParam("customerAccountId") Long customerAccountId, @QueryParam("customerAccountCode") String customerAccountCode);

    /**
     * Retrieve wire payment method by its id
     * 
     * @param id Id
     * @return DerictDebit payment DTO
     */
    @GET
    @Path("/wirePaymentMethod")
    public WirePaymentMethodTokenDto findWirePaymentMethod(@QueryParam("id") Long id);
}