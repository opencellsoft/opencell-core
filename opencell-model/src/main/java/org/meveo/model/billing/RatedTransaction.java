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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.rating.EDR;

/**
 * Rated transaction - usually corresponds 1-1 to Wallet operation, but could also be cases with multiple RatedTransactions created from a single Wallet operation.
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "billing_rated_transaction")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_rated_transaction_seq"), })
@NamedQueries({ @NamedQuery(name = "RatedTransaction.listByWalletOperationId", query = "SELECT r FROM RatedTransaction r where r.walletOperationId=:walletOperationId"),
        @NamedQuery(name = "RatedTransaction.listInvoiced", query = "SELECT r FROM RatedTransaction r where r.wallet=:wallet and invoice is not null order by usageDate desc "),

        @NamedQuery(name = "RatedTransaction.listToInvoiceByOrderNumber", query = "SELECT r FROM RatedTransaction r where "
                + " r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND r.orderNumber=:orderNumber "
                + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate AND r.invoice is null order by r.usageDate desc "),

        @NamedQuery(name = "RatedTransaction.sumByOrderNumber", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax) FROM RatedTransaction r "
                + "WHERE r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN"
                + " AND r.orderNumber=:orderNumber AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.doNotTriggerInvoicing=false AND r.invoice is null"),

        @NamedQuery(name = "RatedTransaction.listToInvoiceBySubscription", query = "SELECT r FROM RatedTransaction r where r.subscription=:subscription"
                + " AND r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.invoice is null order by r.usageDate desc "),

        @NamedQuery(name = "RatedTransaction.listAllRTByBillingAccount", query = "SELECT r FROM RatedTransaction r where r.billingAccount=:billingAccount "
                + " AND r.invoice is null order by r.usageDate desc "),

        @NamedQuery(name = "RatedTransaction.listToInvoiceByBillingAccount", query = "SELECT r FROM RatedTransaction r where r.billingAccount=:billingAccount "
                + " AND r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.invoice is null order by r.usageDate desc "),

        @NamedQuery(name = "RatedTransaction.countListToInvoiceByOrderNumber", query = "SELECT count(r) FROM RatedTransaction r where "
                + "r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND r.orderNumber=:orderNumber and r.invoice is null"
                + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "),
        @NamedQuery(name = "RatedTransaction.countNotInvoinced", query = "SELECT count(r) FROM RatedTransaction r WHERE r.billingAccount=:billingAccount"
                + " AND r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.doNotTriggerInvoicing=false AND r.invoice is null "),
        @NamedQuery(name = "RatedTransaction.sumbillingRunByCycle", query = "SELECT sum(r.amountWithoutTax),sum(r.amountWithTax),sum(r.amountTax) FROM RatedTransaction r"
                + " WHERE r.status=:status AND r.doNotTriggerInvoicing=false AND r.amountWithoutTax<>0 AND r.invoice is null" + " AND r.usageDate<:lastTransactionDate "
                + " AND r.billingAccount.billingCycle=:billingCycle" + " AND (r.billingAccount.nextInvoiceDate >= :startDate)"
                + " AND (r.billingAccount.nextInvoiceDate < :endDate) "),
        @NamedQuery(name = "RatedTransaction.sumBillingByWallet", query = "SELECT r.invoiceSubCategory.id, sum(r.amountWithoutTax),sum(r.amountWithTax),sum(r.amountTax), sum(r.quantity) FROM RatedTransaction r"
                + " WHERE r.status=:status AND r.doNotTriggerInvoicing=false AND r.invoice is null" + " AND r.usageDate<:lastTransactionDate " + " AND r.wallet=:wallet"
                + " GROUP BY r.invoiceSubCategory"),
        @NamedQuery(name = "RatedTransaction.sumMinBilling", query = "SELECT r.invoiceSubCategory.id, sum(r.amountWithoutTax),sum(r.amountWithTax),sum(r.amountTax), sum(r.quantity) FROM RatedTransaction r"
                + " WHERE r.status=:status AND r.doNotTriggerInvoicing=false AND r.invoice is null AND r.billingAccount=:billingAccount AND r.usageDate<:lastTransactionDate AND r.wallet is null"
                + " GROUP BY r.invoiceSubCategory"),
        @NamedQuery(name = "RatedTransaction.sumbillingRunByCycleNoDate", query = "SELECT sum(r.amountWithoutTax),sum(r.amountWithTax),sum(r.amountTax) FROM RatedTransaction r"
                + " WHERE r.status=:status AND r.doNotTriggerInvoicing=false AND r.amountWithoutTax<>0 AND r.invoice is null" + " AND r.usageDate<:lastTransactionDate "
                + " AND r.billingAccount.billingCycle=:billingCycle "),
        @NamedQuery(name = "RatedTransaction.sumbillingRunByList", query = "SELECT sum(r.amountWithoutTax),sum(r.amountWithTax),sum(r.amountTax) FROM RatedTransaction r "
                + "WHERE r.status=:status AND r.doNotTriggerInvoicing=false AND r.amountWithoutTax<>0 AND r.invoice is null" + " AND r.usageDate<:lastTransactionDate "
                + " AND r.billingAccount IN :billingAccountList"),

        @NamedQuery(name = "RatedTransaction.sumBillingAccount", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), sum(r.amountTax) FROM RatedTransaction r "
                + "WHERE r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.doNotTriggerInvoicing=false " + "AND r.invoice is null" + " AND r.billingAccount=:billingAccount"),

        @NamedQuery(name = "RatedTransaction.sumByCharge", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id  FROM RatedTransaction r "
                + " WHERE r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.doNotTriggerInvoicing=false AND r.invoice is null and r.chargeInstance in (:chargeInstances)"
                + " GROUP BY r.invoiceSubCategory.id"),

        @NamedQuery(name = "RatedTransaction.sumBySubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id  FROM RatedTransaction r "
                + " WHERE r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.doNotTriggerInvoicing=false AND r.invoice is null and r.subscription = :subscription"
                + " GROUP BY r.invoiceSubCategory.id"),

        @NamedQuery(name = "RatedTransaction.sumByBillingAccountNoSubscription", query = "SELECT sum(r.amountWithoutTax), sum(r.amountWithTax), r.invoiceSubCategory.id, r.seller.id  FROM RatedTransaction r "
                + " WHERE r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN" + " AND :firstTransactionDate<r.usageDate AND r.usageDate<:lastTransactionDate "
                + " AND r.doNotTriggerInvoicing=false AND r.invoice is null and r.billingAccount = :billingAccount and r.subscription is null"
                + " GROUP BY r.invoiceSubCategory.id, r.seller.id"),        
        
        @NamedQuery(name = "RatedTransaction.updateInvoiced", query = "UPDATE RatedTransaction r "
                + "SET r.billingRun=:billingRun,r.invoice=:invoice,r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED " + "where r.invoice is null"
                + " and r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN " + " and r.doNotTriggerInvoicing=false" + " AND r.usageDate<:lastTransactionDate "
                + " and r.billingAccount=:billingAccount"),
        @NamedQuery(name = "RatedTransaction.updateInvoicedNoBR", query = "UPDATE RatedTransaction r "
                + "SET r.invoice=:invoice,r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED " + "where r.invoice is null"
                + " and r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN " + " and r.doNotTriggerInvoicing=false" + " AND r.usageDate<:lastTransactionDate "
                + " and r.billingAccount=:billingAccount"),
        @NamedQuery(name = "RatedTransaction.getRatedTransactionsBilled", query = "SELECT r.walletOperationId FROM RatedTransaction r "
                + " WHERE r.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED" + " AND r.walletOperationId IN :walletIdList"),
        @NamedQuery(name = "RatedTransaction.setStatusToCanceled", query = "UPDATE RatedTransaction rt set rt.status=org.meveo.model.billing.RatedTransactionStatusEnum.CANCELED"
                + " where rt.walletOperationId IN :notBilledWalletIdList"),
        @NamedQuery(name = "RatedTransaction.getListByInvoiceAndSubCategory", query = "from RatedTransaction t where t.invoice=:invoice and t.invoiceSubCategory=:invoiceSubCategory "),
        @NamedQuery(name = "RatedTransaction.deleteInvoice", query = "UPDATE RatedTransaction r "
                + "set r.invoice=null,r.invoiceAgregateF=null,r.invoiceAgregateR=null,r.invoiceAgregateT=null,r.billingRun=null,r.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN where r.invoice=:invoice"),

        @NamedQuery(name = "RatedTransaction.deleteMinRT", query = "DELETE from RatedTransaction r " + " WHERE r.invoice=:invoice AND r.wallet IS null"),

        @NamedQuery(name = "RatedTransaction.getDistinctOrderNumsByInvoice", query = "SELECT DISTINCT rt.orderNumber from RatedTransaction rt where  rt.invoice=:invoice AND NOT(rt.orderNumber IS null)"),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedByBA", query = "SELECT count(*) FROM RatedTransaction r WHERE r.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED "
                + " AND r.billingAccount=:billingAccount"),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedByUA", query = "SELECT count(*) FROM RatedTransaction r WHERE r.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED "
                + " AND r.wallet.userAccount=:userAccount"),
        @NamedQuery(name = "RatedTransaction.countNotInvoicedByCA", query = "SELECT count(*) FROM RatedTransaction r WHERE r.status <> org.meveo.model.billing.RatedTransactionStatusEnum.BILLED "
                + " AND r.billingAccount.customerAccount=:customerAccount"),
        @NamedQuery(name = "RatedTransaction.setStatusToCanceledByRsCodes", query = "UPDATE RatedTransaction rt set rt.status=org.meveo.model.billing.RatedTransactionStatusEnum.CANCELED"
                + " where rt.id IN :rsToCancelCodes and rt.status=org.meveo.model.billing.RatedTransactionStatusEnum.OPEN") })
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
     * Wallet operation identifier
     */
    @Column(name = "wallet_operation_id")
    private Long walletOperationId;

    /**
     * Wallet operation
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_operation_id", insertable = false, updatable = false)
    private WalletOperation walletOperationEntity;

    /**
     * Billing run that invoiced this Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_run_id")
    private BillingRun billingRun;

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
    @Column(name = "description", length = 255)
    @Size(max = 255)
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
     * Invoice that invoiced this Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    /**
     * Subcategory invoice aggregate that Rated transaction was invoiced under
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "aggregate_id_f")
    private SubCategoryInvoiceAgregate invoiceAgregateF;

    /**
     * Category invoice aggregate that Rated transaction was invoiced under
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "aggregate_id_r")
    private CategoryInvoiceAgregate invoiceAgregateR;

    /**
     * Tax invoice aggregate that Rated transaction was invoiced under
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "aggregate_id_t")
    private TaxInvoiceAgregate invoiceAgregateT;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RatedTransactionStatusEnum status;

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
     * Offer code
     */
    @Column(name = "offer_code", length = 255)
    @Size(max = 255, min = 1)
    protected String offerCode;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;

    /**
     * Tax percent applied
     */
    @Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal taxPercent;

    /**
     * Offer template
     */
    @Transient
    private OfferTemplate offerTemplate;

    /**
     * Corresponding Wallet operation
     */
    @Transient
    private WalletOperation walletOperation;

    public RatedTransaction() {
        super();
    }

    public RatedTransaction(RatedTransaction ratedTransaction) {
        this.setWallet(ratedTransaction.getWallet());
        this.setBillingAccount(ratedTransaction.getBillingAccount());
        this.setUserAccount(ratedTransaction.getUserAccount());
        this.setWalletOperationId(ratedTransaction.getWalletOperationId());
        this.setChargeInstance(ratedTransaction.getChargeInstance());
        this.setUsageDate(ratedTransaction.getUsageDate());
        this.setInvoiceSubCategory(ratedTransaction.getInvoiceSubCategory());
        this.setCode(ratedTransaction.getCode());
        this.setDescription(ratedTransaction.getDescription());
        this.setUnityDescription(ratedTransaction.getUnityDescription());
        this.setUnitAmountWithoutTax(ratedTransaction.getUnitAmountWithoutTax());
        this.setUnitAmountWithTax(ratedTransaction.getUnitAmountWithTax());
        this.setUnitAmountTax(ratedTransaction.getUnitAmountTax());
        this.setQuantity(ratedTransaction.getQuantity());
        this.setAmountWithoutTax(ratedTransaction.getAmountWithoutTax());
        this.setAmountWithTax(ratedTransaction.getAmountWithTax());
        this.setAmountTax(ratedTransaction.getAmountTax());
        this.setInvoice(ratedTransaction.getInvoice());
        this.setStatus(ratedTransaction.getStatus());
        this.setDoNotTriggerInvoicing(ratedTransaction.isDoNotTriggerInvoicing());
        this.setParameter1(ratedTransaction.getParameter1());
        this.setParameter2(ratedTransaction.getParameter2());
        this.setParameter3(ratedTransaction.getParameter3());
        this.setParameterExtra(ratedTransaction.getParameterExtra());
        this.setOrderNumber(ratedTransaction.getOrderNumber());
        this.setPriceplan(ratedTransaction.getPriceplan());
        this.setOfferCode(ratedTransaction.getOfferCode());
        this.setEdr(ratedTransaction.getEdr());
        this.setOfferTemplate(ratedTransaction.getOfferTemplate());
        this.setRatingUnitDescription(ratedTransaction.getRatingUnitDescription());
        this.setStartDate(ratedTransaction.getStartDate());
        this.setEndDate(ratedTransaction.getEndDate());
        this.setSubscription(ratedTransaction.getSubscription());
        this.setSeller(ratedTransaction.getSeller());
        this.setTax(ratedTransaction.getTax());
        this.setTaxPercent(ratedTransaction.getTaxPercent());
    }

    public RatedTransaction(Date usageDate, BigDecimal unitAmountWithoutTax, BigDecimal unitAmountWithTax, BigDecimal unitAmountTax, BigDecimal quantity,
            BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax, RatedTransactionStatusEnum status, WalletInstance wallet, BillingAccount billingAccount,
            UserAccount userAccount, InvoiceSubCategory invoiceSubCategory, String parameter1, String parameter2, String parameter3, String parameterExtra, String orderNumber,
            Subscription subscription, String inputUnitDescription, String ratingUnitDescription, PricePlanMatrix priceplan, String offerCode, EDR edr, String code,
            String description, Date startDate, Date endDate, Seller seller, Tax tax, BigDecimal taxPercent) {

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
        this.status = status;
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
        this.offerCode = offerCode;
        this.edr = edr;
        this.startDate = startDate;
        this.endDate = endDate;
        this.seller = seller;
        this.tax = tax;
        this.taxPercent = taxPercent;
        this.unityDescription = inputUnitDescription;
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public RatedTransaction(WalletOperation walletOperation) {

        super();

        this.code = walletOperation.getCode();
        this.description = walletOperation.getDescription();
        this.walletOperationId = walletOperation.getId();
        this.walletOperation = walletOperation;
        this.chargeInstance = walletOperation.getChargeInstance();
        this.usageDate = walletOperation.getOperationDate();
        this.unitAmountWithoutTax = walletOperation.getUnitAmountWithoutTax();
        this.unitAmountWithTax = walletOperation.getUnitAmountWithTax();
        this.unitAmountTax = walletOperation.getUnitAmountTax();
        this.quantity = walletOperation.getQuantity();
        this.amountWithoutTax = walletOperation.getAmountWithoutTax();
        this.amountWithTax = walletOperation.getAmountWithTax();
        this.amountTax = walletOperation.getAmountTax();
        this.status = RatedTransactionStatusEnum.OPEN;
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
        this.offerCode = walletOperation.getOfferCode();
        this.edr = walletOperation.getEdr();
        this.startDate = walletOperation.getStartDate();
        this.endDate = walletOperation.getEndDate();
        this.tax = walletOperation.getTax();
        this.taxPercent = walletOperation.getTaxPercent();

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

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
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

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public SubCategoryInvoiceAgregate getInvoiceAgregateF() {
        return invoiceAgregateF;
    }

    public void setInvoiceAgregateF(SubCategoryInvoiceAgregate invoiceAgregateF) {
        this.invoiceAgregateF = invoiceAgregateF;
    }

    public CategoryInvoiceAgregate getInvoiceAgregateR() {
        return invoiceAgregateR;
    }

    public void setInvoiceAgregateR(CategoryInvoiceAgregate invoiceAgregateR) {
        this.invoiceAgregateR = invoiceAgregateR;
    }

    public TaxInvoiceAgregate getInvoiceAgregateT() {
        return invoiceAgregateT;
    }

    public void setInvoiceAgregateT(TaxInvoiceAgregate invoiceAgregateT) {
        this.invoiceAgregateT = invoiceAgregateT;
    }

    public RatedTransactionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RatedTransactionStatusEnum status) {
        this.status = status;
    }

    public boolean isDoNotTriggerInvoicing() {
        return doNotTriggerInvoicing;
    }

    public void setDoNotTriggerInvoicing(boolean doNotTriggerInvoicing) {
        this.doNotTriggerInvoicing = doNotTriggerInvoicing;
    }

    public Long getWalletOperationId() {
        return walletOperationId;
    }

    public void setWalletOperationId(Long walletOperationId) {
        this.walletOperationId = walletOperationId;
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

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
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

    public WalletOperation getWalletOperation() {
        return walletOperation;
    }

    public void setWalletOperation(WalletOperation walletOperation) {
        this.walletOperation = walletOperation;
    }

    public WalletOperation getWalletOperationEntity() {
        return walletOperationEntity;
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
}