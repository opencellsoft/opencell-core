package org.meveo.admin.job;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.job.Job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

@Stateless
public class InvoicingJobV2 extends Job {

    @Inject
    private InvoicingJobV2Bean invoicingJobV2Bean;


    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected JobExecutionResultImpl execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
    	invoicingJobV2Bean.execute(result, jobInstance);
        return result;
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.INVOICING;
    }
    

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate customFieldCommitInterval = new CustomFieldTemplate();
		customFieldCommitInterval.setCode("maxBAsPerTransaction");
		customFieldCommitInterval.setAppliesTo("JobInstance_InvoicingJobV2");
		customFieldCommitInterval.setActive(true);
		customFieldCommitInterval.setDescription("commit interval");
		customFieldCommitInterval.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldCommitInterval.setValueRequired(false);
		customFieldCommitInterval.setDefaultValue("1000");
		result.put("maxBAsPerTransaction", customFieldCommitInterval);

        CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
        customFieldNbRuns.setCode(CF_NB_RUNS);
        customFieldNbRuns.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldNbRuns.setActive(true);
        customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbRuns.setValueRequired(false);
        customFieldNbRuns.setDefaultValue("-1");
        customFieldNbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, customFieldNbRuns);

        CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
        customFieldNbWaiting.setCode(Job.CF_WAITING_MILLIS);
        customFieldNbWaiting.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldNbWaiting.setActive(true);
        customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
        customFieldNbWaiting.setDefaultValue("0");
        customFieldNbWaiting.setValueRequired(false);
        customFieldNbWaiting.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, customFieldNbWaiting);

        CustomFieldTemplate customFieldBR = new CustomFieldTemplate();
        customFieldBR.setCode("billingRuns");
        customFieldBR.setAppliesTo("JobInstance_InvoicingJobV2");
        customFieldBR.setActive(true);
        customFieldBR.setDescription(resourceMessages.getString("jobExecution.billingRuns"));
        customFieldBR.setFieldType(CustomFieldTypeEnum.ENTITY);
        customFieldBR.setStorageType(CustomFieldStorageTypeEnum.LIST);
        customFieldBR.setEntityClazz("org.meveo.model.billing.BillingRun");
        customFieldBR.setValueRequired(false);
        customFieldBR.setGuiPosition("tab:Configuration:0;field:2");
        result.put("billingRuns", customFieldBR);
        return result;
    }
}