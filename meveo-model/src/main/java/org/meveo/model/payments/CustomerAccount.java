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
package org.meveo.model.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AccountEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.shared.ContactInformation;

/**
 * Customer Account entity.
 */
@Entity
@CustomFieldEntity(accountLevel=AccountLevelEnum.CA)
@ExportIdentifier({ "code", "provider" })
@DiscriminatorValue(value = "ACCT_CA")
@Table(name = "AR_CUSTOMER_ACCOUNT")
public class CustomerAccount extends AccountEntity {

    public static final String ACCOUNT_TYPE = ((DiscriminatorValue) CustomerAccount.class.getAnnotation(DiscriminatorValue.class)).value();

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

	@ManyToOne
	@JoinColumn(name = "CREDIT_CATEGORY_ID")
	private CreditCategory creditCategory;

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

	@Column(name = "MANDATE_IDENTIFICATION", length = 35)
	private String mandateIdentification = "";

	@Column(name = "MANDATE_DATE")
	@Temporal(TemporalType.DATE)
	private Date mandateDate;
	
	public CustomerAccount() {
        accountType = ACCOUNT_TYPE;
    }
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRADING_LANGUAGE_ID")
	private TradingLanguage tradingLanguage;
	
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

	public String getMandateIdentification() {
		return mandateIdentification;
	}

	public void setMandateIdentification(String mandateIdentification) {
		this.mandateIdentification = mandateIdentification;
	}

	public Date getMandateDate() {
		return mandateDate;
	}

	public void setMandateDate(Date mandateDate) {
		this.mandateDate = mandateDate;
	}

	public TradingLanguage getTradingLanguage() {
		return tradingLanguage;
	}

	public void setTradingLanguage(TradingLanguage tradingLanguage) {
		this.tradingLanguage = tradingLanguage;
	}

	public CreditCategory getCreditCategory() {
		return creditCategory;
	}

	public void setCreditCategory(CreditCategory creditCategory) {
		this.creditCategory = creditCategory;
	}

    @Override
    public ICustomFieldEntity getParentCFEntity() {
        return customer;
    }	
}