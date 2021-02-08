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
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.StringUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.job.Job;
import org.meveo.service.script.Script;

/**
 * The Class ScriptingJob execute the given script.
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ScriptingJob extends Job {

    /** The scripting job bean. */
    @Inject
    ScriptingJobBean scriptingJobBean;

    @SuppressWarnings("unchecked")
    @Override
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        String scriptCode = null;
        try {
            scriptCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "ScriptingJob_script")).getCode();
            Map<String, Object> context = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "ScriptingJob_variables");
            if (context == null) {
                context = new HashMap<String, Object>();
            }
            context.put(Script.CONTEXT_ENTITY, jobInstance);
            context.put(Script.CONTEXT_ACTION, scriptCode);
            context.put(Script.CONTEXT_CURRENT_USER, currentUser);
            context.put(Script.CONTEXT_APP_PROVIDER, appProvider);

            scriptingJobBean.init(result, scriptCode, context);
            String txType = (String) this.getParamOrCFValue(jobInstance, "ScriptingJob_TransactionType", "REQUIRES_NEW");
            if (StringUtils.isBlank(txType) || "REQUIRES_NEW".equals(txType)) {
                scriptingJobBean.execute(result, scriptCode, context);
            } else {
                scriptingJobBean.executeWithoutTx(result, scriptCode, context);
            }
            scriptingJobBean.complete(result, scriptCode, context);

        } catch (Exception e) {
            log.error("Exception on init/execute script", e);
            jobExecutionService.registerError(result, "Error in " + scriptCode + " execution :" + e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.MEDIATION;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate scriptCF = new CustomFieldTemplate();
        scriptCF.setCode("ScriptingJob_script");
        scriptCF.setAppliesTo("JobInstance_ScriptingJob");
        scriptCF.setActive(true);
        scriptCF.setDescription("Script to run");
        scriptCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        scriptCF.setEntityClazz(ScriptInstance.class.getName());
        scriptCF.setValueRequired(true);
        scriptCF.setGuiPosition("tab:Configuration:0;field:0");
        result.put("ScriptingJob_script", scriptCF);

        CustomFieldTemplate variablesCF = new CustomFieldTemplate();
        variablesCF.setCode("ScriptingJob_variables");
        variablesCF.setAppliesTo("JobInstance_ScriptingJob");
        variablesCF.setActive(true);
        variablesCF.setDescription("Script variables");
        variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
        variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
        variablesCF.setValueRequired(false);
        variablesCF.setMaxValue(256L);
        variablesCF.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        variablesCF.setGuiPosition("tab:Configuration:0;field:1");
        result.put("ScriptingJob_variables", variablesCF);

        CustomFieldTemplate transactionTypeCF = new CustomFieldTemplate();
        transactionTypeCF.setCode("ScriptingJob_TransactionType");
        transactionTypeCF.setAppliesTo("JobInstance_ScriptingJob");
        transactionTypeCF.setActive(true);
        transactionTypeCF.setAllowEdit(true);
        transactionTypeCF.setDescription("Transaction type");
        transactionTypeCF.setFieldType(CustomFieldTypeEnum.LIST);
        Map<String, String> listValues = new HashMap<>();
        listValues.put("REQUIRES_NEW", "REQUIRES_NEW");
        listValues.put("NEVER", "NEVER");
        transactionTypeCF.setListValues(listValues);
        transactionTypeCF.setDefaultValue("REQUIRES_NEW");
        transactionTypeCF.setValueRequired(false);
        result.put("ScriptingJob_TransactionType", transactionTypeCF);

        return result;
    }
}