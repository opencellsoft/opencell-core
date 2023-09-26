package org.meveo.admin.job;

import static org.meveo.model.jobs.MeveoJobCategoryEnum.INVOICING;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class BillingRunReportJob extends Job {

    @Inject
    private BillingRunReportJobBean billingRunReportJobBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        billingRunReportJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();
        CustomFieldTemplate customFieldBR = new CustomFieldTemplate();
        customFieldBR.setCode("billingRuns");
        customFieldBR.setAppliesTo("JobInstance_BillingRunReportJob");
        customFieldBR.setActive(true);
        customFieldBR.setDescription(resourceMessages.getString("jobExecution.billingRuns"));
        customFieldBR.setFieldType(CustomFieldTypeEnum.ENTITY);
        customFieldBR.setStorageType(CustomFieldStorageTypeEnum.LIST);
        customFieldBR.setEntityClazz("org.meveo.model.billing.BillingRun");
        customFieldBR.setValueRequired(false);
        customFieldBR.setGuiPosition("tab:Configuration:0;field:2");
        result.put("billingRuns", customFieldBR);
        return result;
    }
}
