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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RoundingModeEnum;
import org.meveo.model.rating.EDR;

/**
 * Consumption operation
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Entity
@Table(name = "billing_wallet_operation")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_wallet_operation_seq"), })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("W")
@NamedQueries({
		@NamedQuery(name = "WalletOperation.listRatedTransactionByWalletOperationId", query = "SELECT o.ratedTransaction FROM WalletOperation o where o.id=:walletOperationId"),
		@NamedQuery(name = "WalletOperation.getRatedTransactionsBilled", query = "SELECT o.id FROM WalletOperation o "
                + " WHERE o.ratedTransaction.status=org.meveo.model.billing.RatedTransactionStatusEnum.BILLED" + " AND o.id IN :walletIdList"),
		@NamedQuery(name = "WalletOperation.listByRatedTransactionId", query = "SELECT o FROM WalletOperation o WHERE o.ratedTransaction.id=:ratedTransactionId"),
		
		@NamedQuery(name = "WalletOperation.listToInvoice", query = "SELECT o FROM WalletOperation o WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) "
                + " AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN"),
        @NamedQuery(name = "WalletOperation.listToInvoiceByUA", query = "SELECT o FROM WalletOperation o WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) "
                + " AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" + " AND o.wallet.userAccount=:userAccount"),
        @NamedQuery(name = "WalletOperation.listToInvoiceBySubscription", query = "SELECT o FROM WalletOperation o WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) "
                + " AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" + " AND o.subscription=:subscription"),
        @NamedQuery(name = "WalletOperation.listToInvoiceByOrderNumber", query = "SELECT o FROM WalletOperation o WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) "
                + " AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" + " AND o.orderNumber=:orderNumber"),
        @NamedQuery(name = "WalletOperation.listToInvoiceIds", query = "SELECT o.id FROM WalletOperation o WHERE (o.invoicingDate is NULL or o.invoicingDate<:invoicingDate ) "
                + " AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN"),
        @NamedQuery(name = "WalletOperation.getBalancesForWalletInstance", query = "SELECT sum(case when o.status in ('OPEN','TREATED') then o.amountWithTax else 0 end), sum(o.amountWithTax) FROM WalletOperation o WHERE o.wallet.id=:walletId and o.status in ('OPEN','RESERVED','TREATED')"),
        @NamedQuery(name = "WalletOperation.getBalancesForCache", query = "SELECT o.wallet.id, sum(case when o.status in ('OPEN','TREATED') then o.amountWithTax else 0 end), sum(o.amountWithTax) FROM WalletOperation o WHERE o.status in ('OPEN','RESERVED','TREATED') and o.wallet.walletTemplate.walletType='PREPAID' group by o.wallet.id"),
        @NamedQuery(name = "WalletOperation.getMaxOpenId", query = "SELECT max(o.id) FROM WalletOperation o WHERE o.wallet=:wallet and "
                + "o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN"),
        @NamedQuery(name = "WalletOperation.getBalanceNoTaxUntilId", query = "SELECT sum(o.amountWithoutTax)*-1 FROM WalletOperation o WHERE o.wallet=:wallet and "
                + "o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" + " AND o.id<=:maxId"),
        @NamedQuery(name = "WalletOperation.getBalanceWithTaxUntilId", query = "SELECT sum(o.amountWithTax)*-1 FROM WalletOperation o WHERE o.wallet=:wallet and "
                + "o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" + " AND o.id<=:maxId"),
        @NamedQuery(name = "WalletOperation.setTreatedStatusUntilId", query = "UPDATE WalletOperation o SET o.status= org.meveo.model.billing.WalletOperationStatusEnum.TREATED "
                + " WHERE o.wallet=:wallet and " + "o.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN" + " AND o.id<=:maxId"),
        @NamedQuery(name = "WalletOperation.setStatusToRerate", query = "update WalletOperation w set w.status=org.meveo.model.billing.WalletOperationStatusEnum.TO_RERATE"
                + " where (w.status=org.meveo.model.billing.WalletOperationStatusEnum.OPEN OR w.status=org.meveo.model.billing.WalletOperationStatusEnum.TREATED)"
                + " and w.id IN :notBilledWalletIdList"),
        @NamedQuery(name = "WalletOperation.listByChargeInstance", query = "SELECT o FROM WalletOperation o WHERE (o.chargeInstance=:chargeInstance ) "),
        @NamedQuery(name = "WalletOperation.deleteScheduled", query = "DELETE WalletOperation o WHERE (o.chargeInstance=:chargeInstance ) "
                + " AND o.status=org.meveo.model.billing.WalletOperationStatusEnum.SCHEDULED"),
        @NamedQuery(name = "WalletOperation.countNotTreatedByBA", query = "SELECT count(*) FROM WalletOperation o WHERE o.status <> org.meveo.model.billing.WalletOperationStatusEnum.TREATED "
                + " AND o.wallet.userAccount.billingAccount=:billingAccount"),
        @NamedQuery(name = "WalletOperation.countNotTreatedByUA", query = "SELECT count(*) FROM WalletOperation o WHERE o.status <> org.meveo.model.billing.WalletOperationStatusEnum.TREATED "
                + " AND o.wallet.userAccount=:userAccount"),
        @NamedQuery(name = "WalletOperation.countNotTreatedByCA", query = "SELECT count(*) FROM WalletOperation o WHERE o.status <> org.meveo.model.billing.WalletOperationStatusEnum.TREATED "
                + " AND o.wallet.userAccount.billingAccount.customerAccount=:customerAccount") })
public class WalletOperation extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    /**
     * The wallet on which the operation is applied.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private WalletInstance wallet;

    /**
     * Operation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "operation_date")
    private Date operationDate;

    /**
     * Invoicing date if invoice date should be in a future and does not match the billing cycle invoicing dates
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "invoicing_date")
    private Date invoicingDate;

    /**
     * Operation type Credit/Debit
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_debit_flag")
    private OperationTypeEnum type;

    /**
     * Associated charge instance
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_instance_id", nullable = false)
    @NotNull
    private ChargeInstance chargeInstance;

    /**
     * Currency of operation rated amounts
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

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
     * Counter instance to track consumption
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counter_id")
    private CounterInstance counter;

    /**
     * Aggregated service instance. Deprecated in 5.3 for not use
     */
    @Deprecated
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aggregate_serv_id")
    private ServiceInstance aggregatedServiceInstance;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "edr_id")
    // private EDR usageEdr;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_1", length = 255)
    @Size(max = 255)
    private String parameter1;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_2", length = 255)
    @Size(max = 255)
    private String parameter2;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_3", length = 255)
    @Size(max = 255)
    private String parameter3;

    /**
     * Additional rating parameter
     */
    @Column(name = "parameter_extra", columnDefinition = "TEXT")
    private String parameterExtra;

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
     * Service/charge subscription timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "subscription_date")
    private Date subscriptionDate;

    /**
     * Offer code
     */
    @Column(name = "offer_code", length = 255)
    @Size(max = 255, min = 1)
    protected String offerCode;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WalletOperationStatusEnum status;

    /**
     * Seller associated to operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    /**
     * Price plan applied during rating
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priceplan_id")
    private PricePlanMatrix priceplan;

    /**
     * Rerated wallet operation
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    private WalletOperation reratedWalletOperation;

    /**
     * Input unit description
     */
    @Column(name = "input_unit_description", length = 20)
    @Size(max = 20)
    private String inputUnitDescription;

    /**
     * Rating unit description
     */
    @Column(name = "rating_unit_description", length = 20)
    @Size(max = 20)
    private String ratingUnitDescription;

    /**
     * Input quantity
     */
    @Column(name = "input_quantity", precision = BaseEntity.NB_PRECISION, scale = BaseEntity.NB_DECIMALS)
    private BigDecimal inputQuantity;

    /**
     * EDR that produced this operation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edr_id")
    private EDR edr;

    /**
     * Order number in cases when operation was originated from an order
     */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    /**
     * Raw rating amount without tax from Price plan without any rounding. Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "raw_amount_without_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithoutTax;

    /**
     * Raw rating amount with tax from Price plan without any rounding. Deprecated in 5.3 for not use.
     */
    @Deprecated
    @Column(name = "raw_amount_with_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithTax;

    /**
     * Associated Invoice subcategory
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    /**
     * Associated Subscription when operation is tied to subscription.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    protected Subscription subscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_transaction_id")
    protected RatedTransaction ratedTransaction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_instance_id")
    private ServiceInstance serviceInstance;

    /**
     * Billing account
     */
    @Transient
    private BillingAccount billingAccount;

    /**
     * Billing run
     */
    @Transient
    private BillingRun billingRun;

    /**
     * Offer template
     */
    @Transient
    private OfferTemplate offerTemplate;

    public WalletInstance getWallet() {
        return wallet;
    }

    public void setWallet(WalletInstance wallet) {
        this.wallet = wallet;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public Date getInvoicingDate() {
        return invoicingDate;
    }

    public void setInvoicingDate(Date invoicingDate) {
        this.invoicingDate = invoicingDate;
    }

    public OperationTypeEnum getType() {
        return type;
    }

    public void setType(OperationTypeEnum type) {
        this.type = type;
    }

    public ChargeInstance getChargeInstance() {
        return chargeInstance;
    }

	public void setChargeInstance(ChargeInstance chargeInstance) {
		if (chargeInstance instanceof RecurringChargeInstance) {
			setServiceInstance(((RecurringChargeInstance) chargeInstance).getServiceInstance());

		} else if (chargeInstance instanceof OneShotChargeInstance) {
			OneShotChargeInstance os = ((OneShotChargeInstance) chargeInstance);
			if (os.getSubscriptionServiceInstance() != null) {
				setServiceInstance(os.getSubscriptionServiceInstance());

			} else {
				setServiceInstance(os.getTerminationServiceInstance());
			}

		} else if (chargeInstance instanceof UsageChargeInstance) {
			setServiceInstance(((UsageChargeInstance) chargeInstance).getServiceInstance());
		}

		this.chargeInstance = chargeInstance;
	}

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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

    public CounterInstance getCounter() {
        return counter;
    }

    public void setCounter(CounterInstance counter) {
        this.counter = counter;
    }

    public ServiceInstance getAggregatedServiceInstance() {
        return aggregatedServiceInstance;
    }

    public void setAggregatedServiceInstance(ServiceInstance aggregatedServiceInstance) {
        this.aggregatedServiceInstance = aggregatedServiceInstance;
    }

    /*
     * public EDR getUsageEdr() { return usageEdr; }
     * 
     * public void setUsageEdr(EDR usageEdr) { this.usageEdr = usageEdr; }
     */
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

    public String getParameterExtra() {
        return parameterExtra;
    }

    public void setParameterExtra(String parameterExtra) {
        this.parameterExtra = parameterExtra;
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

    public WalletOperationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(WalletOperationStatusEnum status) {
        this.status = status;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    /**
     * @return Seller associated to wallet operation
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller Seller associated to wallet operation
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public PricePlanMatrix getPriceplan() {
        return priceplan;
    }

    public void setPriceplan(PricePlanMatrix priceplan) {
        this.priceplan = priceplan;
    }

    public WalletOperation getReratedWalletOperation() {
        return reratedWalletOperation;
    }

    public void setReratedWalletOperation(WalletOperation reratedWalletOperation) {
        this.reratedWalletOperation = reratedWalletOperation;
    }

    public EDR getEdr() {
        return edr;
    }

    public void setEdr(EDR edr) {
        this.edr = edr;
    }

    @Transient
    public WalletOperation getUnratedClone() {
        WalletOperation result = new WalletOperation();
        return fillUnratedClone(result);
    }

    protected WalletOperation fillUnratedClone(WalletOperation result) {
        result.setAggregatedServiceInstance(aggregatedServiceInstance);
        result.setAppendGeneratedCode(appendGeneratedCode);
        result.setAuditable(getAuditable());
        result.setBillingAccount(billingAccount);
        result.setChargeInstance(chargeInstance);
        result.setCode(code);
        result.setCounter(counter);
        result.setCurrency(currency);
        result.setDescription(description);
        result.setEndDate(endDate);
        result.setInvoiceSubCategory(invoiceSubCategory);
        result.setInvoicingDate(invoicingDate);
        result.setOfferCode(offerCode);
        result.setOfferTemplate(offerTemplate);
        result.setOperationDate(operationDate);
        result.setParameter1(parameter1);
        result.setParameter2(parameter2);
        result.setParameter3(parameter3);
        result.setParameterExtra(parameterExtra);
        result.setOrderNumber(orderNumber);
        result.setPriceplan(priceplan);
        result.setQuantity(quantity);
        result.setSeller(seller);
        result.setStartDate(startDate);
        result.setStatus(WalletOperationStatusEnum.OPEN);
        result.setSubscriptionDate(subscriptionDate);
        result.setTax(tax);
        result.setTaxPercent(taxPercent);
        result.setType(type);
        result.setUnitAmountTax(unitAmountTax);
        result.setUnitAmountWithoutTax(unitAmountWithoutTax);
        result.setUnitAmountWithTax(unitAmountWithTax);
        result.setRatingUnitDescription(ratingUnitDescription);
        result.setInputQuantity(inputQuantity);
        result.setInputUnitDescription(inputUnitDescription);
        result.setWallet(wallet);
        result.setEdr(edr);
        result.setSubscription(subscription);
        return result;
    }

    /**
     * @return Billing account associated to wallet operation
     */
    public BillingAccount getBillingAccount() {
        return billingAccount;
    }

    /**
     * @param billingAccount Billing account associated to wallet operation
     */
    public void setBillingAccount(BillingAccount billingAccount) {
        this.billingAccount = billingAccount;
    }

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
        if (offerTemplate != null && offerCode == null) {
            offerCode = offerTemplate.getCode();
        }
    }

    public String getInputUnitDescription() {
        return inputUnitDescription;
    }

    public void setInputUnitDescription(String inputUnitDescription) {
        this.inputUnitDescription = inputUnitDescription;
    }

    public String getRatingUnitDescription() {
        return ratingUnitDescription;
    }

    public void setRatingUnitDescription(String ratingUnitDescription) {
        this.ratingUnitDescription = ratingUnitDescription;
    }

    public BigDecimal getInputQuantity() {
        return inputQuantity;
    }

    public void setInputQuantity(BigDecimal inputQuantity) {
        this.inputQuantity = inputQuantity;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "WalletOperation [wallet=" + wallet + ", operationDate=" + operationDate + ", invoicingDate=" + invoicingDate + ", type=" + type + ", chargeInstance="
                + chargeInstance + ", currency=" + currency + ", taxPercent=" + taxPercent + ", unitAmountWithoutTax=" + unitAmountWithoutTax + ", unitAmountWithTax="
                + unitAmountWithTax + ", unitAmountTax=" + unitAmountTax + ", quantity=" + quantity + ", amountWithoutTax=" + amountWithoutTax + ", amountWithTax=" + amountWithTax
                + ", amountTax=" + amountTax + ", counter=" + counter + ", aggregatedServiceInstance=" + aggregatedServiceInstance + ", parameter1=" + parameter1 + ", parameter2="
                + parameter2 + ", parameter3=" + parameter3 + ", startDate=" + startDate + ", endDate=" + endDate + ", subscriptionDate=" + subscriptionDate + ", offerCode="
                + offerCode + ", status=" + status + ", seller=" + seller + ", priceplan=" + priceplan + ", reratedWalletOperation=" + reratedWalletOperation
                + ", inputUnitDescription=" + inputUnitDescription + ", ratingUnitDescription=" + ratingUnitDescription + ", inputQuantity=" + inputQuantity + ", billingAccount="
                + billingAccount + ", invoiceSubCategory=" + invoiceSubCategory + ", billingRun=" + billingRun + ", offerTemplate=" + offerTemplate + ", orderNumber=" + orderNumber
                + "]";
    }

    public BigDecimal getRawAmountWithoutTax() {
        return rawAmountWithoutTax;
    }

    public void setRawAmountWithoutTax(BigDecimal rawAmountWithoutTax) {
        this.rawAmountWithoutTax = rawAmountWithoutTax;
    }

    public BigDecimal getRawAmountWithTax() {
        return rawAmountWithTax;
    }

    public void setRawAmountWithTax(BigDecimal rawAmountWithTax) {
        this.rawAmountWithTax = rawAmountWithTax;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    /**
     * Compute derived amounts amountWithoutTax/amountWithTax/amountTax unitAmountWithoutTax/unitAmountWithTax/unitAmountTax
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

	public RatedTransaction getRatedTransaction() {
		return ratedTransaction;
	}

	public void setRatedTransaction(RatedTransaction ratedTransaction) {
		this.ratedTransaction = ratedTransaction;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}
}