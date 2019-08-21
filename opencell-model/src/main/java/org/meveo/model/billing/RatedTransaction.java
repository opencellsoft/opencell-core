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
import java.math.RoundingMode;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.rating.EDR;

/**
 * Rated transaction - usually corresponds 1-1 to Wallet operation.
 * <p>
 * Starting from version 7.0 a RatedTransaction can be linked to several WalletOperation.
 * </p>
 *
 * @see WalletOperation
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 * @lastModifiedVersion 7.1
 */
@Entity
@ObservableEntity
@Table(name = "billing_rated_transaction")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_rated_transaction_seq"), })
@NamedQueries({
        @NamedQuery(name = "RatedTransaction.listInvoiced", query = "SELECT r FROM RatedTransaction r where r.wallet=:wallet and processingStatus is not null order by usageDate desc "),

        @NamedQuery(name = "RatedTransaction.listToInvoiceByOrderNumber", query = "SELECT r FROM RatedTransaction r left join fetch r.processingStatus s where "
                + " s is null AND r.orderNumber=:orderNumber " + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate order by r.billingAccount.id "),
        @NamedQuery(name = "RatedTransaction.listToInvoiceBySubscription", query = "SELECT r FROM RatedTransaction r left join fetch r.processingStatus s where r.subscription.id=:subscriptionId"
                + " AND s is null AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "),
        @NamedQuery(name = "RatedTransaction.listToInvoiceByBillingAccount", query = "SELECT r FROM RatedTransaction r left join fetch  r.processingStatus s where r.billingAccount.id=:billingAccountId "
                + " AND s is null AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "),

        @NamedQuery(name = "RatedTransaction.listOrdersBySubscription", query = "SELECT distinct r.orderNumber FROM RatedTransaction r left join r.processingStatus s where r.subscription=:subscription"
                + " AND s is null AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.seller=:seller and r.orderNumber is not null and r.id<=:lastId"),
        @NamedQuery(name = "RatedTransaction.listOrdersByBillingAccount", query = "SELECT distinct r.orderNumber FROM RatedTransaction r left join r.processingStatus s where r.billingAccount=:billingAccount"
                + " AND s is null AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.seller=:seller and r.orderNumber is not null and r.id<=:lastId"),

        @NamedQuery(name = "RatedTransaction.countNotInvoicedOpenByOrder", query = "SELECT count(r) FROM RatedTransaction r left join r.processingStatus s where "
                + " s is null AND r.orderNumber=:orderNumber " + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedOpenByBA", query = "SELECT count(r) FROM RatedTransaction r left join r.processingStatus s WHERE r.billingAccount=:billingAccount"
                + " AND s is null AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableByServiceWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id, r.serviceInstance.id FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " and r.subscription=:subscription and r.serviceInstance.minimumAmountEl is not null GROUP BY r.invoiceSubCategory.id, r.serviceInstance.id"),
        @NamedQuery(name = "RatedTransaction.sumInvoiceableByServiceWithMinAmountByBA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id, r.serviceInstance.id  FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " and r.billingAccount=:billingAccount and r.serviceInstance.minimumAmountEl is not null GROUP BY r.invoiceSubCategory.id, r.serviceInstance.id"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id, r.subscription.id FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " and r.subscription=:subscription and r.subscription.minimumAmountEl is not null GROUP BY r.invoiceSubCategory.id, r.subscription.id"),
        @NamedQuery(name = "RatedTransaction.sumInvoiceableBySubscriptionWithMinAmountByBA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id, r.subscription.id  FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " and r.billingAccount=:billingAccount and r.subscription.minimumAmountEl is not null GROUP BY r.invoiceSubCategory.id, r.subscription.id"),

        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableBySubscription", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate " + " and r.subscription=:subscription"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableBySubscriptionExcludePrepaidWO", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " and r.subscription=:subscription AND r.wallet.id NOT IN (:walletsIds)"),

        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByBA", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate " + " and r.billingAccount=:billingAccount"),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByBAExcludePrepaidWO", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " and r.billingAccount=:billingAccount AND r.wallet.id NOT IN (:walletsIds)"),

        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByOrderNumber", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r left join r.processingStatus s "
                + "WHERE s is null" + " AND r.orderNumber=:orderNumber AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "),
        @NamedQuery(name = "RatedTransaction.sumTotalInvoiceableByOrderNumberExcludePrpaidWO", query = "SELECT new org.meveo.model.billing.Amounts(sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax)) FROM RatedTransaction r left join r.processingStatus s "
                + "WHERE s is null"
                + " AND r.orderNumber=:orderNumber AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate AND r.wallet.id NOT IN (:walletsIds)"),

        @NamedQuery(name = "RatedTransaction.sumInvoiceableByBA", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id, r.seller.id FROM RatedTransaction r left join r.processingStatus s "
                + " WHERE s is null  AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " and r.billingAccount=:billingAccount GROUP BY r.invoiceSubCategory.id, r.seller.id"),

        @NamedQuery(name = "RatedTransaction.cancelByWOIds", query = "UPDATE RatedTransactionProcessingStatus r SET r.status=org.meveo.model.billing.RatedTransactionStatusEnum.CANCELED, r.statusDate = :now WHERE id IN (SELECT o.ratedTransaction.id FROM WalletOperation o WHERE o.id IN :notBilledWalletIdList)"),
        @NamedQuery(name = "RatedTransaction.getListByInvoiceAndSubCategory", query = "select r.ratedTransaction from RatedTransactionProcessingStatus r where r.invoice=:invoice and r.ratedTransaction.invoiceSubCategory=:invoiceSubCategory "),

        @NamedQuery(name = "RatedTransaction.unInvoiceByInvoice", query = "Delete from RatedTransactionProcessingStatus r where r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED and r.invoice=:invoice"),
        @NamedQuery(name = "RatedTransaction.unInvoiceByBR", query = "Delete from RatedTransactionProcessingStatus r where r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED and r.billingRun=:billingRun"),

        @NamedQuery(name = "RatedTransaction.deleteMinRTByInvoice", query = "DELETE from RatedTransaction r  WHERE r.processingStatus.invoice=:invoice AND r.wallet IS null"),
        @NamedQuery(name = "RatedTransaction.deleteMinRTByBR", query = "DELETE from RatedTransaction r  WHERE r.processingStatus.billingRun=:billingRun AND r.wallet IS null"),

        @NamedQuery(name = "RatedTransaction.countNotInvoicedByBA", query = "SELECT count(*) FROM RatedTransaction r left join r.processingStatus s WHERE (s is null or s.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED) "
                + " AND r.billingAccount=:billingAccount"),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedByUA", query = "SELECT count(*) FROM RatedTransaction r left join r.processingStatus s WHERE (s is null or s.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED) "
                + " AND r.wallet.userAccount=:userAccount"),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedByCA", query = "SELECT count(*) FROM RatedTransaction r left join r.processingStatus s WHERE (s is null or s.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED) "
                + " AND r.billingAccount.customerAccount=:customerAccount"),
        @NamedQuery(name = "RatedTransaction.cancelByRTIds", query = "UPDATE RatedTransactionProcessingStatus r set r.status=org.meveo.model.billing.RatedTransactionStatusEnum.CANCELED, r.statusDate = :now where r.id IN :rsIds "),
        @NamedQuery(name = "RatedTransaction.findByWalletOperationId", query = "SELECT o.ratedTransaction FROM WalletOperation o WHERE o.id=:walletOperationId"),

        @NamedQuery(name = "RatedTransaction.listOpenBetweenTwoDates", query = "SELECT r FROM RatedTransaction r join fetch r.priceplan join fetch r.tax join fetch r.billingAccount join fetch r.seller left join r.processingStatus s where "
                + " s is null " + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate order by r.usageDate desc "),
        @NamedQuery(name = "RatedTransaction.deleteNotOpenBetweenTwoDates", query = "delete FROM RatedTransaction r where "
                + " r.processingStatus.status is not null AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "),

        @NamedQuery(name = "RatedTransaction.listByInvoice", query = "SELECT r FROM RatedTransaction r join r.processingStatus s where s.invoice=:invoice order by r.usageDate"),
        @NamedQuery(name = "RatedTransaction.listByInvoiceNotFree", query = "SELECT r FROM RatedTransaction r join r.processingStatus s where s.invoice=:invoice and r.amountWithoutTax<>0 order by r.usageDate"),        
        @NamedQuery(name = "RatedTransaction.listByInvoiceSubCategoryAggr", query = "SELECT r FROM RatedTransaction r join r.processingStatus s where s.invoiceAgregateF=:invoiceAgregateF order by r.usageDate") })
public class RatedTransaction extends BaseEntity implements ISearchable {

    private static final long serialVersionUID = 1L;

    /**
     * Wallet instance associated to rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    /**
     * Billing account associated to rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_account__id", nullable = false)
    @NotNull
    private BillingAccount billingAccount;

    /**
     * User account associated to rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Seller associated to operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    private Seller seller;

    /**
     * Operation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "usage_date")
    private Date usageDate;

    /**
     * Associated Invoice subcategory
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    /**
     * Operation code - corresponds in majority of cases to charge code
     */
    @Column(name = "code", length = 255)
    @Size(max = 255)
    private String code;

    /**
     * Description - corresponds in majority of cases to charge description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Input unit description
     */
    @Column(name = "unity_description", length = 20)
    @Size(max = 20)
    private String unityDescription;

    /**
     * Rating unit description
     */
    @Column(name = "rating_unit_description", length = 20)
    @Size(max = 20)
    private String ratingUnitDescription;

    /**
     * Unit price without tax
     */
    @Column(name = "unit_amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountWithoutTax;

    /**
     * Unit price with tax
     */
    @Column(name = "unit_amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountWithTax;

    /**
     * Unit price tax amount
     */
    @Column(name = "unit_amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitAmountTax;

    /**
     * Quantity
     */
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity;

    /**
     * Total amount without tax
     */
    @Column(name = "amount_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithoutTax;

    /**
     * Total amount with tax
     */
    @Column(name = "amount_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountWithTax;

    /**
     * Total tax amount
     */
    @Column(name = "amount_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal amountTax;

    /**
     * Rated transaction processing status
     */
    @OneToOne(mappedBy = "ratedTransaction", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private RatedTransactionProcessingStatus processingStatus;

    /**
     * Do not trigger invoicing
     */
    @Type(type = "numeric_boolean")
    @Column(name = "do_not_trigger_invoicing")
    private boolean doNotTriggerInvoicing = false;

    /**
     * Additional parameter used in rating
     */
    @Column(name = "parameter_1", length = 255)
    @Size(max = 255)
    private String parameter1;

    /**
     * Additional parameter used in rating
     */
    @Column(name = "parameter_2", length = 255)
    @Size(max = 255)
    private String parameter2;

    /**
     * Additional parameter used in rating
     */
    @Column(name = "parameter_3", length = 255)
    @Size(max = 255)
    private String parameter3;

    /**
     * Operation start date. Used in cases when operation corresponds to a period.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    /**
     * Operation end date. Used in cases when operation corresponds to a period.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    /**
     * Additional parameter used in rating
     */
    @Column(name = "parameter_extra", columnDefinition = "TEXT")
    private String parameterExtra;

    /**
     * Order number in cases when operation was originated from an order
     */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    /**
     * Price plan applied during rating
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priceplan_id")
    private PricePlanMatrix priceplan;

    /**
     * EDR that produced this operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edr_id")
    private EDR edr;

    /**
     * Adjusted rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjusted_rated_tx")
    private RatedTransaction adjustedRatedTx;

    /**
     * Associated Subscription when operation is tied to subscription.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /**
     * Associated Charge instance when operation is tied to charge instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_instance_id")
    private ChargeInstance chargeInstance;

    /**
     * Tax applied
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tax_id", nullable = false)
    @NotNull
    private Tax tax;

    /**
     * Tax percent applied
     */
    @Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    private BigDecimal taxPercent;

    @OneToMany(mappedBy = "ratedTransaction", fetch = FetchType.LAZY)
    public Set<WalletOperation> walletOperations;

    /**
     * Offer template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private OfferTemplate offerTemplate;

    /**
     * Service instance that Wallet operation is applied to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    /**
     * Was tax explicitly overridden during rating and should not be recalculated at invoice time
     */
    @Transient
    private boolean taxOverriden;

    /**
     * Was tax recalculated (changed) during invoicing
     */
    @Transient
    private boolean taxRecalculated;

    public RatedTransaction() {
        super();
    }

    public RatedTransaction(Date usageDate, BigDecimal unitAmountWithoutTax, BigDecimal unitAmountWithTax, BigDecimal unitAmountTax, BigDecimal quantity,
            BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, RatedTransactionStatusEnum status, WalletInstance wallet, BillingAccount billingAccount,
            UserAccount userAccount, InvoiceSubCategory invoiceSubCategory, String parameter1, String parameter2, String parameter3, String parameterExtra, String orderNumber,
            Subscription subscription, String inputUnitDescription, String ratingUnitDescription, PricePlanMatrix priceplan, OfferTemplate offerTemplate, EDR edr, String code,
            String description, Date startDate, Date endDate, Seller seller, Tax tax, BigDecimal taxPercent, ServiceInstance serviceInstance) {

        super();

        this.code = code;
        this.description = description;
        this.usageDate = usageDate;
        this.unitAmountWithoutTax = unitAmountWithoutTax;
        this.unitAmountWithTax = unitAmountWithTax;
        this.unitAmountTax = unitAmountTax;
        this.quantity = quantity;
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
        this.amountTax = amountTax;
        if (status != null && status != RatedTransactionStatusEnum.OPEN) {
            this.processingStatus = new RatedTransactionProcessingStatus(this, status);
        }
        this.wallet = wallet;
        this.billingAccount = billingAccount;
        this.userAccount = userAccount;
        this.invoiceSubCategory = invoiceSubCategory;
        this.parameter1 = parameter1;
        this.parameter2 = parameter2;
        this.parameter3 = parameter3;
        this.parameterExtra = parameterExtra;
        this.orderNumber = orderNumber;
        this.subscription = subscription;
        this.priceplan = priceplan;
        this.offerTemplate = offerTemplate;
        this.edr = edr;
        this.startDate = startDate;
        this.endDate = endDate;
        this.seller = seller;
        this.tax = tax;
        this.taxPercent = taxPercent;
        this.unityDescription = inputUnitDescription;
        this.ratingUnitDescription = ratingUnitDescription;
        this.serviceInstance = serviceInstance;
    }

    public RatedTransaction(WalletOperation walletOperation) {

        super();

        this.code = walletOperation.getCode();
        this.description = walletOperation.getDescription();
        this.chargeInstance = walletOperation.getChargeInstance();
        this.usageDate = walletOperation.getOperationDate();
        this.unitAmountWithoutTax = walletOperation.getUnitAmountWithoutTax();
        this.unitAmountWithTax = walletOperation.getUnitAmountWithTax();
        this.unitAmountTax = walletOperation.getUnitAmountTax();
        this.quantity = walletOperation.getQuantity();
        this.amountWithoutTax = walletOperation.getAmountWithoutTax();
        this.amountWithTax = walletOperation.getAmountWithTax();
        this.amountTax = walletOperation.getAmountTax();
        this.wallet = walletOperation.getWallet();
        this.userAccount = walletOperation.getWallet().getUserAccount();
        this.billingAccount = userAccount.getBillingAccount();
        this.seller = walletOperation.getSeller();
        this.invoiceSubCategory = walletOperation.getInvoiceSubCategory();
        this.parameter1 = walletOperation.getParameter1();
        this.parameter2 = walletOperation.getParameter2();
        this.parameter3 = walletOperation.getParameter3();
        this.parameterExtra = walletOperation.getParameterExtra();
        this.orderNumber = walletOperation.getOrderNumber();
        this.subscription = walletOperation.getSubscription();
        this.priceplan = walletOperation.getPriceplan();
        this.offerTemplate = walletOperation.getOfferTemplate();
        this.edr = walletOperation.getEdr();
        this.startDate = walletOperation.getStartDate();
        this.endDate = walletOperation.getEndDate();
        this.tax = walletOperation.getTax();
        this.taxPercent = walletOperation.getTaxPercent();
        this.serviceInstance = walletOperation.getServiceInstance();

        this.unityDescription = walletOperation.getInputUnitDescription();
        if (this.unityDescription == null) {
            this.unityDescription = walletOperation.getChargeInstance().getChargeTemplate().getInputUnitDescription();
        }
        this.ratingUnitDescription = walletOperation.getRatingUnitDescription();
        if (ratingUnitDescription == null) {
            this.ratingUnitDescription = walletOperation.getChargeInstance().getChargeTemplate().getRatingUnitDescription();
        }
    }

    public WalletInstance getWallet() {
        return wallet;
    }

    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    public Date getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public BigDecimal getUnitAmountWithoutTax() {
        return unitAmountWithoutTax;
    }

    public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
        this.unitAmountWithoutTax = unitAmountWithoutTax;
    }

    public BigDecimal getUnitAmountWithTax() {
        return unitAmountWithTax;
    }

    public void setUnitAmountWithTax(BigDecimal unitAmountWithTax) {
        this.unitAmountWithTax = unitAmountWithTax;
    }

    public BigDecimal getUnitAmountTax() {
        return unitAmountTax;
    }

    public void setUnitAmountTax(BigDecimal unitAmountTax) {
        this.unitAmountTax = unitAmountTax;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public RatedTransactionStatusEnum getStatus() {

        if (processingStatus != null) {
            return processingStatus.getStatus();
        } else {
            return RatedTransactionStatusEnum.OPEN;
        }
    }

    public boolean isDoNotTriggerInvoicing() {
        return doNotTriggerInvoicing;
    }

    public void setDoNotTriggerInvoicing(boolean doNotTriggerInvoicing) {
        this.doNotTriggerInvoicing = doNotTriggerInvoicing;
    }

    /**
     * @return Billing account associated to rated transaction
     */
    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    /**
     * @param billingAccount Billing account associated to rated transaction
     */
    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    /**
     * @return User account associated to rated transaction
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * @param userAccount User account associated to rated transaction
     */
    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return Seller associated to rated transaction
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller Seller associated to rated transaction
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnityDescription() {
        return unityDescription;
    }

    public void setUnityDescription(String unityDescription) {
        this.unityDescription = unityDescription;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
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

    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
    }

    public String getParameterExtra() {
        return parameterExtra;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public PricePlanMatrix getPriceplan() {
        return priceplan;
    }

    public void setPriceplan(PricePlanMatrix priceplan) {
        this.priceplan = priceplan;
    }

    public EDR getEdr() {
        return edr;
    }

    public void setEdr(EDR edr) {
        this.edr = edr;
    }

    public RatedTransaction getAdjustedRatedTx() {
        return adjustedRatedTx;
    }

    public void setAdjustedRatedTx(RatedTransaction adjustedRatedTx) {
        this.adjustedRatedTx = adjustedRatedTx;
    }

    public void recompute() {
        recompute(false);
    }

    // recompute given unit amount and quantity
    public void recompute(boolean isEnterprise) {
        unitAmountTax = unitAmountWithTax.subtract(unitAmountWithoutTax);
        amountWithoutTax = unitAmountWithoutTax.multiply(quantity);

        if (!isEnterprise) {
            amountWithTax = unitAmountWithTax.multiply(quantity);
        }
    }

    /**
     * Recompute derived amounts amountWithoutTax/amountWithTax/amountTax unitAmountWithoutTax/unitAmountWithTax/unitAmountTax
     * 
     * @param isEnterprise Is application used used in B2B (base prices are without tax) or B2C mode (base prices are with tax)
     * @param rounding Rounding precision to apply
     * @param roundingMode Rounding mode to apply
     */
    public void computeDerivedAmounts(boolean isEnterprise, int rounding, RoundingModeEnum roundingMode) {

        // Unit amount calculation is left with higher precision
        BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(unitAmountWithoutTax, unitAmountWithTax, taxPercent, isEnterprise, BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
        unitAmountWithoutTax = amounts[0];
        unitAmountWithTax = amounts[1];
        unitAmountTax = amounts[2];

        amounts = NumberUtils.computeDerivedAmounts(amountWithoutTax, amountWithTax, taxPercent, isEnterprise, rounding, roundingMode.getRoundingMode());
        amountWithoutTax = amounts[0];
        amountWithTax = amounts[1];
        amountTax = amounts[2];
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof RatedTransaction)) {
            return false;
        }

        RatedTransaction other = (RatedTransaction) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }
        if (isTransient() || other.isTransient()) {
            return false;
        }
        return true;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public ChargeInstance getChargeInstance() {
        return chargeInstance;
    }

    public void setChargeInstance(ChargeInstance chargeInstance) {
        this.chargeInstance = chargeInstance;
    }

    /**
     * @return Tax applied
     */
    public Tax getTax() {
        return tax;
    }

    /**
     * @param tax Tax applied
     */
    public void setTax(Tax tax) {
        this.tax = tax;
    }

    /**
     * @return Tax percent applied
     */
    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    /**
     * @param taxPercent Tax percent applied
     */
    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public Set<WalletOperation> getWalletOperations() {
        return walletOperations;
    }

    public void setWalletOperations(Set<WalletOperation> walletOperations) {
        this.walletOperations = walletOperations;
    }

    /**
     * This will be use for backward compatibility back when a WalletOperation is mapped to a RatedTransaction.
     *
     * @return first id of the WalletOperation
     */
    @Deprecated
    public Long getWalletOperationId() {
        if (getWalletOperations() != null && !getWalletOperations().isEmpty() && getWalletOperations().iterator().hasNext()) {
            WalletOperation walletOperation = getWalletOperations().iterator().next();
            if (walletOperation != null) {
                return walletOperation.getId();
            }
            return null;
        }

        return null;
    }

    public void resetAmounts() {
        unitAmountWithoutTax = BigDecimal.ZERO;
        unitAmountWithTax = BigDecimal.ZERO;
        unitAmountTax = BigDecimal.ZERO;
        amountWithoutTax = BigDecimal.ZERO;
        amountWithTax = BigDecimal.ZERO;
        amountTax = BigDecimal.ZERO;
    }

    public BigDecimal getIsEnterpriseAmount(boolean isEnterprise) {
        return isEnterprise ? getAmountWithoutTax() : getAmountWithTax();
    }

    public BigDecimal getIsEnterpriseUnitAmount(boolean isEnterprise) {
        return isEnterprise ? getUnitAmountWithoutTax() : getUnitAmountWithTax();
    }

    public void setIsEnterpriseAmount(boolean isEnterprise, BigDecimal amount) {
        if (isEnterprise) {
            setAmountWithoutTax(amount);

        } else {
            setAmountWithTax(amount);
        }
    }

    public void setIsEnterpriseUnitAmount(boolean isEnterprise, BigDecimal amount) {
        if (isEnterprise) {
            setUnitAmountWithoutTax(amount);

        } else {
            setUnitAmountWithTax(amount);
        }
    }

    /**
     * @return Service instance that Wallet operation is applied to
     */
    public ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    /**
     * @param serviceInstance Service instance that Wallet operation is applied to
     */
    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    /**
     * @return Is this a prepaid transaction
     */
    public boolean isPrepaid() {
        return wallet != null && wallet.isPrepaid();
    }

    /**
     * @return Was tax explicitly overridden during rating and should not be recalculated at invoice time
     */
    public boolean isTaxOverriden() {
        return taxOverriden;
    }

    /**
     * @param taxOverriden Was tax explicitly overridden during rating and should not be recalculated at invoice time
     */
    public void setTaxOverriden(boolean taxOverriden) {
        this.taxOverriden = taxOverriden;
    }

    /**
     * @return Was tax recalculated (changed) during invoicing
     */
    public boolean isTaxRecalculated() {
        return taxRecalculated;
    }

    /**
     * @param taxRecalculated Was tax recalculated (changed) during invoicing
     */
    public void setTaxRecalculated(boolean taxRecalculated) {
        this.taxRecalculated = taxRecalculated;
    }

    /**
     * @return Rated transaction processing status
     */
    public RatedTransactionProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    /**
     * @param processingStatus Rated transaction processing status
     */
    public void setProcessingStatus(RatedTransactionProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }
}