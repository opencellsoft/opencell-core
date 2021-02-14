/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

/**
 * Payment by Direct debit method
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 */
@Entity
@DiscriminatorValue(value = "DIRECTDEBIT")
public class DDPaymentMethod extends PaymentMethod {

	private static final long serialVersionUID = 8578954294545445527L;

	/**
	 * Bank information
	 */
	@Embedded
	private BankCoordinates bankCoordinates = new BankCoordinates();

	/**
	 * Order identification
	 */
	@Column(name = "mandate_identification", length = 255)
	@Size(max = 255)
	private String mandateIdentification = "";

	/**
	 * Order date
	 */
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

	public DDPaymentMethod(CustomerAccount customerAccount, boolean isDisabled, String alias, boolean preferred,
			Date mandateDate, String mandateIdentification, BankCoordinates bankCoordinates) {
		super();
		setPaymentType(PaymentMethodEnum.DIRECTDEBIT);
		setAlias(alias);
		setDisabled(isDisabled);
		setPreferred(preferred);
		this.customerAccount = customerAccount;
		this.mandateDate = mandateDate;
		this.mandateIdentification = mandateIdentification;
		setBankCoordinates(bankCoordinates);
	}

	public BankCoordinates getBankCoordinates() {
		return bankCoordinates;
	}

	public void setBankCoordinates(BankCoordinates bankCoordinates) {
		this.bankCoordinates = bankCoordinates != null ? bankCoordinates : new BankCoordinates();
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
	
	public void anonymize(String code) {
	    super.anonymize(code);
	    setMandateIdentification(code);
	    setMandateDate(null);
	    if(bankCoordinates != null) {
	        bankCoordinates.anonymize(code);
	    }
    }

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public void updateWith(PaymentMethod paymentMethod) {

		DDPaymentMethod otherPaymentMethod = (DDPaymentMethod) paymentMethod;
		setAlias(otherPaymentMethod.getAlias());
		setPreferred(otherPaymentMethod.isPreferred());
		setBankCoordinates(otherPaymentMethod.getBankCoordinates());
		setMandateDate(otherPaymentMethod.getMandateDate());
		setMandateIdentification(otherPaymentMethod.getMandateIdentification());
	}

	@Override
	public String toString() {
		BankCoordinates bc = bankCoordinates == null ? new BankCoordinates() : bankCoordinates;
		return "DDPaymentMethod [ alias = " + getAlias() + ", account_owner = " + bc.getAccountOwner()
				+ ",  bank_name = " + bc.getBankName() + "," + " bic = " + bc.getBic() + ", iban = " + bc.getIban()
				+ ",  mandateIdentification=" + getMandateIdentification() + ", mandateDate=" + getMandateDate() + "]";
	}

}