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
package org.meveo.model.payments;

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
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Customer;
import org.meveo.model.shared.ContactInformation;

/**
 * Customer Account entity.
 */
@Entity
@Table(name = "AR_CUSTOMER_ACCOUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "AR_CUSTOMER_ACCOUNT_SEQ")
public class CustomerAccount extends AccountEntity {

	public static final String ACCOUNT_TYPE = "customerAccount.type";

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_CURRENCY_ID")
	private TradingCurrency tradingCurrency;

	@Column(name = "STATUS", length = 10)
	@Enumerated(EnumType.STRING)
	private CustomerAccountStatusEnum status = CustomerAccountStatusEnum.ACTIVE;

	@Column(name = "PAYMENT_METHOD", length = 20)
	@Enumerated(EnumType.STRING)
	private PaymentMethodEnum paymentMethod;

	@Column(name = "CREDIT_CATEGORY")
	@Enumerated(EnumType.STRING)
	private CreditCategoryEnum creditCategory;

	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<BillingAccount> billingAccounts = new ArrayList<BillingAccount>();

	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<AccountOperation> accountOperations = new ArrayList<AccountOperation>();

	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ActionDunning> actionDunnings = new ArrayList<ActionDunning>();

	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.ALL)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<DDRequestItem> dDRequestItems = new ArrayList<DDRequestItem>();

	@Column(name = "DATE_STATUS")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateStatus;

	@Column(name = "DATE_DUNNING_LEVEL")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDunningLevel;

	@Embedded
	private ContactInformation contactInformation = new ContactInformation();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;

	@Column(name = "DUNNING_LEVEL")
	@Enumerated(EnumType.STRING)
	private DunningLevelEnum dunningLevel = DunningLevelEnum.R0;

	@Column(name = "PASSWORD", length = 10)
	private String password = "";

	public Customer getCustomer() {
		return customer;
	}

	public TradingCurrency getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(TradingCurrency tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public CustomerAccountStatusEnum getStatus() {
		return status;
	}

	public void setStatus(CustomerAccountStatusEnum status) {
		this.status = status;
	}

	public PaymentMethodEnum getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public CreditCategoryEnum getCreditCategory() {
		return creditCategory;
	}

	public void setCreditCategory(CreditCategoryEnum creditCategory) {
		this.creditCategory = creditCategory;
	}

	public Date getDateStatus() {
		return dateStatus;
	}

	public void setDateStatus(Date dateStatus) {
		this.dateStatus = dateStatus;
	}

	public List<BillingAccount> getBillingAccounts() {
		return billingAccounts;
	}

	public void setBillingAccounts(List<BillingAccount> billingAccounts) {
		this.billingAccounts = billingAccounts;
	}

	public List<AccountOperation> getAccountOperations() {
		return accountOperations;
	}

	public void setAccountOperations(List<AccountOperation> accountOperations) {
		this.accountOperations = accountOperations;
	}

	public ContactInformation getContactInformation() {
		if (contactInformation == null) {
			contactInformation = new ContactInformation();
		}
		return contactInformation;
	}

	public void setContactInformation(ContactInformation contactInformation) {
		this.contactInformation = contactInformation;
	}

	@Override
	public String getAccountType() {
		return ACCOUNT_TYPE;
	}

	public void setDunningLevel(DunningLevelEnum dunningLevel) {
		this.dunningLevel = dunningLevel;
	}

	public DunningLevelEnum getDunningLevel() {
		return dunningLevel;
	}

	public Date getDateDunningLevel() {
		return dateDunningLevel;
	}

	public void setDateDunningLevel(Date dateDunningLevel) {
		this.dateDunningLevel = dateDunningLevel;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BillingAccount getDefaultBillingAccount() {
		for (BillingAccount billingAccount : getBillingAccounts()) {
			if (billingAccount.getDefaultLevel()) {
				return billingAccount;
			}
		}
		return null;
	}

	public List<ActionDunning> getActionDunnings() {
		return actionDunnings;
	}

	public void setActionDunnings(List<ActionDunning> actionDunnings) {
		this.actionDunnings = actionDunnings;
	}

	public List<DDRequestItem> getdDRequestItems() {
		return dDRequestItems;
	}

	public void setdDRequestItems(List<DDRequestItem> dDRequestItems) {
		this.dDRequestItems = dDRequestItems;
	}

}
