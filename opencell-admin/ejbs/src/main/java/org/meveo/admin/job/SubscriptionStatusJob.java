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

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.async.SubscriptionStatusAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.job.Job;

/**
 * Handles subscription renewal or termination once subscription expires, fire handles renewal notice events
 * 
 * @author Andrius Karpavicius
 * @author Khalid HORRI
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class SubscriptionStatusJob extends Job {

    @Inject
    private SubscriptionStatusAsync subscriptionStatusAsync;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Override
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        Date untilDate = (Date) this.getParamOrCFValue(jobInstance, "untilDate");
        if (untilDate == null) {
            untilDate = new Date();
        }
        try {
            MeveoUser lastCurrentUser = currentUser.unProxy();

            // process subscriptions update status
            List<Long> subscriptionIds = subscriptionService.getSubscriptionsToRenewOrNotify(untilDate);
            log.info("nbr of subscriptionIds to process:{}", subscriptionIds.size());
            SubListCreator<Long> subsSubListCreator = new SubListCreator<>(subscriptionIds, nbRuns.intValue());
            List<Future<String>> subsFutures = new ArrayList<>();

            while (subsSubListCreator.isHasNext()) {
                subsFutures.add(subscriptionStatusAsync.launchAndForgetUpdateSubs(subsSubListCreator.getNextWorkSet(),
                        untilDate, result, lastCurrentUser));
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
            // Wait for all subs async methods to finish
            for (Future<String> future : subsFutures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    result.addReport(cause.getMessage());
                    log.error("Failed to execute subscriptionStatusAsync.launchAndForgetUpdateSubs() method", cause);
                }
            }

            // process services update status
            List<Long> serviceIds = serviceInstanceService.getSubscriptionsToRenewOrNotify(untilDate);
            log.info("nbr of serviceIds to process:{}", serviceIds.size());
            SubListCreator<Long> servicesSubListCreator = new SubListCreator<>(serviceIds, nbRuns.intValue());
            List<Future<String>> servicesFutures = new ArrayList<>();

            while (servicesSubListCreator.isHasNext()) {
                servicesFutures.add(subscriptionStatusAsync.launchAndForgetUpdateServices(servicesSubListCreator.getNextWorkSet(),
                        untilDate, result, lastCurrentUser));
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }
            // Wait for all services async methods to finish
            for (Future<String> future : servicesFutures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    result.addReport(cause.getMessage());
                    log.error("Failed to execute subscriptionStatusAsync.launchAndForgetUpdateServices() method", cause);
                }
            }

        } catch (Exception e) {
            log.error("Failed to run subscription status job {}", jobInstance.getCode(), e);
            result.registerError(e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode("nbRuns");
        customFieldNbRuns.setAppliesTo("JobInstance_SubscriptionStatusJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:0");
        result.put("nbRuns", customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("waitingMillis");
        customFieldNbWaiting.setAppliesTo("JobInstance_SubscriptionStatusJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:1");
        result.put("waitingMillis", customFieldNbWaiting);

        CustomFieldTemplate untilDate = new CustomFieldTemplate();
        untilDate.setCode("untilDate");
        untilDate.setAppliesTo("JobInstance_SubscriptionStatusJob");
        untilDate.setActive(true);
        untilDate.setDescription(resourceMessages.getString("jobExecution.subscriptionUntilDate"));
        untilDate.setFieldType(CustomFieldTypeEnum.DATE);
        untilDate.setValueRequired(false);
        result.put("untilDate", untilDate);

        return result;
    }

}