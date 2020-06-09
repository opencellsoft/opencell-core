package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Payment by check payment method
 * 
 * @author Andrius Karpavicius
 */
@Entity
@DiscriminatorValue(value = "CHECK")
public class CheckPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571628074346184L;

    public CheckPaymentMethod() {
        this.paymentType = PaymentMethodEnum.CHECK;
    }

    public CheckPaymentMethod(boolean isDisabled, String alias, boolean preferred, CustomerAccount customerAccount) {
        super();
        setDisabled(isDisabled);
        this.paymentType = PaymentMethodEnum.CHECK;
        this.alias = alias;
        this.preferred = preferred;
        this.customerAccount = customerAccount;
    }

    public CheckPaymentMethod(String alias, boolean preferred) {
        super();
        this.paymentType = PaymentMethodEnum.CHECK;
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
        return "CheckPaymentMethod [alias= " + getAlias() + ", preferred=" + isPreferred() + "]";
    }
}