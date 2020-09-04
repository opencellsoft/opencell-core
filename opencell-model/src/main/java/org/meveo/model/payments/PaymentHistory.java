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
package org.meveo.model.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;


/**
 * Payment history.
 *
 * @author anasseh
 * @lastModifiedVersion 9.1.3
 */

@Entity
@Table(name = "ar_payment_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_history_seq"), })
public class PaymentHistory extends AuditableEntity {


	private static final long serialVersionUID = 4319694328397367053L;

	/** The customer Account Code. */
	@Column(name = "customer_account_code")
	@NotNull
	private String customerAccountCode;

	/** The customer Account Name. */
	@Column(name = "customer_account_name")
	private String customerAccountName;

	/** The seller Code. */
	@Column(name = "seller_code")
	private String sellerCode;

	/** The customer Code. */
	@Column(name = "customer_code")
	private String customerCode;

	/** The operation date. */
	@Column(name = "operation_date")
	@NotNull
	private Date operationDate;

	/** The last Update Date. */
	@Column(name = "last_update_date")
	private Date lastUpdateDate;

	/** The amount. */
	@Column(name = "amount_cts")
	@NotNull
	private Long amountCts;

	/** The synchrone status. */
	@Column(name = "sync_status")
	@Enumerated(EnumType.STRING)
	@NotNull
	private PaymentStatusEnum syncStatus;

	/** The asynchrone status. */
	@Column(name = "async_status")
	@Enumerated(EnumType.STRING)
	private PaymentStatusEnum asyncStatus;

	/** The status. */
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private PaymentStatusEnum status;

	/** The external payment id. */
	@Column(name = "external_payment_id")
	private String externalPaymentId;

	/** The error code. */
	@Column(name = "error_code")
	private String errorCode;

	/** The error message. */
	@Column(name = "error_message")
	private String errorMessage;

	/** The error type, rejected or error. */
	@Column(name = "error_type")
	@Enumerated(EnumType.STRING)
	private PaymentErrorTypeEnum errorType;

	/** The payment gateway. */
	@Column(name = "payment_gateway_code")
	private String paymentGatewayCode;

	/** The payment method type. */
	@Column(name = "payment_method_type")
	@Enumerated(EnumType.STRING)
	private PaymentMethodEnum paymentMethodType;

	/** The payment method name: card number or mandat. */
	@Column(name = "payment_method_name")
	private String paymentMethodName;

	/** The operation category, credit for payment or debit for refund. */
	@Column(name = "operation_category")
	@Enumerated(EnumType.STRING)
	@NotNull
	private OperationCategoryEnum operationCategory;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "refund_id")
	private Refund refund;
	
	
	@ManyToMany(mappedBy = "paymentHistories")
	List<AccountOperation> listAoPaid;


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
        this.status = syncStatus;
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
        this.status = asyncStatus;
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
     * Gets the customer account code.
     *
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
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
    public Payment getPayment() {
        return payment;
    }

    /**
     * Sets the payment.
     *
     * @param payment the payment to set
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * Gets the refund.
     *
     * @return the refund
     */
    public Refund getRefund() {
        return refund;
    }

    /**
     * Sets the refund.
     *
     * @param refund the refund to set
     */
    public void setRefund(Refund refund) {
        this.refund = refund;
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
     * Gets the customer code.
     *
     * @return the customerCode
     */
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * Sets the customer code.
     *
     * @param customerCode the customerCode to set
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

	/**
	 * Gets the list ao paid.
	 *
	 * @return the list ao paid
	 */
	public List<AccountOperation> getListAoPaid() {
		if (listAoPaid == null) {
			return new ArrayList<AccountOperation>();
		}
		return listAoPaid;
	}

	/**
	 * Sets the list ao paid.
	 *
	 * @param listAoPaid the new list ao paid
	 */
	public void setListAoPaid(List<AccountOperation> listAoPaid) {
		this.listAoPaid = listAoPaid;
	}

}

