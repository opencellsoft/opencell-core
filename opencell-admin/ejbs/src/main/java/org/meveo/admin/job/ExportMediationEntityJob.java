package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Maps;

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
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        exportMediationEntityJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<>();

        CustomFieldTemplate edrCf = new CustomFieldTemplate();
        edrCf.setCode("ExportMediationEntityJob_edrCf");
        edrCf.setAppliesTo(APPLIES_TO_NAME);
        edrCf.setActive(true);
        edrCf.setDescription(resourceMessages.getString("exportEntityJob.edrCf"));
        edrCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        edrCf.setValueRequired(false);
        result.put("ExportMediationEntityJob_edrCf", edrCf);

        CustomFieldTemplate edrStatusCf = new CustomFieldTemplate();
        edrStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF);
        edrStatusCf.setAppliesTo(APPLIES_TO_NAME);
        edrStatusCf.setActive(true);
        edrStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF));
        edrStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        edrStatusCf.setValueRequired(false);
        edrStatusCf.setMaxValue(100L);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF, edrStatusCf);

        CustomFieldTemplate woCf = new CustomFieldTemplate();
        woCf.setCode("ExportMediationEntityJob_woCf");
        woCf.setAppliesTo(APPLIES_TO_NAME);
        woCf.setActive(true);
        woCf.setDescription(resourceMessages.getString("exportEntityJob.woCf"));
        woCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        woCf.setValueRequired(false);
        result.put("ExportMediationEntityJob_woCf", woCf);

        CustomFieldTemplate woStatusCf = new CustomFieldTemplate();
        woStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF);
        woStatusCf.setAppliesTo(APPLIES_TO_NAME);
        woStatusCf.setActive(true);
        woStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF));
        woStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        woStatusCf.setValueRequired(false);
        woStatusCf.setMaxValue(100L);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF, woStatusCf);

        CustomFieldTemplate rtCf = new CustomFieldTemplate();
        rtCf.setCode("ExportMediationEntityJob_rtCf");
        rtCf.setAppliesTo(APPLIES_TO_NAME);
        rtCf.setActive(true);
        rtCf.setDescription(resourceMessages.getString("exportEntityJob.rtCf"));
        rtCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        rtCf.setValueRequired(false);
        result.put("ExportMediationEntityJob_rtCf", rtCf);

        CustomFieldTemplate rtStatusCf = new CustomFieldTemplate();
        rtStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF);
        rtStatusCf.setAppliesTo(APPLIES_TO_NAME);
        rtStatusCf.setActive(true);
        rtStatusCf.setMaxValue(100L);
        rtStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF));
        rtStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        rtStatusCf.setValueRequired(false);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF, rtStatusCf);

        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode("ExportMediationEntityJob_lastTransactionDate");
        lastTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.lastTransactionDate"));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(false);
        result.put("ExportMediationEntityJob_lastTransactionDate", lastTransactionDate);

        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode("ExportMediationEntityJob_firstTransactionDate");
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.firstTransactionDate"));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setValueRequired(true);
        result.put("ExportMediationEntityJob_firstTransactionDate", firstTransactionDate);

        CustomFieldTemplate maxResult = new CustomFieldTemplate();
        maxResult.setCode("ExportMediationEntityJob_maxResult");
        maxResult.setAppliesTo(APPLIES_TO_NAME);
        maxResult.setActive(true);
        maxResult.setDescription(resourceMessages.getString("exportEntityJob.maxResult"));
        maxResult.setFieldType(CustomFieldTypeEnum.LONG);
        maxResult.setValueRequired(false);
        result.put("ExportMediationEntityJob_maxResult", maxResult);

        CustomFieldTemplate daysToIgnore = new CustomFieldTemplate();
        daysToIgnore.setCode(EXPORT_MEDIATION_DATA_JOB_DAYS_TO_IGNORE);
        daysToIgnore.setAppliesTo(APPLIES_TO_NAME);
        daysToIgnore.setActive(true);
        daysToIgnore.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_DAYS_TO_IGNORE));
        daysToIgnore.setFieldType(CustomFieldTypeEnum.LONG);
        daysToIgnore.setValueRequired(false);
        daysToIgnore.setDefaultValue("0");
        result.put(EXPORT_MEDIATION_DATA_JOB_DAYS_TO_IGNORE, daysToIgnore);

        CustomFieldTemplate exportFileName = new CustomFieldTemplate();
        exportFileName.setCode(EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME);
        exportFileName.setAppliesTo(APPLIES_TO_NAME);
        exportFileName.setActive(true);
        exportFileName.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_FILE_NAME));
        exportFileName.setFieldType(CustomFieldTypeEnum.STRING);
        exportFileName.setMaxValue(100L);
        exportFileName.setValueRequired(false);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME, exportFileName);

        return result;
    }
}