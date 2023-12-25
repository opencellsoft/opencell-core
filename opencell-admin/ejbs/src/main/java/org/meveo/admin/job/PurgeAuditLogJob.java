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
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Job definition to automatically purge audit log based on a duration.
 *
 * @author Abdellatif BARI
 * @since 16.0.0
 */
@Stateless
public class PurgeAuditLogJob extends Job {

    /**
     * Custom field for a maximum age days to purge logs.
     */
    public static final String CF_MAX_AGE_DAYS = "maxAgeDays";

    /**
     * Job bean
     */
    @Inject
    private PurgeAuditLogJobBean purgeAuditLogJobBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        purgeAuditLogJobBean.execute(result, jobInstance);
        return result;
    }

    /**
     * Get job category
     *
     * @return {@link MeveoJobCategoryEnum#RATING}
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        result.put(CF_MAX_AGE_DAYS, CustomFieldTemplateUtils.buildCF(CF_MAX_AGE_DAYS, resourceMessages.getString("jobExecution.maxAgeDays"),
                CustomFieldTypeEnum.LONG, "tab:Configuration:0;field:0", "JobInstance_PurgeAuditLogJob"));

        return result;
    }
}