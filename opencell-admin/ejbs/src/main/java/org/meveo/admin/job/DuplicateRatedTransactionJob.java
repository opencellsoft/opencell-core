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
    
    private static final String JOB_INSTANCE_DUPLICATION_RT_JOB = "JobInstance_DuplicateRatedTransactionJob";
    
    protected static final String DUPLICATION_RT_JOB_NEGATE_AMOUNT = "DuplicateRatedTransactionJob_negateAmount";

    protected static final String DUPLICATION_RT_JOB_ADVANCED_PARAMETERS = "DuplicateRatedTransactionJob_advancedParameters";
    
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
                "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", JOB_INSTANCE_DUPLICATION_RT_JOB));

        result.put(Job.CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", JOB_INSTANCE_DUPLICATION_RT_JOB));

        result.put(DUPLICATION_RT_JOB_NEGATE_AMOUNT, CustomFieldTemplateUtils.buildCF(DUPLICATION_RT_JOB_NEGATE_AMOUNT, "Apply negate amount", CustomFieldTypeEnum.BOOLEAN,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:2", "true", false, CustomFieldStorageTypeEnum.SINGLE, null, JOB_INSTANCE_DUPLICATION_RT_JOB, null));

        result.put(DUPLICATION_RT_JOB_ADVANCED_PARAMETERS, CustomFieldTemplateUtils.buildCF(DUPLICATION_RT_JOB_ADVANCED_PARAMETERS, "Advanced parameters for Job", CustomFieldTypeEnum.STRING,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:3", null, false, CustomFieldStorageTypeEnum.MAP, null, JOB_INSTANCE_DUPLICATION_RT_JOB, null));

        return result;
    }
    
}