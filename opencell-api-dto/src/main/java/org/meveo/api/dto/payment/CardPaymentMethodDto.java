package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;

/**
 * Card payment method.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "CardPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated // Use PaymentMthodDto
public class CardPaymentMethodDto extends PaymentMethodDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1937059617391182742L;

    /**
     * Instantiates a new card payment method dto.
     */
    public CardPaymentMethodDto() {

    }

    /**
     * Instantiates a new card payment method dto.
     *
     * @param cardType the card type
     * @param cardNumber the card number
     * @param monthExpiration the month expiration
     * @param yearExpiration the year expiration
     * @param ownerName the owner name
     */
    public CardPaymentMethodDto(CreditCardTypeEnum cardType, String cardNumber, Integer monthExpiration, Integer yearExpiration, String ownerName) {
        setAlias("Card_" + cardNumber.substring(12, 16));
        setCardNumber(cardNumber);
        setCardType(cardType);
        setMonthExpiration(monthExpiration);
        setYearExpiration(yearExpiration);
        setOwner(ownerName);
    }

    /**
     * Instantiates a new card payment method dto.
     *
     * @param paymentMethod the payment method
     */
    public CardPaymentMethodDto(CardPaymentMethod paymentMethod) {
        super(paymentMethod);
        setCardNumber(paymentMethod.getHiddenCardNumber());
        setCardType(paymentMethod.getCardType());
        setMonthExpiration(paymentMethod.getMonthExpiration());
        setOwner(paymentMethod.getOwner());
        setTokenId(paymentMethod.getTokenId());
        setYearExpiration(paymentMethod.getYearExpiration());
        setInfo1(paymentMethod.getInfo1());
        setInfo2(paymentMethod.getInfo2());
        setInfo3(paymentMethod.getInfo3());
        setInfo4(paymentMethod.getInfo4());
        setInfo5(paymentMethod.getInfo5());
        setUserId(paymentMethod.getUserId());
    }

    /**
     * Instantiates a new card payment method dto.
     *
     * @param paymentMethod the paymentMethod entity
     */
    public CardPaymentMethodDto(PaymentMethodDto paymentMethod) {
        if (paymentMethod == null) {
            return;
        }
        setAlias(paymentMethod.getAlias());
        setPreferred(paymentMethod.isPreferred());
        setCardNumber(paymentMethod.getCardNumber());
        setCardType(paymentMethod.getCardType());
        setCustomerAccountCode(paymentMethod.getCustomerAccountCode());
        setId(paymentMethod.getId());
        setIssueNumber(paymentMethod.getIssueNumber());
        setMonthExpiration(paymentMethod.getMonthExpiration());
        setYearExpiration(paymentMethod.getYearExpiration());
        setTokenId(paymentMethod.getTokenId());
        setOwner(paymentMethod.getOwner());
        setInfo1(paymentMethod.getInfo1());
        setInfo2(paymentMethod.getInfo2());
        setInfo3(paymentMethod.getInfo3());
        setInfo4(paymentMethod.getInfo4());
        setInfo5(paymentMethod.getInfo5());
        setUserId(paymentMethod.getUserId());
    }

    /**
     * From dto.
     *
     * @return the card payment method
     */
    public CardPaymentMethod fromDto() {
        CardPaymentMethod paymentMethod = new CardPaymentMethod(getAlias(), isPreferred());

        if (getTokenId() == null) {
            paymentMethod.setCardNumber(getCardNumber());
            paymentMethod.setIssueNumber(getIssueNumber());
        }
        paymentMethod.setHiddenCardNumber(CardPaymentMethod.hideCardNumber(getCardNumber()));
        paymentMethod.setOwner(getOwner());
        paymentMethod.setCardType(getCardType());
        paymentMethod.setPreferred(isPreferred());
        paymentMethod.setYearExpiration(getYearExpiration());
        paymentMethod.setMonthExpiration(getMonthExpiration());
        paymentMethod.setUserId(getUserId());
        paymentMethod.setInfo1(getInfo1());
        paymentMethod.setInfo2(getInfo2());
        paymentMethod.setInfo3(getInfo3());
        paymentMethod.setInfo4(getInfo4());
        paymentMethod.setInfo5(getInfo5());

        return paymentMethod;
    }
}