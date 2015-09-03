package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class UsageRatingJob extends Job {

    @Inject
    private UsageRatingJobBean usageRatingJobBean;
    
	 @Inject
	 private ResourceBundle resourceMessages;

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobInstance jobInstance, User currentUser) {
        super.execute(jobInstance, currentUser);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
        usageRatingJobBean.execute(result, currentUser,jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.RATING;
    }
    
    @Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate nbRuns = new CustomFieldTemplate();
		nbRuns.setCode("UsageRatingJob_nbRuns");
		nbRuns.setAccountLevel(AccountLevelEnum.TIMER);
		nbRuns.setActive(true);
		nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		nbRuns.setValueRequired(false);
		nbRuns.setDefaultValue("1");
		result.put("UsageRatingJob_nbRuns", nbRuns);

		CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
		waitingMillis.setCode("UsageRatingJob_waitingMillis");
		waitingMillis.setAccountLevel(AccountLevelEnum.TIMER);
		waitingMillis.setActive(true);
		waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
		waitingMillis.setValueRequired(false);
		waitingMillis.setDefaultValue("0");
		result.put("UsageRatingJob_waitingMillis", waitingMillis);

		return result;
	}
}