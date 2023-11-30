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
public class MassUpdateJob extends Job {

	private static final String JOB_INSTANCE_MASS_UPDATE_JOB = "JobInstance_massUpdateJob";

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
		jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_CHUNK_SIZE,
				(Long) getParamOrCFValue(jobInstance, MassUpdateJob.CF_MASS_UPDATE_CHUNK, 100000L));
		jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_UPDATE_QUERY,
				(String)getParamOrCFValue(jobInstance, UpdateStepExecutor.PARAM_UPDATE_QUERY));
		jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY,
				(String)getParamOrCFValue(jobInstance, UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY));
		jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_TABLE_ALIAS,
				(String)getParamOrCFValue(jobInstance, UpdateStepExecutor.PARAM_TABLE_ALIAS));
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return MeveoJobCategoryEnum.UTILS;
	}

	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
		Map<String, CustomFieldTemplate> result = new HashMap<>();

		result.put(CF_NB_RUNS,
				CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"),
						CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", JOB_INSTANCE_MASS_UPDATE_JOB));
		result.put(Job.CF_WAITING_MILLIS,
				CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS,
						resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", JOB_INSTANCE_MASS_UPDATE_JOB));
		result.put(UpdateStepExecutor.PARAM_TABLE_ALIAS,
				CustomFieldTemplateUtils.buildCF(UpdateStepExecutor.PARAM_TABLE_ALIAS,
						resourceMessages.getString("jobExecution.massUpdate.limitQuery"), CustomFieldTypeEnum.STRING,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:2", "a", JOB_INSTANCE_MASS_UPDATE_JOB));
		result.put(CF_MASS_UPDATE_CHUNK,
				CustomFieldTemplateUtils.buildCF(CF_MASS_UPDATE_CHUNK,
						resourceMessages.getString("jobExecution.massUpdate.Size"), CustomFieldTypeEnum.LONG,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:3", "100000", JOB_INSTANCE_MASS_UPDATE_JOB));
		result.put(UpdateStepExecutor.PARAM_UPDATE_QUERY,
				CustomFieldTemplateUtils.buildCF(UpdateStepExecutor.PARAM_UPDATE_QUERY,
						resourceMessages.getString("jobExecution.massUpdate.updateQuery"), CustomFieldTypeEnum.TEXT_AREA,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:4", "", JOB_INSTANCE_MASS_UPDATE_JOB));
		result.put(UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY,
				CustomFieldTemplateUtils.buildCF(UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY,
						resourceMessages.getString("jobExecution.massUpdate.limitQuery"), CustomFieldTypeEnum.TEXT_AREA,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:5", "", JOB_INSTANCE_MASS_UPDATE_JOB));
		return result;
	}
}