package org.meveo.admin.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.meveo.service.script.payment.AccountOperationFilterScript;
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
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)// TODO : nbr of method arguments is disturbing , refactor it by using a dedicated bean/dto
    public void execute(JobExecutionResultImpl result, Long customerAccountId, List<Long> listAOids, Long amountToPay, boolean createAO, boolean matchingAO,
            OperationCategoryEnum operationCategory, PaymentGateway paymentGateway, PaymentMethodEnum paymentMethodType, AccountOperationFilterScript aoFilterScript) {
        
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
                this.checkPaymentRetry(doPaymentResponseDto.getErrorCode(), listAOids, aoFilterScript);
            } else {
                result.registerSucces();
            }

        } catch (Exception e) {
            log.error("Failed to pay recorded invoice id:" + customerAccountId, e);
            result.registerError(customerAccountId, e.getMessage());
            result.addReport("AccountOperation id : " + customerAccountId + " RejectReason : " + e.getMessage());
        }

    }

    private void checkPaymentRetry(String errorCode, List<Long> listAOids, AccountOperationFilterScript aoFilterScript) {
        try {
            Map<String, Object> methodContext = new HashMap<>();
            methodContext.put("ERROR_CODE", errorCode);
            methodContext.put("LIST_AO_IDs", listAOids);
            aoFilterScript.checkPaymentRetry(methodContext);
        } catch (Exception e) {
            log.error(" Error on checkPaymentRetry [{}]", e.getMessage());
        }
    }
}