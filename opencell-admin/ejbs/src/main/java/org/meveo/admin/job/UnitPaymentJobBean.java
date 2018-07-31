package org.meveo.admin.job;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 * @lastModifiedVersion 5.1
 */

@Stateless
public class UnitPaymentJobBean {

    @Inject
    private Logger log;

    @Inject
    private CustomerAccountService customerAccountService;

    @Inject
    private PaymentService paymentService;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long customerAccountId, List<Long> listAOids, Long amountToPay, boolean createAO, boolean matchingAO,
            OperationCategoryEnum operationCategory, PaymentGateway paymentGateway, PaymentMethodEnum paymentMethodType) {
        log.debug("Running with CustomerAccount ID={}", customerAccountId);
        CustomerAccount customerAccount = null;
        try {
            customerAccount = customerAccountService.findById(customerAccountId);
            if (customerAccount == null) {
                return;
            }
            PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
            if (operationCategory == OperationCategoryEnum.CREDIT) {
                if (paymentMethodType == PaymentMethodEnum.CARD) {
                    doPaymentResponseDto = paymentService.payByCardToken(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                } else {
                    doPaymentResponseDto = paymentService.payByMandat(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                }
            } else {
                if (paymentMethodType == PaymentMethodEnum.CARD) {
                    doPaymentResponseDto = paymentService.refundByCardToken(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                } else {
                    doPaymentResponseDto = paymentService.refundByMandat(customerAccount, amountToPay, listAOids, createAO, matchingAO, paymentGateway);
                }
            }
            if (PaymentStatusEnum.ERROR == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.REJECTED == doPaymentResponseDto.getPaymentStatus()) {
                result.registerError(customerAccountId, doPaymentResponseDto.getErrorMessage());
                result.addReport("AccountOperation id : " + customerAccountId + " RejectReason : " + doPaymentResponseDto.getErrorMessage());
            } else {
                result.registerSucces();
            }

        } catch (Exception e) {
            log.error("Failed to pay recorded invoice id:" + customerAccountId, e);
            result.registerError(customerAccountId, e.getMessage());
            result.addReport("AccountOperation id : " + customerAccountId + " RejectReason : " + e.getMessage());
        }

    }
}