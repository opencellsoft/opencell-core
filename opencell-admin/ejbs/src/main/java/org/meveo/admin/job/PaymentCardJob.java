package org.meveo.admin.job;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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

@Startup
@Singleton
@Lock(LockType.READ)
public class PaymentCardJob extends Job {

    @Inject
    private PaymentCardJobBean paymentCardJobBean;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        paymentCardJobBean.execute(result,jobInstance);
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return JobCategoryEnum.ACCOUNT_RECEIVABLES;
    }
    
    @Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate nbRuns = new CustomFieldTemplate();
		nbRuns.setCode("nbRuns");
		nbRuns.setAppliesTo("JOB_PaymentCardJob");
		nbRuns.setActive(true);
		nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
		nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
		nbRuns.setValueRequired(false);
		nbRuns.setDefaultValue("1");
		result.put("nbRuns", nbRuns);

		CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
		waitingMillis.setCode("waitingMillis");
		waitingMillis.setAppliesTo("JOB_PaymentCardJob");
		waitingMillis.setActive(true);
		waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
		waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
		waitingMillis.setValueRequired(false);
		waitingMillis.setDefaultValue("0");
		result.put("waitingMillis", waitingMillis);
		

		CustomFieldTemplate callingMode = new CustomFieldTemplate();
		callingMode.setCode("PaymentCardJob_callingMode");
		callingMode.setAppliesTo("JOB_PaymentCardJob");
		callingMode.setActive(true);
		callingMode.setDefaultValue("SERVICE");
		callingMode.setDescription("Payment mode");
		callingMode.setFieldType(CustomFieldTypeEnum.LIST);
		callingMode.setValueRequired(false);
		Map<String,String> listValues = new HashMap<String,String>();
		listValues.put("SERVICE","SERVICE");
		listValues.put("FILE","FILE");
		callingMode.setListValues(listValues);
		result.put("PaymentCardJob_callingMode", callingMode);

		CustomFieldTemplate outputDirectoryCF = new CustomFieldTemplate();
		outputDirectoryCF.setCode("PaymentCardJob_inputDir");
		outputDirectoryCF.setAppliesTo("JOB_PaymentCardJob");
		outputDirectoryCF.setActive(true);
		outputDirectoryCF.setDescription("Output directory");
		outputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
		outputDirectoryCF.setDefaultValue(null);
		outputDirectoryCF.setValueRequired(false);
		outputDirectoryCF.setMaxValue(256L);
		result.put("PaymentCardJob_inputDir", outputDirectoryCF);

		CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
		recordVariableName.setCode("PaymentCardJob_recordVariableName");
		recordVariableName.setAppliesTo("JOB_PaymentCardJob");
		recordVariableName.setActive(true);
		recordVariableName.setDefaultValue("record");
		recordVariableName.setDescription("Record variable name");
		recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
		recordVariableName.setValueRequired(true);
		recordVariableName.setMaxValue(50L);
		result.put("PaymentCardJob_recordVariableName", recordVariableName);
		
		Map<String,String> lisValuesYesNo = new HashMap<String,String>();
		lisValuesYesNo.put("YES","YES");
		lisValuesYesNo.put("NO","NO");
		
		CustomFieldTemplate createAO = new CustomFieldTemplate();
		createAO.setCode("PaymentCardJob_createAO");
		createAO.setAppliesTo("JOB_PaymentCardJob");
		createAO.setActive(true);
		createAO.setDefaultValue("YES");
		createAO.setDescription("Create AO");
		createAO.setFieldType(CustomFieldTypeEnum.LIST);
		createAO.setValueRequired(false);
		createAO.setListValues(lisValuesYesNo);
		result.put("PaymentCardJob_createAO", createAO);
		
		CustomFieldTemplate matchingAO = new CustomFieldTemplate();
		matchingAO.setCode("PaymentCardJob_matchingAO");
		matchingAO.setAppliesTo("JOB_PaymentCardJob");
		matchingAO.setActive(true);
		matchingAO.setDefaultValue("YES");
		matchingAO.setDescription("Matching AO");
		matchingAO.setFieldType(CustomFieldTypeEnum.LIST);
		matchingAO.setValueRequired(false);
		matchingAO.setListValues(lisValuesYesNo);
		result.put("PaymentCardJob_matchingAO", matchingAO);
		
		CustomFieldTemplate mappingConf = new CustomFieldTemplate();
		mappingConf.setCode("PaymentCardJob_mappingConf");
		mappingConf.setAppliesTo("JOB_PaymentCardJob");
		mappingConf.setActive(true);
		mappingConf.setDescription("Mapping");
		mappingConf.setFieldType(CustomFieldTypeEnum.TEXT_AREA);
		mappingConf.setDefaultValue("");
		mappingConf.setValueRequired(false);
		result.put("PaymentCardJob_mappingConf", mappingConf);

		return result;
	}
}