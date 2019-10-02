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
 * The Class ExportMediationEntityJob to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ExportMediationEntityJob extends Job {
	
	public static final String APPLIES_TO_NAME = "JOB_ExportMediationEntityJob";
	
	public static final String EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF = "ExportMediationEntityJob_woStatusCf";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF = "ExportMediationEntityJob_rtStatusCf";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME = "ExportMediationEntityJob_fileName";
	
	public static final String EXPORT_MEDIATION_DATA_JOB_DAYS_TO_IGNORE = "ExportMediationEntityJob_daysToIgnore";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF = "ExportMediationEntityJob_edrStatusCf";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_FIRST_TRANSACTION_DATE = "ExportMediationEntityJob_firstTransactionDate";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_LAST_TRANSACTION_DATE = "ExportMediationEntityJob_lastTransactionDate";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_RT_CF = "ExportMediationEntityJob_rtCf";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_WO_CF = "ExportMediationEntityJob_woCf";

	public static final String EXPORT_MEDIATION_ENTITY_JOB_EDR_CF = "ExportMediationEntityJob_edrCf";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_FIRST_TRANSACTION_DATE = "exportEntityJob.firstTransactionDate";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_LAST_TRANSACTION_DATE = "exportEntityJob.lastTransactionDate";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_RT_CF = "exportEntityJob.rtCf";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_WO_CF = "exportEntityJob.woCf";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_EDR_CF = "exportEntityJob.edrCf";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_DAYS_TO_IGNORE = "exportEntityJob.daysToIgnore";
	
	public static final String MESSAGE_EXPORT_ENTITY_JOB_FILE_NAME = "exportEntityJob.fileName";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF = "exportEntityJob.rtStatusCf";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF = "exportEntityJob.woStatusCf";

	public static final String MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF = "exportEntityJob.edrStatusCf";

    /** The payment job bean. */
    @Inject
    private ExportMediationEntityJobBean exportMediationEntityJobBean;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        exportMediationEntityJobBean.execute(result, jobInstance);
    }
    
    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.IMPORT_HIERARCHY;
    }
    
    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
    	
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        
        CustomFieldTemplate edrCf = new CustomFieldTemplate();
        edrCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_EDR_CF);
        edrCf.setAppliesTo(APPLIES_TO_NAME);
        edrCf.setActive(true);
        edrCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_EDR_CF));
        edrCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        edrCf.setValueRequired(false);
        edrCf.setDefaultValue("true");
        result.put(EXPORT_MEDIATION_ENTITY_JOB_EDR_CF, edrCf);
        
        CustomFieldTemplate edrStatusCf = new CustomFieldTemplate();
        edrStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF);
        edrStatusCf.setAppliesTo(APPLIES_TO_NAME);
        edrStatusCf.setActive(true);
        edrStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_EDR_STATUS_CF));
        edrStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        edrStatusCf.setValueRequired(false);
        edrStatusCf.setMaxValue(100l);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_EDR_STATUS_CF, edrStatusCf);
        
        CustomFieldTemplate woCf = new CustomFieldTemplate();
        woCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_WO_CF);
        woCf.setAppliesTo(APPLIES_TO_NAME);
        woCf.setActive(true);
        woCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_WO_CF));
        woCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        woCf.setValueRequired(false);
        woCf.setDefaultValue("true");
        result.put(EXPORT_MEDIATION_ENTITY_JOB_WO_CF, woCf);
        
        CustomFieldTemplate woStatusCf = new CustomFieldTemplate();
        woStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF);
        woStatusCf.setAppliesTo(APPLIES_TO_NAME);
        woStatusCf.setActive(true);
        woStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_WO_STATUS_CF));
        woStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        woStatusCf.setValueRequired(false);
        woStatusCf.setMaxValue(100l);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_WO_STATUS_CF, woStatusCf);
        
        CustomFieldTemplate rtCf = new CustomFieldTemplate();
        rtCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_RT_CF);
        rtCf.setAppliesTo(APPLIES_TO_NAME);
        rtCf.setActive(true);
        rtCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_RT_CF));
        rtCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        rtCf.setValueRequired(false);
        rtCf.setDefaultValue("true");
        result.put(EXPORT_MEDIATION_ENTITY_JOB_RT_CF, rtCf);
        
        CustomFieldTemplate rtStatusCf = new CustomFieldTemplate();
        rtStatusCf.setCode(EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF);
        rtStatusCf.setAppliesTo(APPLIES_TO_NAME);
        rtStatusCf.setActive(true);
        rtStatusCf.setMaxValue(100l);
        rtStatusCf.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_RT_STATUS_CF));
        rtStatusCf.setFieldType(CustomFieldTypeEnum.STRING);
        rtStatusCf.setValueRequired(false);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_RT_STATUS_CF, rtStatusCf);
        
        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode(EXPORT_MEDIATION_ENTITY_JOB_LAST_TRANSACTION_DATE);
        lastTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_LAST_TRANSACTION_DATE));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(true);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_LAST_TRANSACTION_DATE, lastTransactionDate);
        
        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode(EXPORT_MEDIATION_ENTITY_JOB_FIRST_TRANSACTION_DATE);
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString(MESSAGE_EXPORT_ENTITY_JOB_FIRST_TRANSACTION_DATE));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setValueRequired(true);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_FIRST_TRANSACTION_DATE, firstTransactionDate);
        
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
        exportFileName.setMaxValue(100l);
        exportFileName.setValueRequired(false);
        result.put(EXPORT_MEDIATION_ENTITY_JOB_FILE_NAME, exportFileName);
        
        return result;
        
    }
    
}