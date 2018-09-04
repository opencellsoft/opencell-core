/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
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
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.payments.CustomerAccount;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "BA", inheritCFValuesFrom = "customerAccount")
@ExportIdentifier({ "code" })
@Table(name = "billing_billing_account")
@DiscriminatorValue(value = "ACCT_BA")
@NamedQueries({ @NamedQuery(name = "BillingAccount.listIdsByBillingRunId", query = "SELECT b.id FROM BillingAccount b where b.billingRun.id=:billingRunId"),
        @NamedQuery(name = "BillingAccount.PreInv", query = "SELECT b FROM BillingAccount b left join fetch b.customerAccount ca left join fetch ca.paymentMethods where b.billingRun.id=:billingRunId") })
public class BillingAccount extends AccountEntity implements IBillableEntity {

    public static final String ACCOUNT_TYPE = ((DiscriminatorValue) BillingAccount.class.getAnnotation(DiscriminatorValue.class)).value();

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private AccountStatusEnum status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate = new Date();

    @Type(type = "numeric_boolean")
    @Column(name = "electronic_billing")
    private Boolean electronicBilling = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_invoice_date")
    private Date nextInvoiceDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "termination_date")
    private Date terminationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<UserAccount> usersAccounts = new ArrayList<>();

    @OneToMany(mappedBy = "billingAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();

    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<BillingRunList> billingRunLists = new ArrayList<>();

    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<InvoiceAgregate> invoiceAgregates = new ArrayList<>();

    @Column(name = "discount_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal discountRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_cycle")
    private BillingCycle billingCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_language_id")
    private TradingLanguage tradingLanguage;

    /**
     * A derived value from tradingLanguage.language.languageCode field
     */
    @Column(name = "trading_language_code", length = 3)
    @Size(max = 3)
    private String tradingLanguageCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run")
    private BillingRun billingRun;

    @Column(name = "br_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal brAmountWithoutTax;

    @Column(name = "br_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal brAmountWithTax;

    @Column(name = "invoice_prefix", length = 255)
    @Size(max = 255)
    private String invoicePrefix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "termin_reason_id")
    private SubscriptionTerminationReason terminationReason;

    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
    private List<RatedTransaction> ratedTransactions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_plan_id")
    private DiscountPlan discountPlan;

    @OneToMany(mappedBy = "billingAccount", fetch = FetchType.LAZY)
    @MapKey(name = "code")
    // TODO : Add orphanRemoval annotation.
    // @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    // key is the counter template code
    Map<String, CounterInstance> counters = new HashMap<String, CounterInstance>();

    @Column(name = "invoicing_threshold")
    private BigDecimal invoicingThreshold;

    /**
     * Expression to determine minimum amount value
     */
    @Column(name = "minimum_amount_el", length = 2000)
    @Size(max = 2000)
    private String minimumAmountEl;

    /**
     * Expression to determine minimum amount value - for Spark
     */
    @Column(name = "minimum_amount_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumAmountElSpark;


    /**
     * Expression to determine rated transaction description to reach minimum amount value
     */
    @Column(name = "minimum_label_el", length = 2000)
    @Size(max = 2000)
    private String minimumLabelEl;


    /**
     * Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    @Column(name = "minimum_label_el_sp", length = 2000)
    @Size(max = 2000)
    private String minimumLabelElSpark;

    @Transient
    private List<RatedTransaction> minRatedTransactions;
    
    @Transient
	private BigDecimal totalInvoicingAmountWithoutTax;
    
    @Transient
	private BigDecimal totalInvoicingAmountWithTax;
    
    @Transient
	private BigDecimal totalInvoicingAmountTax;

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

    public Boolean getElectronicBilling() {
        return electronicBilling;
    }

    public void setElectronicBilling(Boolean electronicBilling) {
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

    /**
     * @return A derived value from tradingLanguage.language.languageCode field
     */
    public String getTradingLanguageCode() {
        return tradingLanguageCode;
    }

    /**
     * @param tradingLanguageCode A derived value from tradingLanguage.language.languageCode field
     */
    public void setTradingLanguageCode(String tradingLanguageCode) {
        this.tradingLanguageCode = tradingLanguageCode;
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

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return new ICustomFieldEntity[] { customerAccount };
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

    /**
     * @return Expression to determine minimum amount value
     */
    public String getMinimumAmountEl() {
        return minimumAmountEl;
    }

    /**
     * @param minimumAmountEl Expression to determine minimum amount value
     */
    public void setMinimumAmountEl(String minimumAmountEl) {
        this.minimumAmountEl = minimumAmountEl;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value
     */
    public String getMinimumLabelEl() {
        return minimumLabelEl;
    }

    /**
     * @param minimumLabelEl Expression to determine rated transaction description to reach minimum amount value
     */
    public void setMinimumLabelEl(String minimumLabelEl) {
        this.minimumLabelEl = minimumLabelEl;
    }

    @Override
	public void anonymize(String code) {
		super.anonymize(code);
		getContactInformation().anonymize(code);
		if (getUsersAccounts() != null) {
			for (UserAccount ua : getUsersAccounts()) {
				ua.anonymize(code);
			}
		}
	}
    
    /**
     * @return Expression to determine minimum amount value - for Spark
     */
    public String getMinimumAmountElSpark() {
        return minimumAmountElSpark;
    }
	public void setMinRatedTransactions(List<RatedTransaction> ratedTransactions) {
		minRatedTransactions = ratedTransactions;
	}

    /**
     * @param minimumAmountElSpark Expression to determine minimum amount value - for Spark
     */
    public void setMinimumAmountElSpark(String minimumAmountElSpark) {
        this.minimumAmountElSpark = minimumAmountElSpark;
    }

    /**
     * @return Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public String getMinimumLabelElSpark() {
        return minimumLabelElSpark;
    }

    /**
     * @param minimumLabelElSpark Expression to determine rated transaction description to reach minimum amount value - for Spark
     */
    public void setMinimumLabelElSpark(String minimumLabelElSpark) {
        this.minimumLabelElSpark = minimumLabelElSpark;
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

}
