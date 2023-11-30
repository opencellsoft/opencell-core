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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.ReratingService;
import org.meveo.service.billing.impl.WalletOperationService;

/**
 * Job implementation to rerate wallet operations
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class ReRatingJobBean extends IteratorBasedScopedJobBean<Long> {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private ReratingService reratingService;

    private boolean useSamePricePlan;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::rerate, null, null, null);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Wallet operation Ids to re-rate
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {
        return getIterator(jobExecutionResult);
    }

    /**
     * Re-rate wallet operation
     *
     * @param walletOperationId Wallet operation id
     * @param jobExecutionResult Job execution result
     */
    private void rerate(Long walletOperationId, JobExecutionResultImpl jobExecutionResult) {

        reratingService.reRate(walletOperationId, useSamePricePlan);
    }
    private Optional<Iterator<Long>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        JobInstance jobInstance = jobExecutionResult.getJobInstance();
        String reratingTarget = (String) this.getParamOrCFValue(jobInstance, ReRatingJob.CF_RERATING_TARGET);
        List<EntityReferenceWrapper> batchEntityWrappers = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, ReRatingJob.CF_TARGET_BATCHES);
        List<Long> targetBatches = CollectionUtils.isNotEmpty(batchEntityWrappers) ? batchEntityWrappers.stream().map(br -> br.getId()).collect(Collectors.toList()) : Collections.emptyList();
        List<Long> ids = walletOperationService.listToRerate(reratingTarget, targetBatches);

        useSamePricePlan = "justPrice".equalsIgnoreCase(jobExecutionResult.getJobInstance().getParametres());

        return Optional.of(new SynchronizedIterator<Long>(ids));
    }

    @Override
    Optional<Iterator<Long>> getSynchronizedIteratorWithLimit(JobExecutionResultImpl jobExecutionResult, int jobItemsLimit) {
        return getSynchronizedIterator(jobExecutionResult, jobItemsLimit);
    }

    @Override
    Optional<Iterator<Long>> getSynchronizedIterator(JobExecutionResultImpl jobExecutionResult) {
        return getSynchronizedIterator(jobExecutionResult, 0);
    }
}