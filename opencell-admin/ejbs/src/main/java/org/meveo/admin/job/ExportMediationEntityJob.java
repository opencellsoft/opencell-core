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
    private static final String APPLIES_TO_NAME = "JOB_ExportMediationEntityJob";

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
        edrCf.setCode("ExportMediationEntityJob_edrCf");
        edrCf.setAppliesTo(APPLIES_TO_NAME);
        edrCf.setActive(true);
        edrCf.setDescription(resourceMessages.getString("exportEntityJob.edrCf"));
        edrCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        edrCf.setValueRequired(false);
        edrCf.setDefaultValue("true");
        result.put("ExportMediationEntityJob_edrCf", edrCf);
        CustomFieldTemplate woCf = new CustomFieldTemplate();
        woCf.setCode("ExportMediationEntityJob_woCf");
        woCf.setAppliesTo(APPLIES_TO_NAME);
        woCf.setActive(true);
        woCf.setDescription(resourceMessages.getString("exportEntityJob.woCf"));
        woCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        woCf.setValueRequired(false);
        woCf.setDefaultValue("true");
        result.put("ExportMediationEntityJob_woCf", woCf);
        CustomFieldTemplate rtCf = new CustomFieldTemplate();
        rtCf.setCode("ExportMediationEntityJob_rtCf");
        rtCf.setAppliesTo(APPLIES_TO_NAME);
        rtCf.setActive(true);
        rtCf.setDescription(resourceMessages.getString("exportEntityJob.rtCf"));
        rtCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        rtCf.setValueRequired(false);
        rtCf.setDefaultValue("true");
        result.put("ExportMediationEntityJob_rtCf", rtCf);
        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode("ExportMediationEntityJob_lastTransactionDate");
        lastTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.lastTransactionDate"));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(true);
        result.put("ExportEntityJob_lastTransactionDate", lastTransactionDate);
        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode("ExportMediationEntityJob_firstTransactionDate");
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.firstTransactionDate"));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setValueRequired(true);
        result.put("ExportMediationEntityJob_firstTransactionDate", firstTransactionDate);
        return result;
    }
}