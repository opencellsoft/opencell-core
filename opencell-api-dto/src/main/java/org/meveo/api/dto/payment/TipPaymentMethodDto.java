package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.BankCoordinatesDto;
import org.meveo.model.payments.TipPaymentMethod;

/**
 * Tip payment method
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "TipPaymentMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class TipPaymentMethodDto extends PaymentMethodDto {

    private static final long serialVersionUID = -5311717336343997030L;

    /**
     * Bank account information
     */
    private BankCoordinatesDto bankCoordinates;

    public TipPaymentMethodDto() {
    }

    public TipPaymentMethodDto(TipPaymentMethod paymentMethod) {
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

    public TipPaymentMethod fromDto() {
        TipPaymentMethod paymentMethod = new TipPaymentMethod(getAlias(), isPreferred());

        if (bankCoordinates != null) {
            paymentMethod.setBankCoordinates(getBankCoordinates().fromDto());
        }

        return paymentMethod;
    }

    @Override
    public String toString() {
        return "TipPaymentMethodDto [alias=" + alias + ", preferred=" + preferred + ", customerAccountCode=" + customerAccountCode + ", bankCoordinates=" + bankCoordinates + "]";
    }
}