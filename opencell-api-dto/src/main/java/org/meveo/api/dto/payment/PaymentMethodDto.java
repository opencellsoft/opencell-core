package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.TipPaymentMethod;

@XmlRootElement(name = "PaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PaymentMethodDto extends BaseDto {

    private static final long serialVersionUID = 4815935377652350103L;

    protected Long id;

    protected String alias;

    protected boolean preferred;

    protected String customerAccountCode;

    public PaymentMethodDto() {
    }

    public PaymentMethodDto(PaymentMethod paymentMethod) {
        this.id = paymentMethod.getId();
        this.alias = paymentMethod.getAlias();
        this.preferred = paymentMethod.isPreferred();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /**
     * Convert entity to DTO
     * 
     * @param paymentMethod Payment method to convert
     * @return DTO
     */
    public static PaymentMethodDto toDto(PaymentMethod paymentMethod) {

        PaymentMethodDto pmDto = null;
        switch (paymentMethod.getPaymentType()) {
        case CARD:
            pmDto = new CardPaymentMethodDto((CardPaymentMethod) paymentMethod);
            break;
        case DIRECTDEBIT:
            pmDto = new DDPaymentMethodDto((DDPaymentMethod) paymentMethod);
            break;
        case TIP:
            pmDto = new TipPaymentMethodDto((TipPaymentMethod) paymentMethod);
            break;
        case CHECK:
        case WIRETRANSFER:
            pmDto = new OtherPaymentMethodDto(paymentMethod);
            break;
        }

        return pmDto;
    }
}