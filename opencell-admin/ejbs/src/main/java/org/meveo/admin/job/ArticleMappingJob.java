package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.ScopedJob;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class ArticleMappingJob extends ScopedJob {

    private static final String JOB_INSTANCE_ARTICLE_MAPPING_JOB = "JobInstance_ArticleMappingJob";

    /**
     * Job bean
     */
    @Inject
    private ArticleMappingJobBean articleMappingJobBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        articleMappingJobBean.execute(result, jobInstance);
        return result;
    }

    /**
     * Get job category
     *
     * @return {@link MeveoJobCategoryEnum#UTILS}
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_JOB_ITEMS_LIMIT, CustomFieldTemplateUtils.buildCF(CF_JOB_ITEMS_LIMIT, resourceMessages.getString("jobExecution.jobItemsLimit"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;field:0", null, false, null, null, JOB_INSTANCE_ARTICLE_MAPPING_JOB));

        result.put(CF_JOB_DURATION_LIMIT, CustomFieldTemplateUtils.buildCF(CF_JOB_DURATION_LIMIT, resourceMessages.getString("jobExecution.jobDurationLimit"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;field:1", null, false, null, null, JOB_INSTANCE_ARTICLE_MAPPING_JOB));

        result.put(CF_JOB_TIME_LIMIT, CustomFieldTemplateUtils.buildCF(CF_JOB_TIME_LIMIT, resourceMessages.getString("jobExecution.jobTimeLimit"),
                CustomFieldTypeEnum.STRING, "tab:Configuration:0;field:2", null, false, null, null, JOB_INSTANCE_ARTICLE_MAPPING_JOB));

        return result;
    }
}