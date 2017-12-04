package org.meveo.service.payments.impl;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;
import org.meveo.model.payments.CustomerAccount;

public interface GatewayPaymentInterface {

    /**
     * 
     * @param customerAccount
     * @param alias An alias for the token. This can be used to visually represent the token. If no alias is given in Create token calls, a payment product specific default is
     *        used, e.g. the obfuscated card number for card payment products.Do not include any unobfuscated sensitive data in the alias.
     * @param cardNumber The complete credit/debit card number (also know as the PAN),Required for Create and Update token.
     * @param cardHolderName Card holder's name on the card
     * @param expirayDate Expiry date of the card Format: MMYY ,Required for Create and Update token.
     * @param issueNumber Issue number on the card (if applicable)
     * @param productPaymentId Payment product identifier (1 Visa | 2 American Express | 3 MasterCard)
     * @return
     * @throws BusinessException
     */
    public String createCardToken(CustomerAccount customerAccount, String alias, String cardNumber, String cardHolderName, String expirayDate, String issueNumber,
            int productPaymentId, String countryCode) throws BusinessException;

    /**
     * 
     * @param paymentToken
     * @param ctsAmount
     * @param additionalParams
     * @return
     * @throws BusinessException
     */
    public PayByCardResponseDto doPaymentToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * 
     * @param customerAccount
     * @param ctsAmount
     * @param cardNumber
     * @param ownerName
     * @param cvv
     * @param expirayDate format MMyy
     * @param cardType
     * @return
     * @throws BusinessException
     */
    public PayByCardResponseDto doPaymentCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * This makes it impossible to process the payment any further and will also try to reverse an authorization on a card.
     * 
     * @param paymentID
     * @throws BusinessException
     */
    public void cancelPayment(String paymentID) throws BusinessException;


    /**
     * 
     * @param paymentToken
     * @param ctsAmount
     * @param additionalParams
     * @return
     * @throws BusinessException
     */
    public PayByCardResponseDto doRefundToken(CardPaymentMethod paymentToken, Long ctsAmount, Map<String, Object> additionalParams) throws BusinessException;

    /**
     * 
     * @param customerAccount
     * @param ctsAmount
     * @param cardNumber
     * @param ownerName
     * @param cvv
     * @param expirayDate format MMyy
     * @param cardType
     * @return
     * @throws BusinessException
     */
    public PayByCardResponseDto doRefundCard(CustomerAccount customerAccount, Long ctsAmount, String cardNumber, String ownerName, String cvv, String expirayDate,
            CreditCardTypeEnum cardType, String countryCode, Map<String, Object> additionalParams) throws BusinessException;

}
