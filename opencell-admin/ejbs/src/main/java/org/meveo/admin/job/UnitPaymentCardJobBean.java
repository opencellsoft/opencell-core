package org.meveo.admin.job;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InsufficientBalanceException;
import org.meveo.event.qualifier.Rejected;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.UsageRatingService;
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

   
    // @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long aoId) {
        log.debug("Running with RecordedInvoice ID={}", aoId);

        RecordedInvoice recordedInvoice = null;
        try {
        	recordedInvoice = recordedInvoiceService.findById(aoId);
            if (recordedInvoice == null) {
                return;
            }
            
            usageRatingService.ratePostpaidUsage(edr);
            
            if (edr.getStatus() == EDRStatusEnum.RATED) {
                edr = edrService.updateNoCheck(edr);
                result.registerSucces();
            } else {
                edr = edrService.updateNoCheck(edr);
                rejectededEdrProducer.fire(edr);
                result.registerError(edr.getId(), edr.getRejectReason());
                result.addReport("EdrId : " + edr.getId() + " RejectReason : " + edr.getRejectReason());
            }
        } catch (BusinessException e) {
            if (!(e instanceof InsufficientBalanceException)) {
                log.error("Failed to unit usage rate for {}", edrId, e);
            }
            unitUsageRatingJobBean.registerFailedEdr(result, edr, e);
        }
    }

   
}