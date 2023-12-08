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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.billing.BatchEntity;
import org.meveo.model.billing.ReratingTargetEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;
import org.meveo.service.job.ScopedJob;

/**
 * Job definition to rerate wallet operations
 */
@Stateless
public class ReRatingJob extends ScopedJob {

    /** The re rating job bean. */
    @Inject
    private ReRatingJobBean reRatingJobBean;

    /**
     * To limit the scope of wallet operations to rerate.
     */
    public static final String CF_RERATING_TARGET = "ReRatingJobBean_reratingTarget";

    /**
     * Custom field contains a list of batch entities
     */
    public static final String CF_TARGET_BATCHES = "ReRatingJobBean_targetBatches";

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        reRatingJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.RATING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"), CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", "JobInstance_ReRatingJob"));
        result.put(Job.CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", "JobInstance_ReRatingJob"));
        result.put(CF_NB_PUBLISHERS, CustomFieldTemplateUtils.buildCF(CF_NB_PUBLISHERS, resourceMessages.getString("jobExecution.nbPublishers"), CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:2", "JobInstance_ReRatingJob"));

        CustomFieldTemplate reratingTargetCFTemplate = CustomFieldTemplateUtils.buildCF(CF_RERATING_TARGET,
                resourceMessages.getString("jobExecution.reratingTarget"), CustomFieldTypeEnum.LIST,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:3",
                ReratingTargetEnum.ALL.name(), false, CustomFieldStorageTypeEnum.SINGLE, null,
                "JobInstance_ReRatingJob", null);
        Map<String, String> listValues = new HashMap();
        for (ReratingTargetEnum reratingTarget : ReratingTargetEnum.values()) {
            listValues.put(reratingTarget.name(), reratingTarget.getLabel());
        }
        reratingTargetCFTemplate.setListValues(listValues);
        result.put(CF_RERATING_TARGET, reratingTargetCFTemplate);


        CustomFieldTemplate targetBatchesCFTemplate = CustomFieldTemplateUtils.buildCF(CF_TARGET_BATCHES,
                resourceMessages.getString("jobExecution.targetBatches"), CustomFieldTypeEnum.ENTITY,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:4",
                null, false, CustomFieldStorageTypeEnum.LIST, null,
                "JobInstance_ReRatingJob", null);
        targetBatchesCFTemplate.setEntityClazz(BatchEntity.class.getName());
        result.put(CF_TARGET_BATCHES, targetBatchesCFTemplate);

        result.put(CF_JOB_ITEMS_LIMIT, CustomFieldTemplateUtils.buildCF(CF_JOB_ITEMS_LIMIT, resourceMessages.getString("jobExecution.jobItemsLimit"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:5", "JobInstance_ReRatingJob"));

        result.put(CF_JOB_DURATION_LIMIT, CustomFieldTemplateUtils.buildCF(CF_JOB_DURATION_LIMIT, resourceMessages.getString("jobExecution.jobDurationLimit"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:6", "JobInstance_ReRatingJob"));

        result.put(CF_JOB_TIME_LIMIT, CustomFieldTemplateUtils.buildCF(CF_JOB_TIME_LIMIT, resourceMessages.getString("jobExecution.jobTimeLimit"),
                CustomFieldTypeEnum.STRING, "tab:Configuration:0;fieldGroup:Configuration:0;field:7", "JobInstance_ReRatingJob", 5L));

        return result;
    }
}