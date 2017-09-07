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
import org.meveo.service.job.Job;

@Stateless
public class SepaDirectDebitJob extends Job {

    @Inject
    private SepaDirectDebitJobBean sepaDirectDebitJobBean;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        sepaDirectDebitJobBean.execute(result,jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }
    
    @Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();		
		CustomFieldTemplate formatTransfo = new CustomFieldTemplate();
		formatTransfo.setCode("fileFormat");
		formatTransfo.setAppliesTo("JOB_SepaDirectDebitJob");
		formatTransfo.setActive(true);
		formatTransfo.setDefaultValue("SEPA");
		formatTransfo.setDescription("File format");
		formatTransfo.setFieldType(CustomFieldTypeEnum.LIST);
		formatTransfo.setValueRequired(false);
		Map<String,String> listValues = new HashMap<String,String>();
		listValues.put("SEPA","SEPA");
		listValues.put("PAYNUM","PAYNUM");
		formatTransfo.setListValues(listValues);
		result.put("fileFormat", formatTransfo);

		return result;
	}
}