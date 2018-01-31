/**
 * 
 */
package org.meveo.api.dto.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;


/**
 * Class to represent payments that was initiated from OC, we find payment done, ,pending,rejected and  error.
 *
 * @author anasseh
 * 
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentHistoryDto extends BaseDto {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /** The customer Account Code. */
    private String customerAccountCode;
    
    /** The seller Code. */
    private String sellerCode;
    
    /** The customer Account Name. */
    private String customerAccountName;

    /** The operation date. */
    private Date operationDate;
    
    /** The updated status date. */
    private Date updatedStatusDate;
    
    /** The amount in cts. */
    private Long amountCts;
    
    /** The synchrone status. */
    private PaymentStatusEnum syncStatus;
    
    /** The asynchrone status. */
    private PaymentStatusEnum asyncStatus;
    
    /** The  status. */
    private PaymentStatusEnum status;    
    
    /** The external payment id. */
    private String externalPaymentId;
    
    /** The error code. */
    private String errorCode;
    
    /** The error message. */
    private String errorMessage;
    
    /** The error type, rejected or error. */
    private PaymentErrorTypeEnum errorType;
    
    /** The payment gateway. */
    private String paymentGatewayCode;
    
    /** The payment method. */
    private PaymentMethodEnum paymentMethodType;
    
    /** The payment method name: card number or mandat. */    
    private String paymentMethodName;
    
    /** The operation category, credit for payment or debit for refund. */
    private OperationCategoryEnum operationCategory;
    
    private AccountOperationDto payment;
    
    private AccountOperationDto refund;
    
    private AccountOperationsDto listAoPaid ;

    /**
     * @return the customerAccountCode
     */
    public String getCustomerAccountCode() {
        return customerAccountCode;
    }

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
    public AccountOperationDto getPayment() {
        return payment;
    }

    /**
     * @param payment the payment to set
     */
    public void setPayment(AccountOperationDto payment) {
        this.payment = payment;
    }

    /**
     * @return the refund
     */
    public AccountOperationDto getRefund() {
        return refund;
    }

    /**
     * @param refund the refund to set
     */
    public void setRefund(AccountOperationDto refund) {
        this.refund = refund;
    }

    /**
     * @return the listAoPaid
     */
    public AccountOperationsDto getListAoPaid() {
        return listAoPaid;
    }

    /**
     * @param listAoPaid the listAoPaid to set
     */
    public void setListAoPaid(AccountOperationsDto listAoPaid) {
        this.listAoPaid = listAoPaid;
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
