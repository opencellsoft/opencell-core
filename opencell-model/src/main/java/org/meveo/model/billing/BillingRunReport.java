package org.meveo.model.billing;

import static java.math.BigDecimal.ZERO;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.TemporalType.TIMESTAMP;
import static org.meveo.model.billing.BillingRunReportTypeEnum.OPEN_RATED_TRANSACTIONS;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "billing_run_report")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@Parameter(name = "sequence_name", value = "bill_run_report_seq"), })
@NamedQueries({
        @NamedQuery(name = "BillingRunReport.findAssociatedReportToBillingRun", query = "select report from BillingRunReport report where report.billingRun.id=:billingRunId ORDER BY report.id ASC")
})
public class BillingRunReport extends AuditableEntity {

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "billing_run_id")
    @NotNull
    private BillingRun billingRun;

    @Enumerated(value = STRING)
    @Column(name = "type")
    @NotNull
    private BillingRunReportTypeEnum type;

    @Temporal(TIMESTAMP)
    @Column(name = "creation_date")
    @NotNull
    private Date creationDate;

    @Column(name = "billing_accounts_count", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal billingAccountsCount;

    @Column(name = "subscriptions_count", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal subscriptionsCount;

    @Column(name = "rated_transactions_count", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal ratedTransactionsCount;

    @Column(name = "total_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal totalAmountWithoutTax;

    @Column(name = "one_shot_transactions_count", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal oneShotTransactionsCount;

    @Column(name = "recurring_transactions_count", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal recurringTransactionsCount;

    @Column(name = "usage_transactions_count", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal usageTransactionsCount;

    @Column(name = "one_shot_total_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal oneShotTotalAmountWithoutTax;

    @Column(name = "recurring_total_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal recurringTotalAmountWithoutTax;

    @Column(name = "usage_total_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal usageTotalAmountWithoutTax;

    @OneToMany(mappedBy = "billingRunReport", fetch = LAZY)
    private List<BillingAccountAmount> topBillingAccounts;

    @OneToMany(mappedBy = "billingRunReport", fetch = LAZY)
    private List<OfferAmount> topOffers;

    @OneToMany(mappedBy = "billingRunReport", fetch = LAZY)
    private List<ProductAmount> topProducts;

    @OneToMany(mappedBy = "billingRunReport", fetch = LAZY)
    private List<AccountingArticleAmount> topAccountingArticles;

    @OneToMany(mappedBy = "billingRunReport", fetch = LAZY)
    private List<SubscriptionAmount> topSubscriptions;

    public BillingRunReport() {
        this.totalAmountWithoutTax = ZERO;
        this.recurringTotalAmountWithoutTax = ZERO;
        this.usageTotalAmountWithoutTax = ZERO;
        this.oneShotTotalAmountWithoutTax = ZERO;
        this.oneShotTransactionsCount = ZERO;
        this.recurringTransactionsCount = ZERO;
        this.usageTransactionsCount = ZERO;
        this.subscriptionsCount = ZERO;
        this.billingAccountsCount = ZERO;
        this.ratedTransactionsCount = ZERO;
        this.creationDate = new Date();
        this.type = OPEN_RATED_TRANSACTIONS;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public BillingRunReportTypeEnum getType() {
        return type;
    }

    public void setType(BillingRunReportTypeEnum type) {
        this.type = type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getBillingAccountsCount() {
        return billingAccountsCount;
    }

    public void setBillingAccountsCount(BigDecimal billingAccountsCount) {
        this.billingAccountsCount = billingAccountsCount;
    }

    public BigDecimal getSubscriptionsCount() {
        return subscriptionsCount;
    }

    public void setSubscriptionsCount(BigDecimal subscriptionAccountsCount) {
        this.subscriptionsCount = subscriptionAccountsCount;
    }

    public BigDecimal getRatedTransactionsCount() {
        return ratedTransactionsCount;
    }

    public void setRatedTransactionsCount(BigDecimal ratedTransactionAccountsCount) {
        this.ratedTransactionsCount = ratedTransactionAccountsCount;
    }

    public BigDecimal getTotalAmountWithoutTax() {
        return totalAmountWithoutTax;
    }

    public void setTotalAmountWithoutTax(BigDecimal totalAmountWithoutTax) {
        this.totalAmountWithoutTax = totalAmountWithoutTax;
    }

    public BigDecimal getOneShotTransactionsCount() {
        return oneShotTransactionsCount;
    }

    public void setOneShotTransactionsCount(BigDecimal oneShotTransactionsCount) {
        this.oneShotTransactionsCount = oneShotTransactionsCount;
    }
    
    public void addOneShotTransactionsCount(BigDecimal oneShotTransactionsCountToAdd) {
        if (this.oneShotTransactionsCount == null) {
        	this.oneShotTransactionsCount = ZERO;
        }
        if (oneShotTransactionsCountToAdd != null) {
        	this.oneShotTransactionsCount = this.oneShotTransactionsCount.add(oneShotTransactionsCountToAdd);
        }
    }

    public BigDecimal getRecurringTransactionsCount() {
        return recurringTransactionsCount;
    }

    public void setRecurringTransactionsCount(BigDecimal recurringTransactionsCount) {
        this.recurringTransactionsCount = recurringTransactionsCount;
    }

    public BigDecimal getUsageTransactionsCount() {
        return usageTransactionsCount;
    }

    public void setUsageTransactionsCount(BigDecimal usageTransactionsCount) {
        this.usageTransactionsCount = usageTransactionsCount;
    }

    public BigDecimal getOneShotTotalAmountWithoutTax() {
        return oneShotTotalAmountWithoutTax;
    }

    public void setOneShotTotalAmountWithoutTax(BigDecimal oneShotTotalAmountWithoutTax) {
        this.oneShotTotalAmountWithoutTax = oneShotTotalAmountWithoutTax;
    }

    public void addOneShotTotalAmountWithoutTax(BigDecimal oneShotTotalAmountWithoutTaxToAdd) {
        if (this.oneShotTotalAmountWithoutTax == null) {
        	this.oneShotTotalAmountWithoutTax = ZERO;
        }
        if (oneShotTotalAmountWithoutTaxToAdd != null) {
        	this.oneShotTotalAmountWithoutTax = this.oneShotTotalAmountWithoutTax.add(oneShotTotalAmountWithoutTaxToAdd);
        }
    }
    
    public BigDecimal getRecurringTotalAmountWithoutTax() {
        return recurringTotalAmountWithoutTax;
    }

    public void setRecurringTotalAmountWithoutTax(BigDecimal recurringTotalAmountWithoutTax) {
        this.recurringTotalAmountWithoutTax = recurringTotalAmountWithoutTax;
    }

    public BigDecimal getUsageTotalAmountWithoutTax() {
        return usageTotalAmountWithoutTax;
    }

    public void setUsageTotalAmountWithoutTax(BigDecimal usageTotalAmountWithoutTax) {
        this.usageTotalAmountWithoutTax = usageTotalAmountWithoutTax;
    }

    public List<BillingAccountAmount> getTopBillingAccounts() {
        return topBillingAccounts;
    }

    public void setTopBillingAccounts(List<BillingAccountAmount> topBillingAccounts) {
        this.topBillingAccounts = topBillingAccounts;
    }

    public List<OfferAmount> getTopOffers() {
        return topOffers;
    }

    public void setTopOffers(List<OfferAmount> topOffers) {
        this.topOffers = topOffers;
    }

    public List<ProductAmount> getTopProducts() {
        return topProducts;
    }

    public void setTopProducts(List<ProductAmount> topProducts) {
        this.topProducts = topProducts;
    }

    public List<AccountingArticleAmount> getTopAccountingArticles() {
        return topAccountingArticles;
    }

    public void setTopAccountingArticles(List<AccountingArticleAmount> topAccountingArticles) {
        this.topAccountingArticles = topAccountingArticles;
    }

    public List<SubscriptionAmount> getTopSubscriptions() {
        return topSubscriptions;
    }

    public void setTopSubscriptions(List<SubscriptionAmount> topSubscriptions) {
        this.topSubscriptions = topSubscriptions;
    }
}
