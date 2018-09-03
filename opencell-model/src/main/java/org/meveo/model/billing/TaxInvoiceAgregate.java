/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("T")
public class TaxInvoiceAgregate extends InvoiceAgregate {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;

    @Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal taxPercent;

    public TaxInvoiceAgregate() {

    }

    public TaxInvoiceAgregate(TaxInvoiceAgregate taxInvoiceAgregate) {
        this.setItemNumber(taxInvoiceAgregate.getItemNumber());
        this.setAmountWithoutTax(taxInvoiceAgregate.getAmountWithoutTax());
        this.setAmountWithTax(taxInvoiceAgregate.getAmountWithTax());
        this.setAmountTax(taxInvoiceAgregate.getAmountTax());
        this.setTaxPercent(taxInvoiceAgregate.getTaxPercent());
        this.setBillingAccount(taxInvoiceAgregate.getBillingAccount());
        this.setBillingRun(taxInvoiceAgregate.getBillingRun());
        this.setUserAccount(taxInvoiceAgregate.getUserAccount());
        this.setDiscountAggregate(false);
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }
}