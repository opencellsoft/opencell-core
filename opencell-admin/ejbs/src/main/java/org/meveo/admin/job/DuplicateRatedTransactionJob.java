package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

@Stateless
public class DuplicateRatedTransactionJob extends Job {

    @Inject
    private DuplicateRatedTransactionJobBean duplicateRatedTransactionJobBean;
    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        duplicateRatedTransactionJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"), CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", false, null, null, "JobInstance_DuplicateRatedTransactionJob"));

        result.put(Job.CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", false, null, null, "JobInstance_DuplicateRatedTransactionJob"));

        return result;
    }
    
    public Map<String, CustomFieldTemplate> getNegateAmountCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate negateAmountCF= new CustomFieldTemplate();
        negateAmountCF.setCode("filters");
        negateAmountCF.setAppliesTo("JobInstance_DuplicateRatedTransactionJob");
        negateAmountCF.setActive(true);
        negateAmountCF.setDescription(resourceMessages.getString("jobExecution.negateAmount"));
        negateAmountCF.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        negateAmountCF.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        result.put("DuplicateRatedTransactionJob_variables", negateAmountCF);

        return result;
    }
}