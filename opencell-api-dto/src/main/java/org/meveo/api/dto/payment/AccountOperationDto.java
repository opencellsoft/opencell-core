package org.meveo.api.dto.payment;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;

/**
 * The Class AccountOperationDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "AccountOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4329241417200680028L;

    /** The id. */
    private Long id;
    
    /** The due date. */
    private Date dueDate;
    
    /** The type. */
    private String type;
    
    /** The transaction date. */
    private Date transactionDate;
    
    /** The transaction category. */
    private OperationCategoryEnum transactionCategory;
    
    /** The reference. */
    private String reference;
    
    /** The account code. */
    @Deprecated
    private String accountCode;
    
    /** The accounting code. */
    private String accountingCode;
    
    /** The account code client side. */
    @Deprecated
    private String accountCodeClientSide;
    
    /** The amount. */
    private BigDecimal amount;
    
    /** The amount without tax. */
    private BigDecimal amountWithoutTax;
    
    /** The tax amount. */
    private BigDecimal taxAmount;
    
    /** The matching amount. */
    private BigDecimal matchingAmount = BigDecimal.ZERO;
    
    /** The un matching amount. */
    private BigDecimal unMatchingAmount = BigDecimal.ZERO;
    
    /** The matching status. */
    private MatchingStatusEnum matchingStatus;
    
    /** The occ code. */
    private String occCode;
    
    /** The occ description. */
    private String occDescription;
    
    /** The customer account. */
    private String customerAccount;
    
    /** The excluded from dunning. */
    private Boolean excludedFromDunning;
    
    /** The order number. */
    // order number, '|' is used as seperator if many orders
    private String orderNumber;

    /** The matching amounts. */
    private MatchingAmountsDto matchingAmounts;
    
    /** The other credit and charge. */
    private OtherCreditAndChargeDto otherCreditAndCharge;
    
    /** The recorded invoice. */
    private RecordedInvoiceDto recordedInvoice;
    
    /** The rejected payment. */
    private RejectedPaymentDto rejectedPayment;

    /** The bank lot. */
    private String bankLot;
    
    /** The bank reference. */
    private String bankReference;
    
    /** The bank collection date. */
    private Date bankCollectionDate;
    
    /** The deposit date. */
    private Date depositDate;
    
    /** The payment method. */
    private String paymentMethod;

    /** The custom fields. */
    private CustomFieldsDto customFields;

    /**
     * Gets the custom fields.
     *
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * Sets the custom fields.
     *
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * Gets the due date.
     *
     * @return the due date
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the due date.
     *
     * @param dueDate the new due date
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the transaction date.
     *
     * @return the transaction date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transaction date.
     *
     * @param transactionDate the new transaction date
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the transaction category.
     *
     * @return the transaction category
     */
    public OperationCategoryEnum getTransactionCategory() {
        return transactionCategory;
    }

    /**
     * Sets the transaction category.
     *
     * @param transactionCategory the new transaction category
     */
    public void setTransactionCategory(OperationCategoryEnum transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    /**
     * Gets the reference.
     *
     * @return the reference
     */
    public String getReference() {
        return reference;
    }

    /**
     * Sets the reference.
     *
     * @param reference the new reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Gets the account code client side.
     *
     * @return the account code client side
     */
    public String getAccountCodeClientSide() {
        return accountCodeClientSide;
    }

    /**
     * Sets the account code client side.
     *
     * @param accountCodeClientSide the new account code client side
     */
    public void setAccountCodeClientSide(String accountCodeClientSide) {
        this.accountCodeClientSide = accountCodeClientSide;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the matching amount.
     *
     * @return the matching amount
     */
    public BigDecimal getMatchingAmount() {
        return matchingAmount;
    }

    /**
     * Sets the matching amount.
     *
     * @param matchingAmount the new matching amount
     */
    public void setMatchingAmount(BigDecimal matchingAmount) {
        this.matchingAmount = matchingAmount;
    }

    /**
     * Gets the un matching amount.
     *
     * @return the un matching amount
     */
    public BigDecimal getUnMatchingAmount() {
        return unMatchingAmount;
    }

    /**
     * Sets the un matching amount.
     *
     * @param unMatchingAmount the new un matching amount
     */
    public void setUnMatchingAmount(BigDecimal unMatchingAmount) {
        this.unMatchingAmount = unMatchingAmount;
    }

    /**
     * Gets the matching status.
     *
     * @return the matching status
     */
    public MatchingStatusEnum getMatchingStatus() {
        return matchingStatus;
    }

    /**
     * Sets the matching status.
     *
     * @param matchingStatus the new matching status
     */
    public void setMatchingStatus(MatchingStatusEnum matchingStatus) {
        this.matchingStatus = matchingStatus;
    }

    /**
     * Gets the occ code.
     *
     * @return the occ code
     */
    public String getOccCode() {
        return occCode;
    }

    /**
     * Sets the occ code.
     *
     * @param occCode the new occ code
     */
    public void setOccCode(String occCode) {
        this.occCode = occCode;
    }

    /**
     * Gets the occ description.
     *
     * @return the occ description
     */
    public String getOccDescription() {
        return occDescription;
    }

    /**
     * Sets the occ description.
     *
     * @param occDescription the new occ description
     */
    public void setOccDescription(String occDescription) {
        this.occDescription = occDescription;
    }

    /**
     * Gets the customer account.
     *
     * @return the customer account
     */
    public String getCustomerAccount() {
        return customerAccount;
    }

    /**
     * Sets the customer account.
     *
     * @param customerAccount the new customer account
     */
    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    /**
     * Gets the excluded from dunning.
     *
     * @return the excluded from dunning
     */
    public Boolean getExcludedFromDunning() {
        return excludedFromDunning;
    }

    /**
     * Sets the excluded from dunning.
     *
     * @param excludedFromDunning the new excluded from dunning
     */
    public void setExcludedFromDunning(Boolean excludedFromDunning) {
        this.excludedFromDunning = excludedFromDunning;
    }

    /**
     * Gets the matching amounts.
     *
     * @return the matching amounts
     */
    public MatchingAmountsDto getMatchingAmounts() {
        return matchingAmounts;
    }

    /**
     * Sets the matching amounts.
     *
     * @param matchingAmounts the new matching amounts
     */
    public void setMatchingAmounts(MatchingAmountsDto matchingAmounts) {
        this.matchingAmounts = matchingAmounts;
    }

    /**
     * Gets the other credit and charge.
     *
     * @return the other credit and charge
     */
    public OtherCreditAndChargeDto getOtherCreditAndCharge() {
        return otherCreditAndCharge;
    }

    /**
     * Sets the other credit and charge.
     *
     * @param otherCreditAndCharge the new other credit and charge
     */
    public void setOtherCreditAndCharge(OtherCreditAndChargeDto otherCreditAndCharge) {
        this.otherCreditAndCharge = otherCreditAndCharge;
    }

    /**
     * Gets the recorded invoice.
     *
     * @return the recorded invoice
     */
    public RecordedInvoiceDto getRecordedInvoice() {
        return recordedInvoice;
    }

    /**
     * Sets the recorded invoice.
     *
     * @param recordedInvoice the new recorded invoice
     */
    public void setRecordedInvoice(RecordedInvoiceDto recordedInvoice) {
        this.recordedInvoice = recordedInvoice;
    }

    /**
     * Gets the rejected payment.
     *
     * @return the rejected payment
     */
    public RejectedPaymentDto getRejectedPayment() {
        return rejectedPayment;
    }

    /**
     * Sets the rejected payment.
     *
     * @param rejectedPayment the new rejected payment
     */
    public void setRejectedPayment(RejectedPaymentDto rejectedPayment) {
        this.rejectedPayment = rejectedPayment;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the bank lot.
     *
     * @return the bank lot
     */
    public String getBankLot() {
        return bankLot;
    }

    /**
     * Sets the bank lot.
     *
     * @param bankLot the new bank lot
     */
    public void setBankLot(String bankLot) {
        this.bankLot = bankLot;
    }

    /**
     * Gets the bank reference.
     *
     * @return the bank reference
     */
    public String getBankReference() {
        return bankReference;
    }

    /**
     * Sets the bank reference.
     *
     * @param bankReference the new bank reference
     */
    public void setBankReference(String bankReference) {
        this.bankReference = bankReference;
    }

    /**
     * Gets the bank collection date.
     *
     * @return the bank collection date
     */
    public Date getBankCollectionDate() {
        return bankCollectionDate;
    }

    /**
     * Sets the bank collection date.
     *
     * @param bankCollectionDate the new bank collection date
     */
    public void setBankCollectionDate(Date bankCollectionDate) {
        this.bankCollectionDate = bankCollectionDate;
    }

    /**
     * Gets the deposit date.
     *
     * @return the deposit date
     */
    public Date getDepositDate() {
        return depositDate;
    }

    /**
     * Sets the deposit date.
     *
     * @param depositDate the new deposit date
     */
    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    /**
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
     * Gets the account code.
     *
     * @return the account code
     */
    public String getAccountCode() {
        return accountCode;
    }

    /**
     * Sets the account code.
     *
     * @param accountCode the new account code
     */
    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    /**
     * Gets the amount without tax.
     *
     * @return the amountWithoutTax
     */
    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    /**
     * Sets the amount without tax.
     *
     * @param amountWithoutTax the amountWithoutTax to set
     */
    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    /**
     * Gets the tax amount.
     *
     * @return the taxAmount
     */
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    /**
     * Sets the tax amount.
     *
     * @param taxAmount the taxAmount to set
     */
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    /**
     * Gets the order number.
     *
     * @return the orderNumber
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the order number.
     *
     * @param orderNumber the orderNumber to set
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the payment method.
     *
     * @return the paymentMethod
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param paymentMethod the paymentMethod to set
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}