/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.AuditableEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.RecordedInvoice;

@Entity
@ObservableEntity
@Table(name = "BILLING_INVOICE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_INVOICE_SEQ")
public class Invoice extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_ACCOUNT_ID")
	private BillingAccount billingAccount = new BillingAccount();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_RUN_ID")
	private BillingRun billingRun;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RECORDED_INVOICE_ID")
	private RecordedInvoice recordedInvoice;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	private List<InvoiceAgregate> invoiceAgregates = new ArrayList<InvoiceAgregate>();

	@Column(name = "INVOICE_NUMBER", length = 20)
	private String invoiceNumber;

	@Column(name = "TEMPORARY_INVOICE_NUMBER", length = 50, unique = true)
	private String temporaryInvoiceNumber;

	@Column(name = "PRODUCT_DATE")
	private Date productDate;

	@Column(name = "INVOICE_DATE")
	private Date invoiceDate;

	@Column(name = "DUE_DATE")
	private Date dueDate;

	@Column(name = "AMOUNT", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amount;

	@Column(name = "DISCOUNT", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal discount;

	@Column(name = "AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithoutTax;

	@Column(name = "AMOUNT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountTax;

	@Column(name = "AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal amountWithTax;

	@Column(name = "NET_TO_PAY", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal netToPay;

	@Column(name = "PAYMENT_METHOD")
	@Enumerated(EnumType.STRING)
	private PaymentMethodEnum paymentMethod;

	@Column(name = "IBAN")
	private String iban;

	@Column(name = "ALIAS")
	private String alias;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_CURRENCY_ID")
	private TradingCurrency tradingCurrency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_COUNTRY_ID")
	private TradingCountry tradingCountry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_LANGUAGE_ID")
	private TradingLanguage tradingLanguage;

	@Column(name = "INVOICE_TYPE", length = 20)
	private String invoiceType;

	@OneToMany(mappedBy = "invoice", fetch = FetchType.LAZY)
	private List<RatedTransaction> ratedTransactions = new ArrayList<RatedTransaction>();

	@Column(name = "COMMENT", length = 1200)
	private String comment;

	@Column(name = "PDF")
	@Basic(fetch = FetchType.LAZY)
	private byte[] pdf;

	@OneToOne(optional = false, mappedBy = "invoice", fetch = FetchType.LAZY)
	public CreditNote creditNote;

	public List<RatedTransaction> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getProductDate() {
		return productDate;
	}

	public void setProductDate(Date productDate) {
		this.productDate = productDate;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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

	public PaymentMethodEnum getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
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

	public List<InvoiceAgregate> getInvoiceAgregates() {
		return invoiceAgregates;
	}

	public void setInvoiceAgregates(List<InvoiceAgregate> invoiceAgregates) {
		this.invoiceAgregates = invoiceAgregates;
	}

	public void addAmountWithTax(BigDecimal amountToAdd) {
		if (amountWithTax == null) {
			amountWithTax = BigDecimal.ZERO;
		}
		if (amountToAdd != null) {
			amountWithTax = amountWithTax.add(amountToAdd);
		}
	}

	public void addAmountWithoutTax(BigDecimal amountToAdd) {
		if (amountWithoutTax == null) {
			amountWithoutTax = BigDecimal.ZERO;
		}
		if (amountToAdd != null) {
			amountWithoutTax = amountWithoutTax.add(amountToAdd);
		}
	}

	public void addAmountTax(BigDecimal amountToAdd) {
		if (amountTax == null) {
			amountTax = BigDecimal.ZERO;
		}
		if (amountToAdd != null) {
			amountTax = amountTax.add(amountToAdd);
		}
	}

	/*
	 * public Blob getPdfBlob() { return pdfBlob; }
	 * 
	 * public void setPdfBlob(Blob pdfBlob) { this.pdfBlob = pdfBlob; }
	 */

	/*
	 * public byte[] getPdf() { byte[] result = null; try { if (pdfBlob != null)
	 * { int size; size = (int) pdfBlob.length(); result = pdfBlob.getBytes(1L,
	 * size); } } catch (SQLException e) {
	 * logger.error("Error while accessing pdf blob field in database. Return null."
	 * , e); } return result; }
	 * 
	 * public void setPdf(byte[] pdf) { this.pdfBlob =
	 * Hibernate.createBlob(pdf); }
	 */

	public byte[] getPdf() {
		return pdf;
	}

	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}

	public String getTemporaryInvoiceNumber() {
		return temporaryInvoiceNumber;
	}

	public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
		this.temporaryInvoiceNumber = temporaryInvoiceNumber;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

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

	public BigDecimal getNetToPay() {
		return netToPay;
	}

	public void setNetToPay(BigDecimal netToPay) {
		this.netToPay = netToPay;
	}

	public RecordedInvoice getRecordedInvoice() {
		return recordedInvoice;
	}

	public void setRecordedInvoice(RecordedInvoice recordedInvoice) {
		this.recordedInvoice = recordedInvoice;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public CreditNote getCreditNote() {
		return creditNote;
	}

	public void setCreditNote(CreditNote creditNote) {
		this.creditNote = creditNote;
	}

}
