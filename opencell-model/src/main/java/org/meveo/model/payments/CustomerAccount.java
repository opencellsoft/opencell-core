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
package org.meveo.model.payments;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICounterEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.ThresholdOptionsEnum;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Customer;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.intcrm.AddressBook;

/**
 * Customer Account
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@WorkflowedEntity
@CustomFieldEntity(cftCodePrefix = "CustomerAccount", inheritCFValuesFrom = "customer")
@ExportIdentifier({ "code" })
@DiscriminatorValue(value = "ACCT_CA")
@Table(name = "ar_customer_account")
@NamedQueries({
		@NamedQuery(name = "CustomerAccount.listCAIdsForPayment", query = "Select ca.id  from CustomerAccount as ca, AccountOperation as ao,PaymentMethod as pm  where ao.transactionCategory='DEBIT' and "
				+ "                   ao.matchingStatus ='O' and ca.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and ao.customerAccount.id = ca.id and pm.paymentType =:paymentMethodIN  and "
				+ "                   ao.paymentMethod =:paymentMethodIN  and pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN  group by ca.id having sum(ao.unMatchingAmount) <> 0"),
		@NamedQuery(name = "CustomerAccount.listCAIdsForRefund", query = "Select ca.id  from CustomerAccount as ca, AccountOperation as ao,PaymentMethod as pm  where ao.transactionCategory='CREDIT' and "
				+ "                   ao.type not in ('P','AP') and ao.matchingStatus ='O' and ca.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and ao.customerAccount.id = ca.id and "
				+ "                   pm.paymentType =:paymentMethodIN  and ao.paymentMethod =:paymentMethodIN  and pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN group by ca.id having sum(ao.unMatchingAmount) <> 0"),
		@NamedQuery(name = "CustomerAccount.getMimimumRTUsed", query = "select ca.minimumAmountEl from CustomerAccount ca where ca.minimumAmountEl is not null"),
		@NamedQuery(name = "CustomerAccount.getCustomerAccountsWithMinAmountELNotNullByBA", query = "select ca from CustomerAccount ca where ca.minimumAmountEl is not null AND ca.status = org.meveo.model.billing.AccountStatusEnum.ACTIVE AND ca=:customerAccount"),
        @NamedQuery(name = "CustomerAccount.getCountByParent", query = "select count(*) from CustomerAccount ca where ca.customer=:parent")		
})

public class CustomerAccount extends AccountEntity implements IWFEntity, ICounterEntity {

	public static final String ACCOUNT_TYPE = ((DiscriminatorValue) CustomerAccount.class.getAnnotation(DiscriminatorValue.class)).value();

	private static final long serialVersionUID = 1L;

	/**
	 * Address book
	 */
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "crm_address_book_id")
	private AddressBook addressbook;

	/**
	 * Currency of account
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_currency_id")
	private TradingCurrency tradingCurrency;

	/**
	 * Account status
	 */
	@Column(name = "status", length = 10)
	@Enumerated(EnumType.STRING)
	private CustomerAccountStatusEnum status = CustomerAccountStatusEnum.ACTIVE;

	/**
	 * Credit category
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "credit_category_id")
	private CreditCategory creditCategory;

	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	/**
	 * Child billing accounts
	 */
	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<BillingAccount> billingAccounts = new ArrayList<>();

	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	/**
	 * Account operations associated with a Customer account
	 */
	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<AccountOperation> accountOperations = new ArrayList<>();

	/**
	 * List of ca's dunning docs
	 */
	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	List<DunningDocument> dunningDocuments = new ArrayList<>();

	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	/**
	 * Dunning actions associated with a Customer account
	 */
	@OneToMany(mappedBy = "customerAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<ActionDunning> actionDunnings = new ArrayList<>();

	/**
	 * Last status change timestamp
	 */
	@Column(name = "date_status")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateStatus = new Date();

	/**
	 * Last dunning level timestamp
	 */
	@Column(name = "date_dunning_level")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDunningLevel;

	/**
	 * Parent cusg
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	/**
	 * Dunning level
	 */
	@Column(name = "dunning_level")
	@Enumerated(EnumType.STRING)
	private DunningLevelEnum dunningLevel = DunningLevelEnum.R0;

	/**
	 * Password
	 */
	@Column(name = "password", length = 10)
	@Size(max = 10)
	private String password;

	/**
	 * Expression to calculate Invoice due date delay value
	 */
	@Column(name = "due_date_delay_el", length = 2000)
	@Size(max = 2000)
	private String dueDateDelayEL;

	/**
	 * Expression to calculate Invoice due date delay value - for Spark
	 */
	@Column(name = "due_date_delay_el_sp", length = 2000)
	@Size(max = 2000)
	private String dueDateDelayELSpark;

	/**
	 * Default language in invoices. Can be overriten in Billing account.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_language_id")
	private TradingLanguage tradingLanguage;

	/**
	 * Available payment methods
	 */
	@OneToMany(mappedBy = "customerAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PaymentMethod> paymentMethods = new ArrayList<PaymentMethod>();

	/**
	 * Is account excluded from payment
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "excluded_from_payment")
	private boolean excludedFromPayment;

	@Transient
	private Map<String, List<PaymentMethod>> auditedMethodPayments;

	/**
	 * Accumulator Counters instantiated on the customer account with Counter
	 * template code as a key.
	 */
	@OneToMany(mappedBy = "customerAccount", fetch = FetchType.LAZY)
	@MapKey(name = "code")
	private Map<String, CounterInstance> counters = new HashMap<>();

	/**
	 * The billable Entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "minimum_target_account_id")
	private BillingAccount minimumTargetAccount;

	/**
	 * Invoicing threshold - do not invoice for a lesser amount.
	 */
	@Column(name = "invoicing_threshold")
	private BigDecimal invoicingThreshold;

	/**
	 * The option on how to check the threshold.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "check_threshold")
	private ThresholdOptionsEnum checkThreshold;

	/**
	 * check threshold per entity?
	 */
	@Type(type = "numeric_boolean")
	@Column(name = "threshold_per_entity")
	private boolean thresholdPerEntity;

	@Transient
	private String dueBalance;

	/**
	 * This method is called implicitly by hibernate, used to enable encryption for
	 * custom fields of this entity
	 */
	@PrePersist
	@PreUpdate
	public void preUpdate() {
		if (cfValues != null) {
			cfValues.setEncrypted(true);
		}
		if (cfAccumulatedValues != null) {
			cfAccumulatedValues.setEncrypted(true);
		}
	}

	public CustomerAccount() {
		accountType = ACCOUNT_TYPE;
	}

	public boolean isThresholdPerEntity() {
		return thresholdPerEntity;
	}

	public void setThresholdPerEntity(boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

	public AddressBook getAddressbook() {
		return addressbook;
	}

	public void setAddressbook(AddressBook addressbook) {
		this.addressbook = addressbook;
	}

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
		if (this.status != status) {
			this.dateStatus = new Date();
		}
		this.status = status;
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

	public List<DunningDocument> getDunningDocuments() {
		return dunningDocuments;
	}

	public void setDunningDocuments(List<DunningDocument> dunningDocuments) {
		this.dunningDocuments = dunningDocuments;
	}

	public List<ActionDunning> getActionDunnings() {
		return actionDunnings;
	}

	public void setActionDunnings(List<ActionDunning> actionDunnings) {
		this.actionDunnings = actionDunnings;
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
	public ICustomFieldEntity[] getParentCFEntities() {
		if (customer != null) {
			return new ICustomFieldEntity[] { customer };
		}
		return null;
	}

	@Override
	public BusinessEntity getParentEntity() {
		return customer;
	}

	@Override
	public Class<? extends BusinessEntity> getParentEntityType() {
		return Customer.class;
	}

	/**
	 * @return Expression to calculate Invoice due date delay value
	 */
	public String getDueDateDelayEL() {
		return dueDateDelayEL;
	}

	/**
	 * @param dueDateDelayEL Expression to calculate Invoice due date delay value
	 */
	public void setDueDateDelayEL(String dueDateDelayEL) {
		this.dueDateDelayEL = dueDateDelayEL;
	}

	/**
	 * @return Expression to calculate Invoice due date delay value - for Spark
	 */
	public String getDueDateDelayELSpark() {
		return dueDateDelayELSpark;
	}

	/**
	 * @param dueDateDelayELSpark Expression to calculate Invoice due date delay
	 *                            value - for Spark
	 */
	public void setDueDateDelayELSpark(String dueDateDelayELSpark) {
		this.dueDateDelayELSpark = dueDateDelayELSpark;
	}

	public List<PaymentMethod> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public void addPaymentMethod(PaymentMethod paymentMethod) {
		if (paymentMethods == null) {
			paymentMethods = new ArrayList<>();
		}
		paymentMethods.add(paymentMethod);
		addPaymentMethodToAudit(new Object() {
		}.getClass().getEnclosingMethod().getName(), paymentMethod);
	}

	public boolean isExcludedFromPayment() {
		return excludedFromPayment;
	}

	public void setExcludedFromPayment(boolean excludedFromPayment) {
		this.excludedFromPayment = excludedFromPayment;
	}

	/**
	 * Get a payment method marked as preferred
	 *
	 * @return Payment method marked as preferred
	 */
	public PaymentMethod getPreferredPaymentMethod() {
		if (getPaymentMethods() != null) {
			for (PaymentMethod paymentMethod : getPaymentMethods()) {
				if (paymentMethod.isPreferred()) {
					return paymentMethod;
				}
			}

			if (!getPaymentMethods().isEmpty() && !getPaymentMethods().get(0).isDisabled()) {
				return getPaymentMethods().get(0);
			}
		}

		return null;
	}

	public PaymentMethodEnum getPreferredPaymentMethodType() {
		PaymentMethod paymentMethod = getPreferredPaymentMethod();
		if (paymentMethod != null) {
			return paymentMethod.getPaymentType();
		}

		return null;
	}

	public List<PaypalPaymentMethod> getPaypalPaymentMethods() {
		List<PaypalPaymentMethod> paypalPaymentMethods = new ArrayList<>();
		if (getPaymentMethods() != null) {
			for (PaymentMethod paymentMethod : getPaymentMethods()) {
				if (paymentMethod instanceof PaypalPaymentMethod) {
					paypalPaymentMethods.add((PaypalPaymentMethod) paymentMethod);
				}
			}
		}
		return paypalPaymentMethods;
	}

	/**
	 * Get a list of card type payment methods
	 *
	 * @param noTokenOnly Retrieve only those that don't have a token
	 * @return A list of card type payment methods
	 */
	public List<CardPaymentMethod> getCardPaymentMethods(boolean noTokenOnly) {

		List<CardPaymentMethod> cardPaymentMethods = new ArrayList<>();

		if (getPaymentMethods() != null) {
			for (PaymentMethod paymentMethod : getPaymentMethods()) {
				if (paymentMethod instanceof CardPaymentMethod) {
					if (noTokenOnly && ((CardPaymentMethod) paymentMethod).getTokenId() == null) {
						cardPaymentMethods.add((CardPaymentMethod) paymentMethod);
					} else if (!noTokenOnly) {
						cardPaymentMethods.add((CardPaymentMethod) paymentMethod);
					}
				}
			}
		}

		return cardPaymentMethods;
	}

	public List<DDPaymentMethod> getDDPaymentMethods() {
		List<DDPaymentMethod> ddPaymentMethods = new ArrayList<>();
		if (getPaymentMethods() != null) {
			for (PaymentMethod paymentMethod : getPaymentMethods()) {
				if (paymentMethod instanceof DDPaymentMethod) {
					ddPaymentMethods.add((DDPaymentMethod) paymentMethod);
				}
			}
		}
		return ddPaymentMethods;
	}

	public List<WirePaymentMethod> getWirePaymentMethods() {
		List<WirePaymentMethod> wirePaymentMethods = new ArrayList<>();
		if (getPaymentMethods() != null) {
			for (PaymentMethod paymentMethod : getPaymentMethods()) {
				if (paymentMethod instanceof WirePaymentMethod) {
					wirePaymentMethods.add((WirePaymentMethod) paymentMethod);
				}
			}
		}
		return wirePaymentMethods;
	}

	public List<CheckPaymentMethod> getCheckPaymentMethods() {
		List<CheckPaymentMethod> checkPaymentMethods = new ArrayList<>();
		if (getPaymentMethods() != null) {
			for (PaymentMethod paymentMethod : getPaymentMethods()) {
				if (paymentMethod instanceof CheckPaymentMethod) {
					checkPaymentMethods.add((CheckPaymentMethod) paymentMethod);
				}
			}
		}
		return checkPaymentMethods;
	}

	public List<StripePaymentMethod> getStripePaymentMethods() {
		List<StripePaymentMethod> stripePaymentMethods = new ArrayList<>();
		if (getPaymentMethods() != null) {
			for (PaymentMethod paymentMethod : getPaymentMethods()) {
				if (paymentMethod instanceof StripePaymentMethod) {
					stripePaymentMethods.add((StripePaymentMethod) paymentMethod);
				}
			}
		}
		return stripePaymentMethods;
	}

	/**
	 * Mark currently valid card payment as preferred
	 *
	 * @return A currently valid card payment
	 */
	public PaymentMethod markCurrentlyValidCardPaymentAsPreferred() {
		if (getPaymentMethods() == null) {
			return null;
		}
		PaymentMethod matchedPaymentMethod = null;
		for (PaymentMethod paymentMethod : getPaymentMethods()) {
			if (paymentMethod.getClass() == CardPaymentMethod.class) {
				if (((CardPaymentMethod) paymentMethod).isValidForDate(new Date()) && !paymentMethod.isDisabled()) {
					paymentMethod.setPreferred(true);
					matchedPaymentMethod = paymentMethod;
					break;
				}
			}
		}
		if (matchedPaymentMethod == null) {
			return null;
		}
		for (PaymentMethod paymentMethod : getPaymentMethods()) {
			if (!paymentMethod.equals(matchedPaymentMethod)) {
				paymentMethod.setPreferred(false);
			}
		}
		return matchedPaymentMethod;
	}

	/**
	 * Ensure that one and only one payment method is marked as preferred. If
	 * currently preferred payment method is of type card, but expired, advance to a
	 * currently valid card payment method if possible. If not possible - leave as
	 * it is. If no preferred payment method was found - mark the first payment
	 * method as preferred.
	 *
	 * @return A preferred payment method
	 */

	public PaymentMethod ensureOnePreferredPaymentMethod() {
		if (getPaymentMethods() == null) {
			return null;
		}
		if (getPaymentMethods().size() == 1) {
			getPaymentMethods().get(0).setPreferred(true);
			return getPaymentMethods().get(0);
		}

		PaymentMethod paymentMethodMatched = null;

		for (PaymentMethod paymentMethod : paymentMethods) {

			if (paymentMethod.getClass().getSimpleName().contains("PaymentMethod")) {
				return null;
			}

			// Ensure that only one payment method is preferred (the first one found, or in
			// case of CC - the first valid if currently preffered CC is expired)
			if (paymentMethod.isPreferred()) {
				// If currently preferred payment method has expired, select a new valid card
				// payment method if available. If not available - continue as is
				if (paymentMethod.getClass() == CardPaymentMethod.class && !((CardPaymentMethod) paymentMethod).isValidForDate(new Date())) {
					paymentMethodMatched = markCurrentlyValidCardPaymentAsPreferred();
					if (paymentMethodMatched == null) {
						paymentMethodMatched = paymentMethod;
					}
					break;
				}
				paymentMethodMatched = paymentMethod;
				break;
			}
		}

		if (paymentMethodMatched != null && !paymentMethodMatched.getClass().getSimpleName().equalsIgnoreCase("PaymentMethod")) {
			for (PaymentMethod paymentMethod : paymentMethods) {
				if (!paymentMethod.equals(paymentMethodMatched)) {
					paymentMethod.setPreferred(false);
				}
			}

			return paymentMethodMatched;
		}

		// As no preferred payment method was found, mark the first available payment
		// method as preferred

		if (!getPaymentMethods().get(0).isDisabled()) {
			getPaymentMethods().get(0).setPreferred(true);
			return getPaymentMethods().get(0);
		}
		return null;

	}

	/**
	 * Check if no more valid Card paymentMethod.
	 *
	 * @return true if no more valid card.
	 */
	public boolean isNoMoreValidCard() {
		for (CardPaymentMethod card : getCardPaymentMethods(false)) {
			if (!card.isDisabled() && card.isValidForDate(new Date())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void anonymize(String code) {
		super.anonymize(code);
		if (isNotEmpty(this.billingAccounts)) {
			this.billingAccounts.forEach(ba -> ba.anonymize(code));
		}
	}

	public Map<String, List<PaymentMethod>> getAuditedMethodPayments() {
		if (auditedMethodPayments == null) {
			auditedMethodPayments = new HashMap<>();
		}
		return auditedMethodPayments;
	}

	public void setAuditedMethodPayments(Map<String, List<PaymentMethod>> auditedMethodPayments) {
		this.auditedMethodPayments = auditedMethodPayments;
	}

	/**
	 * Add payment method action to auditing
	 *
	 * @param action        the action related to payment method
	 * @param paymentMethod the payment method
	 */
	public void addPaymentMethodToAudit(String action, PaymentMethod paymentMethod) {
		if (getAuditedMethodPayments().containsKey(action)) {
			if (!getAuditedMethodPayments().get(action).contains(paymentMethod)) {
				getAuditedMethodPayments().get(action).add(paymentMethod);
			} else {
				getAuditedMethodPayments().get(action).set(getAuditedMethodPayments().get(action).indexOf(paymentMethod), paymentMethod);
			}
		} else {
			List<PaymentMethod> PaymentMethods = new ArrayList<>();
			PaymentMethods.add(paymentMethod);
			getAuditedMethodPayments().put(action, PaymentMethods);
		}
	}

	/**
	 * Gets a counters map.
	 *
	 * @return a counters map
	 */
	@Override
	public Map<String, CounterInstance> getCounters() {
		return counters;
	}

	/**
	 * @return the invoicingThreshold
	 */
	public BigDecimal getInvoicingThreshold() {
		return invoicingThreshold;
	}

	/**
	 * @param invoicingThreshold the invoicingThreshold to set
	 */
	public void setInvoicingThreshold(BigDecimal invoicingThreshold) {
		this.invoicingThreshold = invoicingThreshold;
	}

	/**
	 * Gets the threshold option.
	 *
	 * @return the threshold option
	 */
	public ThresholdOptionsEnum getCheckThreshold() {
		return checkThreshold;
	}

	/**
	 * Sets the threshold option.
	 *
	 * @param checkThreshold the threshold option
	 */
	public void setCheckThreshold(ThresholdOptionsEnum checkThreshold) {
		this.checkThreshold = checkThreshold;
	}

	public BillingAccount getMinimumTargetAccount() {
		return minimumTargetAccount;
	}

	public void setMinimumTargetAccount(BillingAccount minimumTargetAccount) {
		this.minimumTargetAccount = minimumTargetAccount;
	}

	public void setDueBalance(String dueBalance) {
		this.dueBalance = dueBalance;
	}

	public String getDueBalance() {
		return dueBalance;
	}
}