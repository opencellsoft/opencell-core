package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "CHECK")
public class CheckPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571628074346184L;

    public CheckPaymentMethod() {
        this.paymentType = PaymentMethodEnum.CHECK;
    }

    public CheckPaymentMethod(String alias, boolean preferred, CustomerAccount customerAccount) {
        super();
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
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CheckPaymentMethod)) {
            return false;
        }

        // Only one check payment can be as it has no extra information
        return true;
    }

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        setAlias(paymentMethod.getAlias());
        setPreferred(paymentMethod.isPreferred());
    }

    @Override
    public String toString() {
        return "CheckPaymentMethod [alias=" + alias + ", preferred=" + preferred + "]";
    }
}