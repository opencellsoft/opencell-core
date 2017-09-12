package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Stateless
public class UsageRatingJob extends Job {

    @Inject
    private UsageRatingJobBean usageRatingJobBean;
    
    @Inject
    private ResourceBundle resourceMessages;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        usageRatingJobBean.execute(result,jobInstance);
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
		
		CustomFieldTemplate rateUtilDate = new CustomFieldTemplate();
		rateUtilDate.setCode("rateUtilDate");
		rateUtilDate.setAppliesTo("JOB_UsageRatingJob");
		rateUtilDate.setActive(true);
		rateUtilDate.setDescription(resourceMessages.getString("jobExecution.rateUtilDate"));
		rateUtilDate.setFieldType(CustomFieldTypeEnum.DATE);
		rateUtilDate.setValueRequired(false);		
		result.put("rateUtilDate", rateUtilDate);

		return result;
	}
}