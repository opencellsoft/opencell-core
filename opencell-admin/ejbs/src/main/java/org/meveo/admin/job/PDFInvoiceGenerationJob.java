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
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
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

		CustomFieldTemplate customFieldNbRuns = new CustomFieldTemplate();
		customFieldNbRuns.setCode("nbRuns");
		customFieldNbRuns.setAppliesTo("JobInstance_PDFInvoiceGenerationJob");
		customFieldNbRuns.setActive(true);
		customFieldNbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		customFieldNbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbRuns.setValueRequired(false);
		customFieldNbRuns.setDefaultValue("1");
		result.put("nbRuns", customFieldNbRuns);

		CustomFieldTemplate customFieldNbWaiting = new CustomFieldTemplate();
		customFieldNbWaiting.setCode("waitingMillis");
		customFieldNbWaiting.setAppliesTo("JobInstance_PDFInvoiceGenerationJob");
		customFieldNbWaiting.setActive(true);
		customFieldNbWaiting.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		customFieldNbWaiting.setFieldType(CustomFieldTypeEnum.LONG);
		customFieldNbWaiting.setValueRequired(false);
		customFieldNbWaiting.setDefaultValue("0");
		result.put("waitingMillis", customFieldNbWaiting);
		
		return result;
	}
}