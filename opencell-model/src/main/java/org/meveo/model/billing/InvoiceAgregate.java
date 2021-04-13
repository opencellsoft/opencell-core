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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@NamedQueries({ @NamedQuery(name = "InvoiceAgregate.deleteByBR", query = "delete from InvoiceAgregate ia where ia.billingRun.id=:billingRunId"),
        @NamedQuery(name = "InvoiceAgregate.deleteByInvoiceIds", query = "delete from InvoiceAgregate ia where ia.invoice.id IN (:invoicesIds)") })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_invoice_agregate_seq"), })
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

    public void addItemNumber(Integer numberToAdd) {
        if (itemNumber == null) {
            itemNumber = numberToAdd;
        } else {
            itemNumber = itemNumber.intValue() + numberToAdd.intValue();
        }
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

    public void addAmountWithTax(BigDecimal deltaAmount) {
        if (deltaAmount != null) {
            if (amountWithTax == null) {
                amountWithTax = new BigDecimal("0");
            }
            amountWithTax = amountWithTax.add(deltaAmount);
        }
    }

    public void subtractAmountWithTax(BigDecimal deltaAmount) {
        if (deltaAmount != null) {
            if (amountWithTax == null) {
                amountWithTax = new BigDecimal("0");
            }
            amountWithTax = amountWithTax.subtract(deltaAmount);
        }
    }

    public void addAmountWithoutTax(BigDecimal deltaAmount) {
        if (deltaAmount != null) {
            if (amountWithoutTax == null) {
                amountWithoutTax = new BigDecimal("0");
            }
            amountWithoutTax = amountWithoutTax.add(deltaAmount);
        }
    }

    public void subtractAmountWithoutTax(BigDecimal deltaAmount) {
        if (deltaAmount != null) {
            if (amountWithoutTax == null) {
                amountWithoutTax = new BigDecimal("0");
            }
            amountWithoutTax = amountWithoutTax.subtract(deltaAmount);
        }
    }

    public void addAmountTax(BigDecimal deltaAmount) {
        if (deltaAmount != null) {
            if (amountTax == null) {
                amountTax = new BigDecimal("0");
            }
            amountTax = amountTax.add(deltaAmount);
        }
    }

    public void subtractAmountTax(BigDecimal deltaAmount) {
        if (deltaAmount != null) {
            if (amountTax == null) {
                amountTax = new BigDecimal("0");
            }
            amountTax = amountTax.subtract(deltaAmount);
        }
    }

    public void resetAmounts() {
        setAmount(new BigDecimal(0));
        setAmountTax(new BigDecimal(0));
        setAmountWithoutTax(new BigDecimal(0));
        setAmountWithTax(new BigDecimal(0));
    }
}