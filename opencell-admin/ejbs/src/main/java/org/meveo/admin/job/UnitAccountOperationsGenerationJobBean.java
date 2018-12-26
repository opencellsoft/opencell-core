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
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;
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

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long id, ScriptInterface script) {

        try {
           
            Invoice invoice = invoiceService.findById(id);
            recordedInvoiceService.generateRecordedInvoice(invoice);
            invoiceService.update(invoice);
            
            if(script != null) {
                Map<String, Object> context = new HashMap<String, Object>();
                context.put(Script.CONTEXT_ENTITY, invoice.getRecordedInvoice());
                context.put(Script.CONTEXT_CURRENT_USER, currentUser);
                context.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                script.execute(context);
            }
            
            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to generate acount operations", e);
            result.registerError(e.getMessage());
        }
    }
}