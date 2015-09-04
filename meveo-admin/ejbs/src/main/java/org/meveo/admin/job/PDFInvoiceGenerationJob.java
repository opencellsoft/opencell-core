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
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class PDFInvoiceGenerationJob extends Job {

	@Inject
	private PDFInvoiceGenerationJobBean pdfInvoiceGenerationJobBean;

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
		pdfInvoiceGenerationJobBean.execute(result, currentUser, jobInstance);
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.INVOICING;
	}
	
	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
		customFieldNbRuns.setCode("PDFInvoiceGenerationJob_nbRuns");
		customFieldNbRuns.setAccountLevel(AccountLevelEnum.TIMER);
		customFieldNbRuns.setActive(true);
		customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbRuns.setValueRequired(false);
		customFieldNbRuns.setDefaultValue("1");
		result.put("PDFInvoiceGenerationJob_nbRuns", customFieldNbRuns);

		CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
		customFieldNbWaiting.setCode("PDFInvoiceGenerationJob_waitingMillis");
		customFieldNbWaiting.setAccountLevel(AccountLevelEnum.TIMER);
		customFieldNbWaiting.setActive(true);
		customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbWaiting.setValueRequired(false);
		customFieldNbWaiting.setDefaultValue("0");
		result.put("PDFInvoiceGenerationJob_waitingMillis", customFieldNbWaiting);
		
		return result;
	}
}