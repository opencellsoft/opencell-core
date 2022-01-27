/*
 * (C) Copyright 2015-2022 Opencell SAS (https://opencellsoft.com/) and contributors.
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
package org.meveo.admin.job.v2.invoicing;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;


/**
 * The Class InvoicingJobV2 launch invoicing for the available BillingRuns. It's a new job with limited features, be sure your it contains all needed features before using.
 * 
 * @author Mohammed EL-AZZOUZI
 *
 */
@Stateless
public class InvoicingJobV2 extends Job {

    public static final long LIMIT_UPDATE_BY_ID = 10000;
	/** The invoicing job bean. */
    @Inject
    private InvoicingJobBeanV2 invoicingJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        invoicingJobBean.execute(result, jobInstance);
    }


    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }


    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        
        
        CustomFieldTemplate customFieldRecalculateTaxes = new CustomFieldTemplate();
        customFieldRecalculateTaxes.setCode("recalculateTaxes");
        customFieldRecalculateTaxes.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldRecalculateTaxes.setActive(true);
        customFieldRecalculateTaxes.setDescription(resourceMessages.getString("jobExecution.recalculateTaxes"));
        customFieldRecalculateTaxes.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        customFieldRecalculateTaxes.setDefaultValue("false");
        result.put("recalculateTaxes", customFieldRecalculateTaxes);
        
        CustomFieldTemplate customFieldExpectingMassRTsProcessing = new CustomFieldTemplate();
        customFieldExpectingMassRTsProcessing.setCode("expectMassRTsProcessing");
        customFieldExpectingMassRTsProcessing.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldExpectingMassRTsProcessing.setActive(true);
        customFieldExpectingMassRTsProcessing.setDescription(resourceMessages.getString("jobExecution.expectMassRTsProcessing"));
        customFieldExpectingMassRTsProcessing.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        customFieldExpectingMassRTsProcessing.setDefaultValue("false");
        result.put("expectMassRTsProcessing", customFieldExpectingMassRTsProcessing);

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode("nbRuns");
        customFieldNbRuns.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        result.put("nbRuns", customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode("waitingMillis");
        customFieldNbWaiting.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        result.put("waitingMillis", customFieldNbWaiting);

        CustomFieldTemplate customFieldBR = new CustomFieldTemplate();
        customFieldBR.setCode("billingRuns");
        customFieldBR.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldBR.setActive(true);
        customFieldBR.setDescription(resourceMessages.getString("jobExecution.billingRuns"));
        customFieldBR.setFieldType(CustomFieldTypeEnum.ENTITY);
        customFieldBR.setStorageType(CustomFieldStorageTypeEnum.LIST);
        customFieldBR.setEntityClazz("org.meveo.model.billing.BillingRun");
        customFieldBR.setValueRequired(false);
        result.put("billingRuns", customFieldBR);
        return result;
    }
}