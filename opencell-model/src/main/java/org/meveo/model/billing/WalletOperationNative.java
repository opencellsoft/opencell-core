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

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * Consumption operation
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Entity
@Table(name = "billing_wallet_operation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("W")
@NamedQueries({ @NamedQuery(name = "WalletOperationNative.listConvertToRTs", query = "SELECT o FROM WalletOperationNative o WHERE o.status='OPEN' and o.id<=:maxId order by billingAccountId") })
public class WalletOperationNative extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Operation code - corresponds in majority of cases to charge code
     */
    @Column(name = "code")
    @Size(max = 255)
    private String code;

    /**
     * Description - corresponds in majority of cases to charge description
     */
    @Column(name = "description")
    private String description;

//    /**
//     * creation timestamp
//     */
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "created")
//    private Date created;
//
//    /**
//     * Last status change timestamp
//     */
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "updated")
//    private Date updated;

    /**
     * The wallet on which the operation is applied.
     */
    @Column(name = "wallet_id")
    private Long walletId;

//    /**
//     * The old wallet on which the operation is applied. (in case of subscription transfer)
//     */
//    @Column(name = "old_wallet_id")
//    private Long oldWalletId;

    /**
     * Operation date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "operation_date")
    private Date operationDate;

    /**
     * Date past which a charge can be included in the invoice. Allows to exclude charges from the current billing cycle by specifying a future date.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "invoicing_date")
    private Date invoicingDate;

//    /**
//     * Operation type Credit/Debit
//     */
//    @Enumerated(EnumType.STRING)
//    @Column(name = "credit_debit_flag")
//    private OperationTypeEnum type;

    /**
     * Associated charge instance
     */
    @Column(name = "charge_instance_id", nullable = false)
    private Long chargeInstanceId;

    /**
     * Currency of operation rated amounts
     */
    @Column(name = "currency_id")
    private Long currencyId;

    /**
     * Tax applied. An absence of tax class and presence of tax means that tax was set manually and should not be recalculated at invoicing time.
     */
    @Column(name = "tax_id", nullable = false)
    @NotNull
    private Long taxId;

    /**
     * Tax percent applied
     */
    @Column(name = "tax_percent", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
    private BigDecimal taxPercent;

    /**
     * Charge tax class. An absence of tax class and presence of tax means that tax was set manually and should not be recalculated at invoicing time.
     **/
    @Column(name = "tax_class_id", nullable = false)
    private Long taxClassId;

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

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS, nullable = false)
    @NotNull
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

//    /**
//     * Counter instance to track consumption
//     */
//    @Column(name = "counter_id")
//    private Long counterId;

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
    @Type(type = "longText")
    @Column(name = "parameter_extra")
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

//    /**
//     * Service/charge subscription timestamp
//     */
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "subscription_date")
//    private Date subscriptionDate;

//    /**
//     * Offer code
//     */
//    @Column(name = "offer_code", length = 255)
//    @Size(max = 255, min = 1)
//    protected String offerCode;

    /**
     * Seller associated to operation
     */
    @Column(name = "seller_id")
    private Long sellerId;

    /**
     * Price plan applied during rating
     */
    @Column(name = "priceplan_id")
    private Long priceplanId;

//    /**
//     * Wallet operation that rerates this wallet operation
//     */
//    @Column(name = "reratedwalletoperation_id")
//    private Long reratedWalletOperationId;
//
//    /**
//     * Wallet operation that this wallet operation refunds
//     */
//    @Column(name = "refunds_wo_id")
//    private Long refundsWalletOperationId;

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
     * input_unit_unitOfMeasure
     */
    @Column(name = "input_unitofmeasure")
    private Long inputUnitOfMeasureId;

    /**
     * rating_unit_unitOfMeasure
     */
    @Column(name = "rating_unitofmeasure")
    private Long ratingUnitOfMeasureId;

    /**
     * Input quantity
     */
    @Column(name = "input_quantity", precision = BaseEntity.NB_PRECISION, scale = BaseEntity.NB_DECIMALS)
    private BigDecimal inputQuantity;

    /**
     * EDR that produced this operation
     */
    @Column(name = "edr_id")
    private Long edrId;

    /**
     * Order number in cases when operation was originated from an order
     */
    @Column(name = "order_number", length = 100)
    @Size(max = 100)
    private String orderNumber;

    /**
     * Raw rating amount without tax from Price plan. Might differ from amountWitouttax when minimumAmount is set on a price plan.
     */
    @Column(name = "raw_amount_without_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithoutTax;

    /**
     * Raw rating amount with tax from Price plan. Might differ from amountWitouttax when minimumAmount is set on a price plan.
     */
    @Column(name = "raw_amount_with_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal rawAmountWithTax;

    /**
     * Associated Invoice subcategory
     */
    @Column(name = "invoice_sub_category_id")
    private Long invoiceSubCategoryId;

    /**
     * Associated Subscription when operation is tied to subscription.
     */
    @Column(name = "subscription_id")
    protected Long subscriptionId;

    /**
     * Service instance that Wallet operation is applied to
     */
    @Column(name = "service_instance_id")
    private Long serviceInstanceId;

    /**
     * Billing account associated to wallet operation
     */
    @Column(name = "billing_account_id", nullable = false)
    @NotNull
    private Long billingAccountId;

    /**
     * User account associated to wallet operation
     */
    @Column(name = "user_account_id")
    private Long userAccountId;

    /**
     * Offer template
     */
    @Column(name = "offer_id")
    private Long offerTemplateId;

    /**
     * Rated transaction
     */
    @Column(name = "rated_transaction_id")
    protected Long ratedTransactionId;

    /**
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalletOperationStatusEnum status;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "jsonb")
    private CustomFieldValues cfValues;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Accounting code
     */
    @Column(name = "accounting_code_id")
    private Long accountingCodeId;

    /**
     * Sorting index
     */
    @Column(name = "sort_index")
    private Integer sortIndex;

    /**
     * Processing error reason
     */
    @Type(type = "longText")
    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "accounting_article_id")
    private Long accountingArticleId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_version_id")
    private Long productVersionId;

    @Column(name = "order_lot_id")
    private Long orderLotId;

    @Column(name = "discounted_wallet_operation_id")
    private Long discountedWalletOperation;

    @Column(name = "discount_plan_id")
    private Long discountPlanId;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_plan_type", length = 50)
    private DiscountPlanItemTypeEnum discountPlanType;

    @Column(name = "discount_plan_item_id")
    private Long discountPlanItemId;

    /** The amount after discount **/
    @Column(name = "discounted_amount")
    private BigDecimal discountedAmount;

    /** The amount after discount **/
    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "rules_contract_id")
    private Long rulesContractId;

//    @Column(name = "price_plan_matrix_version_id")
//    private Long pricePlanMatrixVersionId;
//
//    @Column(name = "price_plan_matrix_line_id")
//    private Long pricePlanMatrixLineId;

//    @Column(name = "contract_id")
//    private Long contractId;
//
//    @Column(name = "contract_line_id")
//    private Long contractLineId;

    /**
     * Constructor
     */
    public WalletOperationNative() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

//    public Long getOldWalletId() {
//        return oldWalletId;
//    }
//
//    public void setOldWalletId(Long oldWalletId) {
//        this.oldWalletId = oldWalletId;
//    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    /**
     * @return Date past which a charge can be included in the invoice. Allows to exclude charges from the current billing cycle by specifying a future date.
     */
    public Date getInvoicingDate() {
        return invoicingDate;
    }

    /**
     * @param invoicingDate Date past which a charge can be included in the invoice. Allows to exclude charges from the current billing cycle by specifying a future date.
     */
    public void setInvoicingDate(Date invoicingDate) {
        this.invoicingDate = invoicingDate;
    }

//    public OperationTypeEnum getType() {
//        return type;
//    }
//
//    public void setType(OperationTypeEnum type) {
//        this.type = type;
//    }

    public Long getChargeInstanceId() {
        return chargeInstanceId;
    }

    public void setChargeInstanceId(Long chargeInstanceId) {
        this.chargeInstanceId = chargeInstanceId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    /**
     * @return Tax applied
     */
    public Long getTaxId() {
        return taxId;
    }

    /**
     * @param taxId Tax applied
     */
    public void setTaxId(Long taxId) {
        this.taxId = taxId;
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

//    public Long getCounterId() {
//        return counterId;
//    }
//
//    public void setCounterId(Long counterId) {
//        this.counterId = counterId;
//    }

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

//    public Date getSubscriptionDate() {
//        return subscriptionDate;
//    }
//
//    public void setSubscriptionDate(Date subscriptionDate) {
//        this.subscriptionDate = subscriptionDate;
//    }

    /**
     * @return Seller associated to wallet operation
     */
    public Long getSellerId() {
        return sellerId;
    }

    /**
     * @param sellerId Seller associated to wallet operation
     */
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

//    public String getOfferCode() {
//        return offerCode;
//    }
//
//    public void setOfferCode(String offerCode) {
//        this.offerCode = offerCode;
//    }

    public Long getPriceplanId() {
        return priceplanId;
    }

    public void setPriceplanId(Long priceplanId) {
        this.priceplanId = priceplanId;
    }

//    public Long getReratedWalletOperationId() {
//        return reratedWalletOperationId;
//    }
//
//    public void setReratedWalletOperationId(Long reratedWalletOperationId) {
//        this.reratedWalletOperationId = reratedWalletOperationId;
//    }

//    /**
//     * @return Wallet operation that this wallet operation refunds
//     */
//    public Long getRefundsWalletOperationId() {
//        return refundsWalletOperationId;
//    }
//
//    /**
//     * @param refundsWalletOperationId Wallet operation that this wallet operation refunds
//     */
//    public void setRefundsWalletOperationId(Long refundsWalletOperationId) {
//        this.refundsWalletOperationId = refundsWalletOperationId;
//    }

    public Long getEdrId() {
        return edrId;
    }

    public void setEdrId(Long edrId) {
        this.edrId = edrId;
    }

    /**
     * @return Billing account associated to wallet operation
     */
    public Long getBillingAccountId() {
        return billingAccountId;
    }

    /**
     * @param billingAccountId Billing account associated to wallet operation
     */
    public void setBillingAccountId(Long billingAccountId) {
        this.billingAccountId = billingAccountId;
    }

    /**
     * @return User account associated to rated transaction
     */
    public Long getUserAccountId() {
        return userAccountId;
    }

    /**
     * @param userAccountId User account associated to rated transaction
     */
    public void setUserAccountId(Long userAccountId) {
        this.userAccountId = userAccountId;
    }

    public Long getInvoiceSubCategoryId() {
        return invoiceSubCategoryId;
    }

    public void setInvoiceSubCategoryId(Long invoiceSubCategoryId) {
        this.invoiceSubCategoryId = invoiceSubCategoryId;
    }

    public Long getOfferTemplateId() {
        return offerTemplateId;
    }

    public void setOfferTemplateId(Long offerTemplateId) {
        this.offerTemplateId = offerTemplateId;
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

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     * @return Service instance that Wallet operation is applied to
     */
    public Long getServiceInstanceId() {
        return serviceInstanceId;
    }

    /**
     * @param serviceInstanceId Service instance that Wallet operation is applied to
     */
    public void setServiceInstanceId(Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    /**
     * @return Rated transaction
     */
    public Long getRatedTransactionId() {
        return ratedTransactionId;
    }

    /**
     * @param ratedTransactionId Rated transaction
     */
    public void setRatedTransactionId(Long ratedTransactionId) {
        this.ratedTransactionId = ratedTransactionId;
    }

    /**
     * @return Processing status
     */
    public WalletOperationStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Processing status
     */
    public void setStatus(WalletOperationStatusEnum status) {
        this.status = status;
    }

//    /**
//     * @return Last status change date
//     */
//    public Date getUpdated() {
//        return updated;
//    }
//
//    /**
//     * @param updated Last status change date
//     */
//    public void setUpdated(Date updated) {
//        this.updated = updated;
//    }
//
//    /**
//     * @return creation date
//     */
//    public Date getCreated() {
//        return created;
//    }
//
//    /**
//     * @param created creation date
//     */
//    public void setCreated(Date created) {
//        this.created = created;
//    }

    public Long getInputUnitOfMeasureId() {
        return inputUnitOfMeasureId;
    }

    public void setInputUnitOfMeasureId(Long inputUnitOfMeasureId) {
        this.inputUnitOfMeasureId = inputUnitOfMeasureId;
    }

    public Long getRatingUnitOfMeasureId() {
        return ratingUnitOfMeasureId;
    }

    public void setRatingUnitOfMeasureId(Long ratingUnitOfMeasureId) {
        this.ratingUnitOfMeasureId = ratingUnitOfMeasureId;
    }

    /**
     * @return Charge tax class
     */
    public Long getTaxClassId() {
        return taxClassId;
    }

    /**
     * @param taxClassId Charge tax class
     */
    public void setTaxClassId(Long taxClassId) {
        this.taxClassId = taxClassId;
    }

    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return Accounting code
     */
    public Long getAccountingCodeId() {
        return accountingCodeId;
    }

    /**
     * @param accountingCodeId Accounting code
     */
    public void setAccountingCodeId(Long accountingCodeId) {
        this.accountingCodeId = accountingCodeId;
    }

    /**
     * Gets the sorting index.
     *
     * @return the sorting index
     */
    public Integer getSortIndex() {
        return sortIndex;
    }

    /**
     * Sets the sorting index.
     *
     * @param sortIndex the sorting index
     */
    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    /**
     * @return Processing error reason
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * @param rejectReason Processing error reason
     */
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Long getAccountingArticleId() {
        return accountingArticleId;
    }

    public void setAccountingArticleId(Long accountingArticleId) {
        this.accountingArticleId = accountingArticleId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductVersionId() {
        return productVersionId;
    }

    public void setProductVersionId(Long productVersionId) {
        this.productVersionId = productVersionId;
    }

    public Long getOrderLotId() {
        return orderLotId;
    }

    public void setOrderLotId(Long orderLotId) {
        this.orderLotId = orderLotId;
    }

    public Long getDiscountPlanId() {
        return discountPlanId;
    }

    public void setDiscountPlanId(Long discountPlanId) {
        this.discountPlanId = discountPlanId;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public DiscountPlanItemTypeEnum getDiscountPlanType() {
        return discountPlanType;
    }

    public void setDiscountPlanType(DiscountPlanItemTypeEnum discountPlanType) {
        this.discountPlanType = discountPlanType;
    }

    public Long getDiscountPlanItemId() {
        return discountPlanItemId;
    }

    public void setDiscountPlanItemId(Long discountPlanItemId) {
        this.discountPlanItemId = discountPlanItemId;
    }

    public BigDecimal getDiscountedAmount() {
        return discountedAmount;
    }
    
    public void setDiscountedAmount(BigDecimal discountedAmount) {
        this.discountedAmount = discountedAmount;
    }
    
    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Long getRulesContractId() {
        return rulesContractId;
    }

    public void setRulesContractId(Long rulesContractId) {
        this.rulesContractId = rulesContractId;
    }
}