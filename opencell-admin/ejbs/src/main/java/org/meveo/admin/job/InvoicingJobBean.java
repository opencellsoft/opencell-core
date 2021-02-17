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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * @author HORRI Khalid
 * @lastModifiedVersion 5.4
 */
@Stateless
public class InvoicingJobBean extends BaseJobBean {

    @Inject
    protected Logger log;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        jobExecutionService.counterRunningThreads(result, nbRuns);
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {
            List<BillingRun> billingRuns = getBillingRuns(this.getParamOrCFValue(jobInstance, "billingRuns"));

            log.info("BillingRuns to process={}", billingRuns.size());
            result.setNbItemsToProcess(billingRuns.size());
            jobExecutionService.initCounterElementsRemaining(result, billingRuns.size());

            for (BillingRun billingRun : billingRuns) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                try {
                    billingRunService.detach(billingRun);
                    billingRunService.validate(billingRun, nbRuns.longValue(), waitingMillis.longValue(), result.getJobInstance().getId(), result);
                    jobExecutionService.registerSucces(result);
                } catch (Exception e) {
                    log.error("Failed to run invoicing", e);
                    jobExecutionService.registerError(result, e.getMessage());
                }
                jobExecutionService.decCounterElementsRemaining(result);
            }
        } catch (Exception e) {
            log.error("Failed to run invoicing", e);
        }
    }

    /**
     * Get Billing runs to process
     *
     * @param billingRunsCF the billing runs getting from the custom field
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<BillingRun> getBillingRuns(Object billingRunsCF) {
        List<EntityReferenceWrapper> brList = (List<EntityReferenceWrapper>) billingRunsCF;
        List<BillingRun> billingRuns = new ArrayList<>();
        if (brList != null && !brList.isEmpty()) {
            List<Long> ids = brList.stream().map(br -> {
                String compositeCode = br.getCode();
                if (compositeCode == null) {
                    return null;
                }
                return Long.valueOf(compositeCode.split("/")[0]);
            }).collect(Collectors.toList());
            Map<String, Object> filters = new HashedMap();
            filters.put("inList id", ids);
            PaginationConfiguration paginationConfiguration = new PaginationConfiguration(filters);
            billingRuns = billingRunService.list(paginationConfiguration);
        } else {
            if (billingRuns == null || billingRuns.isEmpty()) {
                billingRuns = billingRunService.listByNamedQuery("BillingRun.getForInvoicing");
            }
        }
        return billingRuns;
    }
}