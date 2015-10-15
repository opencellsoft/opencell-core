package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
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
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;

@Startup
@Singleton
public class FlatFileProcessingJob extends Job {

	@Inject
	private FlatFileProcessingJobBean flatFileProcessingJobBean;

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
		try {
			String mappingConf = null;
			String inputDir = null;
			String scriptInstanceFlowCode = null;
			String fileNameExtension = null;
			String recordVariableName = null;
			String originFilename = null;
			String formatTransfo= null;
			Map<String, Object> initContext = new HashMap<String, Object>();
			try {
				recordVariableName = (String) jobInstance.getCFValue("FlatFileProcessingJob_recordVariableName");
				originFilename = (String) jobInstance.getCFValue("FlatFileProcessingJob_originFilename");			
				CustomFieldInstance variablesCFI = jobInstance.getCustomFields().get("FlatFileProcessingJob_variables");
				if (variablesCFI != null) {
					initContext = variablesCFI.getMapValue();
				}
				mappingConf = (String) jobInstance.getCFValue("FlatFileProcessingJob_mappingConf");
				inputDir = ParamBean.getInstance().getProperty("providers.rootDir", "/tmp/meveo/") + File.separator + currentUser.getProvider().getCode() + ((String)jobInstance.getCFValue("FlatFileProcessingJob_inputDir")).replaceAll("\\..", "");
				fileNameExtension = (String) jobInstance.getCFValue("FlatFileProcessingJob_fileNameExtension");
				scriptInstanceFlowCode = (String) jobInstance.getCFValue("FlatFileProcessingJob_scriptsFlow");
				formatTransfo = (String) jobInstance.getCFValue("FlatFileProcessingJob_formatTransfo");

			} catch (Exception e) {
				log.warn("Cant get customFields for " + jobInstance.getJobTemplate(),e);
			}

			ArrayList<String> fileExtensions = new ArrayList<String>();
			fileExtensions.add(fileNameExtension);

			File f = new File(inputDir);
			if (!f.exists()) {
				log.debug("inputDir {} not exist",inputDir);
				f.mkdirs();
				log.debug("inputDir {} creation ok",inputDir);
			}
			File[] files = FileUtils.getFilesForParsing(inputDir, fileExtensions);
			if (files == null || files.length == 0) {
				log.debug("there no file in {} with extension {}",inputDir,fileExtensions);
				return;
			}
	        for (File file : files) {
	        	flatFileProcessingJobBean.execute(result, inputDir, currentUser, file, mappingConf,scriptInstanceFlowCode,recordVariableName,initContext,originFilename,formatTransfo);
	        }

		} catch (Exception e) {
			log.error("Failed to run mediation", e);
			result.registerError(e.getMessage());
		}
	}

	@Override
	public JobCategoryEnum getJobCategory() {
		return JobCategoryEnum.MEDIATION;
	}

	@Override
	public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

		CustomFieldTemplate inputDirectoryCF = new CustomFieldTemplate();
		inputDirectoryCF.setCode("FlatFileProcessingJob_inputDir");
		inputDirectoryCF.setAccountLevel(AccountLevelEnum.TIMER);
		inputDirectoryCF.setActive(true);
		inputDirectoryCF.setDescription(resourceMessages.getString("flatFile.inputDir"));
		inputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
		inputDirectoryCF.setDefaultValue(null);
		inputDirectoryCF.setValueRequired(true);
		result.put("FlatFileProcessingJob_inputDir", inputDirectoryCF);

		CustomFieldTemplate fileNameExtensionCF = new CustomFieldTemplate();
		fileNameExtensionCF.setCode("FlatFileProcessingJob_fileNameExtension");
		fileNameExtensionCF.setAccountLevel(AccountLevelEnum.TIMER);
		fileNameExtensionCF.setActive(true);
		fileNameExtensionCF.setDescription(resourceMessages.getString("flatFile.fileNameExtension"));
		fileNameExtensionCF.setFieldType(CustomFieldTypeEnum.STRING);
		fileNameExtensionCF.setDefaultValue("csv");
		fileNameExtensionCF.setValueRequired(true);
		result.put("FlatFileProcessingJob_fileNameExtension", fileNameExtensionCF);

		CustomFieldTemplate mappingConf = new CustomFieldTemplate();
		mappingConf.setCode("FlatFileProcessingJob_mappingConf");
		mappingConf.setAccountLevel(AccountLevelEnum.TIMER);
		mappingConf.setActive(true);
		mappingConf.setDescription(resourceMessages.getString("flatFile.mappingConf"));
		mappingConf.setFieldType(CustomFieldTypeEnum.TEXT_AREA);
		mappingConf.setDefaultValue("");
		mappingConf.setValueRequired(true);
		result.put("FlatFileProcessingJob_mappingConf", mappingConf);

		CustomFieldTemplate ss = new CustomFieldTemplate();
		ss.setCode("FlatFileProcessingJob_scriptsFlow");
		ss.setAccountLevel(AccountLevelEnum.TIMER);
		ss.setActive(true);
		ss.setDescription(resourceMessages.getString("flatFile.scriptsFlow"));
		ss.setFieldType(CustomFieldTypeEnum.STRING);
		ss.setDefaultValue(null);
		ss.setValueRequired(true);
		result.put("FlatFileProcessingJob_scriptsFlow", ss);

		CustomFieldTemplate variablesCF = new CustomFieldTemplate();
		variablesCF.setCode("FlatFileProcessingJob_variables");
		variablesCF.setAccountLevel(AccountLevelEnum.TIMER);
		variablesCF.setActive(true);
		variablesCF.setDescription("Init and finalize variables");
		variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
		variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
		variablesCF.setValueRequired(false);
		result.put("FlatFileProcessingJob_variables", variablesCF);

		CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
		recordVariableName.setCode("FlatFileProcessingJob_recordVariableName");
		recordVariableName.setAccountLevel(AccountLevelEnum.TIMER);
		recordVariableName.setActive(true);
		recordVariableName.setDefaultValue("record");
		recordVariableName.setDescription("Record variable name");
		recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
		recordVariableName.setValueRequired(true);
		result.put("FlatFileProcessingJob_recordVariableName", recordVariableName);

		CustomFieldTemplate originFilename = new CustomFieldTemplate();
		originFilename.setCode("FlatFileProcessingJob_originFilename");
		originFilename.setAccountLevel(AccountLevelEnum.TIMER);
		originFilename.setActive(true);
		originFilename.setDefaultValue("origin_filename");
		originFilename.setDescription("Filename variable name");
		originFilename.setFieldType(CustomFieldTypeEnum.STRING);
		originFilename.setValueRequired(false);
		result.put("FlatFileProcessingJob_originFilename", originFilename);

		CustomFieldTemplate formatTransfo = new CustomFieldTemplate();
		formatTransfo.setCode("FlatFileProcessingJob_formatTransfo");
		formatTransfo.setAccountLevel(AccountLevelEnum.TIMER);
		formatTransfo.setActive(true);
		formatTransfo.setDefaultValue("None");
		formatTransfo.setDescription("Format transformation");
		formatTransfo.setFieldType(CustomFieldTypeEnum.LIST);
		formatTransfo.setValueRequired(false);
		Map<String,String> listValues = new HashMap<String,String>();
		listValues.put("None","Aucune");
		listValues.put("Xlsx_to_Csv","Excel cvs");
		formatTransfo.setListValues(listValues);
		result.put("FlatFileProcessingJob_formatTransfo", formatTransfo);
		
		return result;
	}
}
