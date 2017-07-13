package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.model.payments.DDPaymentMethod;

/**
 * Direct debit payment method
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "DDPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDPaymentMethodDto extends PaymentMethodDto {

    private static final long serialVersionUID = 4939684774145093492L;

    /**
     * Bank account information
     */
    private BankCoordinatesDto bankCoordinates;

    public DDPaymentMethodDto(DDPaymentMethod paymentMethod) {
        super(paymentMethod);
        if (paymentMethod.getBankCoordinates() != null) {
            bankCoordinates = new BankCoordinatesDto(paymentMethod.getBankCoordinates());
        }
    }

    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
        this.bankCoordinates = bankCoordinates;
    }

    public DDPaymentMethod fromDto() {
        DDPaymentMethod paymentMethod = new DDPaymentMethod(getAlias(), isPreferred());

        if (bankCoordinates != null) {
            paymentMethod.setBankCoordinates(getBankCoordinates().fromDto());
        }

        return paymentMethod;
    }

    @Override
    public String toString() {
        return "DDPaymentMethodDto [alias=" + alias + ", preferred=" + preferred + ", customerAccountCode=" + customerAccountCode + ", bankCoordinates=" + bankCoordinates + "]";
    }
}