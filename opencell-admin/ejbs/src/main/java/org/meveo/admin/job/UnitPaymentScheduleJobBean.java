package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.service.payments.impl.PaymentScheduleInstanceItemService;
import org.slf4j.Logger;

/**
 * 
 * @author anasseh
 * @lastModifiedVersion 5.2
 */

@Stateless
public class UnitPaymentScheduleJobBean {

    @Inject
    private Logger log;

    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

   

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result,PaymentScheduleInstanceItem paymentScheduleInstanceItem) {
        log.debug("Running with paymentScheduleInstanceItem ID={}", paymentScheduleInstanceItem.getId());       
        try {
            
            paymentScheduleInstanceItemService.processItem(paymentScheduleInstanceItem);

        } catch (Exception e) {
            log.error("Failed to process paymentScheduleInstanceItem id:" + paymentScheduleInstanceItem.getId(), e);
            result.registerError(paymentScheduleInstanceItem.getId(), e.getMessage());
            result.addReport("paymentScheduleInstanceItem id: " + paymentScheduleInstanceItem.getId() + " RejectReason : " + e.getMessage());
        }

    }
}