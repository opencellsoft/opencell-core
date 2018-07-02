package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.payment.PaymentResponseDto;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentService;
import org.slf4j.Logger;

/**
 * 
 *  @author anasseh
 *  @lastModifiedVersion 5.0
 */

@Stateless
public class UnitPaymentJobBean {

    @Inject
    private Logger log;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentService paymentService;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long aoId, boolean createAO, boolean matchingAO, OperationCategoryEnum operationCategory, PaymentGateway paymentGateway,
            PaymentMethodEnum paymentMethodType) {
        log.debug("Running with RecordedInvoice ID={}", aoId);
        AccountOperation accountOperation = null;
        try {
            accountOperation = accountOperationService.findById(aoId);
            if (accountOperation == null) {
                return;
            }
            List<Long> listAOids = new ArrayList<>();
            listAOids.add(aoId);
            PaymentResponseDto doPaymentResponseDto = new PaymentResponseDto();
            CustomerAccount customerAccount = accountOperation.getCustomerAccount();
            BigDecimal unMatchingAmount = accountOperation.getUnMatchingAmount();
            BigDecimal oneHundred = new BigDecimal("100");
            if (operationCategory == OperationCategoryEnum.CREDIT) {
                if (paymentMethodType == PaymentMethodEnum.CARD) {
                    doPaymentResponseDto = paymentService.payByCardToken(customerAccount,
                        unMatchingAmount.multiply(oneHundred).longValue(), listAOids, createAO, matchingAO, paymentGateway);
                } else {
                   doPaymentResponseDto = paymentService.payByMandat(customerAccount,
                        unMatchingAmount.multiply(oneHundred).longValue(), listAOids, createAO, matchingAO, paymentGateway);
                }
            } else {
                if (paymentMethodType == PaymentMethodEnum.CARD) {
                    doPaymentResponseDto = paymentService.refundByCardToken(customerAccount,
                        unMatchingAmount.multiply(oneHundred).longValue(), listAOids, createAO, matchingAO, paymentGateway);
                } else {
                    doPaymentResponseDto = paymentService.refundByMandat(customerAccount,
                       unMatchingAmount.multiply(oneHundred).longValue(), listAOids, createAO, matchingAO, paymentGateway);
                }
            }
            if (PaymentStatusEnum.ERROR == doPaymentResponseDto.getPaymentStatus() || PaymentStatusEnum.REJECTED == doPaymentResponseDto.getPaymentStatus()) {
                result.registerError(aoId, doPaymentResponseDto.getErrorMessage());
                result.addReport("AccountOperation id : " + aoId + " RejectReason : " + doPaymentResponseDto.getErrorMessage());
            } else {
                result.registerSucces();

            }

        } catch (Exception e) {
            log.error("Failed to pay recorded invoice id:" + aoId, e);
            result.registerError(aoId, e.getMessage());
            result.addReport("AccountOperation id : " + aoId + " RejectReason : " + e.getMessage());
        }

    }
}