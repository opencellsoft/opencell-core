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
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.job.Job;

/**
 * Job definition to generate invoice XML for all valid invoices that don't have it
 * 
 * @author Abdellatif BARI
 * @author Andrius Karpavicius
 */
@Stateless
public class XMLInvoiceGenerationJob extends Job {

    /** The xml invoice generation job bean. */
    @Inject
    private XMLInvoiceGenerationJobBean xmlInvoiceGenerationJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        xmlInvoiceGenerationJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }

    @Override
    public Class getTargetEntityClass(JobInstance jobInstance) {
        return Invoice.class;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        final String APPLIES_TO = "JobInstance_XMLInvoiceGenerationJob";

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo(APPLIES_TO);
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo(APPLIES_TO);
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        CustomFieldTemplate customFieldInvToProcess = new CustomFieldTemplate();
        final String cfInvToProcessCode = "invoicesToProcess";
        
        customFieldInvToProcess.setCode(cfInvToProcessCode);
        customFieldInvToProcess.setAppliesTo(APPLIES_TO);
        customFieldInvToProcess.setActive(true);
        
        customFieldInvToProcess.setDescription(resourceMessages.getString("InvoicesToProcessEnum.label"));
        customFieldInvToProcess.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        customFieldInvToProcess.setStorageType(CustomFieldStorageTypeEnum.LIST);
        
        Map<String, String> invoicesStatusToProcessValues = new HashMap<String, String>();
        for (InvoiceStatusEnum e : InvoiceStatusEnum.values()) {
        	invoicesStatusToProcessValues.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        customFieldInvToProcess.setListValues(invoicesStatusToProcessValues);
        customFieldInvToProcess.setDefaultValue(InvoiceStatusEnum.VALIDATED.name());
        
        customFieldInvToProcess.setGuiPosition("tab:Configuration:0;field:2");
        result.put(cfInvToProcessCode, customFieldInvToProcess);

        return result;
    }

}