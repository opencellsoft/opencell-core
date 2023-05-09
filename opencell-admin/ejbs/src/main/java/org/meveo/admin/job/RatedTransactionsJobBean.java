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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

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
public class RatedTransactionsJobBean extends IteratorBasedJobBean<WalletOperationNative> {

    private static final long serialVersionUID = -2740290205290535899L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

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
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, null, this::convertWoToRTBatch, this::hasMore, this::closeResultset, this::bindRTs);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    private Optional<Iterator<WalletOperationNative>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

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

        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 10000L);
        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        int fetchSize = batchSize.intValue() * nbThreads.intValue();

        // Number of Wallet operations to process in a single job run
        int processNrInJobRun = ParamBean.getInstance().getPropertyAsInteger("ratedTransactionsJob.processNrInJobRun", 4000000);

        Object[] convertSummary = (Object[]) emWrapper.getEntityManager().createNamedQuery("WalletOperation.getConvertToRTsSummary").getSingleResult();

        nrOfRecords = (Long) convertSummary[0];
        maxId = (Long) convertSummary[1];
        minId = (Long) convertSummary[2];

        if (nrOfRecords.intValue() == 0) {
            return Optional.empty();
        }

        statelessSession = emWrapper.getEntityManager().unwrap(Session.class).getSessionFactory().openStatelessSession();
        scrollableResults = statelessSession.createNamedQuery("WalletOperationNative.listConvertToRTs").setParameter("maxId", maxId).setReadOnly(true).setCacheable(false).setMaxResults(processNrInJobRun)
            .setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY);

        hasMore = nrOfRecords >= processNrInJobRun;

        return Optional.of(new SynchronizedIterator<WalletOperationNative>(scrollableResults, nrOfRecords.intValue()));
    }

    /**
     * Convert a multiple Wallet operations to a Rated transactions
     * 
     * @param walletOperations Wallet operations
     * @param jobExecutionResult Job execution result
     */
    private void convertWoToRTBatch(List<WalletOperationNative> walletOperations, JobExecutionResultImpl jobExecutionResult) {

        List<Long> rtIds = ratedTransactionService.createRatedTransactionsInBatch(walletOperations);

        ratedTransactionService.applyInvoicingRules(rtIds);
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
        scrollableResults.close();
        statelessSession.close();
    }

    /**
     * Bridge discount Rated transactions
     * 
     * @param jobExecutionResult Job execution result
     */
    private void bindRTs(JobExecutionResultImpl jobExecutionResult) {

        if (jobExecutionResult.getJobLauncherEnum() != JobLauncherEnum.WORKER && jobExecutionResult.getStatus() != JobExecutionResultStatusEnum.CANCELLED && nrOfRecords != null && nrOfRecords.intValue() > 0) {
            ratedTransactionService.bridgeDiscountRTs(minId, maxId);
        }
    }

    @Override
    protected boolean isProcessItemInNewTx() {
        return false;
    }
}