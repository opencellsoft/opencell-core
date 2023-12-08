/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.utils.CustomFieldTemplateUtils;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

/**
 * Job definition to link Open RatedTransaction to the
 * discountedRatedTransaction if exist
 * 
 * 
 */
@Stateless
public class RatedTransactionDiscountJob extends Job {

	private static final String JOB_INSTANCE_RATED_TRANSACTION_DISCOUNT_JOB = "JobInstance_RatedTransactionDiscountJob";

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
				(Long) getParamOrCFValue(jobInstance, RatedTransactionDiscountJob.CF_MASS_UPDATE_CHUNK, 100000L));
		jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_NAMED_QUERY,
				("RatedTransaction.massUpdateWithDiscountedRTStep" + (EntityManagerProvider.isDBOracle() ? "Oracle" : "")));
		jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_READ_INTERVAL_QUERY,
				("select min(id), max(id) from RatedTransaction where status ='OPEN' and discountedRatedTransaction is null"));
		jobExecutionResult.addJobParam(UpdateStepExecutor.PARAM_NATIVE_QUERY, (false));
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return MeveoJobCategoryEnum.INVOICING;
	}

	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
		Map<String, CustomFieldTemplate> result = new HashMap<>();

		result.put(CF_NB_RUNS,
				CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"), CustomFieldTypeEnum.LONG,
						"tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", JOB_INSTANCE_RATED_TRANSACTION_DISCOUNT_JOB));
		result.put(Job.CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
				"tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", JOB_INSTANCE_RATED_TRANSACTION_DISCOUNT_JOB));
		result.put(CF_MASS_UPDATE_CHUNK, CustomFieldTemplateUtils.buildCF(CF_MASS_UPDATE_CHUNK, resourceMessages.getString("jobExecution.massUpdate.Size"), CustomFieldTypeEnum.LONG,
				"tab:Configuration:0;fieldGroup:Configuration:0;field:3", "100000", JOB_INSTANCE_RATED_TRANSACTION_DISCOUNT_JOB));

		return result;
	}
}