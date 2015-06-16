package org.meveo.service.job;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
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

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {

        String scriptCode = jobInstance.getStringCustomValue("ScriptingJob_scriptCode");
    	ScriptInterface scriptInterface = JavaCompilerManager.allScriptInterfaces.get(scriptCode);
    	scriptInterface.setup(null);
    	scriptInterface.execute(null);
    	scriptInterface.teardown(null);
       
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
