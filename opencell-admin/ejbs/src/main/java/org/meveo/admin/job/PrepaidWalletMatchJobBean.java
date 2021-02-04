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
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.WalletService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

@Stateless
public class PrepaidWalletMatchJobBean {

    @Inject
    private Logger log;

    @Inject
    private WalletService walletService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(String matchingChargeCode, JobExecutionResultImpl result) {
        log.debug("Running matchingChargeCode={}", matchingChargeCode);

        try {
            List<WalletInstance> wallets = walletService.getWalletsToMatch(new Date());

            log.debug("wallets to match {}", wallets.size());
            result.setNbItemsToProcess(wallets.size());
            jobExecutionService.initCounterElementsRemaining(result, wallets.size());
            int i = 0;
            for (WalletInstance wallet : wallets) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                log.debug("match wallet={}", wallet.getId());
                try {
                    oneShotChargeInstanceService.matchPrepaidWallet(wallet, matchingChargeCode);
                    jobExecutionService.registerSucces(result);
                } catch (Exception e) {
                    log.error("Failed to match prepaid wallet {}", wallet.getId(), e);
                    jobExecutionService.registerError(result, e.getMessage());
                }
                jobExecutionService.decCounterElementsRemaining(result);
            }
        } catch (Exception e) {
            log.error("Failed to match prepaid wallet ", e);
        }
    }
}