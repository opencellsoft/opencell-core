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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.UsageRatingAsync;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

@Stateless
public class UsageRatingJobBean extends BaseJobBean {

    /**
     * Number of EDRS to process in a single job run
     */
    private static int PROCESS_NR_IN_JOB_RUN = 2000000;

    @Inject
    private Logger log;

    @Inject
    private EdrService edrService;

    @Inject
    private UsageRatingAsync usageRatingAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        jobExecutionService.counterRunningThreads(result, nbRuns);
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {
            Date rateUntilDate = null;
            String ratingGroup = null;
            try {
                rateUntilDate = (Date) this.getParamOrCFValue(jobInstance, "rateUntilDate");
                ratingGroup = (String) this.getParamOrCFValue(jobInstance, "ratingGroup");
            } catch (Exception e) {
                log.warn("Cant get customFields for {}. {}", jobInstance.getJobTemplate(), e.getMessage());
            }

            List<Long> edrIds = edrService.getEDRsToRate(rateUntilDate, ratingGroup, PROCESS_NR_IN_JOB_RUN);

            result.setNbItemsToProcess(edrIds.size());
            jobExecutionService.initCounterElementsRemaining(result, edrIds.size());

            List<Future<String>> futures = new ArrayList<>();
            SubListCreator<Long> subListCreator = new SubListCreator(edrIds, nbRuns.intValue());
            log.info("Will rate {} EDRS", edrIds.size());

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(usageRatingAsync.launchAndForget(subListCreator.getNextWorkSet(), result, lastCurrentUser));

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
                    result.addReport(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            // Check if there are any more EDRS to process and mark job as completed if there are none
            edrIds = edrService.getEDRsToRate(rateUntilDate, ratingGroup, 1);
            result.setDone(edrIds.isEmpty());

        } catch (Exception e) {
            log.error("Failed to run usage rating job", e);
            jobExecutionService.registerError(result, e.getMessage());
            result.addReport(e.getMessage());
        }
    }

}
