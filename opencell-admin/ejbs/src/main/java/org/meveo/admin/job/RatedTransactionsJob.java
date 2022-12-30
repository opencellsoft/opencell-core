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

    /** The rated transactions job bean. */
    @Inject
    private RatedTransactionsJobBean ratedTransactionsJobBean;

    /** The rated transactions aggregation job bean. */
    @Inject
    private RatedTransactionsAggregatedJobBean ratedTransactionsAggregatedJobBean;

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
        } else {
            ratedTransactionsAggregatedJobBean.execute(result, jobInstance);
        }
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();
        
        result.put(CF_NB_RUNS, buildCF(CF_NB_RUNS, "jobExecution.nbRuns", CustomFieldTypeEnum.LONG, 
            "tab:Configuration:0;fieldGroup:Configuration:0;field:0", "-1", false, null, null));        
        
        result.put(Job.CF_WAITING_MILLIS, buildCF(Job.CF_WAITING_MILLIS, "jobExecution.waitingMillis", CustomFieldTypeEnum.LONG, 
            "tab:Configuration:0;fieldGroup:Configuration:0;field:1", "0", false, null, null));    
        
        result.put(CF_BATCH_SIZE, buildCF(CF_BATCH_SIZE, "jobExecution.batchSize", CustomFieldTypeEnum.LONG, 
            "tab:Configuration:0;fieldGroup:Configuration:0;field:2", "10000", true, null, null));

        // aggregations
        result.put("woAggregationSettings", buildCF("woAggregationSettings", "jobExecution.woAggregationSettings", 
            CustomFieldTypeEnum.ENTITY, "tab:Configuration:0;fieldGroup:Aggregation Settings:1;field:0", null, false,
            CustomFieldStorageTypeEnum.SINGLE, "org.meveo.model.billing.WalletOperationAggregationSettings"));

        return result;
    }
    
    private CustomFieldTemplate buildCF(String code, String description, CustomFieldTypeEnum type,
            String guiPosition, String defaultValue, boolean valueRequire, CustomFieldStorageTypeEnum cFSTEnum, String entityClazz) {
        CustomFieldTemplate cft = new CustomFieldTemplate();
        cft.setCode(code);
        cft.setAppliesTo("JobInstance_RatedTransactionsJob");
        cft.setActive(true);
        cft.setDescription(resourceMessages.getString(description));
        cft.setFieldType(type);
        cft.setValueRequired(valueRequire);
        cft.setGuiPosition(guiPosition);
        if (defaultValue!= null) {
            cft.setDefaultValue(defaultValue);
        }        
        if (cFSTEnum != null) {
            cft.setStorageType(cFSTEnum);
        }
        if (entityClazz != null) {
            cft.setEntityClazz(entityClazz);
        }        
        return cft;
    }

}