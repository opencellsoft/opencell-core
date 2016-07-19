package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.filter.Filter;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.wf.Workflow;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class WorkflowJob extends Job {

    @Inject
    private WorkflowJobBean workflowJobBean;
    
	 @Inject
	 private ResourceBundle resourceMessages;

    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobInstance jobInstance, User currentUser) {
        super.execute(jobInstance, currentUser);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance, User currentUser) throws BusinessException {
    	workflowJobBean.execute(result, currentUser,jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.UTILS;
    }
    
    @Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        
		
//        CustomFieldTemplate workflowType = new CustomFieldTemplate();
//        workflowType.setCode("wfJob_workflowType");
//        workflowType.setAppliesTo("JOB_WorkflowJob");
//        workflowType.setActive(true);
//        workflowType.setDescription("Workflow type");
//        workflowType.setFieldType(CustomFieldTypeEnum.LIST);
//        workflowType.setValueRequired(true);
//        Map<String,String> listValues = new HashMap<String,String>();
//        listValues.put("OfferValidationWF","OfferValidationWF");
//        listValues.put("InvoiceValidationWF","InvoiceValidationWF");
//        listValues.put("InvoicePaymentWF","InvoicePaymentWF");
//        listValues.put("OrderProcessingWF","OrderProcessingWF");
//        listValues.put("DunningWF","DunningWF");
//        listValues.put("UserCreationWF","UserCreationWF");
//        listValues.put("AccountCreationWF","AccountCreationWF");		
//        workflowType.setListValues(listValues);
//        result.put("wfJob_workflowType", workflowType);
		
		CustomFieldTemplate filterCF = new CustomFieldTemplate();
		filterCF.setCode("wfJob_filter");
		filterCF.setAppliesTo("JOB_WorkflowJob");
		filterCF.setActive(true);
		filterCF.setDescription("Filter");
		filterCF.setFieldType(CustomFieldTypeEnum.ENTITY);
		filterCF.setEntityClazz(Filter.class.getName());
		filterCF.setValueRequired(true);
		result.put("wfJob_filter", filterCF);
		
		CustomFieldTemplate worklowCF = new CustomFieldTemplate();
		worklowCF.setCode("wfJob_workflow");
		worklowCF.setAppliesTo("JOB_WorkflowJob");
		worklowCF.setActive(true);
		worklowCF.setDescription("Workflow");
		worklowCF.setFieldType(CustomFieldTypeEnum.ENTITY);
		worklowCF.setEntityClazz(Workflow.class.getName());
		worklowCF.setValueRequired(true);
		result.put("wfJob_workflow", worklowCF);		
		
		

		CustomFieldTemplate nbRuns = new CustomFieldTemplate();
		nbRuns.setCode("wfJob_nbRuns");
		nbRuns.setAppliesTo("JOB_WorkflowJob");
		nbRuns.setActive(true);
		nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		nbRuns.setValueRequired(false);
		nbRuns.setDefaultValue("1");
		result.put("wfJob_nbRuns", nbRuns);

		CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
		waitingMillis.setCode("wfJob_waitingMillis");
		waitingMillis.setAppliesTo("JOB_WorkflowJob");
		waitingMillis.setActive(true);
		waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
		waitingMillis.setValueRequired(false);
		waitingMillis.setDefaultValue("0");
		result.put("wfJob_waitingMillis", waitingMillis);

		return result;
	}
}