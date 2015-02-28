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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AccountEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentTermEnum;

@Entity
@Table(name = "BILLING_BILLING_ACCOUNT")
@NamedQueries({ @NamedQuery(name = "BillingAccount.listByBillingRunId", query = "SELECT b FROM BillingAccount b where b.billingRun.id=:billingRunId") })
public class BillingAccount extends AccountEntity {

	public static final String ACCOUNT_TYPE = "billingAccount.type";

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", length = 10)
	private AccountStatusEnum status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE")
	private Date statusDate;

	@Embedded
	private BankCoordinates bankCoordinates = new BankCoordinates();

	@Column(name = "EMAIL")
	// @Pattern(regexp = ".+@.+\\..{2,4}")
	private String email;

	@Column(name = "ELECTRONIC_BILLING")
	private Boolean electronicBilling = false;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NEXT_INVOICE_DATE")
	private Date nextInvoiceDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_DATE")
	private Date subscriptionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TERMINATION_DATE")
	private Date terminationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ACCOUNT_ID")
	private CustomerAccount customerAccount;

	@Column(name = "PAYMENT_METHOD")
	@Enumerated(EnumType.STRING)
	private PaymentMethodEnum paymentMethod = PaymentMethodEnum.CHECK;

	@Column(name = "PAYMENT_TERM")
	@Enumerated(EnumType.STRING)
	private PaymentTermEnum paymentTerm;

	@OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<UserAccount> usersAccounts = new ArrayList<UserAccount>();

	@OneToMany(mappedBy = "billingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Invoice> invoices = new ArrayList<Invoice>();

	@OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<BillingRunList> billingRunLists = new ArrayList<BillingRunList>();

	@OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<InvoiceAgregate> invoiceAgregates = new ArrayList<InvoiceAgregate>();

	@Column(name = "DISCOUNT_RATE", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal discountRate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_CYCLE")
	private BillingCycle billingCycle;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_COUNTRY_ID")
	private TradingCountry tradingCountry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_LANGUAGE_ID")
	private TradingLanguage tradingLanguage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BILLING_RUN")
	private BillingRun billingRun;

	@Column(name = "BR_AMOUNT_WITHOUT_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal brAmountWithoutTax;

	@Column(name = "BR_AMOUNT_WITH_TAX", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal brAmountWithTax;

	@Column(name = "INVOICE_PREFIX")
	private String invoicePrefix;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TERMIN_REASON_ID", nullable = true)
	private SubscriptionTerminationReason terminationReason;

	@OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
	private List<RatedTransaction> ratedTransactions;

	@ManyToOne
	@JoinColumn(name = "DISCOUNT_PLAN_ID")
	private DiscountPlan discountPlan;

	@OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
	@MapKey(name = "code")
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	// key is the counter template code
	Map<String, CounterInstance> counters = new HashMap<String, CounterInstance>();

	public List<UserAccount> getUsersAccounts() {
		return usersAccounts;
	}

	public void setUsersAccounts(List<UserAccount> usersAccounts) {
		this.usersAccounts = usersAccounts;
	}

	public PaymentMethodEnum getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public AccountStatusEnum getStatus() {
		return status;
	}

	public void setStatus(AccountStatusEnum status) {
		this.status = status;
		this.statusDate = new Date();
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Boolean getElectronicBilling() {
		return electronicBilling;
	}

	public void setElectronicBilling(Boolean electronicBilling) {
		this.electronicBilling = electronicBilling;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getNextInvoiceDate() {
		return nextInvoiceDate;
	}

	public void setNextInvoiceDate(Date nextInvoiceDate) {
		this.nextInvoiceDate = nextInvoiceDate;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public BankCoordinates getBankCoordinates() {
		return bankCoordinates;
	}

	public void setBankCoordinates(BankCoordinates bankCoordinates) {
		this.bankCoordinates = bankCoordinates;
	}

	public BigDecimal getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(BigDecimal discountRate) {
		this.discountRate = discountRate;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	public BillingCycle getBillingCycle() {
		return billingCycle;
	}

	public void setBillingCycle(BillingCycle billingCycle) {
		this.billingCycle = billingCycle;
	}

	@Override
	public String getAccountType() {
		return ACCOUNT_TYPE;
	}

	public BillingRun getBillingRun() {
		return billingRun;
	}

	public void setBillingRun(BillingRun billingRun) {
		this.billingRun = billingRun;
	}

	public String getInvoicePrefix() {
		return invoicePrefix;
	}

	public void setInvoicePrefix(String invoicePrefix) {
		this.invoicePrefix = invoicePrefix;
	}

	public UserAccount getDefaultUserAccount() {
		for (UserAccount userAccount : getUsersAccounts()) {
			if (userAccount.getDefaultLevel()) {
				return userAccount;
			}
		}
		return null;
	}

	public List<BillingRunList> getBillingRunLists() {
		return billingRunLists;
	}

	public void setBillingRunLists(List<BillingRunList> billingRunLists) {
		this.billingRunLists = billingRunLists;
	}

	public List<InvoiceAgregate> getInvoiceAgregates() {
		return invoiceAgregates;
	}

	public void setInvoiceAgregates(List<InvoiceAgregate> invoiceAgregates) {
		this.invoiceAgregates = invoiceAgregates;
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

	public SubscriptionTerminationReason getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(SubscriptionTerminationReason terminationReason) {
		this.terminationReason = terminationReason;
	}

	public BigDecimal getBrAmountWithoutTax() {
		return brAmountWithoutTax;
	}

	public void setBrAmountWithoutTax(BigDecimal brAmountWithoutTax) {
		this.brAmountWithoutTax = brAmountWithoutTax;
	}

	public BigDecimal getBrAmountWithTax() {
		return brAmountWithTax;
	}

	public void setBrAmountWithTax(BigDecimal brAmountWithTax) {
		this.brAmountWithTax = brAmountWithTax;
	}

	public PaymentTermEnum getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(PaymentTermEnum paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public List<RatedTransaction> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public DiscountPlan getDiscountPlan() {
		return discountPlan;
	}

	public void setDiscountPlan(DiscountPlan discountPlan) {
		this.discountPlan = discountPlan;
	}

	public Map<String, CounterInstance> getCounters() {
		return counters;
	}

	public void setCounters(Map<String, CounterInstance> counters) {
		this.counters = counters;
	}

}
