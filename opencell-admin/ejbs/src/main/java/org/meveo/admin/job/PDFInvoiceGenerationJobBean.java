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
import org.slf4j.Logger;

@Stateless
public class PDFInvoiceGenerationJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private InvoiceService invoiceService;

    @Inject
    private InvoicingAsync invoicingAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        try {

            InvoicesToProcessEnum invoicesToProcessEnum = InvoicesToProcessEnum.valueOf((String) this.getParamOrCFValue(jobInstance, "invoicesToProcess"));
            if (invoicesToProcessEnum == null) {
                invoicesToProcessEnum = InvoicesToProcessEnum.FinalOnly;
            }

            String parameter = jobInstance.getParametres();

            Long billingRunId = null;
            if (parameter != null && parameter.trim().length() > 0) {
                try {
                    billingRunId = Long.parseLong(parameter);
                } catch (Exception e) {
                    log.error("error while getting billing run", e);
                    result.registerError(e.getMessage());
                }
            }

            List<Long> invoiceIds = this.fetchInvoiceIdsToProcess(invoicesToProcessEnum, billingRunId);

            result.setNbItemsToProcess(invoiceIds.size());
            log.info("PDFInvoiceGenerationJob number of invoices to process=" + invoiceIds.size());

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(invoiceIds, nbRuns.intValue());
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(invoicingAsync.generatePdfAsync((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
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
            log.error("Failed to generate PDF invoices", e);
            result.registerError(e.getMessage());
        }
    }

    private List<Long> fetchInvoiceIdsToProcess(InvoicesToProcessEnum invoicesToProcessEnum, Long billingRunId) {

        log.debug(" fetchInvoiceIdsToProcess for invoicesToProcessEnum = {} and billingRunId = {} ", invoicesToProcessEnum, billingRunId);
        List<Long> invoiceIds = null;

        switch (invoicesToProcessEnum) {
        case FinalOnly:
            invoiceIds = invoiceService.getInvoicesIdsValidatedWithNoPdf(billingRunId);
            break;

        case DraftOnly:
            invoiceIds = invoiceService.getDraftInvoiceIdsByBRWithNoPdf(billingRunId);
            break;

        case All:
            invoiceIds = invoiceService.getInvoiceIdsIncludeDraftByBRWithNoPdf(billingRunId);
            break;
        }
        return invoiceIds;

    }
}