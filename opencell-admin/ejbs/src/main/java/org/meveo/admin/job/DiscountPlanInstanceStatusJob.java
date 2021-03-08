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
import org.meveo.model.billing.DiscountPlanInstanceStatusEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;
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
public class DiscountPlanInstanceStatusJob extends Job {

    @Inject
    private DiscountPlanInstanceStatusJobBean discountPlanInstanceStatusJobBean;

    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

    @Override
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        String activateDiscountPlanInstanceFromDateEl = (String) this.getParamOrCFValue(jobInstance, "activateDiscountPlanInstanceFromDateEl");
        String expireDiscountPlanInstanceToDateEl = (String) this.getParamOrCFValue(jobInstance, "expireDiscountPlanInstanceToDateEl");
        boolean expireDiscountPlanInstance = (boolean) this.getParamOrCFValue(jobInstance, "expireDiscountPlanInstance");
        if (expireDiscountPlanInstance) {
            expireDiscountPlanInstance(result, jobInstance, expireDiscountPlanInstanceToDateEl);
        } else {
            activateDiscountPlanInstance(result, jobInstance, activateDiscountPlanInstanceFromDateEl);
        }

    }

    private void activateDiscountPlanInstance(JobExecutionResultImpl result, JobInstance jobInstance, String activateDiscountPlanInstanceFromDateEl) {
        Date activateDiscountPlanInstanceToDate = new Date();
        if (!StringUtils.isBlank(activateDiscountPlanInstanceFromDateEl)) {
            Map<Object, Object> context = new HashMap<>();
            context.put("jobInstance", jobInstance);
            activateDiscountPlanInstanceToDate = ValueExpressionWrapper.evaluateExpression(activateDiscountPlanInstanceFromDateEl, context, Date.class);
        }
        try {

            List<Long> discountPlanInstanceIds = discountPlanInstanceService.getDiscountPlanInstanceToActivate(activateDiscountPlanInstanceToDate);
            jobExecutionService.initCounterElementsRemaining(result, discountPlanInstanceIds.size());
            int i = 0;
            for (Long discountPlanInstanceId : discountPlanInstanceIds) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                discountPlanInstanceStatusJobBean.updateDiscountPlanInstanceStatus(result, discountPlanInstanceId, DiscountPlanInstanceStatusEnum.ACTIVE);
                jobExecutionService.decCounterElementsRemaining(result);
            }

        } catch (Exception e) {
            log.error("Failed to run discount plan instance status job {}", jobInstance.getCode(), e);
            jobExecutionService.registerError(result, e.getMessage());
        }
    }

    private void expireDiscountPlanInstance(JobExecutionResultImpl result, JobInstance jobInstance, String expireDiscountPlanInstanceToDateEl) {
        Date expireDiscountPlanInstanceToDate = new Date();
        if (!StringUtils.isBlank(expireDiscountPlanInstanceToDateEl)) {
            Map<Object, Object> context = new HashMap<>();
            context.put("jobInstance", jobInstance);
            expireDiscountPlanInstanceToDate = ValueExpressionWrapper.evaluateExpression(expireDiscountPlanInstanceToDateEl, context, Date.class);
        }
        try {

            List<Long> discountPlanInstanceIds = discountPlanInstanceService.getDiscountPlanInstanceToExpire(expireDiscountPlanInstanceToDate);
            jobExecutionService.initCounterElementsRemaining(result, discountPlanInstanceIds.size());
            int i = 0;
            for (Long discountPlanInstanceId : discountPlanInstanceIds) {
                i++;
                if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    break;
                }
                discountPlanInstanceStatusJobBean.updateDiscountPlanInstanceStatus(result, discountPlanInstanceId, DiscountPlanInstanceStatusEnum.EXPIRED);
                jobExecutionService.decCounterElementsRemaining(result);
            }

        } catch (Exception e) {
            log.error("Failed to run discount plan instance status job {}", jobInstance.getCode(), e);
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

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo("JobInstance_DiscountPlanInstanceStatusJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo("JobInstance_DiscountPlanInstanceStatusJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        CustomFieldTemplate activateDiscountPlanInstanceFromDateEl = new CustomFieldTemplate();
        activateDiscountPlanInstanceFromDateEl.setCode("activateDiscountPlanInstanceFromDateEl");
        activateDiscountPlanInstanceFromDateEl.setAppliesTo("JobInstance_DiscountPlanInstanceStatusJob");
        activateDiscountPlanInstanceFromDateEl.setActive(true);
        activateDiscountPlanInstanceFromDateEl.setDescription(resourceMessages.getString("jobExecution.activateDiscountPlanInstanceFromDateEl"));
        activateDiscountPlanInstanceFromDateEl.setFieldType(CustomFieldTypeEnum.STRING);
        activateDiscountPlanInstanceFromDateEl.setValueRequired(false);
        activateDiscountPlanInstanceFromDateEl.setGuiPosition("tab:Configuration:0;field:2");
        result.put("activateDiscountPlanInstanceFromDateEl", activateDiscountPlanInstanceFromDateEl);

        CustomFieldTemplate expireDiscountPlanInstanceToDateEl = new CustomFieldTemplate();
        expireDiscountPlanInstanceToDateEl.setCode("expireDiscountPlanInstanceToDateEl");
        expireDiscountPlanInstanceToDateEl.setAppliesTo("JobInstance_DiscountPlanInstanceStatusJob");
        expireDiscountPlanInstanceToDateEl.setActive(true);
        expireDiscountPlanInstanceToDateEl.setDescription(resourceMessages.getString("jobExecution.expireDiscountPlanInstanceToDateEl"));
        expireDiscountPlanInstanceToDateEl.setFieldType(CustomFieldTypeEnum.STRING);
        expireDiscountPlanInstanceToDateEl.setValueRequired(false);
        expireDiscountPlanInstanceToDateEl.setGuiPosition("tab:Configuration:0;field:3");
        result.put("expireDiscountPlanInstanceToDateEl", expireDiscountPlanInstanceToDateEl);

        CustomFieldTemplate expireDiscountPlanInstance = new CustomFieldTemplate();
        expireDiscountPlanInstance.setCode("expireDiscountPlanInstance");
        expireDiscountPlanInstance.setAppliesTo("JobInstance_DiscountPlanInstanceStatusJob");
        expireDiscountPlanInstance.setActive(true);
        expireDiscountPlanInstance.setDescription(resourceMessages.getString("jobExecution.expireDiscountPlanInstance"));
        expireDiscountPlanInstance.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        expireDiscountPlanInstance.setValueRequired(false);
        expireDiscountPlanInstance.setGuiPosition("tab:Configuration:0;field:4");
        result.put("expireDiscountPlanInstance", expireDiscountPlanInstance);

        return result;
    }

}