package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

@Stateless
public class RatingCancellationJob extends Job {

    public static final String CF_INVOICE_LINES_NR_RTS_PER_TX = "JobInstance_RatingCancellationJob_MaxRTsPerTransaction";

    @Inject
    private RatingCancellationJobBean invoiceLinesBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        invoiceLinesBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.RATING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"), CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1",
            false, null, null, "JobInstance_RatingCancellationJob"));
        result.put(Job.CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
            "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", false, null, null, "JobInstance_RatingCancellationJob"));
        result.put(CF_INVOICE_LINES_NR_RTS_PER_TX, CustomFieldTemplateUtils.buildCF(CF_INVOICE_LINES_NR_RTS_PER_TX, resourceMessages.getString("jobExecution.numberOfItems"), CustomFieldTypeEnum.LONG,
            "tab:Configuration:0;fieldGroup:Configuration:0;field:2", "100000", true, null, null, "JobInstance_RatingCancellationJob"));

        return result;
    }
}