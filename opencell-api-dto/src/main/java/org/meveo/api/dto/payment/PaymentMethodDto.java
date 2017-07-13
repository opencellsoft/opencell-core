package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.PaymentMethod;

@XmlRootElement(name = "PaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PaymentMethodDto extends BaseDto {

    private static final long serialVersionUID = 4815935377652350103L;

    protected String alias;

    protected boolean preferred;

    protected String customerAccountCode;

    public PaymentMethodDto() {
    }

    public PaymentMethodDto(PaymentMethod paymentMethod) {
        this.alias = paymentMethod.getAlias();
        this.preferred = paymentMethod.isPreferred();
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }
}