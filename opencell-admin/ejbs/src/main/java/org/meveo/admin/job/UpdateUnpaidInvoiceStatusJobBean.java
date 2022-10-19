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

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.UpdateUnpaidInvoiceStatusAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.UncheckedThreadingException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.job.Job;
import org.slf4j.Logger;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A bean used to update unpaid invoices status
 * 
 * @author Mounir BOUKAYOUA
 * @lastModifiedVersion 10.X
 */
public class UpdateUnpaidInvoiceStatusJobBean extends BaseJobBean {

    @Inject
    protected Logger log;

    @Inject
    private UpdateUnpaidInvoiceStatusAsync updateUnpaidInvoiceStatusAsync;

    @Inject
    InvoiceService invoiceService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.info("Running UpdateUnpaidInvoiceStatusJob with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {
            List<Long> unpaidInvoicesIds = invoiceService.listUnpaidInvoicesIds();

            log.info("Nbr of Invoices to update their status to UNPAID {}", unpaidInvoicesIds.size());
            result.setNbItemsToProcess(unpaidInvoicesIds.size());

            SubListCreator<Long> subListCreator = new SubListCreator<>(unpaidInvoicesIds, nbRuns.intValue());
            List<Future<String>> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {

                Future<String> future = updateUnpaidInvoiceStatusAsync
                        .launchAndForget(subListCreator.getNextWorkSet(), result, lastCurrentUser);
                futures.add(future);
                try {
                    Thread.sleep(waitingMillis);
                } catch (InterruptedException e) {
                    log.error("", e);
                    throw new UncheckedThreadingException(e);
                }
            }

            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    throw new UncheckedThreadingException(e);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    result.addReport(cause.getMessage());
                    log.error("Failed to execute UpdateUnpaidInvoiceStatusJob async method", cause);
                }
            }
        } catch (BusinessException e) {
            log.error("Failed to run the job : UpdateUnpaidInvoiceStatusJobBean", e);
        }
    }
}
