package org.meveo.admin.job;

import java.util.HashMap;
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
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.job.Job;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

@Startup
@Singleton
public class FilteringJob extends Job {

	@Inject
	private FilteringJobBean filteringJobBean;

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Override
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser)
			throws BusinessException {
		String filterCode = ((EntityReferenceWrapper)jobInstance.getCFValue("FilteringJob_filter")).getCode();
		String scriptCode =  ((EntityReferenceWrapper)jobInstance.getCFValue("FilteringJob_script")).getCode();
		String recordVariableName = (String) jobInstance.getCFValue("FilteringJob_recordVariableName");
        Class<ScriptInterface> scriptInterfaceClass = scriptInstanceService.getScriptInterface(currentUser.getProvider(),scriptCode);
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
		return JobCategoryEnum.MEDIATION;
	}

	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate filter = new CustomFieldTemplate();
		filter.setCode("FilteringJob_filter");
		filter.setAccountLevel(AccountLevelEnum.TIMER);
		filter.setActive(true);
		filter.setDescription("Filter");
		filter.setFieldType(CustomFieldTypeEnum.ENTITY);
		filter.setEntityClazz(Filter.class.getName());
		filter.setValueRequired(true);
		result.put("FilteringJob_filter", filter);

		CustomFieldTemplate scriptCF = new CustomFieldTemplate();
		scriptCF.setCode("FilteringJob_script");
		scriptCF.setAccountLevel(AccountLevelEnum.TIMER);
		scriptCF.setActive(true);
		scriptCF.setDescription("Script");
		scriptCF.setFieldType(CustomFieldTypeEnum.ENTITY);
		scriptCF.setEntityClazz(ScriptInstance.class.getName());
		scriptCF.setValueRequired(true);
		result.put("FilteringJob_script", scriptCF);
		
		CustomFieldTemplate variablesCF = new CustomFieldTemplate();
		variablesCF.setCode("FilteringJob_variables");
		variablesCF.setAccountLevel(AccountLevelEnum.TIMER);
		variablesCF.setActive(true);
		variablesCF.setDescription("Init and finalize variables");
		variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
		variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
		variablesCF.setValueRequired(false);
		result.put("FilteringJob_variables", variablesCF); 

		CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
		recordVariableName.setCode("FilteringJob_recordVariableName");
		recordVariableName.setAccountLevel(AccountLevelEnum.TIMER);
		recordVariableName.setActive(true);
		recordVariableName.setDefaultValue("record");
		recordVariableName.setDescription("Record variable name");
		recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
		recordVariableName.setValueRequired(false);
		result.put("FilteringJob_recordVariableName", recordVariableName);

		return result;
	}

}
