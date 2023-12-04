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
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.filter.FilterService;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Job definition to execute the given script for a list of entities returned from the given filter.
 * 
 * @author a.rouaguebe
 */
@Stateless
public class BatchFilteringJob extends Job {

    /** The filtering job bean. */
    @Inject
    private BatchFilteringJobBean batchFilteringJobBean;

    @Inject
    private FilterService filterService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        batchFilteringJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.MEDIATION;
    }

    /**
     * Get entity class associated with a Filter, specified as job instance configuration parameters
     */
    @Override
    public Class getTargetEntityClass(JobInstance jobInstance) {
        EntityReferenceWrapper filterCF = (EntityReferenceWrapper) jobInstance.getCfValue("BatchFilteringJob_filter");
        if (filterCF != null) {
            Filter filter = filterService.findByCode(filterCF.getCode());

            if (filter != null) {
                String className = filter.getEntityClass();
                if (className != null) {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        log.error("Class {}, specified in filter {}, was not found", className, filter.getCode());
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode(CF_NB_RUNS);
        nbRuns.setAppliesTo("JobInstance_BatchFilteringJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("-1");
        nbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode(Job.CF_WAITING_MILLIS);
        waitingMillis.setAppliesTo("JobInstance_BatchFilteringJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, waitingMillis);

        CustomFieldTemplate filter = new CustomFieldTemplate();
        filter.setCode("BatchFilteringJob_filter");
        filter.setAppliesTo("JobInstance_BatchFilteringJob");
        filter.setActive(true);
        filter.setDescription("Filter");
        filter.setFieldType(CustomFieldTypeEnum.ENTITY);
        filter.setEntityClazz(Filter.class.getName());
        filter.setValueRequired(true);
        filter.setGuiPosition("tab:Configuration:0;field:2");
        result.put("BatchFilteringJob_filter", filter);

        CustomFieldTemplate scriptCF = new CustomFieldTemplate();
        scriptCF.setCode("BatchFilteringJob_script");
        scriptCF.setAppliesTo("JobInstance_BatchFilteringJob");
        scriptCF.setActive(true);
        scriptCF.setDescription("Script");
        scriptCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        scriptCF.setEntityClazz(ScriptInstance.class.getName());
        scriptCF.setValueRequired(true);
        scriptCF.setGuiPosition("tab:Configuration:0;field:3");
        result.put("BatchFilteringJob_script", scriptCF);

        CustomFieldTemplate variablesCF = new CustomFieldTemplate();
        variablesCF.setCode("BatchFilteringJob_variables");
        variablesCF.setAppliesTo("JobInstance_BatchFilteringJob");
        variablesCF.setActive(true);
        variablesCF.setDescription("Init and finalize variables");
        variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
        variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
        variablesCF.setValueRequired(false);
        variablesCF.setMaxValue(256L);
        variablesCF.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        variablesCF.setGuiPosition("tab:Configuration:0;field:4");
        result.put("BatchFilteringJob_variables", variablesCF);

        CustomFieldTemplate variablesSqlCF = new CustomFieldTemplate();
        variablesSqlCF.setCode("BatchFilteringJob_sql_variables");
        variablesSqlCF.setAppliesTo("JobInstance_BatchFilteringJob");
        variablesSqlCF.setActive(true);
        variablesSqlCF.setDescription("SQL parameters");
        variablesSqlCF.setFieldType(CustomFieldTypeEnum.STRING);
        variablesSqlCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
        variablesSqlCF.setValueRequired(false);
        variablesSqlCF.setMaxValue(256L);
        variablesSqlCF.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        variablesSqlCF.setGuiPosition("tab:Configuration:0;field:5");
        result.put("BatchFilteringJob_sql_variables", variablesSqlCF);

        CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
        recordVariableName.setCode("BatchFilteringJob_recordVariableName");
        recordVariableName.setAppliesTo("JobInstance_BatchFilteringJob");
        recordVariableName.setActive(true);
        recordVariableName.setDefaultValue("record");
        recordVariableName.setDescription("Record variable name");
        recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
        recordVariableName.setValueRequired(false);
        recordVariableName.setMaxValue(256L);
        recordVariableName.setGuiPosition("tab:Configuration:0;field:6");
        result.put("BatchFilteringJob_recordVariableName", recordVariableName);

        CustomFieldTemplate batchSizeVariableName = new CustomFieldTemplate();
        batchSizeVariableName.setCode(CF_BATCH_SIZE);
        batchSizeVariableName.setAppliesTo("JobInstance_BatchFilteringJob");
        batchSizeVariableName.setActive(true);
        batchSizeVariableName.setDefaultValue("1000");
        batchSizeVariableName.setDescription("Number of records to process in a batch");
        batchSizeVariableName.setFieldType(CustomFieldTypeEnum.LONG);
        batchSizeVariableName.setValueRequired(false);
        batchSizeVariableName.setMaxValue(10000L);
        batchSizeVariableName.setGuiPosition("tab:Configuration:0;field:7");
        result.put("BatchFilteringJob_batchSizeName", batchSizeVariableName);

        return result;
    }

}