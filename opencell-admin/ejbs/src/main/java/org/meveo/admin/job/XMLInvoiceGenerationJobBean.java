package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.InvoicingAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.slf4j.Logger;

@Stateless
public class XMLInvoiceGenerationJobBean {

    @Inject
    private Logger log;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoicingAsync invoicingAsync;

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String parameter, JobInstance jobInstance) {
        log.debug("Running for parameter={}", parameter);
        try {
            Long billingRunId = null;
            if (parameter != null && parameter.trim().length() > 0) {
                try {
                    billingRunId = Long.parseLong(parameter);
                } catch (Exception e) {
                    log.error("error while getting billing run", e);
                    result.registerError(e.getMessage());
                }
            }

            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            nbRuns = (Long) customFieldInstanceService.getCFValue(jobInstance, "nbRuns");
            waitingMillis = (Long) customFieldInstanceService.getCFValue(jobInstance, "waitingMillis");

            try {
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
            } catch (Exception e) {
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }

            List<Long> invoiceIds = invoiceService.getInvoiceIdsByBRWithNoXml(billingRunId);
            log.info("invoices to process={}", invoiceIds == null ? null : invoiceIds.size());
            List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
            SubListCreator subListCreator = new SubListCreator(invoiceIds, nbRuns.intValue());
            result.setNbItemsToProcess(subListCreator.getListSize());
            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(invoicingAsync.generateXmlAsync((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }

            // Wait for all async methods to finish
            for (Future<Boolean> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

        } catch (Exception e) {
            log.error("Failed to generate XML invoices", e);
            result.registerError(e.getMessage());
            result.addReport(e.getMessage());
        }
    }
}