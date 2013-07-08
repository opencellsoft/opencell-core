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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;
import org.meveo.model.crm.Provider;

@Entity
@Table(name = "BILLING_RATED_TRANSACTION")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_RATED_TRANSACTION_SEQ")
public class RatedTransaction extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WALLET_ID")
	private WalletInstance wallet;

	@Column(name = "WALLET_OPERATION_ID")
	private Long walletOperationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_RUN_ID")
	private BillingRun billingRun;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "USAGE_DATE")
	private Date usageDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_SUB_CATEGORY_ID")
	private InvoiceSubCategory invoiceSubCategory;

  

	@Column(name = "UNIT_AMOUNT_WITHOUT_TAX")
	private BigDecimal unitAmountWithoutTax;

	@Column(name = "UNIT_AMOUNT_WITH_TAX")
	private BigDecimal unitAmountWithTax;

	@Column(name = "UNIT_AMOUNT_TAX")
	private BigDecimal unitAmountTax;

	@Column(name = "QUANTITY")
	private BigDecimal quantity;

	@Column(name = "AMOUNT_WITHOUT_TAX")
	private BigDecimal amountWithoutTax;

	@Column(name = "AMOUNT_WITH_TAX")
	private BigDecimal amountWithTax;

	@Column(name = "AMOUNT_TAX")
	private BigDecimal amountTax;


 

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_ID")
	private Invoice invoice;

	@ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "AGGREGATE_ID_F")
	private SubCategoryInvoiceAgregate invoiceAgregateF;

	@ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "AGGREGATE_ID_R")
	private CategoryInvoiceAgregate invoiceAgregateR;

	@ManyToOne(fetch = FetchType.LAZY,cascade=CascadeType.ALL)
	@JoinColumn(name = "AGGREGATE_ID_T")
	private TaxInvoiceAgregate invoiceAgregateT;

 

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private RatedTransactionStatusEnum status;

	@Column(name = "DO_NOT_TRIGGER_INVOICING")
	private boolean doNotTriggerInvoicing = false;
	
	

	
	 

	public RatedTransaction(Long walletOperationId, Date usageDate,
			BigDecimal unitAmountWithoutTax, BigDecimal unitAmountWithTax,
			BigDecimal unitAmountTax, BigDecimal quantity,
			BigDecimal amountWithoutTax, BigDecimal amountWithTax,
			BigDecimal amountTax, RatedTransactionStatusEnum status,Provider provider, WalletInstance wallet,InvoiceSubCategory invoiceSubCategory) {
		super();
		this.walletOperationId = walletOperationId;
		this.usageDate = usageDate;
		this.unitAmountWithoutTax = unitAmountWithoutTax;
		this.unitAmountWithTax = unitAmountWithTax;
		this.unitAmountTax = unitAmountTax;
		this.quantity = quantity;
		this.amountWithoutTax = amountWithoutTax;
		this.amountWithTax = amountWithTax;
		this.amountTax = amountTax;
		this.status = status;
		this.wallet=wallet;
		this.invoiceSubCategory=invoiceSubCategory;
		setProvider(provider);
	}

	public WalletInstance getWallet() {
		return wallet;
	}

	public void setWallet(WalletInstance wallet) {
		this.wallet = wallet;
	}

	 

	public BillingRun getBillingRun() {
		return billingRun;
	}

	public void setBillingRun(BillingRun billingRun) {
		this.billingRun = billingRun;
	}


	public Date getUsageDate() {
		return usageDate;
	}

	public void setUsageDate(Date usageDate) {
		this.usageDate = usageDate;
	}

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	 

	public BigDecimal getUnitAmountWithoutTax() {
		return unitAmountWithoutTax;
	}

	public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
		this.unitAmountWithoutTax = unitAmountWithoutTax;
	}

	public BigDecimal getUnitAmountWithTax() {
		return unitAmountWithTax;
	}

	public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
		this.unitAmountWithTax = unitAmountWithTax;
	}

	public BigDecimal getUnitAmountTax() {
		return unitAmountTax;
	}

	public void setUnitAmountTax(BigDecimal unitAmountTax) {
		this.unitAmountTax = unitAmountTax;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public BigDecimal getAmountTax() {
		return amountTax;
	}

	public void setAmountTax(BigDecimal amountTax) {
		this.amountTax = amountTax;
	}

	 
	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public SubCategoryInvoiceAgregate getInvoiceAgregateF() {
		return invoiceAgregateF;
	}

	public void setInvoiceAgregateF(SubCategoryInvoiceAgregate invoiceAgregateF) {
		this.invoiceAgregateF = invoiceAgregateF;
	}

	public CategoryInvoiceAgregate getInvoiceAgregateR() {
		return invoiceAgregateR;
	}

	public void setInvoiceAgregateR(CategoryInvoiceAgregate invoiceAgregateR) {
		this.invoiceAgregateR = invoiceAgregateR;
	}

	public TaxInvoiceAgregate getInvoiceAgregateT() {
		return invoiceAgregateT;
	}

	public void setInvoiceAgregateT(TaxInvoiceAgregate invoiceAgregateT) {
		this.invoiceAgregateT = invoiceAgregateT;
	}

	public RatedTransactionStatusEnum getStatus() {
		return status;
	}

	public void setStatus(RatedTransactionStatusEnum status) {
		this.status = status;
	}

	public boolean isDoNotTriggerInvoicing() {
		return doNotTriggerInvoicing;
	}

	public void setDoNotTriggerInvoicing(boolean doNotTriggerInvoicing) {
		this.doNotTriggerInvoicing = doNotTriggerInvoicing;
	}

	public Long getWalletOperationId() {
		return walletOperationId;
	}

	public void setWalletOperationId(Long walletOperationId) {
		this.walletOperationId = walletOperationId;
	}



	 

}
