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
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentReplayCauseEnum;
import org.meveo.model.payments.PaymentStatusEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
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
    private RecordedInvoiceService recordedInvoiceService;

    @Inject
    private PaymentService paymentService;

    // @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long aoId, boolean createAO, boolean matchingAO, PaymentGateway paymentGateway) {
        log.debug("Running with RecordedInvoice ID={}", aoId);

        RecordedInvoice recordedInvoice = null;
        try {
            recordedInvoice = recordedInvoiceService.findById(aoId);
            if (recordedInvoice == null) {
                return;
            }
            List<Long> listAOids = new ArrayList<>();
            listAOids.add(aoId);
            PayByCardResponseDto doPaymentResponseDto = paymentService.payByCard(recordedInvoice.getCustomerAccount(),
                recordedInvoice.getUnMatchingAmount().multiply(new BigDecimal("100")).longValue(), listAOids, createAO, matchingAO);
            if (doPaymentResponseDto.getPaymentStatus() == PaymentStatusEnum.ACCEPTED || doPaymentResponseDto.getPaymentStatus() == PaymentStatusEnum.PENDING ) {
                result.registerSucces();
            } else {
                if(doPaymentResponseDto.getPaymentStatus() == PaymentStatusEnum.ERROR ) {
                    result.registerWarning(aoId,"Payment error");
                    if(paymentGateway.getNbTries() > 0 && paymentGateway.getReplayCause() == PaymentReplayCauseEnum.ERROR) {
                        insertPaymentToReplay();
                    }
                }
                if(doPaymentResponseDto.getPaymentStatus() == PaymentStatusEnum.REJECTED ) {
                    result.registerWarning(aoId,"Payment rejected");
                    if(paymentGateway.getNbTries() > 0 && paymentGateway.getReplayCause() == PaymentReplayCauseEnum.REJECT) {
                        insertPaymentToReplay();
                    }
                }                
                               
                
            }

        } catch (Exception e) {
            log.error("Failed to pay recorded invoice id:" + aoId, e);
            result.registerError(aoId, e.getMessage());
            result.addReport("RecordedInvoice id : " + aoId + " RejectReason : " + e.getMessage());
        }
    }
    
    private void insertPaymentToReplay() {
        
    }
}