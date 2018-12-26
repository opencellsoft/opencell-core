package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;


/**
 * The Class FlatFileProcessingJob consume any flat file and execute the given script for each line/record, the beanIO is used to describe file format.
 * @author anasseh
 *
 * @lastModifiedVersion willBeSetLater
 */
@Stateless
public class FlatFileProcessingJob extends Job {

    /** The flat file processing job bean. */
    @Inject
    private FlatFileProcessingJobBean flatFileProcessingJobBean;

    /** The param bean factory. */
    @Inject
    private ParamBeanFactory paramBeanFactory;
    
    /** The Constant CONTINUE. */
    public static final String CONTINUE = "CONTINUE";
    
    /** The Constant STOP. */
    public static final String STOP = "STOP";
    
    /** The Constant ROLLBBACK. */
    public static final String ROLLBBACK = "ROLLBBACK";

    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        try {
            String mappingConf = null;
            String inputDir = null;
            String scriptInstanceFlowCode = null;
            String fileNameExtension = null;
            String recordVariableName = null;
            String originFilename = null;
            String formatTransfo = null;
            String errorAction = null;
            Map<String, Object> initContext = new HashMap<String, Object>();
            try {
                recordVariableName = (String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_recordVariableName");
                originFilename = (String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_originFilename");
                mappingConf = (String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_mappingConf");
                inputDir = paramBeanFactory.getChrootDir() + ((String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_inputDir")).replaceAll("\\..", "");
                fileNameExtension = (String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_fileNameExtension");
                scriptInstanceFlowCode = (String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_scriptsFlow");
                formatTransfo = (String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_formatTransfo");
                errorAction = (String) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_errorAction");
                if (this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_variables") != null) {
                    initContext = (Map<String, Object>) this.getParamOrCFValue(jobInstance, "FlatFileProcessingJob_variables");
                }
            } catch (Exception e) {
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
            }

            ArrayList<String> fileExtensions = new ArrayList<String>();
            fileExtensions.add(fileNameExtension);

            File f = new File(inputDir);
            if (!f.exists()) {
                log.debug("inputDir {} not exist", inputDir);
                f.mkdirs();
                log.debug("inputDir {} creation ok", inputDir);
            }
            File[] files = FileUtils.getFilesForParsing(inputDir, fileExtensions);
            if (files == null || files.length == 0) {
                String msg = String.format("there is no file in %s with extension %s", inputDir, fileExtensions);
                log.debug(msg);
                result.registerError(msg);
                return;
            }
            for (File file : files) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                flatFileProcessingJobBean.execute(result, inputDir, file, mappingConf, scriptInstanceFlowCode, recordVariableName, initContext, originFilename, formatTransfo,errorAction);
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
        inputDirectoryCF.setAppliesTo("JOB_FlatFileProcessingJob");
        inputDirectoryCF.setActive(true);
        inputDirectoryCF.setDescription(resourceMessages.getString("flatFile.inputDir"));
        inputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
        inputDirectoryCF.setDefaultValue(null);
        inputDirectoryCF.setValueRequired(true);
        inputDirectoryCF.setMaxValue(256L);
        result.put("FlatFileProcessingJob_inputDir", inputDirectoryCF);

        CustomFieldTemplate fileNameExtensionCF = new CustomFieldTemplate();
        fileNameExtensionCF.setCode("FlatFileProcessingJob_fileNameExtension");
        fileNameExtensionCF.setAppliesTo("JOB_FlatFileProcessingJob");
        fileNameExtensionCF.setActive(true);
        fileNameExtensionCF.setDescription(resourceMessages.getString("flatFile.fileNameExtension"));
        fileNameExtensionCF.setFieldType(CustomFieldTypeEnum.STRING);
        fileNameExtensionCF.setDefaultValue("csv");
        fileNameExtensionCF.setValueRequired(true);
        fileNameExtensionCF.setMaxValue(256L);
        result.put("FlatFileProcessingJob_fileNameExtension", fileNameExtensionCF);

        CustomFieldTemplate mappingConf = new CustomFieldTemplate();
        mappingConf.setCode("FlatFileProcessingJob_mappingConf");
        mappingConf.setAppliesTo("JOB_FlatFileProcessingJob");
        mappingConf.setActive(true);
        mappingConf.setDescription(resourceMessages.getString("flatFile.mappingConf"));
        mappingConf.setFieldType(CustomFieldTypeEnum.TEXT_AREA);
        mappingConf.setDefaultValue("");
        mappingConf.setValueRequired(true);
        result.put("FlatFileProcessingJob_mappingConf", mappingConf);

        CustomFieldTemplate scriptFlowCF = new CustomFieldTemplate();
        scriptFlowCF.setCode("FlatFileProcessingJob_scriptsFlow");
        scriptFlowCF.setAppliesTo("JOB_FlatFileProcessingJob");
        scriptFlowCF.setActive(true);
        scriptFlowCF.setDescription(resourceMessages.getString("flatFile.scriptsFlow"));
        scriptFlowCF.setFieldType(CustomFieldTypeEnum.STRING);
        scriptFlowCF.setDefaultValue(null);
        scriptFlowCF.setValueRequired(true);
        scriptFlowCF.setMaxValue(256L);
        result.put("FlatFileProcessingJob_scriptsFlow", scriptFlowCF);

        CustomFieldTemplate variablesCF = new CustomFieldTemplate();
        variablesCF.setCode("FlatFileProcessingJob_variables");
        variablesCF.setAppliesTo("JOB_FlatFileProcessingJob");
        variablesCF.setActive(true);
        variablesCF.setDescription("Init and finalize variables");
        variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
        variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
        variablesCF.setValueRequired(false);
        variablesCF.setMaxValue(256L);
        variablesCF.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        result.put("FlatFileProcessingJob_variables", variablesCF);

        CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
        recordVariableName.setCode("FlatFileProcessingJob_recordVariableName");
        recordVariableName.setAppliesTo("JOB_FlatFileProcessingJob");
        recordVariableName.setActive(true);
        recordVariableName.setDefaultValue("record");
        recordVariableName.setDescription("Record variable name");
        recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
        recordVariableName.setValueRequired(true);
        recordVariableName.setMaxValue(50L);
        result.put("FlatFileProcessingJob_recordVariableName", recordVariableName);

        CustomFieldTemplate originFilename = new CustomFieldTemplate();
        originFilename.setCode("FlatFileProcessingJob_originFilename");
        originFilename.setAppliesTo("JOB_FlatFileProcessingJob");
        originFilename.setActive(true);
        originFilename.setDefaultValue("origin_filename");
        originFilename.setDescription("Filename variable name");
        originFilename.setFieldType(CustomFieldTypeEnum.STRING);
        originFilename.setValueRequired(false);
        originFilename.setMaxValue(256L);
        result.put("FlatFileProcessingJob_originFilename", originFilename);

        CustomFieldTemplate formatTransfo = new CustomFieldTemplate();
        formatTransfo.setCode("FlatFileProcessingJob_formatTransfo");
        formatTransfo.setAppliesTo("JOB_FlatFileProcessingJob");
        formatTransfo.setActive(true);
        formatTransfo.setDefaultValue("None");
        formatTransfo.setDescription("Format transformation");
        formatTransfo.setFieldType(CustomFieldTypeEnum.LIST);
        formatTransfo.setValueRequired(false);
        Map<String, String> listValues = new HashMap<String, String>();
        listValues.put("None", "Aucune");
        listValues.put("Xlsx_to_Csv", "Excel cvs");
        formatTransfo.setListValues(listValues);
        result.put("FlatFileProcessingJob_formatTransfo", formatTransfo);
        
        CustomFieldTemplate errorAction = new CustomFieldTemplate();
        errorAction.setCode("FlatFileProcessingJob_errorAction");
        errorAction.setAppliesTo("JOB_FlatFileProcessingJob");
        errorAction.setActive(true);
        errorAction.setDefaultValue(FlatFileProcessingJob.CONTINUE);
        errorAction.setDescription("Error action");
        errorAction.setFieldType(CustomFieldTypeEnum.LIST);
        errorAction.setValueRequired(false);
        Map<String, String> listValuesErrorAction = new HashMap<String, String>();
        listValuesErrorAction.put(FlatFileProcessingJob.CONTINUE, "Continue");
        listValuesErrorAction.put(FlatFileProcessingJob.STOP, "Stop");
        listValuesErrorAction.put(FlatFileProcessingJob.ROLLBBACK, "Rollback");
        errorAction.setListValues(listValuesErrorAction);
        result.put("FlatFileProcessingJob_errorAction", errorAction);

        return result;
    }
}
