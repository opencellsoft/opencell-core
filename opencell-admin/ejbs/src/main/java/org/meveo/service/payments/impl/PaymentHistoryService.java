/**
 * 
 */
package org.meveo.service.payments.impl;

import java.util.Date;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author anasseh
 *
 */
public class PaymentHistoryService extends PersistenceService<PaymentHistory> {

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addHistory(CustomerAccount customerAccount,Payment payment, Long amountCts, PaymentStatusEnum status, PayByCardResponseDto doPaymentResponseDto, PaymentErrorTypeEnum errorType,
            OperationCategoryEnum operationCategory, PaymentGateway paymentGateway, PaymentMethod paymentMethod) throws BusinessException {
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setCustomerAccount(customerAccount);
        paymentHistory.setPayment(payment);
        paymentHistory.setOperationDate(new Date());
        paymentHistory.setAmountCts(amountCts);
        paymentHistory.setErrorCode(doPaymentResponseDto != null ? doPaymentResponseDto.getErrorCode() : null);
        paymentHistory.setErrorMessage(doPaymentResponseDto != null ? doPaymentResponseDto.getErrorMessage() : null);
        paymentHistory.setErrorType(errorType);
        paymentHistory.setExternalPaymentId(payment != null ? payment.getReference() : null);
        paymentHistory.setOperationCategory(operationCategory);
        paymentHistory.setSyncStatus(status);
        paymentHistory.setPaymentGateway(paymentGateway);
        paymentHistory.setPaymentMethod(paymentMethod);
        create(paymentHistory);

    }
}
