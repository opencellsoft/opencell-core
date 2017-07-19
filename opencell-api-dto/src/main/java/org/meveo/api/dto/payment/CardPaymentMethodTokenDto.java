package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "CardPaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardPaymentMethodTokenDto extends BaseResponse {

    private static final long serialVersionUID = 1L;
    private CardPaymentMethodDto cardPaymentMethod;

    public CardPaymentMethodTokenDto() {
    }

    public CardPaymentMethodDto getCardPaymentMethod() {
        return cardPaymentMethod;
    }

    public void setCardPaymentMethod(CardPaymentMethodDto cardPaymentMethod) {
        this.cardPaymentMethod = cardPaymentMethod;
    }

    @Override
    public String toString() {
        return "CardPaymentMethodTokenDto [cardPaymentMethod=" + cardPaymentMethod + "]";
    }
}