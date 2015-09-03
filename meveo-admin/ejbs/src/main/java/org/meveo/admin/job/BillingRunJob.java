package org.meveo.admin.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class BillingRunJob extends Job {

    @Inject
    private BillingRunJobBean billingRunJobBean;

    @Override
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
        String billingCycle = (String) jobInstance.getCFValue("BillingRunJob_billingCycle");
        Date lastTransactionDate = (Date) jobInstance.getCFValue("BillingRunJob_lastTransactionDate");
        Date invoiceDate = (Date) jobInstance.getCFValue("BillingRunJob_invoiceDate");

        billingRunJobBean.execute(result, jobInstance.getParametres(), billingCycle, invoiceDate, lastTransactionDate, currentUser);
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
        lastTransactionDate.setAccountLevel(AccountLevelEnum.TIMER);
        lastTransactionDate.setActive(true);
        lastTransactionDate.setDescription("last transaction date");
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(true);
        result.put("BillingRunJob_lastTransactionDate", lastTransactionDate);

        CustomFieldTemplate invoiceDate = new CustomFieldTemplate();
        invoiceDate.setCode("BillingRunJob_invoiceDate");
        invoiceDate.setAccountLevel(AccountLevelEnum.TIMER);
        invoiceDate.setActive(true);
        invoiceDate.setDescription("invoice date");
        invoiceDate.setFieldType(CustomFieldTypeEnum.DATE);
        invoiceDate.setValueRequired(true);
        result.put("BillingRunJob_invoiceDate", invoiceDate);

        CustomFieldTemplate billingCycle = new CustomFieldTemplate();
        billingCycle.setCode("BillingRunJob_billingCycle");
        billingCycle.setAccountLevel(AccountLevelEnum.TIMER);
        billingCycle.setActive(true);
        billingCycle.setDescription("billing cycle");
        billingCycle.setFieldType(CustomFieldTypeEnum.STRING);
        billingCycle.setValueRequired(true);
        result.put("BillingRunJob_billingCycle", billingCycle);

        return result;
    }

}
