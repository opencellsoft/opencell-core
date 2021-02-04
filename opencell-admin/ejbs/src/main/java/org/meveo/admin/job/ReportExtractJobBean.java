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

import org.meveo.admin.async.ReportExtractAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.finance.ReportExtractService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * List all ReportExtract and dispatched for asynch execution.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class ReportExtractJobBean extends BaseJobBean implements Serializable {

    private static final long serialVersionUID = 9159856207913605563L;

    @Inject
    private Logger log;

    @Inject
    private ReportExtractService reportExtractService;

    @Inject
    private ReportExtractAsync reportExtractAsync;

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

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        jobExecutionService.counterRunningThreads(result, nbRuns);
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {
            Date startDate = null, endDate = null;
            try {
                startDate = (Date) this.getParamOrCFValue(jobInstance, "startDate");
                endDate = (Date) this.getParamOrCFValue(jobInstance, "endDate");
            } catch (Exception e) {
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }

            // Resolve report extracts from CF value
            List<Long> reportExtractIds = null;
            List<EntityReferenceWrapper> reportExtractReferences = (List<EntityReferenceWrapper>) this.getParamOrCFValue(jobInstance, ReportExtractJob.CF_REPORTS);
            if (reportExtractReferences != null && !reportExtractReferences.isEmpty()) {
                reportExtractIds = reportExtractService.findByCodes(reportExtractReferences.stream().map(er -> er.getCode()).collect(Collectors.toList())).stream().map(re -> re.getId()).collect(Collectors.toList());

                // Or use all reports
            } else {
                reportExtractIds = reportExtractService.listIds();
            }

            int reportsToExecute = reportExtractIds == null ? null : reportExtractIds.size();
            log.debug("Reports to execute={}" + reportsToExecute);
            jobExecutionService.initCounterElementsRemaining(result, reportsToExecute);

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(reportExtractIds, nbRuns.intValue());

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(reportExtractAsync.launchAndForget((List<Long>) subListCreator.getNextWorkSet(), result, startDate, endDate, lastCurrentUser));
                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                        Thread.currentThread().interrupt();

                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                    Thread.currentThread().interrupt();

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