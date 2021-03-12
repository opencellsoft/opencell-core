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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationAggregationSettingsService;
import org.meveo.service.billing.impl.WalletOperationService;

/**
 * A job implementation to convert Open Wallet operations to Rated transactions
 * 
 * @author Edward P. Legaspi
 * @author Andrius Karpavicius
 */
@Stateless
public class RatedTransactionsJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = -2740290205290535899L;

    /**
     * Number of Wallet operations to process in a single job run
     */
    private static int PROCESS_NR_IN_JOB_RUN = 200000;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private WalletOperationAggregationSettingsService walletOperationAggregationSettingsService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::convertWoToRT, this::hasMore, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to convert to Rated transactions
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

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

        List<Long> ids = walletOperationService.listToRate(new Date(), PROCESS_NR_IN_JOB_RUN);

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    /**
     * Convert a single Wallet operation to a Rated transaction
     * 
     * @param woId Wallet operation id to convert
     * @param jobExecutionResult Job execution result
     */
    private void convertWoToRT(Long woId, JobExecutionResultImpl jobExecutionResult) {

        WalletOperation walletOperation = walletOperationService.findById(woId);
        ratedTransactionService.createRatedTransaction(walletOperation, false);
    }

    private boolean hasMore(JobInstance jobInstance) {
        List<Long> ids = walletOperationService.listToRate(new Date(), 1);
        return !ids.isEmpty();
    }
}