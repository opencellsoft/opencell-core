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
 * The Class ExportMediationEntityJob to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ExportMediationEntityJob extends Job {

    private static final String APPLIES_TO_NAME = "JobInstance_ExportMediationEntityJob";
    public static final String EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF = "ExportMediationEntityJob_edrStatusCf";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF = "exportEntityJob.edrStatusCf";
    public static final String EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF = "ExportMediationEntityJob_woStatusCf";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF = "exportEntityJob.woStatusCf";
    public static final String EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF = "ExportMediationEntityJob_rtStatusCf";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF = "exportEntityJob.rtStatusCf";
    public static final String EXPORT_MEDIATION_DATA_JOB_DAYS_TO_IGNORE = "ExportMediationEntityJob_daysToIgnore";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_DAYS_TO_IGNORE = "exportEntityJob.daysToIgnore";
    public static final String EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME = "ExportMediationEntityJob_fileName";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_FILE_NAME = "exportEntityJob.fileName";

    /**
     * The export job bean.
     */
    @Inject
    private ExportMediationEntityJobBean exportMediationEntityJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        exportMediationEntityJobBean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.IMPORT_HIERARCHY;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode("ExportMediationEntityJob_lastTransactionDate");
        lastTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.lastTransactionDate"));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(false);
        lastTransactionDate.setGuiPosition("tab:Configuration:0;fieldGroup:Dates configuration:0;field:1");
        result.put("ExportMediationEntityJob_lastTransactionDate", lastTransactionDate);

        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode("ExportMediationEntityJob_firstTransactionDate");
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.firstTransactionDate"));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setValueRequired(true);
        firstTransactionDate.setGuiPosition("tab:Configuration:0;fieldGroup:Dates configuration:0;field:0");
        result.put("ExportMediationEntityJob_firstTransactionDate", firstTransactionDate);

        CustomFieldTemplate maxResult = new CustomFieldTemplate();
        maxResult.setCode("ExportMediationEntityJob_maxResult");
        maxResult.setAppliesTo(APPLIES_TO_NAME);
        maxResult.setActive(true);
        maxResult.setDescription(resourceMessages.getString("exportEntityJob.maxResult"));
        maxResult.setFieldType(CustomFieldTypeEnum.LONG);
        maxResult.setValueRequired(false);
        maxResult.setGuiPosition("tab:Configuration:0;fieldGroup:Configuration:0;field:0");
        result.put("ExportMediationEntityJob_maxResult", maxResult);

        CustomFieldTemplate exportFileName = new CustomFieldTemplate();
        exportFileName.setCode(EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME);
        exportFileName.setAppliesTo(APPLIES_TO_NAME);
        exportFileName.setActive(true);
        exportFileName.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_FILE_NAME));
        exportFileName.setFieldType(CustomFieldTypeEnum.STRING);
        exportFileName.setMaxValue(100L);
        exportFileName.setValueRequired(false);
        exportFileName.setGuiPosition("tab:Configuration:0;fieldGroup:Configuration:0;field:1");
        result.put(EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME, exportFileName);

        CustomFieldTemplate edrStatusCf = new CustomFieldTemplate();
        edrStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF);
        edrStatusCf.setAppliesTo(APPLIES_TO_NAME);
        edrStatusCf.setActive(true);
        edrStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF));
        edrStatusCf.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        edrStatusCf.setStorageType(CustomFieldStorageTypeEnum.LIST);
        edrStatusCf.setValueRequired(false);
        SortedMap<String, String> edrStatusList = new TreeMap<>();
        for (EDRStatusEnum e : EDRStatusEnum.values()) {
            edrStatusList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        edrStatusCf.setListValues(edrStatusList);
        edrStatusCf.setGuiPosition("tab:Configuration:0;fieldGroup:Status:0;field:0");
        result.put(EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF, edrStatusCf);

        CustomFieldTemplate rtStatusCf = new CustomFieldTemplate();
        rtStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF);
        rtStatusCf.setAppliesTo(APPLIES_TO_NAME);
        rtStatusCf.setActive(true);
        rtStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF));
        rtStatusCf.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        rtStatusCf.setStorageType(CustomFieldStorageTypeEnum.LIST);
        rtStatusCf.setValueRequired(false);
        SortedMap<String, String> rtStatusList = new TreeMap<>();
        for (RatedTransactionStatusEnum e : RatedTransactionStatusEnum.values()) {
            rtStatusList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        rtStatusCf.setListValues(rtStatusList);
        rtStatusCf.setGuiPosition("tab:Configuration:0;fieldGroup:Status:0;field:1");
        result.put(EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF, rtStatusCf);

        CustomFieldTemplate woStatusCf = new CustomFieldTemplate();
        woStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF);
        woStatusCf.setAppliesTo(APPLIES_TO_NAME);
        woStatusCf.setActive(true);
        woStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF));
        woStatusCf.setFieldType(CustomFieldTypeEnum.CHECKBOX_LIST);
        woStatusCf.setStorageType(CustomFieldStorageTypeEnum.LIST);
        woStatusCf.setValueRequired(false);
        SortedMap<String, String> woStatusList = new TreeMap<>();
        for (WalletOperationStatusEnum e : WalletOperationStatusEnum.values()) {
            woStatusList.put(e.name(), resourceMessages.getString(e.getLabel()));
        }
        woStatusCf.setListValues(woStatusList);
        woStatusCf.setGuiPosition("tab:Configuration:0;fieldGroup:Status:0;field:2");
        result.put(EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF, woStatusCf);

        return result;
    }
}