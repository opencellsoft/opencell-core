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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.meveo.model.AuditableEntity;
import org.meveo.model.admin.Currency;

@Entity
@Table(name = "BILLING_BILLING_RUN")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_BILLING_RUN_SEQ")
public class BillingRun extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PROCESS_DATE")
	private Date processDate;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "STATUS")
	private BillingRunStatusEnum status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE")
	private Date statusDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_CYCLE_ID")
	private BillingCycle billingCycle;

	@Column(name = "NB_BILLING_ACCOUNT")
	private Integer billingAccountNumber;

	@Column(name = "NB_BILLABLE_BILLING_ACCOUNT")
	private Integer billableBillingAcountNumber;

	@Column(name = "NB_PRODUCIBLE_INVOICE")
	private Integer producibleInvoiceNumber;

	@Column(name = "PRODUCIBLE_AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal producibleAmountWithoutTax;

	@Column(name = "PRODUCIBLE_AMOUNT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal producibleAmountTax;

	@Column(name = "NB_INVOICE")
	private Integer InvoiceNumber;

	@Column(name = "PRODUCIBLE_AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal producibleAmountWithTax;

	@Column(name = "PR_AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal prAmountWithoutTax;

	@Column(name = "PR_AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal prAmountWithTax;

	@Column(name = "PR_AMOUNT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal prAmountTax;

	@OneToMany(mappedBy = "billingRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Invoice> invoices = new ArrayList<Invoice>();

	@OneToMany(mappedBy = "billingRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<BillingRunList> billingRunLists = new HashSet<BillingRunList>();

	@OneToMany(mappedBy = "billingRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<BillingAccount> billableBillingAccounts = new ArrayList<BillingAccount>();

	@OneToMany(mappedBy = "billingRun", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<RatedTransaction> ratedTransactions = new HashSet<RatedTransaction>();

	@Enumerated(value = EnumType.STRING)
	@Column(name = "PROCESS_TYPE")
	private BillingProcessTypesEnum processType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "REJECTION_REASON")
	private String rejectionReason;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PR_CURRENCY_ID")
	private Currency currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PR_COUNTRY_ID")
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PR_LANGUAGE_ID")
	private Language language;
	
	@Column(name = "SELECTED_BILLING_ACCOUNTS")
	private String selectedBillingAccounts;
	
	@Column(name = "XML_INVOICE_GENERATED")
	private Boolean xmlInvoiceGenerated;

	@Transient
	PreInvoicingReportsDTO preInvoicingReports = new PreInvoicingReportsDTO();

	@Transient
	PostInvoicingReportsDTO postInvoicingReports = new PostInvoicingReportsDTO();

	public Date getProcessDate() {
		return processDate;
	}

	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	public BillingRunStatusEnum getStatus() {
		return status;
	}

	public void setStatus(BillingRunStatusEnum status) {
		this.status = status;
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public BillingCycle getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(BillingCycle billingCycle) {
		this.billingCycle = billingCycle;
	}

	public Integer getBillingAccountNumber() {
		return billingAccountNumber;
	}

	public void setBillingAccountNumber(Integer billingAccountNumber) {
		this.billingAccountNumber = billingAccountNumber;
	}

	public Integer getBillableBillingAcountNumber() {
		return billableBillingAcountNumber;
	}

	public void setBillableBillingAcountNumber(Integer billableBillingAcountNumber) {
		this.billableBillingAcountNumber = billableBillingAcountNumber;
	}

	public Integer getProducibleInvoiceNumber() {
		return producibleInvoiceNumber;
	}

	public void setProducibleInvoiceNumber(Integer producibleInvoiceNumber) {
		this.producibleInvoiceNumber = producibleInvoiceNumber;
	}

	public BigDecimal getProducibleAmountWithoutTax() {
		return producibleAmountWithoutTax;
	}

	public void setProducibleAmountWithoutTax(BigDecimal producibleAmountWithoutTax) {
		this.producibleAmountWithoutTax = producibleAmountWithoutTax;
	}

	public BigDecimal getProducibleAmountTax() {
		return producibleAmountTax;
	}

	public void setProducibleAmountTax(BigDecimal producibleAmountTax) {
		this.producibleAmountTax = producibleAmountTax;
	}

	public Integer getInvoiceNumber() {
		return InvoiceNumber;
	}

	public void setInvoiceNumber(Integer invoiceNumber) {
		InvoiceNumber = invoiceNumber;
	}

	public BigDecimal getProducibleAmountWithTax() {
		return producibleAmountWithTax;
	}

	public void setProducibleAmountWithTax(BigDecimal producibleAmountWithTax) {
		this.producibleAmountWithTax = producibleAmountWithTax;
	}

	public void setPrAmountTax(BigDecimal prAmountTax) {
		this.prAmountTax = prAmountTax;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public Set<BillingRunList> getBillingRunLists() {
		return billingRunLists;
	}

	public void setBillingRunLists(Set<BillingRunList> billingRunLists) {
		this.billingRunLists = billingRunLists;
	}

	public BillingProcessTypesEnum getProcessType() {
		return processType;
	}

	public void setProcessType(BillingProcessTypesEnum processType) {
		this.processType = processType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public PreInvoicingReportsDTO getPreInvoicingReports() {
		return preInvoicingReports;
	}

	public void setPreInvoicingReports(PreInvoicingReportsDTO preInvoicingReports) {
		this.preInvoicingReports = preInvoicingReports;
	}

	public PostInvoicingReportsDTO getPostInvoicingReports() {
		return postInvoicingReports;
	}

	public void setPostInvoicingReports(PostInvoicingReportsDTO postInvoicingReports) {
		this.postInvoicingReports = postInvoicingReports;
	}

	public List<BillingAccount> getBillableBillingAccounts() {
		return billableBillingAccounts;
	}

	public void setBillableBillingAccounts(List<BillingAccount> selectedBillingAccounts) {
		this.billableBillingAccounts = selectedBillingAccounts;
	}

	public Set<RatedTransaction> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(Set<RatedTransaction> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public BigDecimal getPrAmountWithoutTax() {
		return prAmountWithoutTax;
	}

	public void setPrAmountWithoutTax(BigDecimal prAmountWithoutTax) {
		this.prAmountWithoutTax = prAmountWithoutTax;
	}

	public BigDecimal getPrAmountWithTax() {
		return prAmountWithTax;
	}

	public void setPrAmountWithTax(BigDecimal prAmountWithTax) {
		this.prAmountWithTax = prAmountWithTax;
	}

	public BigDecimal getPrAmountTax() {
		return prAmountTax;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}


	public String getSelectedBillingAccounts() {
		return selectedBillingAccounts;
	}

	public void setSelectedBillingAccounts(String selectedBillingAccounts) {
		this.selectedBillingAccounts = selectedBillingAccounts;
	}

	public Boolean getXmlInvoiceGenerated() {
		return xmlInvoiceGenerated;
	}

	public void setXmlInvoiceGenerated(Boolean xmlInvoiceGenerated) {
		this.xmlInvoiceGenerated = xmlInvoiceGenerated;
	}
	
	

}
