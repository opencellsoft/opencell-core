package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class PaymentMethodTokenDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "PaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethodTokenDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The payment method. */
    private PaymentMethodDto paymentMethod;

    /**
     * Instantiates a new payment method token dto.
     */
    public PaymentMethodTokenDto() {
    }

    /**
     * Gets the payment method.
     *
     * @return the payment method
     */
    public PaymentMethodDto getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the new payment method
     */
    public void setPaymentMethod(PaymentMethodDto paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    @Override
    public String toString() {
        return "DDPaymentMethodTokenDto [paymentMethod=" + paymentMethod + "]";
    }
}