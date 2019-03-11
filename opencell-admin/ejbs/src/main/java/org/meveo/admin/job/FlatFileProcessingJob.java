package org.meveo.admin.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

    private static final String FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR = "FlatFileProcessingJob_archiveDir";

    private static final String FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER = "FlatFileProcessingJob_fileNameFilter";

    private static final String FLAT_FILE_PROCESSING_JOB_REJECT_DIR = "FlatFileProcessingJob_rejectDir";

    private static final String JOB_FLAT_FILE_PROCESSING_JOB = "JOB_FlatFileProcessingJob";

    private static final String FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR = "FlatFileProcessingJob_outputDir";

    private static final String FLAT_FILE_PROCESSING_JOB_VARIABLES = "FlatFileProcessingJob_variables";

    private static final String FLAT_FILE_PROCESSING_JOB_ERROR_ACTION = "FlatFileProcessingJob_errorAction";

    private static final String FLAT_FILE_PROCESSING_JOB_FORMAT_TRANSFO = "FlatFileProcessingJob_formatTransfo";

    private static final String FLAT_FILE_PROCESSING_JOB_SCRIPTS_FLOW = "FlatFileProcessingJob_scriptsFlow";

    private static final String FLAT_FILE_PROCESSING_JOB_FILE_NAME_EXTENSION = "FlatFileProcessingJob_fileNameExtension";

    private static final String EMPTY_STRING = "";

    private static final String TWO_POINTS_PARENT_DIR = "\\..";

    private static final String FLAT_FILE_PROCESSING_JOB_INPUT_DIR = "FlatFileProcessingJob_inputDir";

    private static final String FLAT_FILE_PROCESSING_JOB_MAPPING_CONF = "FlatFileProcessingJob_mappingConf";

    private static final String FLAT_FILE_PROCESSING_JOB_ORIGIN_FILENAME = "FlatFileProcessingJob_originFilename";

    private static final String FLAT_FILE_PROCESSING_JOB_RECORD_VARIABLE_NAME = "FlatFileProcessingJob_recordVariableName";

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
            String outputDir = null;
            String archiveDir = null;
            String rejectDir = null;
            String fileNameFilter = null;
            String scriptInstanceFlowCode = null;
            String fileNameExtension = null;
            String recordVariableName = null;
            String originFilename = null;
            String formatTransfo = null;
            String errorAction = null;
            Map<String, Object> initContext = new HashMap<String, Object>();
            try {
                recordVariableName = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_RECORD_VARIABLE_NAME);
                originFilename = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ORIGIN_FILENAME);
                mappingConf = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_MAPPING_CONF);
                inputDir = paramBeanFactory.getChrootDir() + File.separator + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_INPUT_DIR)).replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                fileNameExtension = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FILE_NAME_EXTENSION);
                scriptInstanceFlowCode = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_SCRIPTS_FLOW);
                formatTransfo = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FORMAT_TRANSFO);
                errorAction = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ERROR_ACTION);
                if (this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_VARIABLES) != null) {
                    initContext = (Map<String, Object>) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_VARIABLES);
                }
                if(this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR) != null) {
                    outputDir = paramBeanFactory.getChrootDir() + File.separator + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR)).replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                }
                if(this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_REJECT_DIR) != null) {
                    rejectDir = paramBeanFactory.getChrootDir() + File.separator + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_REJECT_DIR)).replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                }
                if(this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR) != null) {
                    archiveDir = paramBeanFactory.getChrootDir() + File.separator + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR)).replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                }
                if(this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER) != null) {
                    fileNameFilter = ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER));
                    fileNameFilter = fileNameFilter.replaceAll(Pattern.quote("*"), "");
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
            File[] files = FileUtils.listFilesByNameFilter(inputDir, fileExtensions,fileNameFilter);
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
                flatFileProcessingJobBean.execute(result, inputDir, outputDir, archiveDir, rejectDir, file, mappingConf, scriptInstanceFlowCode, recordVariableName, initContext, originFilename, formatTransfo,errorAction);
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
        inputDirectoryCF.setCode(FLAT_FILE_PROCESSING_JOB_INPUT_DIR);
        inputDirectoryCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        inputDirectoryCF.setActive(true);
        inputDirectoryCF.setDescription(resourceMessages.getString("flatFile.inputDir"));
        inputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
        inputDirectoryCF.setDefaultValue(null);
        inputDirectoryCF.setValueRequired(true);
        inputDirectoryCF.setMaxValue(256L);
        result.put(FLAT_FILE_PROCESSING_JOB_INPUT_DIR, inputDirectoryCF);

         CustomFieldTemplate archiveDirectoryCF = new CustomFieldTemplate();
         archiveDirectoryCF.setCode(FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR);
         archiveDirectoryCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
         archiveDirectoryCF.setActive(true);
         archiveDirectoryCF.setDescription(resourceMessages.getString("flatFile.archiveDir"));
         archiveDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
         archiveDirectoryCF.setDefaultValue(null);
         archiveDirectoryCF.setValueRequired(false);
         archiveDirectoryCF.setMaxValue(256L);
         result.put(FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR, archiveDirectoryCF);
        
         CustomFieldTemplate rejectDirectoryCF = new CustomFieldTemplate();
         rejectDirectoryCF.setCode(FLAT_FILE_PROCESSING_JOB_REJECT_DIR);
         rejectDirectoryCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
         rejectDirectoryCF.setActive(true);
         rejectDirectoryCF.setDescription(resourceMessages.getString("flatFile.rejectDir"));
         rejectDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
         rejectDirectoryCF.setDefaultValue(null);
         rejectDirectoryCF.setValueRequired(false);
         rejectDirectoryCF.setMaxValue(256L);
         result.put(FLAT_FILE_PROCESSING_JOB_REJECT_DIR, rejectDirectoryCF);
        
         CustomFieldTemplate outputDirectoryCF = new CustomFieldTemplate();
         outputDirectoryCF.setCode(FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR);
         outputDirectoryCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
         outputDirectoryCF.setActive(true);
         outputDirectoryCF.setDescription(resourceMessages.getString("flatFile.outputDir"));
         outputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
         outputDirectoryCF.setDefaultValue(null);
         outputDirectoryCF.setValueRequired(false);
         outputDirectoryCF.setMaxValue(256L);
         result.put(FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR, outputDirectoryCF);
         
         CustomFieldTemplate fileNameKeyCF = new CustomFieldTemplate();
         fileNameKeyCF.setCode(FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER);
         fileNameKeyCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
         fileNameKeyCF.setActive(true);
         fileNameKeyCF.setDescription(resourceMessages.getString("flatFile.fileNameFilter"));
         fileNameKeyCF.setFieldType(CustomFieldTypeEnum.STRING);
         fileNameKeyCF.setDefaultValue(null);
         fileNameKeyCF.setValueRequired(false);
         fileNameKeyCF.setMaxValue(256L);
         result.put(FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER, fileNameKeyCF);

        CustomFieldTemplate fileNameExtensionCF = new CustomFieldTemplate();
        fileNameExtensionCF.setCode(FLAT_FILE_PROCESSING_JOB_FILE_NAME_EXTENSION);
        fileNameExtensionCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        fileNameExtensionCF.setActive(true);
        fileNameExtensionCF.setDescription(resourceMessages.getString("flatFile.fileNameExtension"));
        fileNameExtensionCF.setFieldType(CustomFieldTypeEnum.STRING);
        fileNameExtensionCF.setDefaultValue("csv");
        fileNameExtensionCF.setValueRequired(true);
        fileNameExtensionCF.setMaxValue(256L);
        result.put(FLAT_FILE_PROCESSING_JOB_FILE_NAME_EXTENSION, fileNameExtensionCF);

        CustomFieldTemplate mappingConf = new CustomFieldTemplate();
        mappingConf.setCode(FLAT_FILE_PROCESSING_JOB_MAPPING_CONF);
        mappingConf.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        mappingConf.setActive(true);
        mappingConf.setDescription(resourceMessages.getString("flatFile.mappingConf"));
        mappingConf.setFieldType(CustomFieldTypeEnum.TEXT_AREA);
        mappingConf.setDefaultValue(EMPTY_STRING);
        mappingConf.setValueRequired(true);
        result.put(FLAT_FILE_PROCESSING_JOB_MAPPING_CONF, mappingConf);

        CustomFieldTemplate scriptFlowCF = new CustomFieldTemplate();
        scriptFlowCF.setCode(FLAT_FILE_PROCESSING_JOB_SCRIPTS_FLOW);
        scriptFlowCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        scriptFlowCF.setActive(true);
        scriptFlowCF.setDescription(resourceMessages.getString("flatFile.scriptsFlow"));
        scriptFlowCF.setFieldType(CustomFieldTypeEnum.STRING);
        scriptFlowCF.setDefaultValue(null);
        scriptFlowCF.setValueRequired(true);
        scriptFlowCF.setMaxValue(256L);
        result.put(FLAT_FILE_PROCESSING_JOB_SCRIPTS_FLOW, scriptFlowCF);

        CustomFieldTemplate variablesCF = new CustomFieldTemplate();
        variablesCF.setCode(FLAT_FILE_PROCESSING_JOB_VARIABLES);
        variablesCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        variablesCF.setActive(true);
        variablesCF.setDescription("Init and finalize variables");
        variablesCF.setFieldType(CustomFieldTypeEnum.STRING);
        variablesCF.setStorageType(CustomFieldStorageTypeEnum.MAP);
        variablesCF.setValueRequired(false);
        variablesCF.setMaxValue(256L);
        variablesCF.setMapKeyType(CustomFieldMapKeyEnum.STRING);
        result.put(FLAT_FILE_PROCESSING_JOB_VARIABLES, variablesCF);

        CustomFieldTemplate recordVariableName = new CustomFieldTemplate();
        recordVariableName.setCode(FLAT_FILE_PROCESSING_JOB_RECORD_VARIABLE_NAME);
        recordVariableName.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        recordVariableName.setActive(true);
        recordVariableName.setDefaultValue("record");
        recordVariableName.setDescription("Record variable name");
        recordVariableName.setFieldType(CustomFieldTypeEnum.STRING);
        recordVariableName.setValueRequired(true);
        recordVariableName.setMaxValue(50L);
        result.put(FLAT_FILE_PROCESSING_JOB_RECORD_VARIABLE_NAME, recordVariableName);

        CustomFieldTemplate originFilename = new CustomFieldTemplate();
        originFilename.setCode(FLAT_FILE_PROCESSING_JOB_ORIGIN_FILENAME);
        originFilename.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        originFilename.setActive(true);
        originFilename.setDefaultValue("origin_filename");
        originFilename.setDescription("Filename variable name");
        originFilename.setFieldType(CustomFieldTypeEnum.STRING);
        originFilename.setValueRequired(false);
        originFilename.setMaxValue(256L);
        result.put(FLAT_FILE_PROCESSING_JOB_ORIGIN_FILENAME, originFilename);

        CustomFieldTemplate formatTransfo = new CustomFieldTemplate();
        formatTransfo.setCode(FLAT_FILE_PROCESSING_JOB_FORMAT_TRANSFO);
        formatTransfo.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        formatTransfo.setActive(true);
        formatTransfo.setDefaultValue("None");
        formatTransfo.setDescription("Format transformation");
        formatTransfo.setFieldType(CustomFieldTypeEnum.LIST);
        formatTransfo.setValueRequired(false);
        Map<String, String> listValues = new HashMap<String, String>();
        listValues.put("None", "Aucune");
        listValues.put("Xlsx_to_Csv", "Excel cvs");
        formatTransfo.setListValues(listValues);
        result.put(FLAT_FILE_PROCESSING_JOB_FORMAT_TRANSFO, formatTransfo);
        
        CustomFieldTemplate errorAction = new CustomFieldTemplate();
        errorAction.setCode(FLAT_FILE_PROCESSING_JOB_ERROR_ACTION);
        errorAction.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
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
        result.put(FLAT_FILE_PROCESSING_JOB_ERROR_ACTION, errorAction);

        return result;
    }
}
