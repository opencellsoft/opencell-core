package org.meveo.model.payments;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.meveo.model.billing.BankCoordinates;

@Entity
@DiscriminatorValue(value = "DIRECTDEBIT")

public class DDPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8578954294545445527L;

    @Embedded
    private BankCoordinates bankCoordinates = new BankCoordinates();

    public DDPaymentMethod() {
        this.paymentType = PaymentMethodEnum.DIRECTDEBIT;
    }

    public DDPaymentMethod(String alias, boolean preferred) {
        super();
        this.alias = alias;
        this.preferred = preferred;
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
        } else if (!(obj instanceof DDPaymentMethod)) {
            return false;
        }

        DDPaymentMethod other = (DDPaymentMethod) obj;

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

        DDPaymentMethod otherPaymentMethod = (DDPaymentMethod) paymentMethod;
        setAlias(otherPaymentMethod.getAlias());
        setPreferred(otherPaymentMethod.isPreferred());
        otherPaymentMethod.setBankCoordinates(otherPaymentMethod.getBankCoordinates());
    }

    @Override
    public String toString() {
        return "DDPaymentMethod [alias=" + alias + ", preferred=" + preferred + ", bankCoordinates=" + bankCoordinates + "]";
    }
}