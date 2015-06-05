package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
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
        String billingCycle = null;
        if (jobInstance.getStringCustomValue("BillingRunJob_billingCycle") != null) {
            billingCycle = jobInstance.getStringCustomValue("BillingRunJob_billingCycle");
        }
        Date lastTransactionDate = null;
        if (jobInstance.getDateCustomValue("BillingRunJob_lastTransactionDate") != null) {
            lastTransactionDate = jobInstance.getDateCustomValue("BillingRunJob_lastTransactionDate");
        }
        Date invoiceDate = null;
        if (jobInstance.getDateCustomValue("BillingRunJob_invoiceDate") != null) {
            invoiceDate = jobInstance.getDateCustomValue("BillingRunJob_invoiceDate");
        }

        billingRunJobBean.execute(result, jobInstance.getParametres(), billingCycle, invoiceDate, lastTransactionDate, currentUser);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.INVOICING;
    }

    @Override
    public List<CustomFieldTemplate> getCustomFields(User currentUser) {
        List<CustomFieldTemplate> result = new ArrayList<CustomFieldTemplate>();
        Auditable audit = new Auditable();

        CustomFieldTemplate lastTransactionDate = new CustomFieldTemplate();
        lastTransactionDate.setCode("BillingRunJob_lastTransactionDate");
        lastTransactionDate.setAccountLevel(AccountLevelEnum.TIMER);
        lastTransactionDate.setActive(true);
        audit.setCreated(new Date());
        audit.setCreator(currentUser);
        lastTransactionDate.setAuditable(audit);
        lastTransactionDate.setProvider(currentUser.getProvider());
        lastTransactionDate.setDescription("last transaction date");
        lastTransactionDate.setFieldType(CustomFieldTypeEnum.DATE);
        lastTransactionDate.setValueRequired(true);
        result.add(lastTransactionDate);

        CustomFieldTemplate invoiceDate = new CustomFieldTemplate();
        invoiceDate.setCode("BillingRunJob_invoiceDate");
        invoiceDate.setAccountLevel(AccountLevelEnum.TIMER);
        invoiceDate.setActive(true);
        audit.setCreated(new Date());
        audit.setCreator(currentUser);
        invoiceDate.setAuditable(audit);
        invoiceDate.setProvider(currentUser.getProvider());
        invoiceDate.setDescription("invoice date");
        invoiceDate.setFieldType(CustomFieldTypeEnum.DATE);
        invoiceDate.setValueRequired(true);
        result.add(invoiceDate);

        CustomFieldTemplate billingCycle = new CustomFieldTemplate();
        billingCycle.setCode("BillingRunJob_billingCycle");
        billingCycle.setAccountLevel(AccountLevelEnum.TIMER);
        billingCycle.setActive(true);
        audit.setCreated(new Date());
        audit.setCreator(currentUser);
        billingCycle.setAuditable(audit);
        billingCycle.setProvider(currentUser.getProvider());
        billingCycle.setDescription("billing cycle");
        billingCycle.setFieldType(CustomFieldTypeEnum.STRING);
        billingCycle.setValueRequired(true);
        result.add(billingCycle);

        return result;
    }

}
