package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.job.Job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class ExportEntityJob to export EDR, WO and RTx as XML file.
 *
 * @author khalid HORRI
 * @lastModifiedVersion 7.3
 */
@Stateless
public class ExportEntityJob extends Job {

    private static final String APPLIES_TO_NAME = "JobInstance_ExportEntityJob";
    
    /** The payment job bean. */
    @Inject
    private ExportEntityJobBean exportEntityJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        exportEntityJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.IMPORT_HIERARCHY;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate edrCf = new CustomFieldTemplate();
        edrCf.setCode("ExportEntityJob_edrCf");
        edrCf.setAppliesTo(APPLIES_TO_NAME);
        edrCf.setActive(true);
        edrCf.setDescription(resourceMessages.getString("exportEntityJob.edrCf"));
        edrCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        edrCf.setValueRequired(false);
        result.put("ExportEntityJob_edrCf", edrCf);

        CustomFieldTemplate woCf = new CustomFieldTemplate();
        woCf.setCode("ExportEntityJob_woCf");
        woCf.setAppliesTo(APPLIES_TO_NAME);
        woCf.setActive(true);
        woCf.setDescription(resourceMessages.getString("exportEntityJob.woCf"));
        woCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        woCf.setValueRequired(false);
        result.put("ExportEntityJob_woCf", woCf);

        CustomFieldTemplate rtCf = new CustomFieldTemplate();
        rtCf.setCode("ExportEntityJob_rtCf");
        rtCf.setAppliesTo(APPLIES_TO_NAME);
        rtCf.setActive(true);
        rtCf.setDescription(resourceMessages.getString("exportEntityJob.rtCf"));
        rtCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        rtCf.setValueRequired(false);
        result.put("ExportEntityJob_rtCf", rtCf);

        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode("ExportEntityJob_lastTransactionDate");
        lastTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.lastTransactionDate"));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(true);
        result.put("ExportEntityJob_lastTransactionDate", lastTransactionDate);

        CustomFieldTemplate firstTransactionDate = new CustomFieldTemplate();
        firstTransactionDate.setCode("ExportEntityJob_firstTransactionDate");
        firstTransactionDate.setAppliesTo(APPLIES_TO_NAME);
        firstTransactionDate.setActive(true);
        firstTransactionDate.setDescription(resourceMessages.getString("exportEntityJob.firstTransactionDate"));
        firstTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        firstTransactionDate.setValueRequired(true);
        result.put("ExportEntityJob_firstTransactionDate", firstTransactionDate);

        return result;
    }
}