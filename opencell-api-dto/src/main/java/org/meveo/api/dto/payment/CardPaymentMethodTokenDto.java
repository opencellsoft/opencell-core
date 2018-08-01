package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class CardPaymentMethodTokenDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CardPaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated // Use PaymentMethodTokenDto
public class CardPaymentMethodTokenDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The card payment method. */
    private CardPaymentMethodDto cardPaymentMethod;

    /**
     * Instantiates a new card payment method token dto.
     */
    public CardPaymentMethodTokenDto() {
    }

    /**
     * Instantiates a new card payment method token dto.
     *
     * @param response the response
     */
    public CardPaymentMethodTokenDto(PaymentMethodTokenDto response) {
        this.setActionStatus(response.getActionStatus());
        this.setCardPaymentMethod(new CardPaymentMethodDto(response.getPaymentMethod()));
    }

    /**
     * Gets the card payment method.
     *
     * @return the card payment method
     */
    public CardPaymentMethodDto getCardPaymentMethod() {
        return cardPaymentMethod;
    }

    /**
     * Sets the card payment method.
     *
     * @param cardPaymentMethod the new card payment method
     */
    public void setCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod) {
        this.cardPaymentMethod = cardPaymentMethod;
    }


    @Override
    public String toString() {
        return "CardPaymentMethodTokenDto [cardPaymentMethod=" + cardPaymentMethod + "]";
    }
}