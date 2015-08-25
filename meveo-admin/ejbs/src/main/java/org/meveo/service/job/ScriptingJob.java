package org.meveo.service.job;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.script.JavaCompilerManager;
import org.meveo.script.ScriptInterface;

@Startup
@Singleton
public class ScriptingJob extends Job {
	
	@Inject
	JavaCompilerManager javaCompilerManager;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {

        String scriptCode = jobInstance.getStringCustomValue("ScriptingJob_scriptCode");
    	ScriptInterface scriptInterface = javaCompilerManager.getScriptInterface(currentUser.getProvider(),scriptCode);
    	if(scriptInterface==null){
    		result.registerError("cannot find script with code "+scriptCode);
    	} else {
    		try{
    			scriptInterface.execute(null);	
    		} catch(Exception e){
    			result.registerError("Error in "+scriptCode+" execution :"+e.getMessage());
    		}
    	}
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public List<CustomFieldTemplate> getCustomFields() {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate scriptCodeCF = new CustomFieldTemplate();
		scriptCodeCF.setCode("ScriptingJob_scriptCode");
		scriptCodeCF.setAccountLevel(AccountLevelEnum.TIMER);
		scriptCodeCF.setActive(true);
		scriptCodeCF.setDescription("ScriptCode to run");
		scriptCodeCF.setFieldType(CustomFieldTypeEnum.STRING);
		scriptCodeCF.setValueRequired(true);
		result.add(scriptCodeCF);
		return result;
    }
}
