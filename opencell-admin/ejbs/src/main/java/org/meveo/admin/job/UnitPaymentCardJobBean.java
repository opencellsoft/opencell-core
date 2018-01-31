package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.payment.PayByCardResponseDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RefundService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 */

@Stateless
public class UnitPaymentCardJobBean {
    
    @Inject
    private Logger log;

    @Inject
    private AccountOperationService accountOperationService;

    @Inject
    private PaymentService paymentService;

    @Inject
    private RefundService refundService;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)

    public void execute(JobExecutionResultImpl result, Long aoId, boolean createAO, boolean matchingAO, OperationCategoryEnum operationCategory, PaymentGateway paymentGateway) {
        log.debug("Running with RecordedInvoice ID={}", aoId);

        AccountOperation accountOperation = null;
        try {
            accountOperation = accountOperationService.findById(aoId);
            if (accountOperation == null) {
                return;
            }
            List<Long> listAOids = new ArrayList<>();
            listAOids.add(aoId);
            PayByCardResponseDto doPaymentResponseDto = new PayByCardResponseDto();
            if (operationCategory == OperationCategoryEnum.CREDIT) {
                doPaymentResponseDto = paymentService.payByCardToken(accountOperation.getCustomerAccount(),
                    accountOperation.getUnMatchingAmount().multiply(new BigDecimal("100")).longValue(), listAOids, createAO, matchingAO,paymentGateway);
            } else {
                doPaymentResponseDto = refundService.refundByCardToken(accountOperation.getCustomerAccount(),
                    accountOperation.getUnMatchingAmount().multiply(new BigDecimal("100")).longValue(), listAOids, createAO, matchingAO,paymentGateway);

                if (!StringUtils.isBlank(doPaymentResponseDto.getPaymentID())) {
                    result.registerSucces();
                }
            }

        } catch (Exception e) {
            log.error("Failed to pay recorded invoice id:" + aoId, e);
            result.registerError(aoId, e.getMessage());
            result.addReport("AccountOperation id : " + aoId + " RejectReason : " + e.getMessage());
        }
        
    }
}