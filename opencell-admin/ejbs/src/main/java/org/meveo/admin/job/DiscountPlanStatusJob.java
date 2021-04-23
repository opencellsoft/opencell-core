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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles discount plan expiration
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 11.0
 */
@Stateless
public class DiscountPlanStatusJob extends Job {

    @Inject
    private DiscountPlanStatusJobBean discountPlanStatusJobBean;

    @Inject
    private DiscountPlanService discountPlanService;

    @Override
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        String expireDiscountPlanToDateEl = (String) this.getParamOrCFValue(jobInstance, "expireDiscountPlanToDateEl");
        Date expireDiscountPlanToDate = new Date();
        if (!StringUtils.isBlank(expireDiscountPlanToDateEl)) {
            Map<Object, Object> context = new HashMap<>();
            context.put("jobInstance", jobInstance);
            expireDiscountPlanToDate = ValueExpressionWrapper.evaluateExpression(expireDiscountPlanToDateEl, context, Date.class);
        }
        try {

            List<Long> discountPlanIds = discountPlanService.getDiscountPlanToExpire(expireDiscountPlanToDate);
            jobExecutionService.initCounterElementsRemaining(result, discountPlanIds.size());
            int i = 0;
            for (Long discountPlanId : discountPlanIds) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                discountPlanStatusJobBean.expireDiscountPlan(result, discountPlanId);
                jobExecutionService.decCounterElementsRemaining(result);
            }

        } catch (Exception e) {
            log.error("Failed to run discount plan status job {}", jobInstance.getCode(), e);
            jobExecutionService.registerError(result, e.getMessage());
        }
        return result;

    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo("JobInstance_DiscountPlanStatusJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo("JobInstance_DiscountPlanStatusJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        CustomFieldTemplate expireDiscountPlanToDateEl = new CustomFieldTemplate();
        expireDiscountPlanToDateEl.setCode("expireDiscountPlanToDateEl");
        expireDiscountPlanToDateEl.setAppliesTo("JobInstance_DiscountPlanStatusJob");
        expireDiscountPlanToDateEl.setActive(true);
        expireDiscountPlanToDateEl.setDescription(resourceMessages.getString("jobExecution.expireDiscountPlanToDateEl"));
        expireDiscountPlanToDateEl.setFieldType(CustomFieldTypeEnum.STRING);
        expireDiscountPlanToDateEl.setValueRequired(false);
        expireDiscountPlanToDateEl.setGuiPosition("tab:Configuration:0;field:0");
        result.put("expireDiscountPlanToDateEl", expireDiscountPlanToDateEl);

        return result;
    }

}