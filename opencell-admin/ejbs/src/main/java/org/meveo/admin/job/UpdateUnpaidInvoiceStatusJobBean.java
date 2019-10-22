package org.meveo.admin.job;

import java.util.HashMap;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * A bean used to update unpaid invoices status
 * 
 * @author akadid abdelmounaim
 * @lastModifiedVersion 8.0
 */
public class UpdateUnpaidInvoiceStatusJobBean extends BaseJobBean {

    @Inject
    protected Logger log;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    InvoiceService invoiceService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        
        HashMap<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put("context", jobInstance);
        try {
            invoiceService.updateUnpaidInvoicesStatus();
            //result.setNbItemsToProcess(unpaidInvoices.size());
            /*
            int i = 0;
            for (Invoice invoice : unpaidInvoices) {
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
            	invoice.setStatus(InvoiceStatusEnum.UNPAID);
                result.registerSucces();
            }
            */

        } catch (BusinessException e) {
            log.error("Failed to run the job : SendInvoiceJob", e);
            result.registerError(e.getMessage());
        }

    }
}
