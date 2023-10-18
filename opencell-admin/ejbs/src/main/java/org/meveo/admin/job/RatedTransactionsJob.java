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
import org.meveo.model.billing.WalletOperationAggregationSettings;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.billing.impl.WalletOperationAggregationSettingsService;
import org.meveo.service.job.Job;

/**
 * Job definition to convert Open Wallet operations to Rated transactions
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class RatedTransactionsJob extends Job {

    public static final String CF_MASS_UPDATE_CHUNK = "CF_MASS_UPDATE_CHUNK";
    
    public static final String BILLING_RULES_MAP_KEY = "BILLING_RULES_MAP_KEY";
    public static final String BILLING_ACCOUNTS_MAP_KEY = "BILLING_ACCOUNTS_MAP_KEY";

	/** The rated transactions job bean. */
    @Inject
    private RatedTransactionsJobBean ratedTransactionsJobBean;

    /** The rated transactions aggregation job bean. */
    @Inject
    private RatedTransactionsAggregatedJobBean ratedTransactionsAggregatedJobBean;
    
    @Inject
    private UpdateStepExecutor updateStepExecutor;

    @Inject
    private WalletOperationAggregationSettingsService walletOperationAggregationSettingsService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        EntityReferenceWrapper aggregationSettingsWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "woAggregationSettings", null);
        WalletOperationAggregationSettings aggregationSettings = null;
        if (aggregationSettingsWrapper != null) {
            aggregationSettings = walletOperationAggregationSettingsService.findByCode(aggregationSettingsWrapper.getCode());
        }

        if (aggregationSettings == null) {
            ratedTransactionsJobBean.execute(result, jobInstance);
            initUpdateStepParams(result, jobInstance);
            if(result.getNbItemsCorrectlyProcessed()>0) {
            	updateStepExecutor.execute(result, jobInstance);
            }
        } else {
            ratedTransactionsAggregatedJobBean.execute(result, jobInstance);
        }
        return result;
    }
    
	private void initUpdateStepParams(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        jobExecutionResult.addJobParam(updateStepExecutor.PARAM_CHUNK_SIZE, (Long) getParamOrCFValue(jobInstance, RatedTransactionsJob.CF_MASS_UPDATE_CHUNK, 100000L));
        jobExecutionResult.addJobParam(updateStepExecutor.PARAM_NAMED_QUERY, ("RatedTransaction.massUpdateWithDiscountedRT" + (EntityManagerProvider.isDBOracle() ? "Oracle" : "")));
	}

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        result.put(CF_NB_RUNS, CustomFieldTemplateUtils.buildCF(CF_NB_RUNS, resourceMessages.getString("jobExecution.nbRuns"), CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1",
            false, null, null, "JobInstance_RatedTransactionsJob"));
        result.put(Job.CF_WAITING_MILLIS, CustomFieldTemplateUtils.buildCF(Job.CF_WAITING_MILLIS, resourceMessages.getString("jobExecution.waitingMillis"), CustomFieldTypeEnum.LONG,
            "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", false, null, null, "JobInstance_RatedTransactionsJob"));
        result.put(CF_BATCH_SIZE, CustomFieldTemplateUtils.buildCF(CF_BATCH_SIZE, resourceMessages.getString("jobExecution.batchSize"), CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:2",
            "10000", true, null, null, "JobInstance_RatedTransactionsJob"));
        result.put(CF_MASS_UPDATE_CHUNK, CustomFieldTemplateUtils.buildCF(CF_MASS_UPDATE_CHUNK, resourceMessages.getString("jobExecution.massUpdate.Size"), CustomFieldTypeEnum.LONG, "tab:Configuration:0;fieldGroup:Configuration:0;field:3", "100000",
                false, null, null, "JobInstance_RatedTransactionsJob"));
        // aggregations
        result.put("woAggregationSettings",
            CustomFieldTemplateUtils.buildCF("woAggregationSettings", resourceMessages.getString("jobExecution.woAggregationSettings"), CustomFieldTypeEnum.ENTITY,
                "tab:Configuration:0;fieldGroup:Aggregation Settings:1;field:0", null, false, CustomFieldStorageTypeEnum.SINGLE, "org.meveo.model.billing.WalletOperationAggregationSettings",
                "JobInstance_RatedTransactionsJob"));

        return result;
    }
}