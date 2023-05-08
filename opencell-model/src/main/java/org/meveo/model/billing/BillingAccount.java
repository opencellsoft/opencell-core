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
package org.meveo.model.billing;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICounterEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IDiscountable;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.crm.IInvoicingMinimumApplicable;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.tax.TaxCategory;

/**
 * Billing account
 *
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@WorkflowedEntity
@CustomFieldEntity(cftCodePrefix = "BillingAccount", inheritCFValuesFrom = "customerAccount")
@Table(name = "billing_billing_account")
@DiscriminatorValue(value = "ACCT_BA")
@NamedQueries({ @NamedQuery(name = "BillingAccount.listIdsByBillingRunId", query = "SELECT b.id FROM BillingAccount b where b.billingRun.id=:billingRunId order by b.id"),
        @NamedQuery(name = "BillingAccount.listByBillingRun", query = "select b from BillingAccount b where b.billingRun.id=:billingRunId order by b.id"),
        @NamedQuery(name = "BillingAccount.PreInv", query = "SELECT b FROM BillingAccount b left join fetch b.customerAccount ca left join fetch ca.paymentMethods where b.billingRun.id=:billingRunId"),
        @NamedQuery(name = "BillingAccount.getMinimumAmountUsed", query = "select ba.minimumAmountEl from BillingAccount ba where ba.minimumAmountEl is not null"),
        @NamedQuery(name = "BillingAccount.getUnbilledByBC", query = "select ba.id from BillingAccount ba where ba.billingCycle=:billingCycle and (ba.nextInvoiceDate is null or ba.nextInvoiceDate<:maxNextInvoiceDate) and (ba.billingRun is null OR ba.billingRun<>:billingRun)"),
        @NamedQuery(name = "BillingAccount.getUnbilledByBCWithStartDate", query = "select ba.id from BillingAccount ba where ba.billingCycle=:billingCycle and (ba.nextInvoiceDate is null or ba.nextInvoiceDate>=:minNextInvoiceDate) and (ba.nextInvoiceDate is null or ba.nextInvoiceDate<:maxNextInvoiceDate) and (ba.billingRun is null OR ba.billingRun<>:billingRun)"),
        @NamedQuery(name = "BillingAccount.getBillingAccountsWithMinAmountELNotNullByBA", query = "select ba from BillingAccount ba where ba.minimumAmountEl is not null AND ba.status = org.meveo.model.billing.AccountStatusEnum.ACTIVE AND ba=:billingAccount"),
        @NamedQuery(name = "BillingAccount.getCountByParent", query = "select count(*) from BillingAccount ba where ba.customerAccount=:parent"),
        @NamedQuery(name = "BillingAccount.listByOpenILFromBillingRun", query = "select distinct b from InvoiceLine il join il.billingAccount b where il.billingRun=:billingRun and il.status='OPEN'"),
		@NamedQuery(name = "BillingAccount.getBillingAccountDetailsItems", query = "select distinct b.id, s.id, b.tradingLanguage.id, b.nextInvoiceDate, b.electronicBilling, ca.dueDateDelayEL, cc.exoneratedFromTaxes, cc.exonerationTaxEl, m.id, m.paymentType, m2.id, m2.paymentType, string_agg(concat(CAST(dpi.discountPlan.id as string),'|',CAST(dpi.startDate AS string),'|',CAST(dpi.endDate AS string)),','),"
				+ " sum(case when ao.transactionCategory = 'DEBIT' then ao.unMatchingAmount else (-1 * ao.unMatchingAmount) end) "
				+ " FROM BillingAccount b left join b.customerAccount ca left join ca.customer c left join c.customerCategory cc left join c.seller s "
				+ " left join ca.paymentMethods m "
				+ " left join ca.accountOperations ao "
				+ " left join b.paymentMethod m2 "
				+ " left join b.discountPlanInstances dpi "
				+ " where b.id IN (:baIDs) and (m is null or m.preferred=true) and (ao is null or (ao.matchingStatus in (:aoStatus)))"
				+ " group by b.id, s.id, b.tradingLanguage.id, b.nextInvoiceDate, b.electronicBilling, ca.dueDateDelayEL, cc.exoneratedFromTaxes, cc.exonerationTaxEl, m.id, m.paymentType, m2.id, m2.paymentType"
				+ " order by b.id"),
		@NamedQuery(name = "BillingAccount.getBillingAccountDetailsItemsLimitAOsByDate", query = "select distinct b.id, s.id, b.tradingLanguage.id, b.nextInvoiceDate, b.electronicBilling, ca.dueDateDelayEL, cc.exoneratedFromTaxes, cc.exonerationTaxEl, m.id, m.paymentType, m2.id, m2.paymentType, string_agg(concat(CAST(dpi.discountPlan.id as string),'|',CAST(dpi.startDate AS string),'|',CAST(dpi.endDate AS string)),','),"
				+ " sum(case when ao.transactionCategory = 'DEBIT' then ao.unMatchingAmount else (-1 * ao.unMatchingAmount) end) "
				+ " FROM BillingAccount b left join b.customerAccount ca left join ca.customer c left join c.customerCategory cc left join c.seller s "
				+ " left join ca.paymentMethods m "
				+ " left join ca.accountOperations ao on ao.customerAccount.id=ca.id and (ao.matchingStatus in (:aoStatus) and ao.dueDate<:dueDate) "
				+ " left join b.paymentMethod m2 "
				+ " left join b.discountPlanInstances dpi "
				+ " where b.id IN (:baIDs) and (m is null or m.preferred=true) "
				+ " group by b.id, s.id, b.tradingLanguage.id, b.nextInvoiceDate, b.electronicBilling, ca.dueDateDelayEL, cc.exoneratedFromTaxes, cc.exonerationTaxEl, m.id, m.paymentType, m2.id, m2.paymentType"
				+ " order by b.id"),
		@NamedQuery(name = "BillingAccount.getCountByCreditCategory", query = "select count(*) from BillingAccount ba where ba.id=:id and ba.customerAccount.creditCategory.id in (:creditCategoryIds)") })
public class BillingAccount extends AccountEntity implements IInvoicingMinimumApplicable, IBillableEntity, IWFEntity, IDiscountable, ICounterEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Account status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private AccountStatusEnum status;

    /**
     * Last status change timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate = new Date();

    /**
     * Use electronic billing?
     */
    @Type(type = "numeric_boolean")
    @Column(name = "electronic_billing")
    private boolean electronicBilling;

    /**
     * Next invoice date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_invoice_date")
    private Date nextInvoiceDate;

    /**
     * Account creation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate;

    /**
     * Account termination date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    /**
     * Customer account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id", nullable = false)
    private CustomerAccount customerAccount;

    /**
     * User accounts
     */
    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
    private List<UserAccount> usersAccounts = new ArrayList<>();

    /**
     * Invoices
     */
    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();

    /**
     * Discount rate
     */
    @Column(name = "discount_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountRate;

    /**
     * Billing cycle
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_cycle")
    private BillingCycle billingCycle;

    /**
     * Country for tax calculation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    /**
     * Invoice language
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_language_id")
    private TradingLanguage tradingLanguage;

    /**
     * Last billing run
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run")
    private BillingRun billingRun;

    /**
     * Total amount without tax in the last billing run
     */
    @Column(name = "br_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal brAmountWithoutTax;

    /**
     * Total amount with tax in the last billing run
     */
    @Column(name = "br_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal brAmountWithTax;

    /**
     * Invoice prefix
     */
    @Column(name = "invoice_prefix", length = 255)
    @Size(max = 255)
    private String invoicePrefix;

    /**
     * Subscription termination rules
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termin_reason_id")
    private SubscriptionTerminationReason terminationReason;

    /**
     * Counter instances. Key is the counter template code
     */
    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    private Map<String, CounterInstance> counters = new HashMap<>();

    /**
     * Invoicing threshold - do not invoice for a lesser amount
     */
    @Column(name = "invoicing_threshold")
    private BigDecimal invoicingThreshold;

    /** Corresponding invoice subcategory */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "minimum_invoice_sub_category_id")
    private InvoiceSubCategory minimumInvoiceSubCategory;

    /**
     * Allowed payment methods
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    /**
     * User accounts
     */
    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
    private List<Contract> contracts = new ArrayList<>();
    
    /**
     * A list of rated transactions
     */
    @Transient
    private List<RatedTransaction> minRatedTransactions;

    /**
     * Total invoicing amount without tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithoutTax;

    /**
     * Instance of discount plans. Once instantiated effectivity date is not affected when template is updated.
     */
    @OneToMany(mappedBy = "billingAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<DiscountPlanInstance> discountPlanInstances;

    /**
     * Total invoicing amount with tax
     */
    @Transient
    private BigDecimal totalInvoicingAmountWithTax;

    /**
     * Total invoicing tax amount
     */
    @Transient
    private BigDecimal totalInvoicingAmountTax;

    /**
     * Applicable discount plan. Replaced by discountPlanInstances. Now used only in GUI.
     */
    @Transient
    private DiscountPlan discountPlan;
    /**
     * Email Template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_template_id")
    private EmailTemplate emailTemplate;

    /**
     * Mailing type can be Manual, Auto, Batch
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "mailing_type")
    private MailingTypeEnum mailingType;

    /**
     * A list of emails separated by comma, That can be used a cc
     */
    @Column(name = "cced_emails", length = 2000)
    @Size(max = 2000)
    private String ccedEmails;

    @Column(name = "exemption_reason", length = 2000)
    @Size(max = 2000)
    private String exemptionReason;

    /**
     * Account tax category
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_category_id")
    private TaxCategory taxCategory;

    /**
     * The option on how to check the threshold.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "check_threshold")
    private ThresholdOptionsEnum checkThreshold;
    
    /**
     * A flag to indicate that account is exonerated from taxes
     */
    @Transient
    private Boolean exoneratedFromtaxes;

    /**
     * Tax category resolved
     */
    @Transient
    private TaxCategory taxCategoryResolved;
    
    /**
     * check threshold per entity?
     */
    @Type(type = "numeric_boolean")
    @Column(name = "threshold_per_entity")
    private boolean thresholdPerEntity;
    
    /**
     * list of tag attached
     */    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cpq_billing_account_tags", joinColumns = @JoinColumn(name = "billing_account_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private List<Tag> tags = new ArrayList<>();

	/**
	 * Currency of account
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trading_currency_id", nullable = false)
	private TradingCurrency tradingCurrency;

    @Transient
    private List<InvoiceLine> minInvoiceLines;

    /**
     * IsoIcd
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icd_id")
    private IsoIcd icdId;
    
    public IsoIcd getIcdId() {
        return icdId;
    }

    public void setIcdId(IsoIcd icdId) {
        this.icdId = icdId;
    }

    public boolean isThresholdPerEntity() {
    	return thresholdPerEntity;
	}
    
	public void setThresholdPerEntity(boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

    public List<UserAccount> getUsersAccounts() {
        return usersAccounts;
    }

    public void setUsersAccounts(List<UserAccount> usersAccounts) {
        this.usersAccounts = usersAccounts;
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
        if (this.status != status) {
            this.statusDate = new Date();
        }
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public boolean getElectronicBilling() {
        return electronicBilling;
    }

    public void setElectronicBilling(boolean electronicBilling) {
        this.electronicBilling = electronicBilling;
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

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
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

    public Map<String, CounterInstance> getCounters() {
        return counters;
    }

    public void setCounters(Map<String, CounterInstance> counters) {
        this.counters = counters;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (customerAccount != null) {
            return new ICustomFieldEntity[] { customerAccount };
        }
        return null;
    }

    @Override
    public BusinessEntity getParentEntity() {
        return customerAccount;
    }

    @Override
    public Class<? extends BusinessEntity> getParentEntityType() {
        return CustomerAccount.class;
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

    @Override
    public void anonymize(String code) {
        super.anonymize(code);
        if (isNotEmpty(this.usersAccounts)) {
            this.usersAccounts.forEach(ua -> ua.anonymize(code));
        }
    }

    public void setMinRatedTransactions(List<RatedTransaction> ratedTransactions) {
        minRatedTransactions = ratedTransactions;
    }

    public List<RatedTransaction> getMinRatedTransactions() {
        return minRatedTransactions;
    }

    public BigDecimal getTotalInvoicingAmountWithoutTax() {
        return totalInvoicingAmountWithoutTax;
    }

    public void setTotalInvoicingAmountWithoutTax(BigDecimal totalInvoicingAmountWithoutTax) {
        this.totalInvoicingAmountWithoutTax = totalInvoicingAmountWithoutTax;
    }

    public BigDecimal getTotalInvoicingAmountWithTax() {
        return totalInvoicingAmountWithTax;
    }

    public void setTotalInvoicingAmountWithTax(BigDecimal totalInvoicingAmountWithTax) {
        this.totalInvoicingAmountWithTax = totalInvoicingAmountWithTax;
    }

    public BigDecimal getTotalInvoicingAmountTax() {
        return totalInvoicingAmountTax;
    }

    public void setTotalInvoicingAmountTax(BigDecimal totalInvoicingAmountTax) {
        this.totalInvoicingAmountTax = totalInvoicingAmountTax;
    }

    public List<DiscountPlanInstance> getDiscountPlanInstances() {
        return discountPlanInstances;
    }

    @Override
    public List<DiscountPlanInstance> getAllDiscountPlanInstances() {
        return this.getDiscountPlanInstances();
    }

    @Override
    public void addDiscountPlanInstances(DiscountPlanInstance discountPlanInstance) {
        if (this.getDiscountPlanInstances() == null) {
            this.setDiscountPlanInstances(new ArrayList<>());
        }
        this.getDiscountPlanInstances().add(discountPlanInstance);
    }

    public void setDiscountPlanInstances(List<DiscountPlanInstance> discountPlanInstances) {
        this.discountPlanInstances = discountPlanInstances;
    }

    public DiscountPlan getDiscountPlan() {
        return discountPlan;
    }

    public void setDiscountPlan(DiscountPlan discountPlan) {
        this.discountPlan = discountPlan;
    }

    /**
     * Gets Email Template.
     *
     * @return Email Template.
     */
    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    /**
     * Sets Email template.
     *
     * @param emailTemplate the Email template.
     */
    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    /**
     * Gets Mailing Type.
     *
     * @return Mailing Type.
     */
    public MailingTypeEnum getMailingType() {
        return mailingType;
    }

    /**
     * Sets Mailing Type
     *
     * @param mailingType mailing type
     */
    public void setMailingType(MailingTypeEnum mailingType) {
        this.mailingType = mailingType;
    }

    /**
     * Gets cc Emails
     *
     * @return CC emails
     */
    public String getCcedEmails() {
        return ccedEmails;
    }

    /**
     * Sets cc Emails
     *
     * @param ccedEmails Cc Emails
     */
    public void setCcedEmails(String ccedEmails) {
        this.ccedEmails = ccedEmails;
    }

    /**
     * @return A flag to indicate that account is exonerated from taxes
     */
    public Boolean isExoneratedFromtaxes() {
        return exoneratedFromtaxes;
    }

    /**
     * @param exoneratedFromtaxes A flag to indicate that account is exonerated from taxes
     */
    public void setExoneratedFromtaxes(Boolean exoneratedFromtaxes) {
        this.exoneratedFromtaxes = exoneratedFromtaxes;
    }

    /**
     * @return the minimumInvoiceSubCategory
     */
    public InvoiceSubCategory getMinimumInvoiceSubCategory() {
        return minimumInvoiceSubCategory;
    }

    /**
     * @param minimumInvoiceSubCategory the minimumInvoiceSubCategory to set
     */
    public void setMinimumInvoiceSubCategory(InvoiceSubCategory minimumInvoiceSubCategory) {
        this.minimumInvoiceSubCategory = minimumInvoiceSubCategory;
    }

    /**
     * @return Account tax category
     */
    public TaxCategory getTaxCategory() {
        return taxCategory;
    }

    /**
     * @param taxCategory Account tax category
     */
    public void setTaxCategory(TaxCategory taxCategory) {
        this.taxCategory = taxCategory;
    }

    /**
     * @return Tax category resolved
     */
    public TaxCategory getTaxCategoryResolved() {
        return taxCategoryResolved;
    }

    /**
     * @param taxCategoryResolved Tax category resolved
     */
    public void setTaxCategoryResolved(TaxCategory taxCategoryResolved) {
        this.taxCategoryResolved = taxCategoryResolved;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return tags;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public TradingCurrency getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(TradingCurrency tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BillingAccount other = (BillingAccount) obj;
		return Objects.equals(id, other.id);
	}

    @Override
    public List<InvoiceLine> getMinInvoiceLines() {
        return minInvoiceLines;
    }

    @Override
    public void setMinInvoiceLines(List<InvoiceLine> invoiceLines) {
        this.minInvoiceLines = invoiceLines;
    }

    public List<UserAccount> getParentUserAccounts() {
        return getUsersAccounts()
                .stream()
                .filter(userAccount -> userAccount.getParentUserAccount() == null)
                .collect(toList());
    }
    
    public Seller getSeller() {
    	if(customerAccount==null) {
    		return null;
    	}
    	return customerAccount.getSeller();
    }

    public String getBillingAccountTradingLanguageCode() {
        return ofNullable(tradingLanguage)
                .map(TradingLanguage::getLanguageCode)
                .orElse(null);
    }

    public String getExemptionReason() {
        return exemptionReason;
    }

    public void setExemptionReason(String exemptionReason) {
        this.exemptionReason = exemptionReason;
    }
}