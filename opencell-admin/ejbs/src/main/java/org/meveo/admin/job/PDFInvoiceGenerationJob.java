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
 * The Class PDFInvoiceGenerationJob generate PDF for all valid invoices that dont have it.
 */
@Stateless
public class PDFInvoiceGenerationJob extends Job {

	/** The pdf invoice generation job bean. */
	@Inject
	private PDFInvoiceGenerationJobBean pdfInvoiceGenerationJobBean;


	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
		pdfInvoiceGenerationJobBean.execute(result, jobInstance);
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.INVOICING;
	}
	
	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();
        
        final String APPLIES_TO = "JOB_PDFInvoiceGenerationJob";

		CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
		customFieldNbRuns.setCode("nbRuns");
		customFieldNbRuns.setAppliesTo(APPLIES_TO);
		customFieldNbRuns.setActive(true);
		customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbRuns.setValueRequired(false);
		customFieldNbRuns.setDefaultValue("1");
		result.put("nbRuns", customFieldNbRuns);

		CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
		customFieldNbWaiting.setCode("waitingMillis");
		customFieldNbWaiting.setAppliesTo(APPLIES_TO);
		customFieldNbWaiting.setActive(true);
		customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbWaiting.setValueRequired(false);
		customFieldNbWaiting.setDefaultValue("0");
		result.put("waitingMillis", customFieldNbWaiting);
		
		CustomFieldTemplate customFieldInvToProcess = new CustomFieldTemplate();
		final String cfInvToProcessCode = "invoicesToProcess";
		
		Map<String, String> invoicesToProcessValues = new HashMap<String, String>();
		invoicesToProcessValues.put(InvoicesToProcessEnum.FinalOnly.name(), InvoicesToProcessEnum.FinalOnly.name());
		invoicesToProcessValues.put(InvoicesToProcessEnum.DraftOnly.name(), InvoicesToProcessEnum.DraftOnly.name());
		invoicesToProcessValues.put(InvoicesToProcessEnum.All.name(), InvoicesToProcessEnum.All.name());
		
		customFieldInvToProcess.setCode(cfInvToProcessCode);
		customFieldInvToProcess.setAppliesTo(APPLIES_TO);
		customFieldInvToProcess.setActive(true);
		customFieldInvToProcess.setDefaultValue(InvoicesToProcessEnum.FinalOnly.name());
		customFieldInvToProcess.setDescription(resourceMessages.getString("InvoicesToProcessEnum.label"));
		customFieldInvToProcess.setFieldType(CustomFieldTypeEnum.LIST);
		customFieldInvToProcess.setValueRequired(true);
		customFieldInvToProcess.setListValues(invoicesToProcessValues);
		result.put(cfInvToProcessCode, customFieldInvToProcess);
		
		return result;
	}
}