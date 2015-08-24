package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

/**
 * @author Edward P. Legaspi
 **/
@Startup
@Singleton
public class FilteringJob extends Job {

	@Inject
	private FilteringJobBean filteringJobBean;

	@Override
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser)
			throws BusinessException {
		String filterCode = null;
		String scriptInstanceCode = null;
		if (jobInstance.getStringCustomValue("FilteringJob_filterCode") != null) {
			filterCode = jobInstance.getStringCustomValue("FilteringJob_filterCode");
		}
		if (jobInstance.getStringCustomValue("FilteringJob_scriptIntanceCode") != null) {
			scriptInstanceCode = jobInstance.getStringCustomValue("FilteringJob_scriptIntanceCode");
		}

		filteringJobBean.execute(result, jobInstance.getParametres(), filterCode, scriptInstanceCode, currentUser);
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.UTILS;
	}

	@Override
	public List<CustomFieldTemplate> getCustomFields() {
		List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();

		CustomFieldTemplate filter = new CustomFieldTemplate();
		filter.setCode("FilteringJob_filterCode");
		filter.setAccountLevel(AccountLevelEnum.CUST);
		filter.setActive(true);
		filter.setDescription("Filter");
		filter.setFieldType(CustomFieldTypeEnum.STRING);
		filter.setValueRequired(true);
		result.add(filter);

		CustomFieldTemplate scriptInstance = new CustomFieldTemplate();
		scriptInstance.setCode("FilteringJob_scriptIntanceCode");
		scriptInstance.setAccountLevel(AccountLevelEnum.CUST);
		scriptInstance.setActive(true);
		scriptInstance.setDescription("Script Instance");
		scriptInstance.setFieldType(CustomFieldTypeEnum.STRING);
		scriptInstance.setValueRequired(true);
		result.add(scriptInstance);

		return result;
	}

}
