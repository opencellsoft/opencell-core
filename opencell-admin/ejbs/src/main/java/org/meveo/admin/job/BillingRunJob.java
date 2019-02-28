package org.meveo.admin.job;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;


/**
 * The Class BillingRunJob create a BillingRun for the given BillingCycles, lastTransactionDate,invoiceDate.
 */
@Stateless
public class BillingRunJob extends Job {

    /** The billing run job bean. */
    @Inject
    private BillingRunJobBean billingRunJobBean;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        billingRunJobBean.execute(result,  jobInstance);
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
        lastTransactionDate.setDescription(resourceMessages.getString("jobExecution.lastTransationDate"));
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(false);
        result.put("BillingRunJob_lastTransactionDate", lastTransactionDate);

        CustomFieldTemplate invoiceDate = new CustomFieldTemplate();
        invoiceDate.setCode("BillingRunJob_invoiceDate");
        invoiceDate.setAppliesTo("JOB_BillingRunJob");
        invoiceDate.setActive(true);
        invoiceDate.setDescription(resourceMessages.getString("jobExecution.InvoiceDate"));
        invoiceDate.setFieldType(CustomFieldTypeEnum.DATE);
        invoiceDate.setValueRequired(false);
        result.put("BillingRunJob_invoiceDate", invoiceDate);

        CustomFieldTemplate billingCycle = new CustomFieldTemplate();
        billingCycle.setCode("BillingRunJob_billingCycle");
        billingCycle.setAppliesTo("JOB_BillingRunJob");
        billingCycle.setActive(true);
        billingCycle.setDescription(resourceMessages.getString("jobExecution.billingCycles"));
        billingCycle.setFieldType(CustomFieldTypeEnum.ENTITY);
        billingCycle.setStorageType(CustomFieldStorageTypeEnum.LIST);
        billingCycle.setEntityClazz("org.meveo.model.billing.BillingCycle");
        billingCycle.setValueRequired(true);
        result.put("BillingRunJob_billingCycle", billingCycle);

        CustomFieldTemplate billingCycleType = new CustomFieldTemplate();
        billingCycleType.setCode("BillingRunJob_billingRun_Process");
        billingCycleType.setAppliesTo("JOB_BillingRunJob");
        billingCycleType.setActive(true);
        billingCycleType.setDescription(resourceMessages.getString("jobExecution.billingRunProcess"));
        billingCycleType.setFieldType(CustomFieldTypeEnum.LIST);
        billingCycleType.setStorageType(CustomFieldStorageTypeEnum.SINGLE);
        Map<String, String> listValues = new HashMap();
        for(BillingProcessTypesEnum type : BillingProcessTypesEnum.values()){
            listValues.put(""+type.getId(), resourceMessages.getString(type.getLabel()));
        }
        billingCycleType.setListValues(listValues);
        billingCycleType.setValueRequired(false);
        result.put("BillingRunJob_billingRun_Process", billingCycleType);

        return result;
    }
}
