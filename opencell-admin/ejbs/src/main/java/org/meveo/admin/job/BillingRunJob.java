package org.meveo.admin.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.job.Job;


/**
 * The Class BillingRunJob create a BillingRun for the given BillingCycle, lastTransactionDate,invoiceDate. 
 */
@Stateless
public class BillingRunJob extends Job {

    /** The billing run job bean. */
    @Inject
    private BillingRunJobBean billingRunJobBean;

    /** The custom field instance service. */
    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    
    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        String billingCycle = (String) customFieldInstanceService.getCFValue(jobInstance, "BillingRunJob_billingCycle");
        Date lastTransactionDate = (Date) customFieldInstanceService.getCFValue(jobInstance, "BillingRunJob_lastTransactionDate");
        Date invoiceDate = (Date) customFieldInstanceService.getCFValue(jobInstance, "BillingRunJob_invoiceDate");

        billingRunJobBean.execute(result, jobInstance.getParametres(), billingCycle, invoiceDate, lastTransactionDate);
    }

   
    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.INVOICING;
    }

   
    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode("BillingRunJob_lastTransactionDate");
        lastTransactionDate.setAppliesTo("JOB_BillingRunJob");
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription("last transaction date");
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(false);
        result.put("BillingRunJob_lastTransactionDate", lastTransactionDate);

        CustomFieldTemplate invoiceDate = new CustomFieldTemplate();
        invoiceDate.setCode("BillingRunJob_invoiceDate");
        invoiceDate.setAppliesTo("JOB_BillingRunJob");
        invoiceDate.setActive(true);
        invoiceDate.setDescription("invoice date");
        invoiceDate.setFieldType(CustomFieldTypeEnum.DATE);
        invoiceDate.setValueRequired(false);
        result.put("BillingRunJob_invoiceDate", invoiceDate);

        CustomFieldTemplate billingCycle = new CustomFieldTemplate();
        billingCycle.setCode("BillingRunJob_billingCycle");
        billingCycle.setAppliesTo("JOB_BillingRunJob");
        billingCycle.setActive(true);
        billingCycle.setDescription("billing cycle");
        billingCycle.setFieldType(CustomFieldTypeEnum.STRING);
        billingCycle.setValueRequired(true);
        billingCycle.setMaxValue(50L);
        result.put("BillingRunJob_billingCycle", billingCycle);

        return result;
    }
}
