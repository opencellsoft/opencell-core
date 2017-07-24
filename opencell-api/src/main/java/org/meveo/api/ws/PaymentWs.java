package org.meveo.api.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.CardPaymentMethodDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokenDto;
import org.meveo.api.dto.payment.CardPaymentMethodTokensDto;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.payment.PayByCardDto;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.api.dto.payment.PaymentDto;
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

    /**
     * Make a payment by card. Either with a provided card information, or an existing and preferred card payment method
     * 
     * @param payByCardDto Payment by card information
     * @return Payment by card information
     */
    @WebMethod
    public PayByCardResponseDto payByCard(@WebParam(name = "payByCard") PayByCardDto payByCardDto);
}