/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
import org.meveo.model.admin.FileFormat;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldMapKeyEnum;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.admin.impl.FileFormatService;
import org.meveo.service.job.Job;

/**
 * The Class FlatFileProcessingJob consume any flat file and execute the given script for each line/record, the beanIO is used to describe file format.
 * 
 * @author anasseh
 *
 * @lastModifiedVersion willBeSetLater
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.2.1
 */
@Stateless
public class FlatFileProcessingJob extends Job {

    private static final String FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR = "FlatFileProcessingJob_archiveDir";

    private static final String FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER = "FlatFileProcessingJob_fileNameFilter";

    private static final String FLAT_FILE_PROCESSING_JOB_REJECT_DIR = "FlatFileProcessingJob_rejectDir";

    private static final String JOB_FLAT_FILE_PROCESSING_JOB = "JobInstance_FlatFileProcessingJob";

    private static final String FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR = "FlatFileProcessingJob_outputDir";

    private static final String FLAT_FILE_PROCESSING_JOB_VARIABLES = "FlatFileProcessingJob_variables";

    private static final String FLAT_FILE_PROCESSING_JOB_ERROR_ACTION = "FlatFileProcessingJob_errorAction";

    private static final String FLAT_FILE_PROCESSING_JOB_FORMAT_TRANSFO = "FlatFileProcessingJob_formatTransfo";

    private static final String FLAT_FILE_PROCESSING_JOB_SCRIPTS_FLOW = "FlatFileProcessingJob_scriptsFlow";

    private static final String FLAT_FILE_PROCESSING_JOB_FILE_NAME_EXTENSION = "FlatFileProcessingJob_fileNameExtension";

    private static final String FLAT_FILE_PROCESSING_JOB_NB_LINES_TO_PROCESS = "FlatFileProcessingJob_nbLinesToProcess";

    private static final String EMPTY_STRING = "";

    private static final String TWO_POINTS_PARENT_DIR = "\\..";

    private static final String FLAT_FILE_PROCESSING_JOB_FILE_FORMAT = "FlatFileProcessingJob_fileFormat";

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

    @Inject
    private FileFormatService fileFormatService;

    /** The Constant CONTINUE. */
    public static final String CONTINUE = "CONTINUE";

    /** The Constant STOP. */
    public static final String STOP = "STOP";

    /** The Constant ROLLBACK. */
    public static final String ROLLBACK = "ROLLBACK";

    @SuppressWarnings("unchecked")
    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {
        try {
            String outputDir = null;
            String archiveDir = null;
            String rejectDir = null;
            String fileNameFilter = null;
            String scriptInstanceFlowCode = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_SCRIPTS_FLOW);
            String fileNameExtension = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FILE_NAME_EXTENSION);
            Long nbLinesToProcess = (Long) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_NB_LINES_TO_PROCESS);
            String recordVariableName = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_RECORD_VARIABLE_NAME);
            String filenameVariableName = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ORIGIN_FILENAME);
            String formatTransfo = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FORMAT_TRANSFO);
            String errorAction = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ERROR_ACTION);
            Map<String, Object> initContext = new HashMap<String, Object>();
            if (this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_VARIABLES) != null) {
                initContext = (Map<String, Object>) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_VARIABLES);
            }

            Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, CF_NB_RUNS, 1L);
            if (nbRuns == -1) {
                nbRuns = (long) Runtime.getRuntime().availableProcessors();
            }
            jobExecutionService.counterRunningThreads(result, nbRuns);
            Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

            Boolean oneFilePerJob = (Boolean) this.getParamOrCFValue(jobInstance, "oneFilePerJob", Boolean.FALSE);

            EntityReferenceWrapper fileFormatWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FILE_FORMAT);
            FileFormat fileFormat = null;
            if (fileFormatWrapper != null && fileFormatWrapper.getCode() != null) {
                fileFormat = fileFormatService.findByCode(fileFormatWrapper.getCode());
            }

            String mappingConf = (String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_MAPPING_CONF, fileFormat != null ? fileFormat.getConfigurationTemplate() : null);

            String inputDir = paramBeanFactory.getChrootDir() + File.separator
                    + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_INPUT_DIR, fileFormat != null ? fileFormat.getInputDirectory() : null)).replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);

            if (this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR, fileFormat != null ? fileFormat.getOutputDirectory() : null) != null) {
                outputDir = paramBeanFactory.getChrootDir() + File.separator
                        + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_OUTPUT_DIR, fileFormat != null ? fileFormat.getOutputDirectory() : null)).replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
            }
            if (this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_REJECT_DIR, fileFormat != null ? fileFormat.getRejectDirectory() : null) != null) {
                rejectDir = paramBeanFactory.getChrootDir() + File.separator
                        + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_REJECT_DIR, fileFormat != null ? fileFormat.getRejectDirectory() : null)).replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
            }
            if (this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR, fileFormat != null ? fileFormat.getArchiveDirectory() : null) != null) {
                archiveDir = paramBeanFactory.getChrootDir() + File.separator
                        + ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_ARCHIVE_DIR, fileFormat != null ? fileFormat.getArchiveDirectory() : null)).replaceAll(TWO_POINTS_PARENT_DIR,
                            EMPTY_STRING);
            }
            if (this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER, fileFormat != null ? fileFormat.getFileNamePattern() : null) != null) {
                fileNameFilter = ((String) this.getParamOrCFValue(jobInstance, FLAT_FILE_PROCESSING_JOB_FILE_NAME_FILTER, fileFormat != null ? fileFormat.getFileNamePattern() : null));
                fileNameFilter = fileNameFilter.replaceAll(Pattern.quote("*"), "");
            }

            ArrayList<String> fileExtensions = new ArrayList<String>();
            fileExtensions.add(fileNameExtension);

            File f = new File(inputDir);
            if (!f.exists()) {
                f.mkdirs();
            }

            String inputDirParent = f.getParent();
            outputDir = outputDir != null ? outputDir : inputDirParent + File.separator + "output";
            rejectDir = rejectDir != null ? rejectDir : inputDirParent + File.separator + "reject";
            archiveDir = archiveDir != null ? archiveDir : inputDirParent + File.separator + "archive";

            f = new File(outputDir);
            if (!f.exists()) {
                log.debug("outputDir {} not exist", outputDir);
                f.mkdirs();
                log.debug("outputDir {} creation ok", outputDir);
            }
            f = new File(rejectDir);
            if (!f.exists()) {
                log.debug("rejectDir {} not exist", rejectDir);
                f.mkdirs();
                log.debug("rejectDir {} creation ok", rejectDir);
            }
            f = new File(archiveDir);
            if (!f.exists()) {
                log.debug("archiveDir {} not exist", archiveDir);
                f.mkdirs();
                log.debug("archiveDir {} creation ok", archiveDir);
            }

            File[] files = FileUtils.listFilesByNameFilter(inputDir, fileExtensions, fileNameFilter);
            if (files == null || files.length == 0) {
                log.debug("There is no file in {} with extension {} to by processed by FlatFileProcessing {} job", inputDir, fileExtensions, result.getJobInstance().getCode());
                return;
            }
            for (File file : files) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }

                String fileName = file.getName();
                flatFileProcessingJobBean.execute(result, inputDir, outputDir, archiveDir, rejectDir, file, mappingConf, scriptInstanceFlowCode, recordVariableName, initContext, filenameVariableName, formatTransfo, nbLinesToProcess,
                    errorAction, nbRuns, waitingMillis);

                result.addReport("Processed file: " + fileName);
                if (oneFilePerJob) {
                    break;
                }
            }

            // Process one file at a time
            if (oneFilePerJob && files.length > 1) {
                result.setDone(false);
            }

        } catch (Exception e) {
            log.error("Failed to run flat file processing job", e);
            jobExecutionService.registerError(result, e.getMessage());
        }
    }

    @Override
    public JobCategoryEnum getJobCategory() {
        return MeveoJobCategoryEnum.MEDIATION;
    }

    @Override
    public Map<String, CustomFieldTemplate> getCustomFields() {
        Map<String, CustomFieldTemplate> result = new HashMap<String, CustomFieldTemplate>();

        CustomFieldTemplate fileFormatCF = new CustomFieldTemplate();
        fileFormatCF.setCode(FLAT_FILE_PROCESSING_JOB_FILE_FORMAT);
        fileFormatCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        fileFormatCF.setActive(true);
        fileFormatCF.setDescription(resourceMessages.getString("flatFile.fileFormat"));
        fileFormatCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        fileFormatCF.setEntityClazz(FileFormat.class.getName());
        fileFormatCF.setDefaultValue(null);
        fileFormatCF.setValueRequired(false);
        fileFormatCF.setMaxValue(256L);
        fileFormatCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:0");
        result.put(FLAT_FILE_PROCESSING_JOB_FILE_FORMAT, fileFormatCF);

        CustomFieldTemplate inputDirectoryCF = new CustomFieldTemplate();
        inputDirectoryCF.setCode(FLAT_FILE_PROCESSING_JOB_INPUT_DIR);
        inputDirectoryCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        inputDirectoryCF.setActive(true);
        inputDirectoryCF.setDescription(resourceMessages.getString("flatFile.inputDir"));
        inputDirectoryCF.setFieldType(CustomFieldTypeEnum.STRING);
        inputDirectoryCF.setDefaultValue(null);
        inputDirectoryCF.setValueRequired(false);
        inputDirectoryCF.setMaxValue(256L);
        inputDirectoryCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:1");
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
        archiveDirectoryCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:2");
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
        rejectDirectoryCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:3");
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
        outputDirectoryCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:4");
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
        fileNameKeyCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:5");
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
        fileNameExtensionCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:6");
        result.put(FLAT_FILE_PROCESSING_JOB_FILE_NAME_EXTENSION, fileNameExtensionCF);

        CustomFieldTemplate nbLinesToProcessCF = new CustomFieldTemplate();
        nbLinesToProcessCF.setCode(FLAT_FILE_PROCESSING_JOB_NB_LINES_TO_PROCESS);
        nbLinesToProcessCF.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        nbLinesToProcessCF.setActive(true);
        nbLinesToProcessCF.setDescription(resourceMessages.getString("flatFile.nbLinesToProcess"));
        nbLinesToProcessCF.setFieldType(CustomFieldTypeEnum.LONG);
        nbLinesToProcessCF.setDefaultValue("1");
        nbLinesToProcessCF.setValueRequired(false);
        nbLinesToProcessCF.setGuiPosition("tab:Configuration:0;fieldGroup:File configuration:1;field:7");
        result.put(FLAT_FILE_PROCESSING_JOB_NB_LINES_TO_PROCESS, nbLinesToProcessCF);

        CustomFieldTemplate mappingConf = new CustomFieldTemplate();
        mappingConf.setCode(FLAT_FILE_PROCESSING_JOB_MAPPING_CONF);
        mappingConf.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        mappingConf.setActive(true);
        mappingConf.setDescription(resourceMessages.getString("flatFile.mappingConf"));
        mappingConf.setFieldType(CustomFieldTypeEnum.TEXT_AREA);
        mappingConf.setDefaultValue(EMPTY_STRING);
        mappingConf.setValueRequired(false);
        mappingConf.setGuiPosition("tab:Configuration:0;fieldGroup:Record configuration:2;field:0");
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
        scriptFlowCF.setGuiPosition("tab:Configuration:0;fieldGroup:Data processing configuration:3;field:0");
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
        variablesCF.setGuiPosition("tab:Configuration:0;fieldGroup:Data processing configuration:3;field:1");
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
        recordVariableName.setGuiPosition("tab:Configuration:0;fieldGroup:Data processing configuration:3;field:2");
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
        originFilename.setGuiPosition("tab:Configuration:0;fieldGroup:Data processing configuration:3;field:3");
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
        formatTransfo.setGuiPosition("tab:Configuration:0;fieldGroup:Data processing configuration:3;field:4");
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
        listValuesErrorAction.put(FlatFileProcessingJob.ROLLBACK, "Rollback");
        errorAction.setListValues(listValuesErrorAction);
        errorAction.setGuiPosition("tab:Configuration:0;fieldGroup:Data processing configuration:3;field:5");
        result.put(FLAT_FILE_PROCESSING_JOB_ERROR_ACTION, errorAction);

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode(CF_NB_RUNS);
        nbRuns.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setDefaultValue("1");
        nbRuns.setValueRequired(false);
        nbRuns.setGuiPosition("tab:Configuration:0;fieldGroup:Execution configuration:0;field:0");
        result.put(CF_NB_RUNS, nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode(Job.CF_WAITING_MILLIS);
        waitingMillis.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setValueRequired(false);
        waitingMillis.setGuiPosition("tab:Configuration:0;fieldGroup:Execution configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, waitingMillis);

        CustomFieldTemplate oneFilePerJob = new CustomFieldTemplate();
        oneFilePerJob.setCode("oneFilePerJob");
        oneFilePerJob.setAppliesTo(JOB_FLAT_FILE_PROCESSING_JOB);
        oneFilePerJob.setActive(true);
        oneFilePerJob.setDescription(resourceMessages.getString("jobExecution.oneFilePerJob"));
        oneFilePerJob.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        oneFilePerJob.setDefaultValue("false");
        oneFilePerJob.setValueRequired(false);
        oneFilePerJob.setGuiPosition("tab:Configuration:0;fieldGroup:Execution configuration:0;field:2");
        result.put("oneFilePerJob", oneFilePerJob);

        return result;
    }
}
