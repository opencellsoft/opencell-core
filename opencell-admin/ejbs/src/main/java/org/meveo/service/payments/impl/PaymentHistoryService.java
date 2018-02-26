/**
 * 
 */
package org.meveo.service.payments.impl;

import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.Refund;
import org.meveo.service.base.PersistenceService;

/**
 * @author anasseh
 *
 */
public class PaymentHistoryService extends PersistenceService<PaymentHistory> {

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addHistory(CustomerAccount customerAccount, Payment payment,Refund refund, Long amountCts, PaymentStatusEnum status, String errorCode,String errorMessage,
            PaymentErrorTypeEnum errorType, OperationCategoryEnum operationCategory, PaymentGateway paymentGateway, PaymentMethod paymentMethod) throws BusinessException {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setCustomerAccountCode(customerAccount.getCode());
        paymentHistory.setCustomerAccountName(customerAccount.getName() == null ? null : customerAccount.getName().getFullName());
        paymentHistory.setSellerCode(customerAccount.getCustomer().getSeller().getCode());
        paymentHistory.setPayment(payment);
        paymentHistory.setRefund(refund);
        paymentHistory.setOperationDate(new Date());
        paymentHistory.setAmountCts(amountCts);
        paymentHistory.setErrorCode(errorCode);
        paymentHistory.setErrorMessage(errorMessage);
        paymentHistory.setErrorType(errorType);
        paymentHistory.setExternalPaymentId(payment != null ? payment.getReference() : (refund != null ? refund.getReference() : null));
        paymentHistory.setOperationCategory(operationCategory);
        paymentHistory.setSyncStatus(status);
        paymentHistory.setPaymentGatewayCode(paymentGateway == null ? null : paymentGateway.getCode());
        paymentHistory.setLastUpdateDate(paymentHistory.getOperationDate());
        if (paymentMethod != null) {
            paymentHistory.setPaymentMethodType(paymentMethod.getPaymentType());
            String pmVal = null;
            if (paymentMethod instanceof DDPaymentMethod) {
                pmVal = ((DDPaymentMethod) paymentMethod).getMandateIdentification();
            }
            if (paymentMethod instanceof CardPaymentMethod) {
                pmVal = ((CardPaymentMethod) paymentMethod).getHiddenCardNumber();
            }
            paymentHistory.setPaymentMethodName(pmVal);
        }
        super.create(paymentHistory);

    }
    
    public PaymentHistory findHistoryByPaymentId(String paymentId) {
        try {
            QueryBuilder qb = new QueryBuilder(PaymentHistory.class, "a");
            qb.addCriterion("externalPaymentId", "=", paymentId, false);
            return (PaymentHistory) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException ne) {
            return null;
        } 
    }
}
