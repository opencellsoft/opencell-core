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
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

/**
 * Job definition to send invoice PDF by email
 * 
 * @author HORRI Khalid
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class SendInvoiceJob extends Job {

    @Inject
    SendInvoiceJobBean sendInvoiceJobBean;

    /**
     * The actual job execution logic implementation.
     *
     * @param result Job execution results
     * @param jobInstance Job instance to execute
     * @throws BusinessException Any exception
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        sendInvoiceJobBean.execute(result, jobInstance);
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
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate sendDraft = new CustomFieldTemplate();
        sendDraft.setCode("sendDraft");
        sendDraft.setAppliesTo("JobInstance_SendInvoiceJob");
        sendDraft.setActive(true);
        sendDraft.setDescription(resourceMessages.getString("jobExecution.sendDraft"));
        sendDraft.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        sendDraft.setValueRequired(false);
        sendDraft.setGuiPosition("tab:Configuration:0;field:0");
        result.put("sendDraft", sendDraft);

        CustomFieldTemplate overrideEmailEl = new CustomFieldTemplate();
        overrideEmailEl.setCode("overrideEmailEl");
        overrideEmailEl.setAppliesTo("JobInstance_SendInvoiceJob");
        overrideEmailEl.setActive(true);
        overrideEmailEl.setDescription(resourceMessages.getString("jobExecution.overrideEmailEl"));
        overrideEmailEl.setFieldType(CustomFieldTypeEnum.STRING);
        overrideEmailEl.setValueRequired(false);
        overrideEmailEl.setMaxValue(Long.MAX_VALUE);
        overrideEmailEl.setGuiPosition("tab:Configuration:0;field:1");
        result.put("overrideEmailEl", overrideEmailEl);

        return result;
    }
}
