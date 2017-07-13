package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * Payment method other than Card, Direct debit or TIP. For those cases use a specific CardPaymentMethodDto, DDPaymentMethodDto or TipPaymentMethodDto classes.
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "OtherPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class OtherPaymentMethodDto extends PaymentMethodDto {

    private static final long serialVersionUID = -8688697830266213908L;

    /**
     * Payment type. Allowed values are: Check and Wire transfer
     */
    public PaymentMethodEnum paymentMethodType;

    public OtherPaymentMethodDto() {
    }

    public OtherPaymentMethodDto(PaymentMethod paymentMethod) {
        super(paymentMethod);
        paymentMethodType = paymentMethod.getPaymentType();
    }

    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    @Override
    public String toString() {
        return "OtherPaymentMethodDto [alias=" + alias + ", preferred=" + preferred + ", customerAccountCode=" + customerAccountCode + ", paymentMethodType=" + paymentMethodType
                + "]";
    }
}