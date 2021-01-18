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
import org.meveo.model.IBillableEntity;
import org.meveo.model.ICounterEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IDiscountable;
import org.meveo.model.IWFEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.communication.email.MailingTypeEnum;
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
@ExportIdentifier({ "code" })
@Table(name = "billing_billing_account")
@DiscriminatorValue(value = "ACCT_BA")
@NamedQueries({ @NamedQuery(name = "BillingAccount.listIdsByBillingRunId", query = "SELECT b.id FROM BillingAccount b where b.billingRun.id=:billingRunId order by b.id"),
        @NamedQuery(name = "BillingAccount.listByBillingRun", query = "select b from BillingAccount b where b.billingRun.id=:billingRunId order by b.id"),
        @NamedQuery(name = "BillingAccount.PreInv", query = "SELECT b FROM BillingAccount b left join fetch b.customerAccount ca left join fetch ca.paymentMethods where b.billingRun.id=:billingRunId"),
        @NamedQuery(name = "BillingAccount.getMimimumRTUsed", query = "select ba.minimumAmountEl from BillingAccount ba where ba.minimumAmountEl is not null"),
        @NamedQuery(name = "BillingAccount.getUnbilledByBC", query = "select ba.id from BillingAccount ba where ba.billingCycle=:billingCycle and (ba.nextInvoiceDate is null or ba.nextInvoiceDate<:maxNextInvoiceDate) and (ba.billingRun is null OR ba.billingRun<>:billingRun)"),
        @NamedQuery(name = "BillingAccount.getUnbilledByBCWithStartDate", query = "select ba.id from BillingAccount ba where ba.billingCycle=:billingCycle and (ba.nextInvoiceDate is null or ba.nextInvoiceDate>=:minNextInvoiceDate) and (ba.nextInvoiceDate is null or ba.nextInvoiceDate<:maxNextInvoiceDate) and (ba.billingRun is null OR ba.billingRun<>:billingRun)"),
        @NamedQuery(name = "BillingAccount.getBillingAccountsWithMinAmountELNotNullByBA", query = "select ba from BillingAccount ba where ba.minimumAmountEl is not null AND ba.status = org.meveo.model.billing.AccountStatusEnum.ACTIVE AND ba=:billingAccount"),
        @NamedQuery(name = "BillingAccount.getCountByParent", query = "select count(*) from BillingAccount ba where ba.customerAccount=:parent") })
public class BillingAccount extends AccountEntity implements IBillableEntity, IWFEntity, IDiscountable, ICounterEntity {

    public static final String ACCOUNT_TYPE = ((DiscriminatorValue) BillingAccount.class.getAnnotation(DiscriminatorValue.class)).value();

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
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    /**
     * User accounts
     */
    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<UserAccount> usersAccounts = new ArrayList<>();

    /**
     * Invoices
     */
    @OneToMany(mappedBy = "billingAccount", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();

    /**
     * For GDPR - Billing runs
     */
   @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<BillingRunList> billingRunLists = new ArrayList<>();

    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    /**
     * For GDPR - Invoice aggregates
     */
    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<InvoiceAgregate> invoiceAgregates = new ArrayList<>();

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

    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    // key is the counter template code
    /**
     * Counter instances
     */
    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    Map<String, CounterInstance> counters = new HashMap<String, CounterInstance>();

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
     * For GDPR - Instance of discount plans. Once instantiated effectivity date is not affected when template is updated.
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

    public boolean isThresholdPerEntity() {
    	return thresholdPerEntity;
	}
    
	public void setThresholdPerEntity(boolean thresholdPerEntity) {
		this.thresholdPerEntity = thresholdPerEntity;
	}

	public BillingAccount() {
        accountType = ACCOUNT_TYPE;
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
}