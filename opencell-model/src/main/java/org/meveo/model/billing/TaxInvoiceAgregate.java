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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;


    /**
     * Instantiates a new tax aggregate
     */
    public TaxInvoiceAgregate() {

    }

    /**
     * Instantiates a new tax aggregate
     * 
     * @param billingAccount Billing account
     * @param tax Tax applied
     * @param taxPercent Tax percent applied
     * @param invoice Invoice
     */
    public TaxInvoiceAgregate(BillingAccount billingAccount, Tax tax, BigDecimal taxPercent, Invoice invoice) {
        this.setBillingAccount(billingAccount);
        this.setTax(tax);
        this.setTaxPercent(taxPercent);
        this.setAccountingCode(tax.getAccountingCode());
        this.invoice = invoice;
        if (invoice != null) {
            this.billingRun = invoice.getBillingRun();
        }
        resetAmounts();
        this.setUseSpecificPriceConversion(invoice != null && invoice.isUseSpecificPriceConversion());
    }

    /**
     * Copies tax aggregate
     * 
     * @param taxInvoiceAgregate Tax aggregate to copy
     */
    public TaxInvoiceAgregate(TaxInvoiceAgregate taxInvoiceAgregate) {
        this.setItemNumber(taxInvoiceAgregate.getItemNumber());
        this.setAmountWithoutTax(taxInvoiceAgregate.getAmountWithoutTax());
        this.setAmountWithTax(taxInvoiceAgregate.getAmountWithTax());
        this.setAmountTax(taxInvoiceAgregate.getAmountTax());
        this.setTaxPercent(taxInvoiceAgregate.getTaxPercent());
        this.setBillingAccount(taxInvoiceAgregate.getBillingAccount());
        this.setBillingRun(taxInvoiceAgregate.getBillingRun());
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

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TaxInvoiceAgregate)) {
            return false;
        }

        TaxInvoiceAgregate other = (TaxInvoiceAgregate) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (this.getTax() == null) {
            return false;
        }

        return this.getTax().getId().equals(other.getTax().getId()) && this.getTaxPercent().compareTo(other.getTaxPercent()) == 0;
    }

}