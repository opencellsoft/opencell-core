package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.Invoice;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.PaymentScheduleInstanceItemService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.slf4j.Logger;

/**
 * @author anasseh
 **/
@Stateless
public class UnitCheckPaymentScheduleCallbackJobBean {

    @Inject
    private Logger log;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;
    
    @Inject
    private PaymentScheduleInstanceItemService paymentScheduleInstanceItemService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long id) {

        try {
            RecordedInvoice recordedInvoice = recordedInvoiceService.findById(id);
            paymentScheduleInstanceItemService.checkPaymentRecordInvoice(recordedInvoice);
           

            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to generate acount operations", e);
            result.registerError(e.getMessage());
        }
    }
}