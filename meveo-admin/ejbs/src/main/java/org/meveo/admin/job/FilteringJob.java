package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.service.job.Job;
import org.meveo.service.script.JavaCompilerManager;
import org.meveo.service.script.ScriptInterface;

@Startup
@Singleton
public class FilteringJob extends Job {

	@Inject
	private FilteringJobBean filteringJobBean;

	@Inject
	private JavaCompilerManager javaCompilerManager;

	@Override
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser)
			throws BusinessException {
		String filterCode = jobInstance.getCustomFields().get("FilteringJob_filter").getEntityReferenceValue().getCode();
		String scriptCode =  jobInstance.getCustomFields().get("FilteringJob_script").getEntityReferenceValue().getCode();
		String recordVariableName = jobInstance.getStringCustomValue("FilteringJob_recordVariableName");
        Class<ScriptInterface> scriptInterfaceClass = javaCompilerManager.getScriptInterface(currentUser.getProvider(),scriptCode);
    	if(scriptInterfaceClass==null){
    		result.registerError("cannot find script with code "+scriptCode);
    	} else {
    		Map<String,Object> context = new HashMap<String,Object>();
			CustomFieldInstance variablesCFI = jobInstance.getCustomFields().get("FilteringJob_variables");
			if(variablesCFI!=null){
				context = variablesCFI.getMapValue();
			}
			filteringJobBean.execute(result, jobInstance.getParametres(), filterCode, scriptCode,context,recordVariableName, currentUser);
    	}
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.UTILS;
	}

	@Override
	public List<CustomFieldTemplate> getCustomFields() {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate filter = new CustomFieldTemplate();
		filter.setCode("FilteringJob_filter");
		filter.setAccountLevel(AccountLevelEnum.TIMER);
		filter.setActive(true);
		filter.setDescription("Filter");
		filter.setFieldType(CustomFieldTypeEnum.ENTITY);
		filter.setEntityClazz(Filter.class.getName());
		filter.setValueRequired(true);
		result.add(filter);

		CustomFieldTemplate scriptCF = new CustomFieldTemplate();
		scriptCF.setCode("FilteringJob_script");
		scriptCF.setAccountLevel(AccountLevelEnum.TIMER);
		scriptCF.setActive(true);
		scriptCF.setDescription("Script");
		scriptCF.setFieldType(CustomFieldTypeEnum.ENTITY);
		scriptCF.setEntityClazz(ScriptInstance.class.getName());
		scriptCF.setValueRequired(true);
		result.add(scriptCF);
		
		CustomFieldTemplate variablesCF = new CustomFieldTemplate();
		variablesCF.setCode("FilteringJob_variables");
		variablesCF.setAccountLevel(AccountLevelEnum.TIMER);
		variablesCF.setActive(true);
		variablesCF.setDescription("Init and finalize variables");
		variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
		variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
		variablesCF.setValueRequired(false);
		result.add(variablesCF); 

		CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
		recordVariableName.setCode("FilteringJob_recordVariableName");
		recordVariableName.setAccountLevel(AccountLevelEnum.TIMER);
		recordVariableName.setActive(true);
		recordVariableName.setDefaultValue("record");
		recordVariableName.setDescription("Record variable name");
		recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
		recordVariableName.setValueRequired(false);
		result.add(recordVariableName);

		return result;
	}

}
