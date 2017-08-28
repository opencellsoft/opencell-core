package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CreditCardTypeEnum;

/**
 * Card payment method
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "CardPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated // Use PaymentMthodDto
public class CardPaymentMethodDto extends PaymentMethodDto {

    private static final long serialVersionUID = 1937059617391182742L;

   
    
    public CardPaymentMethodDto() {

    }

    public CardPaymentMethodDto(CreditCardTypeEnum cardType, String cardNumber, Integer monthExpiration, Integer yearExpiration, String ownerName) {
        setAlias("Card_" + cardNumber.substring(12, 16));
        setCardNumber(cardNumber);
        setCardType(cardType);
        setMonthExpiration(monthExpiration);
        setYearExpiration(yearExpiration);
        setOwner(ownerName);
    }

    public CardPaymentMethodDto(CardPaymentMethod paymentMethod) {
        super(paymentMethod);
        setCardNumber(paymentMethod.getHiddenCardNumber());
        setCardType(paymentMethod.getCardType());
        setMonthExpiration(paymentMethod.getMonthExpiration());
        setOwner(paymentMethod.getOwner());
        setTokenId(paymentMethod.getTokenId());
        setYearExpiration(paymentMethod.getYearExpiration());
        setUserId(paymentMethod.getUserId());
    }


    public CardPaymentMethodDto(PaymentMethodDto paymentMethod) {
		setAlias(paymentMethod.getAlias());
		setCardNumber(paymentMethod.getCardNumber());
		setCardType(paymentMethod.getCardType());
		setCustomerAccountCode(paymentMethod.getCustomerAccountCode());
		setId(paymentMethod.getId());
		setIssueNumber(paymentMethod.getIssueNumber());
		setMonthExpiration(paymentMethod.getMonthExpiration());
		setYearExpiration(paymentMethod.getYearExpiration());
		
	}

	public CardPaymentMethod fromDto() {
        CardPaymentMethod paymentMethod = new CardPaymentMethod(getAlias(), isPreferred());

        if (getTokenId() == null) {
            paymentMethod.setCardNumber(getCardNumber());
            paymentMethod.setIssueNumber(getIssueNumber());
        }
        paymentMethod.setHiddenCardNumber(StringUtils.hideCardNumber(getCardNumber()));
        paymentMethod.setOwner(getOwner());
        paymentMethod.setCardType(getCardType());
        paymentMethod.setPreferred(isPreferred());
        paymentMethod.setYearExpiration(getYearExpiration());
        paymentMethod.setMonthExpiration(getMonthExpiration());
        paymentMethod.setUserId(getUserId());

        return paymentMethod;
    }   
}