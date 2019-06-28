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

        CustomFieldTemplate woCf = new CustomFieldTemplate();
        woCf.setCode("PurgeMediationDataJob_woCf");
        woCf.setAppliesTo(APPLIES_TO_NAME);
        woCf.setActive(true);
        woCf.setDescription(resourceMessages.getString("exportEntityJob.woCf"));
        woCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        woCf.setValueRequired(false);
        result.put("PurgeMediationDataJob_woCf", woCf);

        CustomFieldTemplate rtCf = new CustomFieldTemplate();
        rtCf.setCode("PurgeMediationDataJob_rtCf");
        rtCf.setAppliesTo(APPLIES_TO_NAME);
        rtCf.setActive(true);
        rtCf.setDescription(resourceMessages.getString("exportEntityJob.rtCf"));
        rtCf.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        rtCf.setValueRequired(false);
        result.put("PurgeMediationDataJob_rtCf", rtCf);

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
        lastTransactionDate.setValueRequired(true);
        result.put("PurgeMediationDataJob_lastTransactionDate", lastTransactionDate);

        return result;
    }
}