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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.RecurringChargeAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.BillingCycleService;
import org.meveo.service.billing.impl.RecurringChargeInstanceService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionErrorService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

@Stateless
public class RecurringRatingJobBean extends BaseJobBean implements Serializable {

    private static final long serialVersionUID = 2226065462536318643L;

    @Inject
    private RecurringChargeAsync recurringChargeAsync;

    @Inject
    private RecurringChargeInstanceService recurringChargeInstanceService;

    @Inject
    private Logger log;

    @Inject
    private BillingCycleService billingCycleService;

    @Inject
    private JobExecutionErrorService jobExecutionErrorService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("start in running with parameter={}", jobInstance.getParametres());

        jobExecutionErrorService.purgeJobErrors(jobInstance);

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        jobExecutionService.counterRunningThreads(result, nbRuns);
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {
            // Determine rateUntilDate in the following order: rateUntilDate CF value, rateUntilDateEL CF value, today
            Date rateUntilDate = null;
            try {
                rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
            } catch (Exception e) {
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }
            if (rateUntilDate == null) {
                String rateUntilDateEL = (String) this.getParamOrCFValue(jobInstance, "rateUntilDateEL");
                if (rateUntilDateEL != null) {
                    rateUntilDate = ValueExpressionWrapper.evaluateExpression(rateUntilDateEL, null, Date.class);
                }
            }

            if (rateUntilDate == null) {
                rateUntilDate = new Date();
            }

            // Resolve billing cycles from CF value
            List<BillingCycle> billingCycles = null;
            List<EntityReferenceWrapper> billingCycleReferences = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, "rateBC");
            if (billingCycleReferences != null && !billingCycleReferences.isEmpty()) {
                billingCycles = billingCycleService.findByCodes(billingCycleReferences.stream().map(er -> er.getCode()).collect(Collectors.toList()));
            }

            List<Long> ids = recurringChargeInstanceService.findRecurringChargeInstancesToRate(InstanceStatusEnum.ACTIVE, rateUntilDate, billingCycles);
            int inputSize = ids.size();
            result.setNbItemsToProcess(inputSize);
            log.info("RecurringRatingJob - charges to rate={}", inputSize);
            jobExecutionService.initCounterElementsRemaining(result, inputSize);

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(ids, nbRuns.intValue());
            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(recurringChargeAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, rateUntilDate, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }

            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    jobExecutionService.registerError(result, cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to run recurring rating job", e);
            jobExecutionService.registerError(result, e.getMessage());
        }
        log.debug("end running RecurringRatingJobBean!");
    }
}
