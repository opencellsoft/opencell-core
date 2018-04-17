package org.meveo.api.dto.payment;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class CardPaymentMethodTokensDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CardPaymentMethods")
@XmlAccessorType(XmlAccessType.FIELD)
public class CardPaymentMethodTokensDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6255115951743225824L;

    /** The card payment methods. */
    @XmlElementWrapper(name = "cardPaymentMethods")
    @XmlElement(name = "cardPaymentMethod")
    private List<CardPaymentMethodDto> cardPaymentMethods = new ArrayList<CardPaymentMethodDto>();

    /**
     * Instantiates a new card payment method tokens dto.
     */
    public CardPaymentMethodTokensDto() {
    }

    /**
     * Instantiates a new card payment method tokens dto.
     *
     * @param response the response
     */
    public CardPaymentMethodTokensDto(PaymentMethodTokensDto response) {
        setActionStatus(response.getActionStatus());
        for (PaymentMethodDto paymentMethodDto : response.getPaymentMethods()) {
            this.cardPaymentMethods.add(new CardPaymentMethodDto(paymentMethodDto));
        }

    }

    /**
     * Gets the card payment methods.
     *
     * @return the card payment methods
     */
    public List<CardPaymentMethodDto> getCardPaymentMethods() {
        return cardPaymentMethods;
    }

    /**
     * Sets the card payment methods.
     *
     * @param cardPaymentMethods the new card payment methods
     */
    public void setCardPaymentMethods(List<CardPaymentMethodDto> cardPaymentMethods) {
        this.cardPaymentMethods = cardPaymentMethods;
    }
}