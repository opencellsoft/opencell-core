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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;

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
    private SubscriptionStatusJobBean subscriptionStatusJobBean;

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Override
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        Date untilDate = (Date) this.getParamOrCFValue(jobInstance, "untilDate");
        if (untilDate == null) {
            untilDate = new Date();
        }
        try {

            List<Long> subscriptionIds = subscriptionService.getSubscriptionsToRenewOrNotify(untilDate);
            jobExecutionService.initCounterElementsRemaining(result, subscriptionIds.size());
            int i = 0;
            for (Long subscriptionId : subscriptionIds) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                subscriptionStatusJobBean.updateSubscriptionStatus(result, subscriptionId, untilDate);
                jobExecutionService.decCounterElementsRemaining(result);
            }

        } catch (Exception e) {
            log.error("Failed to run subscription status job {}", jobInstance.getCode(), e);
            jobExecutionService.registerError(result, e.getMessage());
        }

        try {
            List<Long> serviceIds = serviceInstanceService.getSubscriptionsToRenewOrNotify(untilDate);
            int i = 0;
            for (Long serviceId : serviceIds) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                subscriptionStatusJobBean.updateServiceInstanceStatus(result, serviceId, untilDate);
            }

        } catch (Exception e) {
            log.error("Failed to run subscription status job {}", jobInstance.getCode(), e);
            jobExecutionService.registerError(result, e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate untilDate = new CustomFieldTemplate();
        untilDate.setCode("untilDate");
        untilDate.setAppliesTo("JobInstance_SubscriptionStatusJob");
        untilDate.setActive(true);
        untilDate.setDescription(resourceMessages.getString("jobExecution.subscriptionUntilDate"));
        untilDate.setFieldType(CustomFieldTypeEnum.DATE);
        untilDate.setValueRequired(false);
        untilDate.setGuiPosition("tab:Configuration:0;field:0");
        result.put("untilDate", untilDate);

        return result;
    }

}