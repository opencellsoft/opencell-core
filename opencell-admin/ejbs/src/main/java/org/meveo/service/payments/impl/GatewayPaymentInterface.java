package org.meveo.service.payments.impl;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.MandatInfoDto;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.DDRequestLOT;

/**
 * @author anasseh
 *
 */
public interface GatewayPaymentInterface {

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
     * @param countryCode country code.
     * @return cart token.
     * @throws BusinessException business exception
     */
    String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            CreditCardTypeEnum cardType, String countryCode) throws BusinessException;

    /**
     * Initiate a payment with token.
     * 
     * @param paymentToken payment token
     * @param ctsAmount amount in cent
     * @param additionalParams additional params
     * @return payment by card dto
     * @throws BusinessException business exception.
     */
    PayByCardResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

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
     * @return payment by card response dtO
     * @throws BusinessException business exception.
     */
    PayByCardResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * Initiate a payment sepa whit valid mandat.
     * 
     * @param paymentToken payment token(mandat)
     * @param ctsAmount amount in cent
     * @param additionalParams additional params
     * @return payment by card dto
     * @throws BusinessException business exception.
     */
    PayByCardResponseDto doPaymentSepa(DDPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * This makes it impossible to process the payment any further and will also try to reverse an authorization on a card.
     * 
     * @param paymentID payment id
     * @throws BusinessException business exception
     */
    void cancelPayment(String paymentID) throws BusinessException;

    /**
     * @param ddRequestLot debit direct request by lot
     * @throws BusinessException business exception
     */
    void doBulkPaymentAsFile(DDRequestLOT ddRequestLot) throws BusinessException;

    // TODO PaymentRun
    /**
     * @param ddRequestLot debit direct request lot
     * @throws BusinessException business exception
     */
    void doBulkPaymentAsService(DDRequestLOT ddRequestLot) throws BusinessException;

    /**
     * 
     * @param paymentToken payment token
     * @param ctsAmount amount in cent
     * @param additionalParams additional params.
     * @return payment by card response dto
     * @throws BusinessException business exception.
     */
    PayByCardResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

    /**
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
     * @return payment by card
     * @throws BusinessException business exception.
     */
    PayByCardResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * Check mandat.
     * 
     * @param mandatReference Mandat reference to check
     * @return MandatInfoDto
     * @throws BusinessException
     */
    public MandatInfoDto checkMandat(String mandatReference) throws BusinessException;
}
