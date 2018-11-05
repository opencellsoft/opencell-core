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


/**
 * The Class AccountOperationsGenerationJob generate the invoice account operation for all invoices that dont have it yet.
 */
@Stateless
public class AccountOperationsGenerationJob extends Job {

    /** The account operations generation job bean. */
    @Inject
    private AccountOperationsGenerationJobBean accountOperationsGenerationJobBean;
            
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        accountOperationsGenerationJobBean.execute(result, jobInstance );
    }

    
    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.PAYMENT;
    }
    
    
    @Override
   	public Map<String, CustomFieldTemplate> getCustomFields() {
           Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

   		CustomFieldTemplate nbRuns = new CustomFieldTemplate();
   		nbRuns.setCode("nbRuns");
   		nbRuns.setAppliesTo("JOB_AccountOperationsGenerationJob");
   		nbRuns.setActive(true);
   		nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
   		nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
   		nbRuns.setValueRequired(false);
   		nbRuns.setDefaultValue("1");
   		result.put("nbRuns", nbRuns);

   		CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
   		waitingMillis.setCode("waitingMillis");
   		waitingMillis.setAppliesTo("JOB_AccountOperationsGenerationJob");
   		waitingMillis.setActive(true);
   		waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
   		waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
   		waitingMillis.setValueRequired(false);
   		waitingMillis.setDefaultValue("0");
   		result.put("waitingMillis", waitingMillis);

   		return result;
   	}
}