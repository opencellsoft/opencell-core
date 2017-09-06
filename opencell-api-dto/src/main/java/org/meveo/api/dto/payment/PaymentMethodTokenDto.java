package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "PaymentMethodToken")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMethodTokenDto extends BaseResponse {

    private static final long serialVersionUID = 1L;
    private PaymentMethodDto paymentMethod;

    public PaymentMethodTokenDto() {
    }

    public PaymentMethodDto getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodDto paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "DDPaymentMethodTokenDto [paymentMethod=" + paymentMethod + "]";
    }
}