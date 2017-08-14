package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.CheckPaymentMethod;

/**
 * Check payment method
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "CheckPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class CheckPaymentMethodDto extends PaymentMethodDto {

    private static final long serialVersionUID = -5311717336343997030L;

    public CheckPaymentMethodDto() {
    }

    public CheckPaymentMethodDto(CheckPaymentMethod paymentMethod) {
        super(paymentMethod);       
    }

    public CheckPaymentMethod fromDto() {
    	CheckPaymentMethod paymentMethod = new CheckPaymentMethod(getAlias(), isPreferred());
        return paymentMethod;
    }

    @Override
    public String toString() {
        return "TipPaymentMethodDto [alias=" + alias + ", preferred=" + preferred + ", customerAccountCode=" + customerAccountCode + "]";
    }
}