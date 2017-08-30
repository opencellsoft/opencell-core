package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.meveo.model.billing.BankCoordinates;

@Entity
@DiscriminatorValue(value = "TIP")

public class TipPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 7211596905386049994L;

    @Embedded
    private BankCoordinates bankCoordinates = new BankCoordinates();

    public TipPaymentMethod() {
        this.paymentType = PaymentMethodEnum.TIP;
    }

    public TipPaymentMethod(String alias, boolean preferred) {
        super();
        this.paymentType = PaymentMethodEnum.TIP;
        this.alias = alias;
        this.preferred = preferred;
    }

    public TipPaymentMethod(CustomerAccount customerAccount, String alias, BankCoordinates bankCoordinates) {
        super();
        this.paymentType = PaymentMethodEnum.TIP;
        setAlias(alias);
        setPreferred(preferred);
        this.customerAccount = customerAccount;
        this.bankCoordinates = bankCoordinates;
	}

	public BankCoordinates getBankCoordinates() {
        return bankCoordinates;
    }

    public void setBankCoordinates(BankCoordinates bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TipPaymentMethod)) {
            return false;
        }

        TipPaymentMethod other = (TipPaymentMethod) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        if (bankCoordinates != null) {
            return bankCoordinates.equals(other.getBankCoordinates());
        } else if (bankCoordinates == null && other.getBankCoordinates() == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        TipPaymentMethod otherPaymentMethod = (TipPaymentMethod) paymentMethod;
        setAlias(otherPaymentMethod.getAlias());
        setPreferred(otherPaymentMethod.isPreferred());
        otherPaymentMethod.setBankCoordinates(otherPaymentMethod.getBankCoordinates());
    }

    @Override
    public String toString() {
        return "TipPaymentMethod [alias=" + alias + ", preferred=" + preferred + ", bankCoordinates=" + bankCoordinates + "]";
    }
}