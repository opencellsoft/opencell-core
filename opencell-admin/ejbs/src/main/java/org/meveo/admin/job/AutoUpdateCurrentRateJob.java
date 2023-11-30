package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static javax.ejb.TransactionAttributeType.NEVER;

@Stateless
public class AutoUpdateCurrentRateJob extends Job {

    private static final String APPLIES_TO_NAME = "JobInstance_AutoUpdateCurrentRateJob";
    private static final String CF_CLEAN_APPLIED_RATE_INVOICE = "cleanAppliedRateInvoice";

    @Inject
    private AutoUpdateCurrentRateJobBean autoUpdateCurrentRateJobBean;

    @Override
    @TransactionAttribute(NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        autoUpdateCurrentRateJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_CLEAN_APPLIED_RATE_INVOICE, CustomFieldTemplateUtils.buildCF(CF_CLEAN_APPLIED_RATE_INVOICE,
                resourceMessages.getString("jobExecution.cleanAppliedRateInvoice"), CustomFieldTypeEnum.BOOLEAN,
                "tab:Configuration:0;field:0", "false", true, APPLIES_TO_NAME));

        return result;
    }

}