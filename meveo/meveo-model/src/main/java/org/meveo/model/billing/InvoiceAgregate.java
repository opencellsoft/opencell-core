/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;

/**
 * @author R.AITYAAZZA
 * 
 */
@Entity
@Table(name = "BILLING_INVOICE_AGREGATE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_AGREGATE_SEQ")
public abstract class InvoiceAgregate extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILLING_ACCOUNT_ID")
    private BillingAccount billingAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BILLING_RUN_ID")
    private BillingRun billingRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount userAccount;

    @Column(name = "ITEM_NUMBER")
    private Integer itemNumber;

    @Column(name = "ACCOUNTING_CODE", length = 255)
    private String accountingCode;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

    @Column(name = "TAX_PERCENT", precision = 23, scale = 12)
    private BigDecimal taxPercent;

    @Column(name = "QUANTITY")
    private Integer quantity = 0;

    @Column(name = "AMOUNT", precision = 23, scale = 12)
    private BigDecimal amount;

    @Column(name = "DISCOUNT", precision = 23, scale = 12)
    private BigDecimal discount;

    @Column(name = "AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
    private BigDecimal amountWithoutTax;

    @Column(name = "AMOUNT_TAX", precision = 23, scale = 12)
    private BigDecimal amountTax;

    @Column(name = "AMOUNT_WITH_TAX", precision = 23, scale = 12)
    private BigDecimal amountWithTax;
  
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_CURRENCY_ID")
    private TradingCurrency tradingCurrency ;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRADING_COUNTRY_ID")
    private TradingCountry tradingCountry; 
    

    @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "TRADING_LANGUAGE_ID")
   private TradingLanguage tradingLanguage;
    
    
    @Column(name = "PR_DESCRIPTION", length = 50)
    private String prDescription;
  
    @Column(name = "DESCRIPTION_DISCOUNT", length = 50)
    private String descriptionDiscount;

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
 
	public String getDescriptionDiscount() {
		return descriptionDiscount;
	}

	public void setDescriptionDiscount(String descriptionDiscount) {
		this.descriptionDiscount = descriptionDiscount;
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

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Integer getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(Integer itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(Integer quantity) {
        this.quantity = this.quantity + quantity;
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
        if (amountWithoutTax == null) {
            amountWithoutTax = new BigDecimal("0");
        }
        amountWithoutTax = amountWithoutTax.add(amountToAdd);
    }

    public void addAmountTax(BigDecimal amountToAdd) {
        if (amountTax == null) {
            amountTax = new BigDecimal("0");
        }
        amountTax = amountTax.add(amountToAdd);
    }

}
