package org.meveo.admin.job.importexport;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

@Stateless
public class ImportCustomerBankDetailsJob extends Job {    
    @Inject
    private ImportCustomerBankDetailsJobBean importCustomerBankDetailsJobBean;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        //protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        importCustomerBankDetailsJobBean.execute(result, jobInstance.getParametres());
        //return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.IMPORT_HIERARCHY;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        generateCustomFields(result, CF_NB_RUNS, "jobExecution.nbRuns", "-1", "tab:Configuration:0;field:0");
        generateCustomFields(result, Job.CF_WAITING_MILLIS, "jobExecution.waitingMillis", "0", "tab:Configuration:0;field:1");

        return result;
    }
    
    private void generateCustomFields(Map<String, CustomFieldTemplate> result, String code, String description, String defaultVaue, String guiPosition) {
        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(code);
        customFieldNbRuns.setAppliesTo("JobInstance_ImportCustomerBankDetailsJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString(description));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue(defaultVaue);
        customFieldNbRuns.setGuiPosition(guiPosition);
        result.put(code, customFieldNbRuns);
    }
    
}