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
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;

/**
 * Job implementation to match prepaid wallets
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class PrepaidWalletMatchJobBean extends IteratorBasedJobBean<Long> {

    private static final long serialVersionUID = -3877756729428288947L;

    @Inject
    private WalletService walletService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    private String matchingChargeCode;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::matchPrepaidWallet, null, null, JobSpeedEnum.NORMAL);
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of Prepaid wallet ids
     */
    private Optional<Iterator<Long>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        matchingChargeCode = jobExecutionResult.getJobInstance().getParametres();

        List<Long> wallets = walletService.getWalletsToMatch(new Date());

        return Optional.of(new SynchronizedIterator<Long>(wallets));
    }

    /**
     * Match prepaid wallets
     * 
     * @param walletId Prepaid wallet ID to match
     * @param jobExecutionResult Job execution result
     */
    private void matchPrepaidWallet(Long walletId, JobExecutionResultImpl jobExecutionResult) {

        WalletInstance wallet = walletService.findById(walletId);
        oneShotChargeInstanceService.matchPrepaidWallet(wallet, matchingChargeCode);
    }
}