package org.meveo.service.payments.impl;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.HostedCheckoutInput;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author anasseh
 * @lastModifiedVersion 5.0
 *
 */
public interface GatewayPaymentInterface {
    
    /**
     * Set the payment gateway to use.
     * 
     * @param paymentGateway
     */
    public void setPaymentGateway(PaymentGateway paymentGateway);

    /**
     * Declare a card on the psp and return the token for the future uses.
     * 
     * @param customerAccount customer account.
     * @param alias An alias for the token. This can be used to visually represent the token. If no alias is given in Create token calls, a payment product specific default is
     *        used, e.g. the obfuscated card number for card payment products.Do not include any unobfuscated sensitive data in the alias.
     * @param cardNumber The complete credit/debit card number (also know as the PAN),Required for Create and Update token.
     * @param cardHolderName Card holder's name on the card
     * @param expirayDate Expiry date of the card Format: MMYY ,Required for Create and Update token.
     * @param issueNumber Issue number on the card (if applicable)
     * @param cardType ( Visa | American Express | MasterCard)
     * @return cart token.
     * @throws BusinessException business exception
     */
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType) throws BusinessException;

    /**
     * Initiate a payment with token.
     * 
     * @param paymentToken payment token
     * @param ctsAmount amount in cent
     * @param additionalParams additional params
     * @return payment response dto
     * @throws BusinessException business exception.
     */
    public PaymentResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * Initiate a payment with card and save the token for this card.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent
     * @param cardNumber card number
     * @param ownerName owner name
     * @param cvv cvv
     * @param expirayDate format MMyy
     * @param cardType card type
     * @param countryCode country code
     * @param additionalParams additional params
     * @return payment response dto
     * @throws BusinessException business exception.
     */
    public PaymentResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * Initiate a payment sepa whit valid mandat.
     * 
     * @param paymentToken payment token(mandat)
     * @param ctsAmount amount in cent
     * @param additionalParams additional params
     * @return payment response dto
     * @throws BusinessException business exception.
     */
    public PaymentResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;
    
    /**
     * Initiate a payment out sepa whit valid mandat.
     * 
     * @param paymentToken payment token(mandat)
     * @param ctsAmount amount in cent
     * @param additionalParams additional params
     * @return payment response dto
     * @throws BusinessException business exception.
     */
    public PaymentResponseDto doRefundSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * Check a payment.
     * 
     * @param paymentID payment id
     * @param paymentMethodType payment method (CARD or DIRECTDEBIT)
     * @return payment response dto
     * @throws BusinessException business exception
     */
    public PaymentResponseDto checkPayment(String paymentID,PaymentMethodEnum paymentMethodType) throws BusinessException;
    
    /**
     * Cancel a pending payment.
     * 
     * @param paymentID payment id
     * @throws BusinessException business exception
     */
    public void cancelPayment(String paymentID) throws BusinessException;
    

    // TODO PaymentRun
    /**
     * @param ddRequestLot debit direct request lot
     * @throws BusinessException business exception
     */
    public void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException;

    /**
     * Initiate a refund with token. 
     * 
     * @param paymentToken payment token
     * @param ctsAmount amount in cent
     * @param additionalParams additional params.
     * @return payment by card response dto
     * @throws BusinessException business exception.
     */
    public PaymentResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * Initiate a refund with card.
     * 
     * @param customerAccount customer account
     * @param ctsAmount amount in cent
     * @param cardNumber car number
     * @param ownerName owner name
     * @param cvv cvv
     * @param expirayDate format MMyy
     * @param cardType card type
     * @param countryCode country code.
     * @param additionalParams additional params.
     * @return payment response dto
     * @throws BusinessException business exception.
     */
    public PaymentResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * Check mandate by RUM or ID.
     * 
     * @param mandatReference Mandate reference (RUM) to check
     * @param mandateId Mandate id to check
     * @return MandatInfoDto
     * @throws BusinessException Business Exception 
     */
    public MandatInfoDto checkMandat(String mandatReference,String mandateId) throws BusinessException;

    /**
     * return the url of Hosted Checkout
     *
     * @param hostedCheckoutInput
     * @return url of Hosted Checkout
     * @throws BusinessException
     *
     * @author Mounir Bahije
     */
    public String getHostedCheckoutUrl(HostedCheckoutInput hostedCheckoutInput)  throws BusinessException;
}