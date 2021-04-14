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

/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Class to represent payments that was initiated from OC, we find payment done, ,pending,rejected and error.
 *
 * @author anasseh
 * @lastModifiedVersion 5.0.2
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentHistoryDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The customer Account Code. */
    @Schema(description = "The customer Account Code")
    private String customerAccountCode;

    /** The seller Code. */
    @Schema(description = "The seller Code")
    private String sellerCode;
    
    /** The customer Code. */
    @XmlTransient
    @Schema(description = "The customer Code")
    private String customerCode;    

    /** The customer Account Name. */
    @Schema(description = "The customer Account Name")
    private String customerAccountName;

    /** The operation date. */
    @Schema(description = "The operation date")
    private Date operationDate;

    /** The updated status date. */
    @Schema(description = "The updated status date")
    private Date updatedStatusDate;

    /** The last Update Date. */
    @Schema(description = "The last Update Date")
    private Date lastUpdateDate;

    /** The amount in cts. */
    @Schema(description = "The amount in cts")
    private Long amountCts;

    /** The synchrone status. */
    @Schema(description = "The synchrone status", example = "possible value are : ACCEPTED, PENDING, REJECTED, ERROR, NOT_PROCESSED")
    private PaymentStatusEnum syncStatus;

    /** The asynchrone status. */
    @Schema(description = "The asynchrone status", example = "possible value are : ACCEPTED, PENDING, REJECTED, ERROR, NOT_PROCESSED")
    private PaymentStatusEnum asyncStatus;

    /** The status. */
    @Schema(description = "The status", example = "possible value are : ACCEPTED, PENDING, REJECTED, ERROR, NOT_PROCESSED")
    private PaymentStatusEnum status;

    /** The external payment id. */
    @Schema(description = "he external payment id")
    private String externalPaymentId;

    /** The error code. */
    @Schema(description = "The error code")
    private String errorCode;

    /** The error message. */
    @Schema(description = "The error message")
    private String errorMessage;

    /** The error type, rejected or error. */
    @Schema(description = "The error type, rejected or error", example = "possible value are :  ERROR, REJECT" )
    private PaymentErrorTypeEnum errorType;

    /** The payment gateway. */
    @Schema(description = "The payment gateway")
    private String paymentGatewayCode;

    /** The payment method. */
    @Schema(description = "The payment method", example = "possible value are : CHECK, DIRECTDEBIT, WIRETRANSFER, CARD, PAYPAL, STRIPE, CASH")
    private PaymentMethodEnum paymentMethodType;

    /** The payment method name: card number or mandat. */
    @Schema(description = "The payment method name: card number or mandat")
    private String paymentMethodName;

    /** The operation category, credit for payment or debit for refund. */
    @Schema(description = "The operation category, credit for payment or debit for refund", example = "possible valye are : DEBIT, CREDIT")
    private OperationCategoryEnum operationCategory;

    /** The payment. */
    @Schema(description = "The payment")
    private AccountOperationDto payment;

    /** The refund. */
    @Schema(description = "The refund")
    private AccountOperationDto refund;

    /** The list ao paid. */
    @Schema(description = "The list ao paid")
    private AccountOperationsDto listAoPaid;

    /**
     * Gets the customer account code.
     *
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * Gets the seller code.
     *
     * @return the sellerCode
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * Sets the seller code.
     *
     * @param sellerCode the sellerCode to set
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * Sets the customer account code.
     *
     * @param customerAccountCode the customerAccountCode to set
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * Gets the customer account name.
     *
     * @return the customerAccountName
     */
    public String getCustomerAccountName() {
        return customerAccountName;
    }

    /**
     * Sets the customer account name.
     *
     * @param customerAccountName the customerAccountName to set
     */
    public void setCustomerAccountName(String customerAccountName) {
        this.customerAccountName = customerAccountName;
    }

    /**
     * Gets the operation date.
     *
     * @return the operationDate
     */
    public Date getOperationDate() {
        return operationDate;
    }

    /**
     * Sets the operation date.
     *
     * @param operationDate the operationDate to set
     */
    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    /**
     * Gets the updated status date.
     *
     * @return the updatedStatusDate
     */
    public Date getUpdatedStatusDate() {
        return updatedStatusDate;
    }

    /**
     * Sets the updated status date.
     *
     * @param updatedStatusDate the updatedStatusDate to set
     */
    public void setUpdatedStatusDate(Date updatedStatusDate) {
        this.updatedStatusDate = updatedStatusDate;
    }

    /**
     * Gets the amount cts.
     *
     * @return the amountCts
     */
    public Long getAmountCts() {
        return amountCts;
    }

    /**
     * Sets the amount cts.
     *
     * @param amountCts the amountCts to set
     */
    public void setAmountCts(Long amountCts) {
        this.amountCts = amountCts;
    }

    /**
     * Gets the sync status.
     *
     * @return the syncStatus
     */
    public PaymentStatusEnum getSyncStatus() {
        return syncStatus;
    }

    /**
     * Sets the sync status.
     *
     * @param syncStatus the syncStatus to set
     */
    public void setSyncStatus(PaymentStatusEnum syncStatus) {
        this.syncStatus = syncStatus;
    }

    /**
     * Gets the async status.
     *
     * @return the asyncStatus
     */
    public PaymentStatusEnum getAsyncStatus() {
        return asyncStatus;
    }

    /**
     * Sets the async status.
     *
     * @param asyncStatus the asyncStatus to set
     */
    public void setAsyncStatus(PaymentStatusEnum asyncStatus) {
        this.asyncStatus = asyncStatus;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public PaymentStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(PaymentStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the external payment id.
     *
     * @return the externalPaymentId
     */
    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    /**
     * Sets the external payment id.
     *
     * @param externalPaymentId the externalPaymentId to set
     */
    public void setExternalPaymentId(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    /**
     * Gets the error code.
     *
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the error code.
     *
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the error message.
     *
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the error type.
     *
     * @return the errorType
     */
    public PaymentErrorTypeEnum getErrorType() {
        return errorType;
    }

    /**
     * Sets the error type.
     *
     * @param errorType the errorType to set
     */
    public void setErrorType(PaymentErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    /**
     * Gets the payment gateway code.
     *
     * @return the paymentGatewayCode
     */
    public String getPaymentGatewayCode() {
        return paymentGatewayCode;
    }

    /**
     * Sets the payment gateway code.
     *
     * @param paymentGatewayCode the paymentGatewayCode to set
     */
    public void setPaymentGatewayCode(String paymentGatewayCode) {
        this.paymentGatewayCode = paymentGatewayCode;
    }

    /**
     * Gets the payment method type.
     *
     * @return the paymentMethodType
     */
    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * Sets the payment method type.
     *
     * @param paymentMethodType the paymentMethodType to set
     */
    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * Gets the payment method name.
     *
     * @return the paymentMethodName
     */
    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    /**
     * Sets the payment method name.
     *
     * @param paymentMethodName the paymentMethodName to set
     */
    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    /**
     * Gets the operation category.
     *
     * @return the operationCategory
     */
    public OperationCategoryEnum getOperationCategory() {
        return operationCategory;
    }

    /**
     * Sets the operation category.
     *
     * @param operationCategory the operationCategory to set
     */
    public void setOperationCategory(OperationCategoryEnum operationCategory) {
        this.operationCategory = operationCategory;
    }

    /**
     * Gets the payment.
     *
     * @return the payment
     */
    public AccountOperationDto getPayment() {
        return payment;
    }

    /**
     * Sets the payment.
     *
     * @param payment the payment to set
     */
    public void setPayment(AccountOperationDto payment) {
        this.payment = payment;
    }

    /**
     * Gets the refund.
     *
     * @return the refund
     */
    public AccountOperationDto getRefund() {
        return refund;
    }

    /**
     * Sets the refund.
     *
     * @param refund the refund to set
     */
    public void setRefund(AccountOperationDto refund) {
        this.refund = refund;
    }

    /**
     * Gets the list ao paid.
     *
     * @return the listAoPaid
     */
    public AccountOperationsDto getListAoPaid() {
        return listAoPaid;
    }

    /**
     * Sets the list ao paid.
     *
     * @param listAoPaid the listAoPaid to set
     */
    public void setListAoPaid(AccountOperationsDto listAoPaid) {
        this.listAoPaid = listAoPaid;
    }

    /**
     * Gets the last update date.
     *
     * @return the lastUpdateDate
     */
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    /**
     * Sets the last update date.
     *
     * @param lastUpdateDate the lastUpdateDate to set
     */
    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
    
    


    /**
     * @return the customerCode
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * @param customerCode the customerCode to set
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    @Override
    public String toString() {
        return "PaymentHistoryDto [customerAccountCode=" + customerAccountCode + ", customerAccountName=" + customerAccountName + ", operationDate=" + operationDate
                + ", updatedStatusDate=" + updatedStatusDate + ", amountCts=" + amountCts + ", syncStatus=" + syncStatus + ", asyncStatus=" + asyncStatus + ", status=" + status
                + ", externalPaymentId=" + externalPaymentId + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + ", errorType=" + errorType + ", paymentGatewayCode="
                + paymentGatewayCode + ", paymentMethodType=" + paymentMethodType + ", paymentMethodName=" + paymentMethodName + ", operationCategory=" + operationCategory
                + ", payment=" + payment + ", refund=" + refund + ", listAoPaid=" + listAoPaid + "]";
    }

}
