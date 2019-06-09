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
package org.meveo.model.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ISearchable;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.custom.CustomFieldValues;
import org.meveo.model.persistence.CustomFieldValuesConverter;

/**
 * Account Transaction.
 *
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 * 
 */
@Entity
@ObservableEntity
@Table(name = "ar_account_operation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_account_operation_seq"), })
@CustomFieldEntity(cftCodePrefix = "ACC_OP")
@NamedQueries({
        @NamedQuery(name = "AccountOperation.listAoToPayWithoutCA", query = "Select ao  from AccountOperation as ao,PaymentMethod as pm  where ao.transactionCategory='DEBIT' and " + 
            "                               ao.matchingStatus ='O' and ao.customerAccount.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and pm.paymentType =:paymentMethodIN  and " + 
            "                               ao.paymentMethod =:paymentMethodIN  and pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN  "),  
        @NamedQuery(name = "AccountOperation.listAoToPay", query = "Select ao  from AccountOperation as ao,PaymentMethod as pm  where ao.customerAccount.id=:caIdIN  and ao.transactionCategory='DEBIT' and " + 
                "                               (ao.matchingStatus ='O' or ao.matchingStatus ='P') and ao.customerAccount.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and pm.paymentType =:paymentMethodIN  and " + 
                "                               ao.paymentMethod =:paymentMethodIN  and pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN  "),       
        @NamedQuery(name = "AccountOperation.listAOIdsToRefund", query = "Select ao  from AccountOperation as ao,PaymentMethod as pm  where ao.customerAccount.id=:caIdIN and ao.type not in ('P','AP') and " +
                "                               ao.transactionCategory='CREDIT' and ao.matchingStatus ='O' and ao.customerAccount.excludedFromPayment = false and ao.customerAccount.id = pm.customerAccount.id and " + 
                "                               pm.paymentType =:paymentMethodIN  and ao.paymentMethod =:paymentMethodIN  and pm.preferred is true and ao.dueDate >=:fromDueDateIN and ao.dueDate <:toDueDateIN   "),
        @NamedQuery(name = "AccountOperation.countUnmatchedAOByCA", query = "Select count(*) from AccountOperation as ao where ao.unMatchingAmount <> 0 and ao.customerAccount=:customerAccount")
})
public class AccountOperation extends AuditableEntity implements ICustomFieldEntity, ISearchable {

    private static final long serialVersionUID = 1L;

    @Column(name = "due_date")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "transaction_type", insertable = false, updatable = false, length = 31)
    @Size(max = 31)
    private String type;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "transaction_category")
    @Enumerated(EnumType.STRING)
    private OperationCategoryEnum transactionCategory;

    @Column(name = "reference", length = 255)
    @Size(max = 255)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @Deprecated
    @Column(name = "account_code_client_side", length = 255)
    @Size(max = 255)
    private String accountCodeClientSide;

    @Column(name = "amount", precision = 23, scale = 12)
    private BigDecimal amount;

    @Column(name = "amount_without_tax", precision = 23, scale = 12)
    private BigDecimal amountWithoutTax;

    @Column(name = "tax_amount", precision = 23, scale = 12)
    private BigDecimal taxAmount;

    @Column(name = "matching_amount", precision = 23, scale = 12)
    private BigDecimal matchingAmount = BigDecimal.ZERO;

    @Column(name = "un_matching_amount", precision = 23, scale = 12)
    private BigDecimal unMatchingAmount = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_account_id")
    private CustomerAccount customerAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_status")
    private MatchingStatusEnum matchingStatus;

    @OneToMany(mappedBy = "accountOperation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatchingAmount> matchingAmounts = new ArrayList<>();

    @Column(name = "occ_code", length = 255)
    @Size(max = 255)
    private String occCode;

    @Column(name = "occ_description", length = 255)
    @Size(max = 255)
    private String occDescription;

    @Column(name = "order_num")
    private String orderNumber;// order number, '|' will be used as seperator if many orders

    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    // @Type(type = "json")
    @Convert(converter = CustomFieldValuesConverter.class)
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    @Column(name = "bank_lot", columnDefinition = "text")
    private String bankLot;

    @Column(name = "bank_reference", length = 255)
    @Size(max = 255)
    private String bankReference;

    @Column(name = "deposit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date depositDate;

    @Column(name = "bank_collection_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bankCollectionDate;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;
    
    @OneToMany(mappedBy = "recordedInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices;
    
    @Transient
    private String code;

    @Transient
    private String description;
    
    @Column(name = "payment_info", length = 255)
    @Size(max = 255)
    private String paymentInfo;// IBAN for direct debit

    @Column(name = "payment_info1", length = 255)
    @Size(max = 255)
    private String paymentInfo1;// bank code

    @Column(name = "payment_info2", length = 255)
    @Size(max = 255)
    private String paymentInfo2;// code guichet

    @Column(name = "payment_info3", length = 255)
    @Size(max = 255)
    private String paymentInfo3;// Num compte

    @Column(name = "payment_info4", length = 255)
    @Size(max = 255)
    private String paymentInfo4;// RIB

    @Column(name = "payment_info5", length = 255)
    @Size(max = 255)
    private String paymentInfo5;// bankName

    @Column(name = "payment_info6", length = 255)
    @Size(max = 255)
    private String paymentInfo6;// bic

    @Column(name = "billing_account_name", length = 255)
    @Size(max = 255)
    private String billingAccountName;
   
    @ManyToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ddrequest_item_id")
    private DDRequestItem ddRequestItem;
   
    @ManyToOne(optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "rejected_payment_id")
    private RejectedPayment rejectedPayment;
    
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
        this.matchingAmount = matchingAmount;
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
        this.unMatchingAmount = unMatchingAmount;
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

    public String getOccCode() {
        return occCode;
    }

    public void setOccCode(String occCode) {
        this.occCode = occCode;
    }

    public String getOccDescription() {
        return occDescription;
    }

    public void setOccDescription(String occDescription) {
        this.occDescription = occDescription;
    }

    @Override
    public int hashCode() {
        return 961 + ("AccountOperation" + occCode).hashCode();
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
        if (occCode == null) {
            if (other.occCode != null)
                return false;
        } else if (!occCode.equals(other.occCode))
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

    public String getCode() {
        return occCode;
    }

    public void setCode(String code) {
        this.occCode = code;
    }

    public String getDescription() {
        return occDescription;
    }

    public void setDescription(String description) {
        this.occDescription = description;
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
    
    
    
}
