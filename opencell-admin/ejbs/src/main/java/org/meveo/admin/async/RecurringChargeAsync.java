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

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitRecurringRatingJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author anasseh
 * 
 */

@Stateless
public class RecurringChargeAsync {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Inject
    private UnitRecurringRatingJobBean unitRecurringRatingJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Process recurring charges of given recuring charge instances. Once charge instance at a time in a separate transaction.
     * 
     * @param ids A list of recurring charge instance ids
     * @param result Job execution result
     * @param maxDate Process untill date
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, Date maxDate, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        int i = 0;
        for (Long id : ids) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            log.debug("run recurringChargeInstace ID {}", id);
            unitRecurringRatingJobBean.execute(result, id, maxDate);
        }
        log.debug("End launchAndForget!");

        return new AsyncResult<String>("OK");
    }
}