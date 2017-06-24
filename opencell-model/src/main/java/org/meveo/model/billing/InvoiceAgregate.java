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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableEntity;

@Entity
@Table(name = "billing_invoice_agregate")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "billing_invoice_agregate_seq"), })
public abstract class InvoiceAgregate extends EnableEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_account_id")
	private BillingAccount billingAccount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "invoice_id")
	private Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_run_id")
	private BillingRun billingRun;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;

	@Column(name = "item_number")
	private Integer itemNumber;

	@Column(name = "accounting_code", length = 255)
	@Size(max = 255)
	private String accountingCode;

	@Column(name = "description", length = 255)
    @Size(max = 255)
	private String description;

	@Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal taxPercent;

	@Column(name = "quantity")
	private BigDecimal quantity;

	@Column(name = "amount", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amount;

	@Column(name = "discount", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal discount;

	@Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithoutTax;

	@Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountTax;

	@Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithTax;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_currency_id")
	private TradingCurrency tradingCurrency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_country_id")
	private TradingCountry tradingCountry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_language_id")
	private TradingLanguage tradingLanguage;

	@Column(name = "pr_description", length = 255)
	@Size(max = 255)
	private String prDescription;

	@Type(type="numeric_boolean")
    @Column(name = "discount_aggregate", nullable = false)
	@NotNull
	private boolean discountAggregate;
	
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

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public void addQuantity(BigDecimal quantity) {
		this.quantity = this.quantity.add(quantity);
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
		if(amountToAdd!=null){
		if (amountWithoutTax == null) {
			amountWithoutTax = new BigDecimal("0");
		}
		amountWithoutTax = amountWithoutTax.add(amountToAdd);
	}
	}

	public void addAmountTax(BigDecimal amountToAdd) {
		if(amountToAdd!=null){
		if (amountTax == null) {
			amountTax = new BigDecimal("0");
		}
		amountTax = amountTax.add(amountToAdd);
	}
	}

	public boolean isDiscountAggregate() {
		return discountAggregate;
	}

	public void setDiscountAggregate(boolean discountAggregate) {
		this.discountAggregate = discountAggregate;
	}

	public void resetAmounts() {
		setAmount(new BigDecimal(0));
		setAmountTax(new BigDecimal(0));
		setAmountWithoutTax(new BigDecimal(0));
		setAmountWithTax(new BigDecimal(0));
	}
	
	@Override
	public boolean equals(Object obj) {	    

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof InvoiceAgregate)) {
            return false;
        }
        
		InvoiceAgregate temp = (InvoiceAgregate) obj;
		if (temp instanceof CategoryInvoiceAgregate && this instanceof CategoryInvoiceAgregate) {
			CategoryInvoiceAgregate temp1 = (CategoryInvoiceAgregate) this;
			CategoryInvoiceAgregate temp2 = (CategoryInvoiceAgregate) temp;
			return temp1.getInvoiceCategory().getCode().equals(temp2.getInvoiceCategory().getCode());
		} else if (temp instanceof SubCategoryInvoiceAgregate && this instanceof SubCategoryInvoiceAgregate) {
			SubCategoryInvoiceAgregate temp1 = (SubCategoryInvoiceAgregate) this;
			SubCategoryInvoiceAgregate temp2 = (SubCategoryInvoiceAgregate) temp;
			return temp1.getInvoiceSubCategory().getCode().equals(temp2.getInvoiceSubCategory().getCode());
		} else if (temp instanceof TaxInvoiceAgregate && this instanceof TaxInvoiceAgregate) {
			TaxInvoiceAgregate temp1 = (TaxInvoiceAgregate) this;
			TaxInvoiceAgregate temp2 = (TaxInvoiceAgregate) temp;
			return temp1.getTax().getCode().equals(temp2.getTax().getCode());
		}

		return false;
	}
	
}
