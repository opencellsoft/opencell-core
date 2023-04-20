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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.commons.utils.StringUtils;

/**
 * Payment by Stripe payment method
 * 
 * @author anasseh
 */
@Entity
@DiscriminatorValue(value = "STRIPEDIRECTLINK")
public class StripePaymentMethod extends PaymentMethod {

    private static final long serialVersionUID = 8726571611074346199L;
    
    public StripePaymentMethod() {
        this.paymentType = PaymentMethodEnum.STRIPEDIRECTLINK;
    }

    public StripePaymentMethod(boolean isDisabled, String alias, boolean preferred, CustomerAccount customerAccount,String userId) {
        super();
        setDisabled(isDisabled);
        this.paymentType = PaymentMethodEnum.STRIPEDIRECTLINK;
        this.alias = alias;
        this.preferred = preferred;
        this.customerAccount = customerAccount;
        if(StringUtils.isBlank(userId)) {
        	this.userId = customerAccount.getContactInformationNullSafe().getEmail();
        }else {
        	this.userId = userId;
        }
    }

    public StripePaymentMethod(String alias, boolean preferred) {
        super();
        this.paymentType = PaymentMethodEnum.STRIPEDIRECTLINK;
        this.alias = alias;
        this.preferred = preferred;
    }
    


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof StripePaymentMethod)) {
			return false;
		}

		StripePaymentMethod other = (StripePaymentMethod) obj;

		if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
			return true;
		}

		if (getUserId() != null && getUserId().equals(other.getUserId())) {
			return true;
		}

		
		return false;
	}

    @Override
    public void updateWith(PaymentMethod paymentMethod) {

        setAlias(paymentMethod.getAlias());
        setPreferred(paymentMethod.isPreferred());
    }

    @Override
    public String toString() {
        return "STRIPEPaymentMethod [alias= " + getAlias() + ", preferred=" + isPreferred() + "]";
    }
}