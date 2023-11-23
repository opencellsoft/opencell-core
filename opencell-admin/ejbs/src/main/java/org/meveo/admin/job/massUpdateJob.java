package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
public class massUpdateJob extends Job {

	public static final String CF_MASS_UPDATE_CHUNK = "CF_MASS_UPDATE_CHUNK";

	@Inject
	private UpdateStepExecutor updateStepExecutor;

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	protected JobExecutionResultImpl execute(JobExecutionResultImpl updateResult, JobInstance jobInstance) throws BusinessException {

		initUpdateStepParams(updateResult, jobInstance);
		updateStepExecutor.execute(updateResult, jobInstance);
		return updateResult;
	}

	private void initUpdateStepParams(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
		jobExecutionResult.addJobParam(updateStepExecutor.PARAM_CHUNK_SIZE,
				(Long) getParamOrCFValue(jobInstance, massUpdateJob.CF_MASS_UPDATE_CHUNK, 100000L));
		
		jobExecutionResult.addJobParam(updateStepExecutor.PARAM_UPDATE_QUERY,
				(String)getParamOrCFValue(jobInstance, updateStepExecutor.PARAM_UPDATE_QUERY));
		jobExecutionResult.addJobParam(updateStepExecutor.PARAM_READ_INTERVAL_QUERY,
				(String)getParamOrCFValue(jobInstance, UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY));
		jobExecutionResult.addJobParam(updateStepExecutor.PARAM_TABLE_ALIAS,
				(String)getParamOrCFValue(jobInstance, UpdateStepExecutor.PARAM_TABLE_ALIAS));
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return MeveoJobCategoryEnum.INVOICING;
	}

	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
		Map<String, CustomFieldTemplate> result = new HashMap<>();

		result.put(CF_NB_RUNS,
				CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"),
						CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", false,
						null, null, "JobInstance_massUpdateJob"));
		result.put(Job.CF_WAITING_MILLIS,
				CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS,
						resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", false, null, null,
						"JobInstance_massUpdateJob"));
		result.put(UpdateStepExecutor.PARAM_TABLE_ALIAS,
				CustomFieldTemplateUtils.buildCF(UpdateStepExecutor.PARAM_TABLE_ALIAS,
						resourceMessages.getString("jobExecution.massUpdate.limitQuery"), CustomFieldTypeEnum.STRING,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:2", "", false, null, null,
						"JobInstance_massUpdateJob"));
		result.put(CF_MASS_UPDATE_CHUNK,
				CustomFieldTemplateUtils.buildCF(CF_MASS_UPDATE_CHUNK,
						resourceMessages.getString("jobExecution.massUpdate.Size"), CustomFieldTypeEnum.LONG,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:3", "100000", false, null, null,
						"JobInstance_massUpdateJob"));
		result.put(UpdateStepExecutor.PARAM_UPDATE_QUERY,
				CustomFieldTemplateUtils.buildCF(UpdateStepExecutor.PARAM_UPDATE_QUERY,
						resourceMessages.getString("jobExecution.massUpdate.updateQuery"), CustomFieldTypeEnum.TEXT_AREA,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:4", "", false, null, null,
						"JobInstance_massUpdateJob"));
		result.put(UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY,
				CustomFieldTemplateUtils.buildCF(UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY,
						resourceMessages.getString("jobExecution.massUpdate.limitQuery"), CustomFieldTypeEnum.TEXT_AREA,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:5", "", false, null, null,
						"JobInstance_massUpdateJob"));
		return result;
	}
}