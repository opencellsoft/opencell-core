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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

import org.meveo.model.AccountEntity;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethodEnum;

@Entity
@Table(name = "BILLING_BILLING_ACCOUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_BILLING_ACCOUNT_SEQ")
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
	private PaymentMethodEnum paymentMethod=PaymentMethodEnum.CHECK;

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

	@Column(name = "DISCOUNT_RATE", precision = 19, scale = 8)
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
	
	@Column(name = "BR_AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
	private BigDecimal brAmountWithoutTax;

	@Column(name = "BR_AMOUNT_WITH_TAX", precision = 23, scale = 12)
	private BigDecimal brAmountWithTax;

	@Column(name = "INVOICE_PREFIX")
	private String invoicePrefix;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TERMIN_REASON_ID", nullable = true)
	private SubscriptionTerminationReason terminationReason;

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
		if (bankCoordinates == null) {
			bankCoordinates = new BankCoordinates();
		}
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
	
	
}
