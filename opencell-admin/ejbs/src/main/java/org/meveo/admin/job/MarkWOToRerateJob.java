package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.model.communication.email.EmailTemplate;
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
 * Job definition to mark Open Wallet operations to rerate.
 *
 * @author Abdellatif BARI
 * @since 15.1.0
 */
@Stateless
public class MarkWOToRerateJob extends Job {

    /**
     * Custom field contains notification message which will send when job is done
     */
    public static final String CF_EMAIL_TEMPLATE = "ReRatingJobBean_emailTemplate";

    /**
     * Job bean
     */
    @Inject
    private MarkWOToRerateJobBean markWOToRerateJobBean;

    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        markWOToRerateJobBean.execute(result, jobInstance);
        return result;
    }

    /**
     * Get job category
     *
     * @return {@link MeveoJobCategoryEnum#RATING}
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.RATING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();
        CustomFieldTemplate emailTemplateCF = CustomFieldTemplateUtils.buildCF(CF_EMAIL_TEMPLATE, resourceMessages.getString("jobExecution.emailTemplate"), CustomFieldTypeEnum.ENTITY,
                "tab:Configuration:0;fieldGroup:Configuration:0;field:0", null, false, null, EmailTemplate.class.getName(), "JobInstance_MarkWOToRerateJob", null);
        emailTemplateCF.setDataFilterEL("{\"media\":\"EMAIL\"}");
        result.put(CF_EMAIL_TEMPLATE, emailTemplateCF);
        return result;
    }
}