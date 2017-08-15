package org.meveo.api.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

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
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.dto.payment.TipPaymentMethodDto;
import org.meveo.api.dto.payment.TipPaymentMethodTokenDto;
import org.meveo.api.dto.payment.TipPaymentMethodTokensDto;
import org.meveo.api.dto.payment.WirePaymentMethodDto;
import org.meveo.api.dto.payment.WirePaymentMethodTokenDto;
import org.meveo.api.dto.payment.WirePaymentMethodTokensDto;
import org.meveo.api.dto.response.CustomerPaymentsResponse;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.model.payments.DDRequestOpStatusEnum;

@WebService
public interface PaymentWs extends IBaseWs {

    @WebMethod
    public ActionStatus create(@WebParam(name = "PaymentDto") PaymentDto postData);

    @WebMethod
    public CustomerPaymentsResponse list(@WebParam(name = "customerAccountCode") String customerAccountCode);

    /**
     * create a ddrequestLotOp by dto
     * 
     * @param ddrequestLotOp
     * @return
     */
    @WebMethod
    ActionStatus createDDRequestLotOp(@WebParam(name = "ddrequestLotOp") DDRequestLotOpDto ddrequestLotOp);

    /**
     * list ddrequestLotOps by fromDueDate,toDueDate,status
     * 
     * @param fromDueDate
     * @param toDueDate
     * @param status
     * @return
     */
    @WebMethod
    DDRequestLotOpsResponseDto listDDRequestLotops(@WebParam(name = "fromDueDate") Date fromDueDate, @WebParam(name = "toDueDate") Date toDueDate,
            @WebParam(name = "status") DDRequestOpStatusEnum status);
    
    
    /**
     * Make a payment by card. Either with a provided card information, or an existing and preferred card payment method
     * 
     * @param payByCardDto Payment by card information
     * @return Payment by card information
     */
    @WebMethod
    public PayByCardResponseDto payByCard(@WebParam(name = "payByCard") PayByCardDto payByCardDto);

    /************************************************************************************************/
    /****                                 Card Payment Method                                    ****/
    /************************************************************************************************/
    
    /**
     * Add a new card payment method. It will be marked as preferred.
     * 
     * @param cardPaymentMethod Card payment method DTO
     * @return Card payment DTO with Token id from payment gateway
     */
    @WebMethod
    public CardPaymentMethodTokenDto addCardPaymentMethod(@WebParam(name = "cardPaymentMethod") CardPaymentMethodDto cardPaymentMethod);

    /**
     * Update existing card payment method.
     * 
     * @param cardPaymentMethod Card payment method DTO
     * @return Action status
     */
    public ActionStatus updateCardPaymentMethod(@WebParam(name = "cardPaymentMethod") CardPaymentMethodDto cardPaymentMethod);

    /**
     * Remove card payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @WebMethod
    public ActionStatus removeCardPaymentMethod(@WebParam(name = "id") Long id);

    /**
     * List available card payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of card payment methods
     */
    @WebMethod
    public CardPaymentMethodTokensDto listCardPaymentMethods(@WebParam(name = "customerAccountId") Long customerAccountId,
            @WebParam(name = "customerAccountCode") String customerAccountCode);

    /**
     * Retrieve card payment method by its id
     * 
     * @param id Id
     * @return Card payment DTO
     */
    @WebMethod
    public CardPaymentMethodTokenDto findCardPaymentMethod(@WebParam(name = "id") Long id);


    
    /************************************************************************************************/
    /****                                 DirectDebit Payment Method                             ****/
    /************************************************************************************************/
    
    /**
     * Add a new directDebit payment method. It will be marked as preferred.
     * 
     * @param ddPaymentMethod DD payment method DTO
     * @return DD payment DTO with Token id from payment gateway
     */
    @WebMethod
    public DDPaymentMethodTokenDto addDDPaymentMethod(@WebParam(name = "ddPaymentMethod") DDPaymentMethodDto ddPaymentMethod);

    /**
     * Update existing dd payment method.
     * 
     * @param ddPaymentMethod DD payment method DTO
     * @return Action status
     */
    public ActionStatus updateDDPaymentMethod(@WebParam(name = "ddPaymentMethod") DDPaymentMethodDto ddPaymentMethod);

    /**
     * Remove directDebit payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @WebMethod
    public ActionStatus removeDDPaymentMethod(@WebParam(name = "id") Long id);

    /**
     * List available directDebit payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of dd payment methods
     */
    @WebMethod
    public DDPaymentMethodTokensDto listDDPaymentMethods(@WebParam(name = "customerAccountId") Long customerAccountId,
            @WebParam(name = "customerAccountCode") String customerAccountCode);

    /**
     * Retrieve directDebit payment method by its id
     * 
     * @param id Id
     * @return DD payment DTO
     */
    @WebMethod
    public DDPaymentMethodTokenDto findDDPaymentMethod(@WebParam(name = "id") Long id);
    
    /************************************************************************************************/
    /****                                 Tip Payment Method                                     ****/
    /************************************************************************************************/
    /**
     * Add a new tip payment method. It will be marked as preferred.
     * 
     * @param tipPaymentMethod Tip payment method DTO
     * @return Tip payment DTO with Token id from payment gateway
     */
    @WebMethod
    public TipPaymentMethodTokenDto addTipPaymentMethod(@WebParam(name = "tipPaymentMethod") TipPaymentMethodDto tipPaymentMethod);

    /**
     * Update existing tip payment method.
     * 
     * @param tipPaymentMethod Tip payment method DTO
     * @return Action status
     */
    public ActionStatus updateTipPaymentMethod(@WebParam(name = "tipPaymentMethod") TipPaymentMethodDto tipPaymentMethod);

    /**
     * Remove tip payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @WebMethod
    public ActionStatus removeTipPaymentMethod(@WebParam(name = "id") Long id);

    /**
     * List available tip payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of tip payment methods
     */
    @WebMethod
    public TipPaymentMethodTokensDto listTipPaymentMethods(@WebParam(name = "customerAccountId") Long customerAccountId,
            @WebParam(name = "customerAccountCode") String customerAccountCode);

    /**
     * Retrieve tip payment method by its id
     * 
     * @param id Id
     * @return Tip payment DTO
     */
    @WebMethod
    public TipPaymentMethodTokenDto findTipPaymentMethod(@WebParam(name = "id") Long id);
    
    /************************************************************************************************/
    /****                                 Check Payment Method                                   ****/
    /************************************************************************************************/
    
    /**
     * Add a new check payment method. It will be marked as preferred.
     * 
     * @param checkPaymentMethod Check payment method DTO
     * @return Check payment DTO with Token id from payment gateway
     */
    @WebMethod
    public CheckPaymentMethodTokenDto addCheckPaymentMethod(@WebParam(name = "checkPaymentMethod") CheckPaymentMethodDto checkPaymentMethod);

    /**
     * Update existing check payment method.
     * 
     * @param checkPaymentMethod Check payment method DTO
     * @return Action status
     */
    public ActionStatus updateCheckPaymentMethod(@WebParam(name = "checkPaymentMethod") CheckPaymentMethodDto checkPaymentMethod);

    /**
     * Remove check payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @WebMethod
    public ActionStatus removeCheckPaymentMethod(@WebParam(name = "id") Long id);

    /**
     * List available check payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of check payment methods
     */
    @WebMethod
    public CheckPaymentMethodTokensDto listCheckPaymentMethods(@WebParam(name = "customerAccountId") Long customerAccountId,
            @WebParam(name = "customerAccountCode") String customerAccountCode);

    /**
     * Retrieve check payment method by its id
     * 
     * @param id Id
     * @return Check payment DTO
     */
    @WebMethod
    public CheckPaymentMethodTokenDto findCheckPaymentMethod(@WebParam(name = "id") Long id);
    
    /************************************************************************************************/
    /****                                 Wire Payment Method                                    ****/
    /************************************************************************************************/
    /**
     * Add a new wire payment method. It will be marked as preferred.
     * 
     * @param wirePaymentMethod Wire payment method DTO
     * @return Wire payment DTO with Token id from payment gateway
     */
    @WebMethod
    public WirePaymentMethodTokenDto addWirePaymentMethod(@WebParam(name = "wirePaymentMethod") WirePaymentMethodDto wirePaymentMethod);

    /**
     * Update existing wire payment method.
     * 
     * @param wirePaymentMethod Wire payment method DTO
     * @return Action status
     */
    public ActionStatus updateWirePaymentMethod(@WebParam(name = "wirePaymentMethod") WirePaymentMethodDto wirePaymentMethod);

    /**
     * Remove wire payment method. If it was marked as preferred, some other payment method will be marked as preferred
     * 
     * @param id Id
     * @return Action status
     */
    @WebMethod
    public ActionStatus removeWirePaymentMethod(@WebParam(name = "id") Long id);

    /**
     * List available wire payment methods for a given customer account identified either by id or by code
     * 
     * @param customerAccountId Customer account id
     * @param customerAccountCode Customer account code
     * @return A list of wire payment methods
     */
    @WebMethod
    public WirePaymentMethodTokensDto listWirePaymentMethods(@WebParam(name = "customerAccountId") Long customerAccountId,
            @WebParam(name = "customerAccountCode") String customerAccountCode);

    /**
     * Retrieve wire payment method by its id
     * 
     * @param id Id
     * @return Wire payment DTO
     */
    @WebMethod
    public WirePaymentMethodTokenDto findWirePaymentMethod(@WebParam(name = "id") Long id);
}