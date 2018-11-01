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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

/**
 * Invoice aggregate
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@Table(name = "billing_invoice_agregate")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_agregate_seq"), })
public abstract class InvoiceAgregate extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Billing account that invoice was issued to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account_id")
    protected BillingAccount billingAccount;

    /**
     * Invoice that Invoice aggregate is part of
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    protected Invoice invoice;

    /**
     * Billing run that produced the invoice
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    protected BillingRun billingRun;

    /**
     * Number of Rated transactions that fall in this aggregate
     */
    @Column(name = "item_number")
    protected Integer itemNumber;

    /**
     * Description
     */
    @Column(name = "description", length = 255)
    @Size(max = 255)
    protected String description;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amount;

    /**
     * Aggregate amount without tax
     */
    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal amountWithoutTax = BigDecimal.ZERO;

    /**
     * Aggregate tax amount
     */
    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal amountTax = BigDecimal.ZERO;

    /**
     * Aggregate amount with tax
     */
    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    protected BigDecimal amountWithTax = BigDecimal.ZERO;

    /**
     * Currency that invoice is in
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    protected TradingCurrency tradingCurrency;

    /**
     * Country that invoice is for (for tax calculation)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    protected TradingCountry tradingCountry;

    /**
     * Invoice language
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_language_id")
    protected TradingLanguage tradingLanguage;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "pr_description", length = 255)
    @Size(max = 255)
    protected String prDescription;

    public TradingCurrency getTradingCurrency() {
        return tradingCurrency;
    }

    public void setTradingCurrency(TradingCurrency tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    public TradingCountry getTradingCountry() {
        return tradingCountry;
    }

    public void setTradingCountry(TradingCountry tradingCountry) {
        this.tradingCountry = tradingCountry;
    }

    public TradingLanguage getTradingLanguage() {
        return tradingLanguage;
    }

    public void setTradingLanguage(TradingLanguage tradingLanguage) {
        this.tradingLanguage = tradingLanguage;
    }

    public String getPrDescription() {
        return prDescription;
    }

    public void setPrDescription(String prDescription) {
        this.prDescription = prDescription;
    }

    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public Integer getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        if (invoice != null) {
            invoice.getInvoiceAgregates().add(this);
        }
    }

    public void addAmount(BigDecimal amountToAdd) {
        if (amount == null) {
            amount = new BigDecimal("0");
        }
        amount = amount.add(amountToAdd);
    }

    public void addAmountWithTax(BigDecimal amountToAdd) {
        if (amountWithTax == null) {
            amountWithTax = new BigDecimal("0");
        }
        amountWithTax = amountWithTax.add(amountToAdd);
    }

    public void addAmountWithoutTax(BigDecimal amountToAdd) {
        if (amountToAdd != null) {
            if (amountWithoutTax == null) {
                amountWithoutTax = new BigDecimal("0");
            }
            amountWithoutTax = amountWithoutTax.add(amountToAdd);
        }
    }

    public void addAmountTax(BigDecimal amountToAdd) {
        if (amountToAdd != null) {
            if (amountTax == null) {
                amountTax = new BigDecimal("0");
            }
            amountTax = amountTax.add(amountToAdd);
        }
    }

    public void resetAmounts() {
        setAmount(new BigDecimal(0));
        setAmountTax(new BigDecimal(0));
        setAmountWithoutTax(new BigDecimal(0));
        setAmountWithTax(new BigDecimal(0));
    }
}