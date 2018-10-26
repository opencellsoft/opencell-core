package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

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
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.2
 **/
@Stateless
public class UnitAccountOperationsGenerationJobBean {

    @Inject
    private Logger log;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private RecordedInvoiceService recordedInvoiceService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long id, ScriptInterface script) {

        try {
           
            Invoice invoice = invoiceService.findById(id);
            recordedInvoiceService.generateRecordedInvoice(invoice);
            invoiceService.update(invoice);
            
            if(script != null) {
                Map<String, Object> executeParams = new HashMap<String, Object>();
                executeParams.put(Script.CONTEXT_ENTITY, invoice.getRecordedInvoice());
                script.execute(executeParams);
            }
            
            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to generate acount operations", e);
            result.registerError(e.getMessage());
        }
    }
}