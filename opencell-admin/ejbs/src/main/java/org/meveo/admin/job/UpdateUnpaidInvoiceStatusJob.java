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

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import java.util.HashMap;
import java.util.Map;

/**
 * A Job to update unpaid invoice status
 * @author akadid abdelmounaim
 * @lastModifiedVersion 8.0
 */
@Stateless
public class UpdateUnpaidInvoiceStatusJob extends Job {

    @Inject
    UpdateUnpaidInvoiceStatusJobBean unpaidInvoiceStatusJobBean;
    /**
     * The actual job execution logic implementation.
     *
     * @param result      Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    @Override
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
    	unpaidInvoiceStatusJobBean.execute(result, jobInstance);
        return result;
    }

    /**
     * @return job category enum
     */
    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();
        String Appliesto =  "JobInstance_UpdateUnpaidInvoiceStatusJob";
        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(Job.CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo(Appliesto);
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;fieldGroup:Configuration:0;field:0");
        result.put(Job.CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo(Appliesto);
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;fieldGroup:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        return result;
    }
}
