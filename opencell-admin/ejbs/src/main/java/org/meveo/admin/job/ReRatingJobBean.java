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

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

@Stateless
public class ReRatingJobBean extends BaseJobBean implements Serializable {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatingService ratingService;

    @Inject
    protected Logger log;

    @Inject
    private JobExecutionService jobExecutionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobExecutionResultImpl result, boolean useSamePricePlan) {
        log.debug("Running useSamePricePlan={}", useSamePricePlan);

        try {
            List<Long> walletOperationIds = walletOperationService.listToRerate();

            log.info("rerate with useSamePricePlan={} ,#operations={}", useSamePricePlan, walletOperationIds.size());
            result.setNbItemsToProcess(walletOperationIds.size());
            int i = 0;
            for (Long walletOperationId : walletOperationIds) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                try {
                    ratingService.reRateInNewTx(walletOperationId, useSamePricePlan);
                    result.registerSucces();
                } catch (Exception e) {
                    // rejectededOperationProducer.fire(walletOperationId);
                    log.error("Failed to rerate operation {}", walletOperationId, e.getMessage());
                    result.registerError(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to rerate operations", e);
        }
    }
}
