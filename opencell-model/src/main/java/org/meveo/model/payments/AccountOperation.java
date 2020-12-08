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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.IWFEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditTarget;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.finance.AccountingWriting;

/**
 * Account operation
 *
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@WorkflowedEntity
@ObservableEntity
@Table(name = "ar_account_operation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_account_operation_seq"), })
@CustomFieldEntity(cftCodePrefix = "AccountOperation")
@NamedQueries({
        @NamedQuery(name = "AccountOperation.listAoToPayOrRefundWithoutCA", query = "Select ao  from AccountOperation as ao,PaymentMethod as pm  where ao.transactionCategory=:opCatToProcessIN and ao.type  in ('I','OCC') and" +
                "                               ao.matchingStatus ='O' and ao.customerAccount.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and pm.paymentType =:paymentMethodIN  and " +
                "                               pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN  "),
        @NamedQuery(name = "AccountOperation.listAoToPayOrRefundByCA", query = "Select ao  from AccountOperation as ao, PaymentMethod as pm where ao.transactionCategory=:opCatToProcessIN and ao.customerAccount.id=:caIdIN and ao.type  "
                + "                             in ('I','OCC')  and ao.customerAccount.id = pm.customerAccount.id " +
                "                               (ao.matchingStatus ='O' or ao.matchingStatus ='P') and ao.customerAccount.excludedFromPayment = false and " +
                "                                pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN  "),
        @NamedQuery(name = "AccountOperation.listAoToPayOrRefundWithoutCAbySeller", query = "Select ao  from AccountOperation as ao,PaymentMethod as pm  where ao.seller =:sellerIN and ao.transactionCategory=:opCatToProcessIN and ao.type  in ('I','OCC') and" +
                "                               ao.matchingStatus ='O' and ao.customerAccount.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and pm.paymentType =:paymentMethodIN  and " +
                "                                pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN  "),
        @NamedQuery(name = "AccountOperation.listAoToPayOrRefundBySeller", query = "Select ao  from AccountOperation as ao,PaymentMethod as pm  where ao.seller =:sellerIN and ao.transactionCategory=:opCatToProcessIN and ao.customerAccount.id=:caIdIN and ao.type  "
                + "                             in ('I','OCC')  and " +
                "                               (ao.matchingStatus ='O' or ao.matchingStatus ='P') and ao.customerAccount.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and pm.paymentType =:paymentMethodIN  and " +
                "                               pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN  "),
        @NamedQuery(name = "AccountOperation.countUnmatchedAOByCA", query = "Select count(*) from AccountOperation as ao where ao.unMatchingAmount <> 0 and ao"
                + ".customerAccount=:customerAccount"),
        @NamedQuery(name = "AccountOperation.listByCustomerAccount", query = "select ao from AccountOperation ao inner join ao.customerAccount ca where ca=:customerAccount")
})
public class AccountOperation extends BusinessEntity implements ICustomFieldEntity, ISearchable, IWFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Operation due date
     */
    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    /**
     * Operation type
     */
    @Column(name = "transaction_type", insertable = false, updatable = false, length = 31)
    @Size(max = 31)
    private String type;

    /**
     * Operation date
     */
    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    /**
     * Operation category Debit/Credit
     */
    @Column(name = "transaction_category")
    @Enumerated(EnumType.STRING)
    private OperationCategoryEnum transactionCategory;

    /**
     * Reference
     */
    @Column(name = "reference", length = 255)
    @Size(max = 255)
    private String reference;

    /**
     * Accounting code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /**
     * List of associated accounting writing
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "accountOperations")
    private List<AccountingWriting> accountingWritings = new ArrayList<>();

    /**
     * Deprecated in 5.2. Use accountingCode instead
     */
    @Deprecated
    @Column(name = "account_code_client_side", length = 255)
    @Size(max = 255)
    private String accountCodeClientSide;

    /**
     * Amount with tax
     */
    @Column(name = "amount", precision = 23, scale = 12)
    private BigDecimal amount;

    /**
     * Amount without tax
     */
    @Column(name = "amount_without_tax", precision = 23, scale = 12)
    private BigDecimal amountWithoutTax;

    /**
     * Tax amount
     */
    @Column(name = "tax_amount", precision = 23, scale = 12)
    private BigDecimal taxAmount;

    /**
     * Matched amount
     */
    @Column(name = "matching_amount", precision = 23, scale = 12)
    private BigDecimal matchingAmount = BigDecimal.ZERO;

    /**
     * Unmatched amount
     */
    @Column(name = "un_matching_amount", precision = 23, scale = 12)
    private BigDecimal unMatchingAmount = BigDecimal.ZERO;

    /**
     * Associated Customer account
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    /**
     * Matching status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "matching_status")
    private MatchingStatusEnum matchingStatus;

    /**
     * A list of matches
     */
    @OneToMany(mappedBy = "accountOperation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchingAmount> matchingAmounts = new ArrayList<>();

    /**
     * Associated order number. Multiple orders separated by '|'.
     */
    @Column(name = "order_num")
    private String orderNumber;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;

    /**
     * Bank LOT number
     */
    @Column(name = "bank_lot", columnDefinition = "text")
    private String bankLot;

    /**
     * Bank reference
     */
    @Column(name = "bank_reference", length = 255)
    @Size(max = 255)
    private String bankReference;

    /**
     * Deposit timestamp
     */
    @Column(name = "deposit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date depositDate;

    /**
     * Bank collection timestamp
     */
    @Column(name = "bank_collection_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bankCollectionDate;

    /**
     * Payment method
     */
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    /**
     * Associated invoices
     */
    @OneToMany(mappedBy = "recordedInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices;

    /**
     * Additional payment information - // IBAN for direct debit
     */
    @Column(name = "payment_info", length = 255)
    @Size(max = 255)
    private String paymentInfo;

    /**
     * Additional payment information - bank code
     */
    @Column(name = "payment_info1", length = 255)
    @Size(max = 255)
    private String paymentInfo1;

    /**
     * Additional payment information - Code box/code guichet
     */
    @Column(name = "payment_info2", length = 255)
    @Size(max = 255)
    private String paymentInfo2;

    /**
     * Additional payment information - Account number
     */
    @Column(name = "payment_info3", length = 255)
    @Size(max = 255)
    private String paymentInfo3;

    /**
     * Additional payment information - RIB
     */
    @Column(name = "payment_info4", length = 255)
    @Size(max = 255)
    private String paymentInfo4;

    /**
     * Additional payment information - Bank name
     */
    @Column(name = "payment_info5", length = 255)
    @Size(max = 255)
    private String paymentInfo5;

    /**
     * Additional payment information - BIC
     */
    @Column(name = "payment_info6", length = 255)
    @Size(max = 255)
    private String paymentInfo6;

    /**
     * Billing account name
     */
    @Column(name = "billing_account_name", length = 255)
    @Size(max = 255)
    private String billingAccountName;

    /**
     * DD request item
     */
    @ManyToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ddrequest_item_id")
    private DDRequestItem ddRequestItem;


    @ManyToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "rejected_payment_id")
    private RejectedPayment rejectedPayment;

    @ManyToOne(optional = true)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @OneToOne(mappedBy = "accountOperation")
    private PaymentVentilation paymentVentilation;

    /**
     * Associated Subscription
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;


    @ManyToMany
    @JoinTable(name = "ar_ao_payment_histories", joinColumns = @JoinColumn(name = "ao_id"), inverseJoinColumns = @JoinColumn(name = "history_id"))
    private List<PaymentHistory> paymentHistories = new ArrayList<PaymentHistory>();

    /**
     * A collection date.
     */
    @Column(name = "collection_date")
    @AuditTarget(type = AuditChangeTypeEnum.OTHER, history = true, notif = true)
    private Date collectionDate;

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public OperationCategoryEnum getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(OperationCategoryEnum transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;

    }

    public BigDecimal getMatchingAmount() {
        return matchingAmount;
    }

    public void setMatchingAmount(BigDecimal matchingAmount) {
        if(matchingAmount == null) {
            this.matchingAmount =BigDecimal.ZERO;
        }else {
            this.matchingAmount = matchingAmount;
        }
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getUnMatchingAmount() {
        return unMatchingAmount;
    }

    public void setUnMatchingAmount(BigDecimal unMatchingAmount) {
       if(unMatchingAmount == null) {
           this.unMatchingAmount = BigDecimal.ZERO;
       }else {
           this.unMatchingAmount = unMatchingAmount;
       }
    }

    public CustomerAccount getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(CustomerAccount customerAccount) {
        this.customerAccount = customerAccount;
    }

    public String getAccountCodeClientSide() {
        return accountCodeClientSide;
    }

    public void setAccountCodeClientSide(String accountCodeClientSide) {
        this.accountCodeClientSide = accountCodeClientSide;
    }

    public MatchingStatusEnum getMatchingStatus() {
        return matchingStatus;
    }

    public void setMatchingStatus(MatchingStatusEnum matchingStatus) {
        this.matchingStatus = matchingStatus;
    }

    public PaymentVentilation getPaymentVentilation() {
        return paymentVentilation;
    }

    public void setPaymentVentilation(PaymentVentilation paymentVentilation) {
        this.paymentVentilation = paymentVentilation;
    }

    @Override
    public int hashCode() {
        return 961 + ("AccountOperation" + code).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof AccountOperation)) {
            return false;
        }

        AccountOperation other = (AccountOperation) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        return true;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setMatchingAmounts(List<MatchingAmount> matchingAmounts) {
        this.matchingAmounts = matchingAmounts;
    }

    public List<MatchingAmount> getMatchingAmounts() {
        return matchingAmounts;
    }

    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
    	if (uuid == null) {
    		uuid = UUID.randomUUID().toString();
    	}
    }

    @Override
    public String getUuid() {
    	setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        return null;
    }

    /**
     * @return the orderNumber
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * @param orderNumber the orderNumber to set
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getBankLot() {
        return bankLot;
    }

    public void setBankLot(String bankLot) {
        this.bankLot = bankLot;
    }

    public String getBankReference() {
        return bankReference;
    }

    public void setBankReference(String bankReference) {
        this.bankReference = bankReference;
    }

    public Date getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    public Date getBankCollectionDate() {
        return bankCollectionDate;
    }

    public void setBankCollectionDate(Date bankCollectionDate) {
        this.bankCollectionDate = bankCollectionDate;
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public List<AccountingWriting> getAccountingWritings() {
		return accountingWritings;
	}

	public void setAccountingWritings(List<AccountingWriting> accountingWritings) {
		this.accountingWritings = accountingWritings;
	}

    /**
     * @return the amountWithoutTax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * @param amountWithoutTax the amountWithoutTax to set
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * @return the taxAmount
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * @param taxAmount the taxAmount to set
     */
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * @return the paymentMethod
     */
    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * @param paymentMethod the paymentMethod to set
     */
    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    /**
     * @return the paymentInfo
     */
    public String getPaymentInfo() {
        return paymentInfo;
    }

    /**
     * @param paymentInfo the paymentInfo to set
     */
    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    /**
     * @return the paymentInfo1
     */
    public String getPaymentInfo1() {
        return paymentInfo1;
    }

    /**
     * @param paymentInfo1 the paymentInfo1 to set
     */
    public void setPaymentInfo1(String paymentInfo1) {
        this.paymentInfo1 = paymentInfo1;
    }

    /**
     * @return the paymentInfo2
     */
    public String getPaymentInfo2() {
        return paymentInfo2;
    }

    /**
     * @param paymentInfo2 the paymentInfo2 to set
     */
    public void setPaymentInfo2(String paymentInfo2) {
        this.paymentInfo2 = paymentInfo2;
    }

    /**
     * @return the paymentInfo3
     */
    public String getPaymentInfo3() {
        return paymentInfo3;
    }

    /**
     * @param paymentInfo3 the paymentInfo3 to set
     */
    public void setPaymentInfo3(String paymentInfo3) {
        this.paymentInfo3 = paymentInfo3;
    }

    /**
     * @return the paymentInfo4
     */
    public String getPaymentInfo4() {
        return paymentInfo4;
    }

    /**
     * @param paymentInfo4 the paymentInfo4 to set
     */
    public void setPaymentInfo4(String paymentInfo4) {
        this.paymentInfo4 = paymentInfo4;
    }

    /**
     * @return the paymentInfo5
     */
    public String getPaymentInfo5() {
        return paymentInfo5;
    }

    /**
     * @param paymentInfo5 the paymentInfo5 to set
     */
    public void setPaymentInfo5(String paymentInfo5) {
        this.paymentInfo5 = paymentInfo5;
    }

    /**
     * @return the paymentInfo6
     */
    public String getPaymentInfo6() {
        return paymentInfo6;
    }

    /**
     * @param paymentInfo6 the paymentInfo6 to set
     */
    public void setPaymentInfo6(String paymentInfo6) {
        this.paymentInfo6 = paymentInfo6;
    }

    /**
     * @return the billingAccountName
     */
    public String getBillingAccountName() {
        return billingAccountName;
    }

    /**
     * @param billingAccountName the billingAccountName to set
     */
    public void setBillingAccountName(String billingAccountName) {
        this.billingAccountName = billingAccountName;
    }

    /**
     * @return the ddRequestItem
     */
    public DDRequestItem getDdRequestItem() {
        return ddRequestItem;
    }

    /**
     * @param ddRequestItem the ddRequestItem to set
     */
    public void setDdRequestItem(DDRequestItem ddRequestItem) {
        this.ddRequestItem = ddRequestItem;
    }

    /**
     * @return the rejectedPayment
     */
    public RejectedPayment getRejectedPayment() {
        return rejectedPayment;
    }

    /**
     * @param rejectedPayment the rejectedPayment to set
     */
    public void setRejectedPayment(RejectedPayment rejectedPayment) {
        this.rejectedPayment = rejectedPayment;
    }

    /**
     * @return the seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * @param seller the seller to set
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public List<PaymentHistory> getPaymentHistories() {
        return paymentHistories;
    }

    public void setPaymentHistories(List<PaymentHistory> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }

    /**
     * Gets Collection Date
     *
     * @return a CollectionDate
     */
    public Date getCollectionDate() {
        return collectionDate;
    }

    /**
     * Sets Collection Date
     *
     * @param collectionDate
     */
    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }
}
