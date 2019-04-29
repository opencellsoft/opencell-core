/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.RatedTransactionsJobAggregationSetting.AggregationLevelEnum;
import org.meveo.service.job.Job;


/**
 * The Class RatedTransactionsJob create RatedTransaction for all opened WalletOperations.
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
        return JobCategoryEnum.INVOICING;
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
        customFieldNbRuns.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:0");
        result.put("nbRuns", customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("waitingMillis");
        customFieldNbWaiting.setAppliesTo("JobInstance_RatedTransactionsJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Custom fields:0;fieldGroup:Configuration:0;field:1");
        result.put("waitingMillis", customFieldNbWaiting);
        
        // aggregations
        
        CustomFieldTemplate cfActivateAggregation = new CustomFieldTemplate();
        cfActivateAggregation.setCode("activateAggregation");
        cfActivateAggregation.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfActivateAggregation.setActive(true);
        cfActivateAggregation.setDescription(resourceMessages.getString("ratedTransactionsJob.activateAggregation"));
        cfActivateAggregation.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfActivateAggregation.setDefaultValue("false");
        cfActivateAggregation.setValueRequired(false);
        cfActivateAggregation.setGuiPosition("tab:Custom fields:0;fieldGroup:Aggregation Settings:1;field:0");
        result.put("activateAggregation", cfActivateAggregation);
        
        CustomFieldTemplate cfGlobalAggregation = new CustomFieldTemplate();
        cfGlobalAggregation.setCode("globalAggregation");
        cfGlobalAggregation.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfGlobalAggregation.setActive(true);
        cfGlobalAggregation.setDescription(resourceMessages.getString("ratedTransactionsJob.globalAggregation"));
        cfGlobalAggregation.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfGlobalAggregation.setDefaultValue("false");
        cfGlobalAggregation.setValueRequired(false);
        cfGlobalAggregation.setGuiPosition("tab:Custom fields:0;fieldGroup:Aggregation Settings:1;field:1");
        result.put("globalAggregation", cfGlobalAggregation);
        
        CustomFieldTemplate cfAggregateByDay = new CustomFieldTemplate();
        cfAggregateByDay.setCode("aggregateByDay");
        cfAggregateByDay.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfAggregateByDay.setActive(true);
        cfAggregateByDay.setDescription(resourceMessages.getString("ratedTransactionsJob.aggregateByDay"));
        cfAggregateByDay.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfAggregateByDay.setDefaultValue("true");
        cfAggregateByDay.setValueRequired(false);
        cfAggregateByDay.setGuiPosition("tab:Custom fields:0;fieldGroup:Aggregation Settings:1;field:2");
        result.put("aggregateByDay", cfAggregateByDay);
        
        Map<String, String> listValues = new HashMap<>();
		listValues.put(AggregationLevelEnum.BA.name(), "Billing Account");
		listValues.put(AggregationLevelEnum.UA.name(), "User Account");
		listValues.put(AggregationLevelEnum.SUB.name(), "Subscription");
		listValues.put(AggregationLevelEnum.SI.name(), "Service Instance");
		listValues.put(AggregationLevelEnum.CI.name(), "Charge Instance");
		listValues.put(AggregationLevelEnum.DESC.name(), "Description");
        
        CustomFieldTemplate cfAggregationLevel = new CustomFieldTemplate();
        cfAggregationLevel.setCode("aggregationLevel");
        cfAggregationLevel.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfAggregationLevel.setActive(true);
        cfAggregationLevel.setDescription(resourceMessages.getString("ratedTransactionsJob.aggregationLevel"));
        cfAggregationLevel.setFieldType(CustomFieldTypeEnum.LIST);
        cfAggregateByDay.setDefaultValue(AggregationLevelEnum.BA.name());
        cfAggregationLevel.setValueRequired(false);
        cfAggregationLevel.setListValues(listValues);
        cfAggregationLevel.setGuiPosition("tab:Custom fields:0;fieldGroup:Aggregation Settings:1;field:3");
        result.put("aggregationLevel", cfAggregationLevel);
        
        CustomFieldTemplate cfCriteriaOrder = new CustomFieldTemplate();
        cfCriteriaOrder.setCode("aggregateByOrder");
        cfCriteriaOrder.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfCriteriaOrder.setActive(true);
        cfCriteriaOrder.setDescription(resourceMessages.getString("ratedTransactionsJob.aggregateByOrder"));
        cfCriteriaOrder.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfCriteriaOrder.setDefaultValue("false");
        cfCriteriaOrder.setValueRequired(false);
        cfCriteriaOrder.setGuiPosition("tab:Custom fields:0;fieldGroup:Additional Criteria:2;field:0");
        result.put("criteriaOrder", cfCriteriaOrder);
        
        CustomFieldTemplate cfCriteriaParam1 = new CustomFieldTemplate();
        cfCriteriaParam1.setCode("aggregateByParam1");
        cfCriteriaParam1.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfCriteriaParam1.setActive(true);
        cfCriteriaParam1.setDescription(resourceMessages.getString("ratedTransactionsJob.aggregateByParam1"));
        cfCriteriaParam1.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfCriteriaParam1.setDefaultValue("false");
        cfCriteriaParam1.setValueRequired(false);
        cfCriteriaParam1.setGuiPosition("tab:Custom fields:0;fieldGroup:Additional Criteria:2;field:1");
        result.put("criteriaParam1", cfCriteriaParam1);
        
        CustomFieldTemplate cfCriteriaParam2 = new CustomFieldTemplate();
        cfCriteriaParam2.setCode("aggregateByParam2");
        cfCriteriaParam2.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfCriteriaParam2.setActive(true);
        cfCriteriaParam2.setDescription(resourceMessages.getString("ratedTransactionsJob.aggregateByParam2"));
        cfCriteriaParam2.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfCriteriaParam2.setDefaultValue("false");
        cfCriteriaParam2.setValueRequired(false);
        cfCriteriaParam2.setGuiPosition("tab:Custom fields:0;fieldGroup:Additional Criteria:2;field:2");
        result.put("criteriaParam2", cfCriteriaParam2);
        
        CustomFieldTemplate cfCriteriaParam3 = new CustomFieldTemplate();
        cfCriteriaParam3.setCode("aggregateByParam3");
        cfCriteriaParam3.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfCriteriaParam3.setActive(true);
        cfCriteriaParam3.setDescription(resourceMessages.getString("ratedTransactionsJob.aggregateByParam3"));
        cfCriteriaParam3.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfCriteriaParam3.setDefaultValue("false");
        cfCriteriaParam3.setValueRequired(false);
        cfCriteriaParam3.setGuiPosition("tab:Custom fields:0;fieldGroup:Additional Criteria:2;field:3");
        result.put("criteriaParam3", cfCriteriaParam3);
        
        CustomFieldTemplate cfCriteriaExtra = new CustomFieldTemplate();
        cfCriteriaExtra.setCode("aggregateByExtraParam");
        cfCriteriaExtra.setAppliesTo("JobInstance_RatedTransactionsJob");
        cfCriteriaExtra.setActive(true);
        cfCriteriaExtra.setDescription(resourceMessages.getString("ratedTransactionsJob.aggregateByExtraParam"));
        cfCriteriaExtra.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        cfCriteriaExtra.setDefaultValue("false");
        cfCriteriaExtra.setValueRequired(false);
        cfCriteriaExtra.setGuiPosition("tab:Custom fields:0;fieldGroup:Additional Criteria:2;field:4");
        result.put("criteriaExtra", cfCriteriaExtra);

        return result;
    }

}