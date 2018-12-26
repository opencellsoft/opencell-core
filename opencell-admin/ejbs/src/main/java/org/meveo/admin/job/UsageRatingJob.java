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
 * The Class UsageRatingJob rate all opened EDRs.
 */
@Stateless
public class UsageRatingJob extends Job {

    /** The usage rating job bean. */
    @Inject
    private UsageRatingJobBean usageRatingJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        usageRatingJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.RATING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode("nbRuns");
        nbRuns.setAppliesTo("JOB_UsageRatingJob");
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setValueRequired(false);
        nbRuns.setDefaultValue("1");
        result.put("nbRuns", nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode("waitingMillis");
        waitingMillis.setAppliesTo("JOB_UsageRatingJob");
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setValueRequired(false);
        waitingMillis.setDefaultValue("0");
        result.put("waitingMillis", waitingMillis);

        CustomFieldTemplate rateUntilDate = new CustomFieldTemplate();
        rateUntilDate.setCode("rateUntilDate");
        rateUntilDate.setAppliesTo("JOB_UsageRatingJob");
        rateUntilDate.setActive(true);
        rateUntilDate.setDescription(resourceMessages.getString("jobExecution.rateUntilDate"));
        rateUntilDate.setFieldType(CustomFieldTypeEnum.DATE);
        rateUntilDate.setValueRequired(false);
        result.put("rateUntilDate", rateUntilDate);
        
        CustomFieldTemplate ratingGroup = new CustomFieldTemplate();
        ratingGroup.setCode("ratingGroup");
        ratingGroup.setAppliesTo("JOB_UsageRatingJob");
        ratingGroup.setActive(true);
        ratingGroup.setDescription(resourceMessages.getString("subscription.ratingGroup"));
        ratingGroup.setFieldType(CustomFieldTypeEnum.STRING);
        ratingGroup.setValueRequired(false);
        ratingGroup.setDefaultValue(null);
        result.put("ratingGroup", ratingGroup);

        return result;
    }

}