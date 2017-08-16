package org.meveo.api.dto.payment;

import java.util.Date;

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
    private String mandateIdentification = "";
    private Date mandateDate;

    public DDPaymentMethodDto() {
        super();
    }

    public DDPaymentMethodDto(DDPaymentMethod paymentMethod) {
        super(paymentMethod);
        if (paymentMethod.getBankCoordinates() != null) {
            bankCoordinates = new BankCoordinatesDto(paymentMethod.getBankCoordinates());
        }
        mandateIdentification = paymentMethod.getMandateIdentification();
        mandateDate = paymentMethod.getMandateDate();
    }

    public DDPaymentMethodDto(BankCoordinatesDto bankCoordinates) {
        super();
        this.bankCoordinates = bankCoordinates;
    }

    public BankCoordinatesDto getBankCoordinates() {
        return bankCoordinates;
    }

    public void setBankCoordinates(BankCoordinatesDto bankCoordinates) {
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

	public DDPaymentMethod fromDto() {
        DDPaymentMethod paymentMethod = new DDPaymentMethod(getAlias(), isPreferred());

        if (bankCoordinates != null) {
            paymentMethod.setBankCoordinates(getBankCoordinates().fromDto());
        }
        paymentMethod.setMandateDate(getMandateDate());
        paymentMethod.setMandateIdentification(getMandateIdentification());
        return paymentMethod;
    }

	@Override
	public String toString() {
		return "DDPaymentMethodDto [isPrefered="+isPreferred()+", alias="+getAlias()+",bankCoordinates=" + bankCoordinates + ", mandateIdentification="
				+ mandateIdentification + ", mandateDate=" + mandateDate + "]";
	}

    
}