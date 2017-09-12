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

@Stateless
public class RecurringRatingJob extends Job {

    @Inject
    private RecurringRatingJobBean recurringRatingJobBean;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        recurringRatingJobBean.execute(result,jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.RATING;
    }
    
    @Override
  	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

  		CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
  		customFieldNbRuns.setCode("nbRuns");
  		customFieldNbRuns.setAppliesTo("JOB_RecurringRatingJob");
  		customFieldNbRuns.setActive(true);
  		customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
  		customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
  		customFieldNbRuns.setValueRequired(false);
  		customFieldNbRuns.setDefaultValue("1");
  		result.put("nbRuns", customFieldNbRuns);

  		CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
  		customFieldNbWaiting.setCode("waitingMillis");
  		customFieldNbWaiting.setAppliesTo("JOB_RecurringRatingJob");
  		customFieldNbWaiting.setActive(true);
  		customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
  		customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
  		customFieldNbWaiting.setValueRequired(false);
  		customFieldNbWaiting.setDefaultValue("0");
  		result.put("waitingMillis", customFieldNbWaiting);
  		
		CustomFieldTemplate rateUtilDate = new CustomFieldTemplate();
		rateUtilDate.setCode("rateUtilDate");
		rateUtilDate.setAppliesTo("JOB_RecurringRatingJob");
		rateUtilDate.setActive(true);
		rateUtilDate.setDescription(resourceMessages.getString("jobExecution.rateUtilDate"));
		rateUtilDate.setFieldType(CustomFieldTypeEnum.DATE);
		rateUtilDate.setValueRequired(false);		
		result.put("rateUtilDate", rateUtilDate);

  		return result;
  	}
}