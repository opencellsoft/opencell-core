package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.WirePaymentMethod;

/**
 * Wire payment method
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "WirePaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class WirePaymentMethodDto extends PaymentMethodDto {

    private static final long serialVersionUID = -5311717336343997030L;

    public WirePaymentMethodDto() {
    }

    public WirePaymentMethodDto(WirePaymentMethod paymentMethod) {
        super(paymentMethod);       
    }

    public WirePaymentMethod fromDto() {
    	WirePaymentMethod paymentMethod = new WirePaymentMethod(getAlias(), isPreferred());
        return paymentMethod;
    }

    @Override
    public String toString() {
        return "TipPaymentMethodDto [alias=" + alias + ", preferred=" + preferred + ", customerAccountCode=" + customerAccountCode + "]";
    }
}