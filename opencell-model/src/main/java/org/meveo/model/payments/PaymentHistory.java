/**
 * 
 */
package org.meveo.model.payments;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;

/**
 * @author anasseh
 *
 */

@Entity
@Table(name = "ar_payment_history")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_payment_history_seq"), })
public class PaymentHistory extends EnableEntity{

    /**
     * 
     */
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
    
    /** The operation date. */
    @Column(name = "operation_date")
    @NotNull
    private Date operationDate;
    
    /** The updated status date. */
    @Column(name = "updated_status_date")
    private Date updatedStatusDate;
    
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
    
    

    /**
     * @return the sellerCode
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * @param sellerCode the sellerCode to set
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * @return the operationDate
     */
    public Date getOperationDate() {
        return operationDate;
    }

    /**
     * @param operationDate the operationDate to set
     */
    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    /**
     * @return the updatedStatusDate
     */
    public Date getUpdatedStatusDate() {
        return updatedStatusDate;
    }

    /**
     * @param updatedStatusDate the updatedStatusDate to set
     */
    public void setUpdatedStatusDate(Date updatedStatusDate) {
        this.updatedStatusDate = updatedStatusDate;
    }

    /**
     * @return the amountCts
     */
    public Long getAmountCts() {
        return amountCts;
    }

    /**
     * @param amountCts the amountCts to set
     */
    public void setAmountCts(Long amountCts) {
        this.amountCts = amountCts;
    }

    /**
     * @return the syncStatus
     */
    public PaymentStatusEnum getSyncStatus() {
        return syncStatus;
    }

    /**
     * @param syncStatus the syncStatus to set
     */
    public void setSyncStatus(PaymentStatusEnum syncStatus) {
        this.syncStatus = syncStatus;
        this.status = syncStatus;
    }

    /**
     * @return the asyncStatus
     */
    public PaymentStatusEnum getAsyncStatus() {
        return asyncStatus;
    }

    /**
     * @param asyncStatus the asyncStatus to set
     */
    public void setAsyncStatus(PaymentStatusEnum asyncStatus) {
        this.asyncStatus = asyncStatus;
        this.status = asyncStatus;
    }

    /**
     * @return the externalPaymentId
     */
    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    /**
     * @param externalPaymentId the externalPaymentId to set
     */
    public void setExternalPaymentId(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorType
     */
    public PaymentErrorTypeEnum getErrorType() {
        return errorType;
    }

    /**
     * @param errorType the errorType to set
     */
    public void setErrorType(PaymentErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

  

    /**
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

    /**
     * @param customerAccountCode the customerAccountCode to set
     */
    public void setCustomerAccountCode(String customerAccountCode) {
        this.customerAccountCode = customerAccountCode;
    }

    /**
     * @return the customerAccountName
     */
    public String getCustomerAccountName() {
        return customerAccountName;
    }

    /**
     * @param customerAccountName the customerAccountName to set
     */
    public void setCustomerAccountName(String customerAccountName) {
        this.customerAccountName = customerAccountName;
    }

    /**
     * @return the status
     */
    public PaymentStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(PaymentStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the paymentGatewayCode
     */
    public String getPaymentGatewayCode() {
        return paymentGatewayCode;
    }

    /**
     * @param paymentGatewayCode the paymentGatewayCode to set
     */
    public void setPaymentGatewayCode(String paymentGatewayCode) {
        this.paymentGatewayCode = paymentGatewayCode;
    }

    /**
     * @return the paymentMethodType
     */
    public PaymentMethodEnum getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * @param paymentMethodType the paymentMethodType to set
     */
    public void setPaymentMethodType(PaymentMethodEnum paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * @return the paymentMethodName
     */
    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    /**
     * @param paymentMethodName the paymentMethodName to set
     */
    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    /**
     * @return the operationCategory
     */
    public OperationCategoryEnum getOperationCategory() {
        return operationCategory;
    }

    /**
     * @param operationCategory the operationCategory to set
     */
    public void setOperationCategory(OperationCategoryEnum operationCategory) {
        this.operationCategory = operationCategory;
    }

    /**
     * @return the payment
     */
    public Payment getPayment() {
        return payment;
    }

    /**
     * @param payment the payment to set
     */
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    /**
     * @return the refund
     */
    public Refund getRefund() {
        return refund;
    }

    /**
     * @param refund the refund to set
     */
    public void setRefund(Refund refund) {
        this.refund = refund;
    }
    

}
