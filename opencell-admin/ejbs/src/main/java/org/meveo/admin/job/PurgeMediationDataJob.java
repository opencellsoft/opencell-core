package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class job  to remove not open EDR, WO, RTx between two dates.
 * 
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class PurgeMediationDataJob extends Job {

    private static final String APPLIES_TO_NAME = "JobInstance_PurgeMediationDataJob";
    public static final String PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF = "PurgeMediationDataJob_edrStatusCf";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF = "exportEntityJob.edrStatusCf";
    public static final String PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF = "PurgeMediationDataJob_woStatusCf";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF = "exportEntityJob.woStatusCf";
    public static final String PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF = "PurgeMediationDataJob_rtStatusCf";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF = "exportEntityJob.rtStatusCf";
    public static final String PURGE_MEDIATION_DATA_JOB_DAYS_TO_RETAIN = "PurgeMediationDataJob_daysToRetain";
    public static final String MESSAGE_EXPORT_ENTITY_JOB_DAYS_TO_RETAIN = "exportEntityJob.daysToRetain";
    
    /** The purge data job bean. */
    @Inject
    private PurgeMediationDataJobBean purgeMediationDataJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        purgeMediationDataJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        
    	Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate edrCf = new CustomFieldTemplate();
        edrCf.setCode("PurgeMediationDataJob_edrCf");
        edrCf.setAppliesTo(APPLIES_TO_NAME);
        edrCf.setActive(true);
        edrCf.setDescription(resourceMessages.getString("exportEntityJob.edrCf"));
        edrCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        edrCf.setValueRequired(false);
        result.put("PurgeMediationDataJob_edrCf", edrCf);
        
        CustomFieldTemplate edrStatusCf = new CustomFieldTemplate();
        edrStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF);
        edrStatusCf.setAppliesTo(APPLIES_TO_NAME);
        edrStatusCf.setActive(true);
        edrStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF));
        edrStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        edrStatusCf.setValueRequired(false);
        edrStatusCf.setMaxValue(100l);
        result.put(PURGE_MEDIATION_DATA_JOB_EDR_STATUS_CF, edrStatusCf);

        CustomFieldTemplate woCf = new CustomFieldTemplate();
        woCf.setCode("PurgeMediationDataJob_woCf");
        woCf.setAppliesTo(APPLIES_TO_NAME);
        woCf.setActive(true);
        woCf.setDescription(resourceMessages.getString("exportEntityJob.woCf"));
        woCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        woCf.setValueRequired(false);
        result.put("PurgeMediationDataJob_woCf", woCf);
        
        CustomFieldTemplate woStatusCf = new CustomFieldTemplate();
        woStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF);
        woStatusCf.setAppliesTo(APPLIES_TO_NAME);
        woStatusCf.setActive(true);
        woStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF));
        woStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        woStatusCf.setValueRequired(false);
        woStatusCf.setMaxValue(100l);
        result.put(PURGE_MEDIATION_DATA_JOB_WO_STATUS_CF, woStatusCf);

        CustomFieldTemplate rtCf = new CustomFieldTemplate();
        rtCf.setCode("PurgeMediationDataJob_rtCf");
        rtCf.setAppliesTo(APPLIES_TO_NAME);
        rtCf.setActive(true);
        rtCf.setDescription(resourceMessages.getString("exportEntityJob.rtCf"));
        rtCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        rtCf.setValueRequired(false);
        result.put("PurgeMediationDataJob_rtCf", rtCf);
        
        CustomFieldTemplate rtStatusCf = new CustomFieldTemplate();
        rtStatusCf.setCode(PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF);
        rtStatusCf.setAppliesTo(APPLIES_TO_NAME);
        rtStatusCf.setActive(true);
        rtStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF));
        rtStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        rtStatusCf.setValueRequired(false);
        rtStatusCf.setMaxValue(100l);
        result.put(PURGE_MEDIATION_DATA_JOB_RT_STATUS_CF, rtStatusCf);

        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode("PurgeMediationDataJob_firstTransactionDate");
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.firstTransactionDate"));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setValueRequired(true);
        result.put("PurgeMediationDataJob_firstTransactionDate", firstTransactionDate);

        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode("PurgeMediationDataJob_lastTransactionDate");
        lastTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.lastTransactionDate"));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(false);
        result.put("PurgeMediationDataJob_lastTransactionDate", lastTransactionDate);
        
        CustomFieldTemplate daysToRetain = new CustomFieldTemplate();
        daysToRetain.setCode(PURGE_MEDIATION_DATA_JOB_DAYS_TO_RETAIN);
        daysToRetain.setAppliesTo(APPLIES_TO_NAME);
        daysToRetain.setActive(true);
        daysToRetain.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_DAYS_TO_RETAIN));
        daysToRetain.setFieldType(CustomFieldTypeEnum.LONG);
        daysToRetain.setValueRequired(false);
        daysToRetain.setDefaultValue("0");
        result.put(PURGE_MEDIATION_DATA_JOB_DAYS_TO_RETAIN, daysToRetain);

        return result;
    }
}