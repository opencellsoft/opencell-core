/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionErrorService;
import org.meveo.service.job.JobExecutionService;
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
    private JobExecutionErrorService jobExecutionErrorService;

    @Inject
    protected JobExecutionService jobExecutionService;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        jobExecutionErrorService.purgeJobErrors(jobInstance);

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        jobExecutionService.counterRunningThreads(result, nbRuns);
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {

            InvoicesToProcessEnum invoicesToProcessEnum = InvoicesToProcessEnum.valueOf((String) this.getParamOrCFValue(jobInstance, "invoicesToProcess", "FinalOnly"));
            String parameter = jobInstance.getParametres();

            Long billingRunId = null;
            if (parameter != null && parameter.trim().length() > 0) {
                try {
                    billingRunId = Long.parseLong(parameter);
                } catch (Exception e) {
                    log.error("error while getting billing run", e);
                    jobExecutionService.registerError(result, e.getMessage());
                }
            }

            List<Long> invoiceIds = this.fetchInvoiceIdsToProcess(invoicesToProcessEnum, billingRunId);

            result.setNbItemsToProcess(invoiceIds.size());
            log.info("PDFInvoiceGenerationJob number of invoices to process=" + invoiceIds.size());
            jobExecutionService.initCounterElementsRemaining(result, invoiceIds.size());

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
                    jobExecutionService.registerError(result, cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to generate PDF invoices", e);
            jobExecutionService.registerError(result, e.getMessage());
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