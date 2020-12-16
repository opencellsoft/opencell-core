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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperationStatusEnum;
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
 * The Class job to remove not open EDR, WO, RTx between two dates.
 * 
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class PurgeMediationDataJob extends Job {

    private static final String APPLIES_TO_NAME = "JobInstance_PurgeMediationDataJob";
    public static final String PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF = "PurgeMediationDataJob_edrStatusCf";
    public static final String MESSAGE_PURGE_JOB_EDR_STATUS_CF = "exportEntityJob.edrStatusCf";
    public static final String PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF = "PurgeMediationDataJob_woStatusCf";
    public static final String MESSAGE_PURGE_JOB_WO_STATUS_CF = "exportEntityJob.woStatusCf";
    public static final String PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF = "PurgeMediationDataJob_rtStatusCf";
    public static final String MESSAGE_PURGE_JOB_RT_STATUS_CF = "exportEntityJob.rtStatusCf";

    /**
     * The purge data job bean.
     */
    @Inject
    private PurgeMediationDataJobBean purgeMediationDataJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        purgeMediationDataJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode("PurgeMediationDataJob_firstTransactionDate");
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.firstTransactionDate"));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setDefaultValue(null);
        firstTransactionDate.setValueRequired(false);
        firstTransactionDate.setGuiPosition("tab:Custom fields:0;fieldGroup:Date configuration:0;field:0");
        result.put("PurgeMediationDataJob_firstTransactionDate", firstTransactionDate);

        CustomFieldTemplate numberOf = new CustomFieldTemplate();
        numberOf.setCode("PurgeMediationDataJob_numberOf");
        numberOf.setAppliesTo(APPLIES_TO_NAME);
        numberOf.setActive(true);
        numberOf.setDescription(resourceMessages.getString("exportEntityJob.numberOf"));
        numberOf.setFieldType(CustomFieldTypeEnum.LONG);
        numberOf.setDefaultValue("1");
        numberOf.setValueRequired(true);
        numberOf.setGuiPosition("tab:Custom fields:0;fieldGroup:Transaction date before number of period:1;field:1");
        result.put("PurgeMediationDataJob_numberOf", numberOf);

        CustomFieldTemplate period = new CustomFieldTemplate();
        period.setCode("PurgeMediationDataJob_period");
        period.setAppliesTo(APPLIES_TO_NAME);
        period.setActive(true);
        period.setDescription(resourceMessages.getString("exportEntityJob.period"));
        period.setFieldType(CustomFieldTypeEnum.LIST);
        period.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        Map<String, String> listValues = new HashMap<>();
        listValues.put(String.valueOf(Calendar.DAY_OF_MONTH), "Days");
        listValues.put(String.valueOf(Calendar.MONTH), "Months");
        listValues.put(String.valueOf(Calendar.YEAR), "Years");
        period.setListValues(listValues);
        period.setDefaultValue(String.valueOf(Calendar.MONTH));
        period.setValueRequired(true);
        period.setGuiPosition("tab:Custom fields:0;fieldGroup:Transaction date before number of period:1;field:2");
        result.put("PurgeMediationDataJob_period", period);

        CustomFieldTemplate edrStatusCf = new CustomFieldTemplate();
        edrStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF);
        edrStatusCf.setAppliesTo(APPLIES_TO_NAME);
        edrStatusCf.setActive(true);
        edrStatusCf.setDescription(resourceMessages.getString(MESSAGE_PURGE_JOB_EDR_STATUS_CF));
        edrStatusCf.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        edrStatusCf.setStorageType(CustomFieldStorageTypeEnum.LIST);
        edrStatusCf.setValueRequired(false);
        SortedMap<String, String> edrStatusList = new TreeMap<>();
        for (EDRStatusEnum e : EDRStatusEnum.values()) {
            edrStatusList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        edrStatusCf.setListValues(edrStatusList);
        edrStatusCf.setGuiPosition("tab:Custom fields:0;fieldGroup:Status:0;field:0");
        result.put(PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF, edrStatusCf);

        CustomFieldTemplate rtStatusCf = new CustomFieldTemplate();
        rtStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF);
        rtStatusCf.setAppliesTo(APPLIES_TO_NAME);
        rtStatusCf.setActive(true);
        rtStatusCf.setDescription(resourceMessages.getString(MESSAGE_PURGE_JOB_RT_STATUS_CF));
        rtStatusCf.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        rtStatusCf.setStorageType(CustomFieldStorageTypeEnum.LIST);
        rtStatusCf.setValueRequired(false);
        SortedMap<String, String> rtStatusList = new TreeMap<>();
        for (RatedTransactionStatusEnum e : RatedTransactionStatusEnum.values()) {
            rtStatusList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        rtStatusCf.setListValues(rtStatusList);
        rtStatusCf.setGuiPosition("tab:Custom fields:0;fieldGroup:Status:0;field:1");
        result.put(PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF, rtStatusCf);

        CustomFieldTemplate woStatusCf = new CustomFieldTemplate();
        woStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF);
        woStatusCf.setAppliesTo(APPLIES_TO_NAME);
        woStatusCf.setActive(true);
        woStatusCf.setDescription(resourceMessages.getString(MESSAGE_PURGE_JOB_WO_STATUS_CF));
        woStatusCf.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        woStatusCf.setStorageType(CustomFieldStorageTypeEnum.LIST);
        woStatusCf.setValueRequired(false);
        SortedMap<String, String> woStatusList = new TreeMap<>();
        for (WalletOperationStatusEnum e : WalletOperationStatusEnum.values()) {
            woStatusList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        woStatusCf.setListValues(woStatusList);
        woStatusCf.setGuiPosition("tab:Custom fields:0;fieldGroup:Status:0;field:2");
        result.put(PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF, woStatusCf);

        return result;
    }
}