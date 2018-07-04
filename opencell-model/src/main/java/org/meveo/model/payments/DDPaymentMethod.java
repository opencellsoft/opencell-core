package org.meveo.model.payments;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.meveo.model.billing.BankCoordinates;

@Entity
@DiscriminatorValue(value = "DIRECTDEBIT")

public class DDPaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8578954294545445527L;

    @Embedded
    private BankCoordinates bankCoordinates = new BankCoordinates();

    @Column(name = "mandate_identification", length = 255)
    @Size(max = 255)
    private String mandateIdentification = "";

    @Column(name = "mandate_date")
    @Temporal(TemporalType.DATE)
    private Date mandateDate;

    public DDPaymentMethod() {
        this.paymentType = PaymentMethodEnum.DIRECTDEBIT;
    }

    public DDPaymentMethod(String alias, boolean preferred) {
        super();
        this.paymentType = PaymentMethodEnum.DIRECTDEBIT;
        this.alias = alias;
        this.preferred = preferred;
    }

    public DDPaymentMethod(CustomerAccount customerAccount, boolean isDisabled, String alias, boolean preferred, Date mandateDate, String mandateIdentification,
            BankCoordinates bankCoordinates) {
        super();
        setPaymentType(PaymentMethodEnum.CARD);
        setAlias(alias);
        setDisabled(isDisabled);
        setPreferred(preferred);
        this.customerAccount = customerAccount;
        this.mandateDate = mandateDate;
        this.mandateIdentification = mandateIdentification;
        this.bankCoordinates = bankCoordinates;
    }

    public BankCoordinates getBankCoordinates() {
        return bankCoordinates;
    }

    public void setBankCoordinates(BankCoordinates bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    public String getMandateIdentification() {
        return mandateIdentification;
    }

    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    public Date getMandateDate() {
        return mandateDate;
    }

    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
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

        if (getMandateIdentification() != null && getMandateIdentification().equals(other.getMandateIdentification())) {
            return true;
        }

        if (bankCoordinates != null) {
            return bankCoordinates.equals(other.getBankCoordinates());
        }
        return false;
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
        return "DDPaymentMethod [ alias = " + getAlias() + ", account_owner = " + bankCoordinates.getAccountOwner() + ",  bank_name = " + bankCoordinates.getBankName() + ","
                + " bic = " + bankCoordinates.getBic() + ", iban = " + bankCoordinates.getIban() + ",  mandateIdentification=" + getMandateIdentification() + ", mandateDate=" + getMandateDate() + "]";
    }

}