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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.commons.utils.ParamBean;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.billing.WalletOperationNative;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobExecutionResultStatusEnum;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.JobLauncherEnum;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationAggregationSettingsService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.job.Job;

/**
 * A job implementation to convert Open Wallet operations to Rated transactions
 * 
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 */
@Stateless
public class RatedTransactionsJobBean extends IteratorBasedScopedJobBean<WalletOperationNative> {

    private static final long serialVersionUID = -2740290205290535899L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private JobContextHolder jobContextHolder;

    @Inject
    private WalletOperationAggregationSettingsService walletOperationAggregationSettingsService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    private boolean hasMore = false;
    private StatelessSession statelessSession;
    private ScrollableResults scrollableResults;

    private Long minId = null;
    private Long maxId = null;
    private Long nrOfRecords = null;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED) // Transaction set to REQUIRED, so ScrollableResultset would do paging. With TX=NEVER all data is retrieved at once resulting in memory increase
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::initJobOnWorkerNode, null, this::convertWoToRTBatch, this::hasMore, this::closeResultset, this::bindRTs);
        jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_MIN_ID, minId);
        jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_MAX_ID, maxId);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    private Optional<Iterator<WalletOperationNative>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        if ((boolean) getParamOrCFValue(jobInstance, RatedTransactionsJob.CF_USE_JOB_CONTEXT, true)) {
            initBillingAccountsData();
            initBillingRulesData();
        }

        EntityReferenceWrapper aggregationSettingsWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "woAggregationSettings", null);
        WalletOperationAggregationSettings aggregationSettings = null;
        if (aggregationSettingsWrapper != null) {
            aggregationSettings = walletOperationAggregationSettingsService.findByCode(aggregationSettingsWrapper.getCode());
        }

        // Aggregation is not supported here
        if (aggregationSettings != null) {
            return Optional.empty();
        }

        log.info("Remove wallet operations rated to 0");
        walletOperationService.removeZeroWalletOperation();

        return getIterator(jobExecutionResult);
    }

    /**
     * Initialize job settings on worker node
     * 
     * @param jobExecutionResult Job execution result
     */
    private void initJobOnWorkerNode(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        if ((boolean) getParamOrCFValue(jobInstance, RatedTransactionsJob.CF_USE_JOB_CONTEXT, true)) {
            initBillingAccountsData();
            initBillingRulesData();
        }
    }

    /**
     * Convert a multiple Wallet operations to a Rated transactions
     * 
     * @param walletOperations Wallet operations
     * @param jobExecutionResult Job execution result
     */
    private void convertWoToRTBatch(List<WalletOperationNative> walletOperations, JobExecutionResultImpl jobExecutionResult) {
        ratedTransactionService.createRatedTransactionsInBatch(walletOperations);
    }

    private boolean hasMore(JobInstance jobInstance) {
        return hasMore;
    }

    /**
     * Close data resultset
     * 
     * @param jobExecutionResult Job execution result
     */
    private void closeResultset(JobExecutionResultImpl jobExecutionResult) {
        if (scrollableResults != null) {
            scrollableResults.close();
            statelessSession.close();
        }
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return false;
    }

    /**
     * Bridge discount Rated transactions and destroy job context if needed
     * 
     * @param jobExecutionResult Job execution result
     */
    private void bindRTs(JobExecutionResultImpl jobExecutionResult) {
        Boolean runDiscountStep = (Boolean) getParamOrCFValue(jobExecutionResult.getJobInstance(), RatedTransactionsJob.CF_RUN_DISCOUNT_STEP, true);
        if (runDiscountStep && jobExecutionResult.getJobLauncherEnum() != JobLauncherEnum.WORKER && jobExecutionResult.getStatus() != JobExecutionResultStatusEnum.CANCELLED && nrOfRecords != null
                && nrOfRecords.intValue() > 0) {
            ratedTransactionService.bridgeDiscountRTs(minId, maxId);
        }
        if (!hasMore) {
            jobContextHolder.clearMap(RatedTransactionsJob.BILLING_RULES_MAP_KEY);
            jobContextHolder.clearMap(RatedTransactionsJob.BILLING_ACCOUNTS_MAP_KEY);
        }
    }

    public void initBillingAccountsData() {
        if (jobContextHolder.isNotEmpty(RatedTransactionsJob.BILLING_ACCOUNTS_MAP_KEY)) {
            return;
        }
        TypedQuery<Object[]> query = emWrapper.getEntityManager().createNamedQuery("BillingAccount.listIdByCode", Object[].class);
        List<Object[]> results = query.getResultList();
        Map<String, Long> data = new HashMap<>(results.stream().collect(Collectors.toMap(arr -> (String) arr[0], arr -> (Long) arr[1])));
        jobContextHolder.putMap(RatedTransactionsJob.BILLING_ACCOUNTS_MAP_KEY, data);
    }

    public void initBillingRulesData() {
        if (jobContextHolder.isNotEmpty(RatedTransactionsJob.BILLING_RULES_MAP_KEY)) {
            return;
        }
        TypedQuery<Object[]> query = emWrapper.getEntityManager().createNamedQuery("BillingRule.findAllByContractIdForRating", Object[].class);
        List<Object[]> results = query.getResultList();
        Map<Long, List<Object[]>> data = new HashMap<>(
            results.stream().collect(Collectors.groupingBy(result -> (Long) result[0], Collectors.mapping(result -> new Object[] { (Long) result[1], (String) result[2], (String) result[3] }, Collectors.toList()))));
        jobContextHolder.putMap(RatedTransactionsJob.BILLING_RULES_MAP_KEY, data);
    }

    private Optional<Iterator<WalletOperationNative>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 10000L);
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        int fetchSize = batchSize.intValue() * nbThreads.intValue();

        // Number of Wallet operations to process in a single job run
        int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("jobs.ratedTransactionsJob.processNrInJobRun", 4000000);

        if (jobItemsLimit > 0) {
            List<Long> ids = emWrapper.getEntityManager().createNamedQuery("WalletOperation.getOpenIds", Long.class).setMaxResults(jobItemsLimit).getResultList();

            nrOfRecords = Long.valueOf(ids.size());
            if (!ids.isEmpty()) {
                maxId = Long.valueOf(ids.get(ids.size() - 1));
                minId = Long.valueOf(ids.get(0));
            }
        } else {
            Object[] convertSummary = (Object[]) emWrapper.getEntityManager().createNamedQuery("WalletOperation.getConvertToRTsSummary").getSingleResult();

            nrOfRecords = (Long) convertSummary[0];
            maxId = (Long) convertSummary[1];
            minId = (Long) convertSummary[2];
        }

        if (nrOfRecords.intValue() == 0) {
            return Optional.empty();
        }

        statelessSession = emWrapper.getEntityManager().unwrap(Session.class).getSessionFactory().openStatelessSession();
        scrollableResults = statelessSession.createNamedQuery("WalletOperationNative.listConvertToRTs").setParameter("maxId", maxId).setReadOnly(true).setCacheable(false)
            .setMaxResults(processNrInJobRun > jobItemsLimit && jobItemsLimit > 0 ? jobItemsLimit : processNrInJobRun).setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

        hasMore = nrOfRecords >= processNrInJobRun;

        return Optional.of(new SynchronizedIterator<WalletOperationNative>(scrollableResults, nrOfRecords.intValue()));

    }

    @Override
    Optional<Iterator<WalletOperationNative>> getSynchronizedIteratorWithLimit(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        return getSynchronizedIterator(jobExecutionResult, jobItemsLimit);
    }

    @Override
    Optional<Iterator<WalletOperationNative>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult) {
        return getSynchronizedIterator(jobExecutionResult, 0);
    }
}