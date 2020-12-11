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

package org.meveo.api.dto.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.IEntityDto;
import org.meveo.model.audit.AuditChangeTypeEnum;
import org.meveo.model.audit.AuditTarget;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentHistory;

/**
 * The Class AccountOperationDto.
 *
 * @author Edward P. Legaspi
 * @author anasseh
 * @author melyoussoufi
 * @lastModifiedVersion 7.3.0
 */
@XmlRootElement(name = "AccountOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountOperationDto extends AuditableEntityDto implements IEntityDto {

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
    private BigDecimal matchingAmount;

    /** The un matching amount. */
    private BigDecimal unMatchingAmount;

    /** The matching status. */
    private MatchingStatusEnum matchingStatus;

    /** The occ code. */
    private String code;

    /** The occ description. */
    private String description;

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

    /**
     * The bank lot.
     */
    private String bankLot;

    /**
     * The bank reference.
     */
    private String bankReference;

    /**
     * The bank collection date.
     */
    private Date bankCollectionDate;

    /**
     * The deposit date.
     */
    private Date depositDate;

    /**
     * The payment method.
     */
    private String paymentMethod;

    /**
     * The custom fields.
     */
    private CustomFieldsDto customFields;

    /**
     * The payment info.
     */
    private String paymentInfo;// IBAN for direct debit

    /**
     * The payment info 1.
     */
    private String paymentInfo1;// bank code

    /**
     * The payment info 2.
     */
    private String paymentInfo2;// code guichet

    /**
     * The payment info 3.
     */
    private String paymentInfo3;// Num compte

    /**
     * The payment info 4.
     */
    private String paymentInfo4;// RIB

    /**
     * The payment info 5.
     */
    private String paymentInfo5;// bankName

    /**
     * The payment info 6.
     */
    private String paymentInfo6;// bic

    /**
     * The billing account name.
     */
    private String billingAccountName;

    @XmlElementWrapper(name = "paymentHistories")
    @XmlElement(name = "paymentHistory")
    private List<PaymentHistoryDto> paymentHistories = new ArrayList<PaymentHistoryDto>();

    /**
     * A collection date.
     */
    private Date collectionDate;

    /**
     * Instantiates a new account operation dto.
     */
    public AccountOperationDto() {
        // TODO Auto-generated constructor stub
    }

    /**
     * Instantiates a new account operation dto.
     *
     * @param ao the ao
     */
    public AccountOperationDto(AccountOperation ao) {
        this(ao, null);
    }

    /**
     * Instantiates a new account operation dto.
     *
     * @param accountOp the account op
     * @param customFieldsDto the custom fields dto
     */
    public AccountOperationDto(AccountOperation accountOp, CustomFieldsDto customFieldsDto) {
        super(accountOp);
        setId(accountOp.getId());
        setDueDate(accountOp.getDueDate());
        setType(accountOp.getType());
        setTransactionDate(accountOp.getTransactionDate());
        setTransactionCategory(accountOp.getTransactionCategory());
        setReference(accountOp.getReference());
        if (accountOp.getAccountingCode() != null) {
            setAccountingCode(accountOp.getAccountingCode().getCode());
            setAccountCode(accountOp.getAccountingCode().getCode());
        }
        setAccountCodeClientSide(accountOp.getAccountCodeClientSide());
        setAmount(accountOp.getAmount());
        setMatchingAmount(accountOp.getMatchingAmount());
        setUnMatchingAmount(accountOp.getUnMatchingAmount());
        setMatchingStatus(accountOp.getMatchingStatus());
        setCode(accountOp.getCode());
        setDescription(accountOp.getDescription());
        setBankLot(accountOp.getBankLot());
        setBankReference(accountOp.getBankReference());
        setDepositDate(accountOp.getDepositDate());
        setBankCollectionDate(accountOp.getBankCollectionDate());
        setTaxAmount(accountOp.getTaxAmount());
        setAmountWithoutTax(accountOp.getAmountWithoutTax());
        setOrderNumber(accountOp.getOrderNumber());
        setPaymentMethod(accountOp.getPaymentMethod() != null ? accountOp.getPaymentMethod().name() : null);
        setExcludedFromDunning(accountOp.getMatchingStatus() == MatchingStatusEnum.I);
        List<MatchingAmount> tempMatchingAmounts = accountOp.getMatchingAmounts();
        if (tempMatchingAmounts != null && !tempMatchingAmounts.isEmpty()) {
            MatchingAmountDto matchingAmountDto = null;
            MatchingAmountsDto matchingAmountsDto = new MatchingAmountsDto();
            matchingAmountsDto.setMatchingAmount(new ArrayList<>());
            for (MatchingAmount tempMatchingAmount : tempMatchingAmounts) {
                matchingAmountDto = new MatchingAmountDto();
                matchingAmountDto.setAuditable(tempMatchingAmount);
                if (tempMatchingAmount.getMatchingCode() != null) {
                    matchingAmountDto.setMatchingCode(tempMatchingAmount.getMatchingCode().getCode());
                }
                matchingAmountDto.setMatchingAmount(tempMatchingAmount.getMatchingAmount());
                matchingAmountsDto.getMatchingAmount().add(matchingAmountDto);
            }
            setMatchingAmounts(matchingAmountsDto);
        }
        if (accountOp.getRejectedPayment() != null) {
            setRejectedPayment(new RejectedPaymentDto(accountOp.getRejectedPayment()));
        }
        setCollectionDate(accountOp.getCollectionDate());
        setCustomFields(customFieldsDto);
    }

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
    public String getCode() {
        return code;
    }

    /**
     * Sets the occ code.
     *
     * @param occCode the new occ code
     */
    public void setCode(String occCode) {
        this.code = occCode;
    }

    /**
     * Gets the occ description.
     *
     * @return the occ description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the occ description.
     *
     * @param occDescription the new occ description
     */
    public void setDescription(String occDescription) {
        this.description = occDescription;
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

    /**
     * Gets the payment info.
     *
     * @return the paymentInfo
     */
    public String getPaymentInfo() {
        return paymentInfo;
    }

    /**
     * Sets the payment info.
     *
     * @param paymentInfo the paymentInfo to set
     */
    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    /**
     * Gets the payment info 1.
     *
     * @return the paymentInfo1
     */
    public String getPaymentInfo1() {
        return paymentInfo1;
    }

    /**
     * Sets the payment info 1.
     *
     * @param paymentInfo1 the paymentInfo1 to set
     */
    public void setPaymentInfo1(String paymentInfo1) {
        this.paymentInfo1 = paymentInfo1;
    }

    /**
     * Gets the payment info 2.
     *
     * @return the paymentInfo2
     */
    public String getPaymentInfo2() {
        return paymentInfo2;
    }

    /**
     * Sets the payment info 2.
     *
     * @param paymentInfo2 the paymentInfo2 to set
     */
    public void setPaymentInfo2(String paymentInfo2) {
        this.paymentInfo2 = paymentInfo2;
    }

    /**
     * Gets the payment info 3.
     *
     * @return the paymentInfo3
     */
    public String getPaymentInfo3() {
        return paymentInfo3;
    }

    /**
     * Sets the payment info 3.
     *
     * @param paymentInfo3 the paymentInfo3 to set
     */
    public void setPaymentInfo3(String paymentInfo3) {
        this.paymentInfo3 = paymentInfo3;
    }

    /**
     * Gets the payment info 4.
     *
     * @return the paymentInfo4
     */
    public String getPaymentInfo4() {
        return paymentInfo4;
    }

    /**
     * Sets the payment info 4.
     *
     * @param paymentInfo4 the paymentInfo4 to set
     */
    public void setPaymentInfo4(String paymentInfo4) {
        this.paymentInfo4 = paymentInfo4;
    }

    /**
     * Gets the payment info 5.
     *
     * @return the paymentInfo5
     */
    public String getPaymentInfo5() {
        return paymentInfo5;
    }

    /**
     * Sets the payment info 5.
     *
     * @param paymentInfo5 the paymentInfo5 to set
     */
    public void setPaymentInfo5(String paymentInfo5) {
        this.paymentInfo5 = paymentInfo5;
    }

    /**
     * Gets the payment info 6.
     *
     * @return the paymentInfo6
     */
    public String getPaymentInfo6() {
        return paymentInfo6;
    }

    /**
     * Sets the payment info 6.
     *
     * @param paymentInfo6 the paymentInfo6 to set
     */
    public void setPaymentInfo6(String paymentInfo6) {
        this.paymentInfo6 = paymentInfo6;
    }

    /**
     * Gets the billing account name.
     *
     * @return the billingAccountName
     */
    public String getBillingAccountName() {
        return billingAccountName;
    }

    /**
     * Sets the billing account name.
     *
     * @param billingAccountName the billingAccountName to set
     */
    public void setBillingAccountName(String billingAccountName) {
        this.billingAccountName = billingAccountName;
    }

    public List<PaymentHistoryDto> getPaymentHistories() {
        return paymentHistories;
    }

    public void setPaymentHistories(List<PaymentHistoryDto> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }
}