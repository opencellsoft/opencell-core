package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.DDRequestBuilder;
import org.meveo.service.job.Job;


/**
 * The Class SepaDirectDebitJob generate sepa/paynum or custom files for available DirectDebit request operations.
 */
@Stateless
public class SepaDirectDebitJob extends Job {

    /** The sepa direct debit job bean. */
    @Inject
    private SepaDirectDebitJobBean sepaDirectDebitJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        sepaDirectDebitJobBean.execute(result, jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();        
        CustomFieldTemplate payentGatewayCF = new CustomFieldTemplate();
        payentGatewayCF.setCode("SepaJob_ddRequestBuilder");
        payentGatewayCF.setAppliesTo("JOB_SepaDirectDebitJob");
        payentGatewayCF.setActive(true);
        payentGatewayCF.setDescription("DDRequest builder");
        payentGatewayCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        payentGatewayCF.setEntityClazz(DDRequestBuilder.class.getName());
        payentGatewayCF.setValueRequired(true);
        result.put("SepaJob_ddRequestBuilder", payentGatewayCF);
       
        return result;
    }
}