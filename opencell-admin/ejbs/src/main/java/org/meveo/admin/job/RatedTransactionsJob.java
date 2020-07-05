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
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting.AggregationLevelEnum;
import org.meveo.service.job.Job;

/**
 * The Class RatedTransactionsJob create RatedTransaction for all OPEN WalletOperations.
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RatedTransactionsJob extends Job {

    /** The rated transactions job bean. */
    @Inject
    private RatedTransactionsJobBean ratedTransactionsJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        ratedTransactionsJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode("nbRuns");
        customFieldNbRuns.setAppliesTo("JobInstance_RatedTransactionsJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;fieldGroup:Configuration:0;field:0");
        result.put("nbRuns", customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("waitingMillis");
        customFieldNbWaiting.setAppliesTo("JobInstance_RatedTransactionsJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;fieldGroup:Configuration:0;field:1");
        result.put("waitingMillis", customFieldNbWaiting);

        // aggregations
        CustomFieldTemplate customFieldAggregationMatrix = new CustomFieldTemplate();
        customFieldAggregationMatrix.setCode("woAggregationMatrix");
        customFieldAggregationMatrix.setAppliesTo("JobInstance_RatedTransactionsJob");
        customFieldAggregationMatrix.setActive(true);
        customFieldAggregationMatrix.setDescription(resourceMessages.getString("jobExecution.woAggregationMatrix"));
        customFieldAggregationMatrix.setFieldType(CustomFieldTypeEnum.ENTITY);
        customFieldAggregationMatrix.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        customFieldAggregationMatrix.setEntityClazz("org.meveo.model.billing.WalletOperationAggregationMatrix");
        customFieldAggregationMatrix.setValueRequired(false);
        customFieldAggregationMatrix.setGuiPosition("tab:Configuration:0;fieldGroup:Aggregation Settings:1;field:0");
        result.put("woAggregationMatrix", customFieldAggregationMatrix);

        CustomFieldTemplate cfGlobalAggregation = new CustomFieldTemplate();
        cfGlobalAggregation.setCode("globalAggregation");
        cfGlobalAggregation.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfGlobalAggregation.setActive(true);
        cfGlobalAggregation.setDescription(resourceMessages.getString("ratedTransactionsJob.globalAggregation"));
        cfGlobalAggregation.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfGlobalAggregation.setDefaultValue("false");
        cfGlobalAggregation.setValueRequired(false);
        cfGlobalAggregation.setGuiPosition("tab:Configuration:0;fieldGroup:Aggregation Settings:1;field:1");
        result.put("globalAggregation", cfGlobalAggregation);

        CustomFieldTemplate cfPeriodAggregation = new CustomFieldTemplate();
        cfPeriodAggregation.setCode("periodAggregation");
        cfPeriodAggregation.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfPeriodAggregation.setActive(true);
        cfPeriodAggregation.setDescription(resourceMessages.getString("ratedTransactionsJob.periodAggregation"));
        cfPeriodAggregation.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfPeriodAggregation.setDefaultValue("false");
        cfPeriodAggregation.setValueRequired(false);
        cfPeriodAggregation.setGuiPosition("tab:Configuration:0;fieldGroup:Aggregation Settings:1;field:2");
        result.put("periodAggregation", cfPeriodAggregation);

        CustomFieldTemplate customFieldFilter = new CustomFieldTemplate();
        customFieldFilter.setCode("woFilters");
        customFieldFilter.setAppliesTo("JobInstance_RatedTransactionsJob");
        customFieldFilter.setActive(true);
        customFieldFilter.setDescription(resourceMessages.getString("jobExecution.woFilters"));
        customFieldFilter.setFieldType(CustomFieldTypeEnum.ENTITY);
        customFieldFilter.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        customFieldFilter.setEntityClazz("org.meveo.model.filter.Filter");
        customFieldFilter.setValueRequired(false);
        customFieldFilter.setGuiPosition("tab:Configuration:0;fieldGroup:Aggregation Settings:1;field:3");
        result.put("woFilters", customFieldFilter);

        return result;
    }

}