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

import org.meveo.admin.job.UnitPaymentScheduleJobBean;
import org.meveo.admin.job.logging.JobMultithreadingHistoryInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.PaymentScheduleInstanceItem;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;

/**
 * The Class PaymentScheduleProcessingAsync.
 *
 * @author anasseh
 * @lastModifiedVersion 5.1
 */

@Stateless
public class PaymentScheduleProcessingAsync {

    /** The unit toto ps job bean. */
    @Inject
    private UnitPaymentScheduleJobBean unitPaymentScheduleJobBean;

    /** The JobExecution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    /** The current user provider. */
    @Inject
    private CurrentUserProvider currentUserProvider;

   

    /**
     * Process payment schedule.
     * 
     *
     * @param paymentScheduleInstanceItems the payment schedule instance items
     * @param result the result
     * @param lastCurrentUser the last current user
     * @return future result
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    @Interceptors({ JobMultithreadingHistoryInterceptor.class })
    public Future<String> launchAndForget(List<PaymentScheduleInstanceItem> paymentScheduleInstanceItems, JobExecutionResultImpl result,  MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        int i = 0;
        for (PaymentScheduleInstanceItem item : paymentScheduleInstanceItems) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitPaymentScheduleJobBean.execute(result, item);

            jobExecutionService.decCounterElementsRemaining(result);
        }
        return new AsyncResult<String>("OK");
    }
}
