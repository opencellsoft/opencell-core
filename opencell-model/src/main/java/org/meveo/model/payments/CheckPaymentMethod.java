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