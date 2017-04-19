package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.job.Job;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

@Stateless
public class FilteringJob extends Job {

	@Inject
	private FilteringJobBean filteringJobBean;

	@Inject
	private ScriptInstanceService scriptInstanceService;
	
	@Inject
	private CustomFieldInstanceService customFieldInstanceService;
    
	@SuppressWarnings("unchecked")
    @Override
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance)
			throws BusinessException {
        String filterCode = ((EntityReferenceWrapper) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_filter")).getCode();
        String scriptCode = ((EntityReferenceWrapper) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_script")).getCode();
        String recordVariableName = (String) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_recordVariableName");

        ScriptInterface scriptInterface = null;
        try {
            scriptInterface = scriptInstanceService.getScriptInstance(scriptCode);

        } catch (EntityNotFoundException | InvalidScriptException e) {
            result.registerError(e.getMessage());
            return;
        }
        
        Map<String, Object> context = (Map<String, Object>) customFieldInstanceService.getCFValue(jobInstance, "FilteringJob_variables");
        if (context == null) {
            context = new HashMap<String, Object>();
        }
        filteringJobBean.execute(result, jobInstance.getParametres(), filterCode, scriptInterface, context, recordVariableName);
    }

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.MEDIATION;
	}

	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate filter = new CustomFieldTemplate();
		filter.setCode("FilteringJob_filter");
		filter.setAppliesTo("JOB_FilteringJob");
		filter.setActive(true);
		filter.setDescription("Filter");
		filter.setFieldType(CustomFieldTypeEnum.ENTITY);
		filter.setEntityClazz(Filter.class.getName());
		filter.setValueRequired(true);
		result.put("FilteringJob_filter", filter);

		CustomFieldTemplate scriptCF = new CustomFieldTemplate();
		scriptCF.setCode("FilteringJob_script");
		scriptCF.setAppliesTo("JOB_FilteringJob");
		scriptCF.setActive(true);
		scriptCF.setDescription("Script");
		scriptCF.setFieldType(CustomFieldTypeEnum.ENTITY);
		scriptCF.setEntityClazz(ScriptInstance.class.getName());
		scriptCF.setValueRequired(true);
		result.put("FilteringJob_script", scriptCF);
		
		CustomFieldTemplate variablesCF = new CustomFieldTemplate();
		variablesCF.setCode("FilteringJob_variables");
		variablesCF.setAppliesTo("JOB_FilteringJob");
		variablesCF.setActive(true);
		variablesCF.setDescription("Init and finalize variables");
		variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
		variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
		variablesCF.setValueRequired(false);
		variablesCF.setMaxValue(256L);
		variablesCF.setMapKeyType(CustomFieldMapKeyEnum.STRING);
		result.put("FilteringJob_variables", variablesCF); 

		CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
		recordVariableName.setCode("FilteringJob_recordVariableName");
		recordVariableName.setAppliesTo("JOB_FilteringJob");
		recordVariableName.setActive(true);
		recordVariableName.setDefaultValue("record");
		recordVariableName.setDescription("Record variable name");
		recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
		recordVariableName.setValueRequired(false);
		recordVariableName.setMaxValue(256L);		
		result.put("FilteringJob_recordVariableName", recordVariableName);

		return result;
	}

}
