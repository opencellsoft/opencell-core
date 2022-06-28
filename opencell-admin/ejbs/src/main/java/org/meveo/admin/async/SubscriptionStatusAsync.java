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

import org.meveo.admin.job.UnitSubscriptionStatusJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.AggregatedWalletOperation;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting;
import org.meveo.service.job.JobExecutionService;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author M.BOUKAYOUA
 * @lastModifiedVersion 9.X
 */
@Stateless
public class SubscriptionStatusAsync {

    @Inject
    private UnitSubscriptionStatusJobBean unitSubscriptionStatusJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Update sub's status, one sub at a time in a separate transaction.
     * 
     * @param subscriptionIds A list of subs ids to update
     * @param untilDate until date
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForgetUpdateSubs(List<Long> subscriptionIds, Date untilDate, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        int i = 0;
        for (Long subscriptionId : subscriptionIds) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            unitSubscriptionStatusJobBean.updateSubscriptionStatus(result, subscriptionId, untilDate);
        }
        return new AsyncResult<>("OK");
    }

    /**
     * Update services's status, one service at a time in a separate transaction.
     * 
     * @param serviceIds A list of services ids to update
     * @param untilDate until date
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForgetUpdateServices(List<Long> serviceIds, Date untilDate, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        int i = 0;
        for (Long serviceId : serviceIds) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            unitSubscriptionStatusJobBean.updateServiceInstanceStatus(result, serviceId, untilDate);
        }
        return new AsyncResult<>("OK");
    }

}
