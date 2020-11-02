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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.RatedTransactionAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.WalletOperationAggregationSettingsService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.Job;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RatedTransactionsJobBean extends BaseJobBean {

    /**
     * Number of Wallet operations to process in a single job run
     */
    private static int PROCESS_NR_IN_JOB_RUN = 2000000;

    @Inject
    private Logger log;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionAsync ratedTransactionAsync;

    @Inject
    private WalletOperationAggregationSettingsService walletOperationAggregationSettingsService;

    @Inject
    private FilterService filterService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running for with parameter={}", jobInstance.getParametres());

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {

            EntityReferenceWrapper aggregationSettingsWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "woAggregationSettings", null);
            WalletOperationAggregationSettings aggregationSettings = null;
            if (aggregationSettingsWrapper != null) {
                aggregationSettings = walletOperationAggregationSettingsService.findByCode(aggregationSettingsWrapper.getCode());
            }
            removeZeroWalletOperation();
            if (aggregationSettings != null) {
                executeWithAggregation(result, nbRuns, waitingMillis, aggregationSettings);

            } else {
                executeWithoutAggregation(result, nbRuns, waitingMillis);
            }

        } catch (Exception e) {
            log.error("Failed to rate transactions", e);
        }
    }

    private void removeZeroWalletOperation() {
        log.info("Remove wellet oprations rated to 0");
        walletOperationService.removeZeroWalletOperation();
    }

    private void executeWithoutAggregation(JobExecutionResultImpl result, Long nbRuns, Long waitingMillis) throws Exception {
        List<Long> walletOperations = walletOperationService.listToRate(new Date(), PROCESS_NR_IN_JOB_RUN);
        log.info("WalletOperations to convert into rateTransactions={}", walletOperations.size());
        result.setNbItemsToProcess(walletOperations.size());

        SubListCreator<Long> subListCreator = new SubListCreator<>(walletOperations, nbRuns.intValue());
        List<Future<String>> futures = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();
        while (subListCreator.isHasNext()) {
            futures.add(ratedTransactionAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, lastCurrentUser));
            try {
                Thread.sleep(waitingMillis.longValue());

            } catch (InterruptedException e) {
                log.error("", e);
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
                result.addReport(cause.getMessage());
                log.error("Failed to execute async method", cause);
            }
        }

        // Check if there are any more Wallet Operations to process and mark job as completed if there are none
        walletOperations = walletOperationService.listToRate(new Date(), PROCESS_NR_IN_JOB_RUN);
        result.setDone(walletOperations.isEmpty());
    }

    private void executeWithAggregation(JobExecutionResultImpl result, Long nbRuns, Long waitingMillis, WalletOperationAggregationSettings aggregationSetting) throws Exception {
        Date invoicingDate = new Date();
        List<AggregatedWalletOperation> aggregatedWo = walletOperationService.listToInvoiceIdsWithGrouping(invoicingDate, aggregationSetting);

        if (aggregatedWo == null || aggregatedWo.isEmpty()) {
            return;
        }

        log.info("Aggregated walletOperations to convert into rateTransactions={}", aggregatedWo.size());
        result.setNbItemsToProcess(aggregatedWo.size());

        SubListCreator<AggregatedWalletOperation> subListCreator = new SubListCreator<>(aggregatedWo, nbRuns.intValue());
        List<Future<String>> asyncReturns = new ArrayList<>();
        MeveoUser lastCurrentUser = currentUser.unProxy();
        while (subListCreator.isHasNext()) {
            asyncReturns.add(ratedTransactionAsync.launchAndForget((List<AggregatedWalletOperation>) subListCreator.getNextWorkSet(), result, lastCurrentUser, aggregationSetting,
                invoicingDate));
            try {
                Thread.sleep(waitingMillis.longValue());

            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        for (Future<String> futureItsNow : asyncReturns) {
            futureItsNow.get();
        }
    }

}
