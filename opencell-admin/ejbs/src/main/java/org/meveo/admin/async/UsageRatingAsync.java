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

/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobMultithreadingHistoryInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.job.JobExecutionService;

/**
 * @author anasseh
 * 
 */

@Stateless
public class UsageRatingAsync {

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Rate usage charges for a list of EDRs. One EDR at a time in a separate transaction.
     * 
     * @param edrs A list of EDRs
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     * @throws BusinessException BusinessException
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    @Interceptors({ JobMultithreadingHistoryInterceptor.class })
    public Future<String> launchAndForget(List<Long> edrs, JobExecutionResultImpl result, MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        int i = 0;
        for (Long edrId : edrs) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            try {
                usageRatingService.ratePostpaidUsage(edrId);
                jobExecutionService.registerSucces(result);

            } catch (Exception e) {

                String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);

                StringBuilder aLine = new StringBuilder("Edr Id : ").append(edrId).append(" RejectReason : ").append(rejectReason);
                jobExecutionService.registerError(result, aLine.toString());
            }
            
            jobExecutionService.decCounterElementsRemaining(result);
        }
        return new AsyncResult<String>("OK");
    }
}
