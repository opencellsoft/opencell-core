package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Payment by wire transfer method
 * 
 * @author Andrius Karpavicius
 */
@Entity
@DiscriminatorValue(value = "WIRETRANSFER")
public class WirePaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571628074346184L;

    public WirePaymentMethod() {
        this.paymentType = PaymentMethodEnum.WIRETRANSFER;
    }

    public WirePaymentMethod(boolean isDisabled, String alias, boolean preferred, CustomerAccount customerAccount) {
        super();
        this.paymentType = PaymentMethodEnum.WIRETRANSFER;
        this.alias = alias;
        this.preferred = preferred;
        this.customerAccount = customerAccount;
        setDisabled(isDisabled);
    }

    public WirePaymentMethod(String alias, boolean preferred) {
        super();
        this.alias = alias;
        this.preferred = preferred;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        setAlias(paymentMethod.getAlias());
        setPreferred(paymentMethod.isPreferred());
    }

    @Override
    public String toString() {
        return "WirePaymentMethod [alias= " + getAlias() + ", preferred=" + isPreferred() + "]";
    }
}