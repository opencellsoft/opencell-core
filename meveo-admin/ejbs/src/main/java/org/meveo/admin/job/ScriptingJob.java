package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.job.Job;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

@Startup
@Singleton
public class ScriptingJob extends Job {
	
	@Inject
	ScriptInstanceService scriptInstanceService;
	
	@Override
	@Asynchronous
	public void execute(JobInstance jobInstance, User currentUser) {
		super.execute(jobInstance, currentUser);
	}

	@SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {

	    String scriptCode = ((EntityReferenceWrapper)customFieldInstanceService.getCFValue(jobInstance, "ScriptingJob_script", currentUser)).getCode();
        Class<ScriptInterface> scriptInterfaceClass = scriptInstanceService.getScriptInterface(currentUser.getProvider(),scriptCode);
    	if(scriptInterfaceClass==null){
    		result.registerError("cannot find script with code "+scriptCode);
    	} else {
    		try{
    			ScriptInterface scriptInterface=scriptInterfaceClass.newInstance();
                Map<String, Object> context = (Map<String, Object>) customFieldInstanceService.getCFValue(jobInstance, "ScriptingJob_variables", currentUser);
                if (context == null) {
                    context = new HashMap<String, Object>();
                }
    			scriptInterface.execute(context,currentUser.getProvider(),currentUser);	
    		} catch(Exception e){
    			result.registerError("Error in "+scriptCode+" execution :"+e.getMessage());
    		}
    	}
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.MEDIATION;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate scriptCF = new CustomFieldTemplate();
		scriptCF.setCode("ScriptingJob_script");
		scriptCF.setAppliesTo("JOB_ScriptingJob");
		scriptCF.setActive(true);
		scriptCF.setDescription("Script to run");
		scriptCF.setFieldType(CustomFieldTypeEnum.ENTITY);
		scriptCF.setEntityClazz(ScriptInstance.class.getName());
		scriptCF.setValueRequired(true);
		result.put("ScriptingJob_script", scriptCF);
		
		CustomFieldTemplate variablesCF = new CustomFieldTemplate();
		variablesCF.setCode("ScriptingJob_variables");
		variablesCF.setAppliesTo("JOB_ScriptingJob");
		variablesCF.setActive(true);
		variablesCF.setDescription("Script variables");
		variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
		variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
		variablesCF.setValueRequired(false);
		variablesCF.setMaxValue(100L);
		result.put("ScriptingJob_variables", variablesCF); 
		
		return result;
    }
}
