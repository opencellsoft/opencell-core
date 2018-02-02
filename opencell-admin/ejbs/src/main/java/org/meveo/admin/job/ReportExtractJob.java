package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

/**
 * @author Edward P. Legaspi
 * @created 30 Jan 2018
 **/
@Stateless
public class ReportExtractJob extends Job {

    @Inject
    private ReportExtractJobBean reportingJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        reportingJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.DWH;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode("nbRuns");
        customFieldNbRuns.setAppliesTo("JOB_ReportExtractJob");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("1");
        result.put("nbRuns", customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("waitingMillis");
        customFieldNbWaiting.setAppliesTo("JOB_ReportExtractJob");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        result.put("waitingMillis", customFieldNbWaiting);

        CustomFieldTemplate customFieldStartDate = new CustomFieldTemplate();
        customFieldStartDate.setCode("startDate");
        customFieldStartDate.setAppliesTo("JOB_ReportExtractJob");
        customFieldStartDate.setActive(true);
        customFieldStartDate.setDescription(resourceMessages.getString("jobExecution.startDate"));
        customFieldStartDate.setFieldType(CustomFieldTypeEnum.DATE);
        customFieldStartDate.setValueRequired(false);
        result.put("startDate", customFieldStartDate);

        CustomFieldTemplate customFieldEndDate = new CustomFieldTemplate();
        customFieldEndDate.setCode("endDate");
        customFieldEndDate.setAppliesTo("JOB_ReportExtractJob");
        customFieldEndDate.setActive(true);
        customFieldEndDate.setDescription(resourceMessages.getString("jobExecution.endDate"));
        customFieldEndDate.setFieldType(CustomFieldTypeEnum.DATE);
        customFieldEndDate.setValueRequired(false);
        result.put("endDate", customFieldEndDate);

        return result;
    }

}
