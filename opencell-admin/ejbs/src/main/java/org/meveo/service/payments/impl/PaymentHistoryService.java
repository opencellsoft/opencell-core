/**
 * 
 */
package org.meveo.service.payments.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CardPaymentMethod;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentErrorTypeEnum;
import org.meveo.model.payments.PaymentHistory;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.Refund;
import org.meveo.service.base.PersistenceService;

/**
 * @author anasseh
 * @lastModifiedVersion 5.0.2
 */
@Stateless
public class PaymentHistoryService extends PersistenceService<PaymentHistory> {
	
    /** The account operation service. */
    @Inject
    private AccountOperationService accountOperationService;
	

    @JpaAmpNewTx   
    public void addHistory(CustomerAccount customerAccount, Payment payment,Refund refund, Long amountCts, PaymentStatusEnum status, String errorCode,String errorMessage,
            PaymentErrorTypeEnum errorType, OperationCategoryEnum operationCategory, String paymentGatewayCode, PaymentMethod paymentMethod,List<Long> aoIdsToPay) throws BusinessException {
    	List<AccountOperation> aoToPay = new ArrayList<AccountOperation>();
    	if(aoIdsToPay != null) {
        	for(Long aoId : aoIdsToPay) {
        		aoToPay.add(accountOperationService.findById(aoId));
            }
    	}
    	addHistoryAOs(customerAccount, payment, refund, amountCts, status, errorCode, errorMessage, errorType, operationCategory, paymentGatewayCode, paymentMethod, aoToPay);
    }
    
	@JpaAmpNewTx
	public void addHistoryAOs(CustomerAccount customerAccount, Payment payment, Refund refund, Long amountCts, PaymentStatusEnum status, String errorCode, String errorMessage,
			PaymentErrorTypeEnum errorType, OperationCategoryEnum operationCategory, String paymentGatewayCode, PaymentMethod paymentMethod, List<AccountOperation> aoToPay)
			throws BusinessException {
		PaymentHistory paymentHistory = new PaymentHistory();
		paymentHistory.setCustomerAccountCode(customerAccount.getCode());
		paymentHistory.setCustomerAccountName(customerAccount.getName() == null ? null : customerAccount.getName().getFullName());
		if (customerAccount.getCustomer().getSeller() != null) {
			paymentHistory.setSellerCode(customerAccount.getCustomer().getSeller().getCode());
		}
		paymentHistory.setCustomerCode(customerAccount.getCustomer().getCode());
		paymentHistory.setPayment(payment);
		paymentHistory.setRefund(refund);
		paymentHistory.setOperationDate(new Date());
		paymentHistory.setAmountCts(amountCts);
		paymentHistory.setErrorCode(errorCode);
		paymentHistory.setErrorMessage(errorMessage);
		paymentHistory.setErrorType(errorType);
		paymentHistory.setExternalPaymentId(payment != null ? payment.getReference() : (refund != null ? refund.getReference() : null));
		paymentHistory.setOperationCategory(payment != null ? OperationCategoryEnum.CREDIT : OperationCategoryEnum.DEBIT );
		paymentHistory.setSyncStatus(status);
		paymentHistory.setPaymentGatewayCode(paymentGatewayCode);
		paymentHistory.setLastUpdateDate(paymentHistory.getOperationDate());
		if (payment != null) {
			if (payment.getPaymentMethod() != null && payment.getPaymentMethod().isSimple()) {
				paymentHistory.setPaymentMethodType(payment.getPaymentMethod());
				paymentHistory.setPaymentMethodName(payment.getPaymentInfo());
			}
		} else if (refund != null) {
			if (refund.getPaymentMethod() != null && refund.getPaymentMethod().isSimple()) {
				paymentHistory.setPaymentMethodType(refund.getPaymentMethod());
				paymentHistory.setPaymentMethodName(refund.getPaymentInfo());
			}
		}
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

		for (AccountOperation ao : aoToPay) {
			ao.getPaymentHistories().add(paymentHistory);
			paymentHistory.getListAoPaid().add(ao);
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
