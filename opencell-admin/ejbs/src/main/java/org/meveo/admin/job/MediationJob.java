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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.custom.CustomFieldTypeEnum;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.jobs.MeveoJobCategoryEnum;
import org.meveo.service.admin.impl.FileFormatService;
import org.meveo.service.job.Job;

/**
 * The Class MediationJob consume standard cdr files.
 * 
 * @author Wassim Drira
 * @author HORRI khalid
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Stateless
public class MediationJob extends Job {

    private static final String JOB_INSTANCE_MEDIATION_JOB = "JobInstance_MediationJob";

    private static final String MEDIATION_JOB_PARSER = "MediationJob_parser";

    private static final String MEDIATION_JOB_READER = "MediationJob_reader";

    private static final String MEDIATION_JOB_FILE_FORMAT = "MediationJob_fileFormat";
    
    private static final String EMPTY_STRING = "";

    private static final String TWO_POINTS_PARENT_DIR = "\\..";

    @Inject
    private MediationJobBean mediationJobBean;

    @Inject
    private ParamBeanFactory paramBeanFactory;
    
    @Inject
    FileFormatService fileFormatService;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    protected void execute(JobExecutionResultImpl result, JobInstance jobInstance) throws BusinessException {

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        jobExecutionService.counterInc(result, "running_threads", nbRuns);
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        Boolean oneFilePerJob = (Boolean) this.getParamOrCFValue(jobInstance, "oneFilePerJob", Boolean.FALSE);
        
//        EntityReferenceWrapper reader = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, MEDIATION_JOB_READER);
//        String readerCode = reader != null ? reader.getCode() : null;
//        
//        EntityReferenceWrapper parser = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, MEDIATION_JOB_PARSER);
//        String parserCode = parser != null ? parser.getCode() : null;
        String readerCode = (String) this.getParamOrCFValue(jobInstance, MEDIATION_JOB_READER);
        
        String parserCode = (String) this.getParamOrCFValue(jobInstance, MEDIATION_JOB_PARSER);
        ParamBean parambean = paramBeanFactory.getInstance();
        String cdrExtension = parambean.getProperty("mediation.extensions", "csv");
        ArrayList<String> cdrExtensions = new ArrayList<String>();
        cdrExtensions.add(cdrExtension);
        try {            
            String inputDir = null;
            String outputDir = null;
            String rejectDir = null;
            String archiveDir = null;
            String mappingConf = null;
            String recordName = null;
            EntityReferenceWrapper fileFormatWrapper = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, MEDIATION_JOB_FILE_FORMAT);
            FileFormat fileFormat = null;
            if (fileFormatWrapper != null && fileFormatWrapper.getCode() != null) {
                fileFormat = fileFormatService.findByCode(fileFormatWrapper.getCode());
            } 
            String meteringDir = parambean.getChrootDir(currentUser.getProviderCode()) + File.separator;
            if( fileFormat != null) {
                inputDir = meteringDir + fileFormat.getInputDirectory().replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                outputDir = meteringDir + fileFormat.getOutputDirectory().replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                rejectDir = meteringDir + fileFormat.getRejectDirectory().replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                archiveDir = meteringDir + fileFormat.getArchiveDirectory().replaceAll(TWO_POINTS_PARENT_DIR, EMPTY_STRING);
                mappingConf = fileFormat.getConfigurationTemplate();
                recordName = fileFormat.getRecordName();
            } else {                
                meteringDir = meteringDir + "imports" + File.separator + "metering" + File.separator;
                inputDir = meteringDir + "input";
                outputDir = meteringDir + "output";
                rejectDir = meteringDir + "reject";
                archiveDir = meteringDir + "archive";
            }
            File f = new File(inputDir);
            if (!f.exists()) {
                f.mkdirs();
            }                                          
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

            File[] files = FileUtils.listFiles(inputDir, cdrExtensions);
            if (files == null || files.length == 0) {
                log.debug("There is no file in {} with extension {} to by processed by Mediation {} job", inputDir, cdrExtensions, result.getJobInstance().getCode());
                return;
            }
            for (File file : files) {
                if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                    break;
                }
                
                int countLines = FileUtils.countLines(file);
                jobExecutionService.initCounterElementsRemaining(result, countLines);
                String fileName = file.getName();
                mediationJobBean.execute(result, inputDir, outputDir, archiveDir, rejectDir, file, jobInstance.getParametres(), nbRuns, waitingMillis, readerCode, parserCode, mappingConf,recordName);
                
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
            log.error("Failed to run mediation job", e);
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

        CustomFieldTemplate nbRuns = new CustomFieldTemplate();
        nbRuns.setCode(CF_NB_RUNS);
        nbRuns.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        nbRuns.setActive(true);
        nbRuns.setDescription(resourceMessages.getString("jobExecution.nbRuns"));
        nbRuns.setFieldType(CustomFieldTypeEnum.LONG);
        nbRuns.setDefaultValue("-1");
        nbRuns.setValueRequired(false);
        nbRuns.setGuiPosition("tab:Configuration:0;field:0");
        result.put(CF_NB_RUNS, nbRuns);

        CustomFieldTemplate waitingMillis = new CustomFieldTemplate();
        waitingMillis.setCode(Job.CF_WAITING_MILLIS);
        waitingMillis.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        waitingMillis.setActive(true);
        waitingMillis.setDescription(resourceMessages.getString("jobExecution.waitingMillis"));
        waitingMillis.setFieldType(CustomFieldTypeEnum.LONG);
        waitingMillis.setDefaultValue("0");
        waitingMillis.setValueRequired(false);
        waitingMillis.setGuiPosition("tab:Configuration:0;field:1");
        result.put(Job.CF_WAITING_MILLIS, waitingMillis);

        CustomFieldTemplate oneFilePerJob = new CustomFieldTemplate();
        oneFilePerJob.setCode("oneFilePerJob");
        oneFilePerJob.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        oneFilePerJob.setActive(true);
        oneFilePerJob.setDescription(resourceMessages.getString("jobExecution.oneFilePerJob"));
        oneFilePerJob.setFieldType(CustomFieldTypeEnum.BOOLEAN);
        oneFilePerJob.setDefaultValue("false");
        oneFilePerJob.setValueRequired(false);
        oneFilePerJob.setGuiPosition("tab:Configuration:0;field:2");
        result.put("oneFilePerJob", oneFilePerJob);
        
        CustomFieldTemplate fileFormatCF = new CustomFieldTemplate();
        fileFormatCF.setCode(MEDIATION_JOB_FILE_FORMAT);
        fileFormatCF.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        fileFormatCF.setActive(true);
        fileFormatCF.setDescription(resourceMessages.getString("mediationJob.fileFormat"));
        fileFormatCF.setFieldType(CustomFieldTypeEnum.ENTITY);
        fileFormatCF.setEntityClazz(FileFormat.class.getName());
        fileFormatCF.setDefaultValue(null);
        fileFormatCF.setValueRequired(false);
        fileFormatCF.setMaxValue(256L);
        fileFormatCF.setGuiPosition("tab:Configuration:0;field:3");
        result.put(MEDIATION_JOB_FILE_FORMAT, fileFormatCF);
        
        CustomFieldTemplate parserCF = new CustomFieldTemplate();
        parserCF.setCode(MEDIATION_JOB_PARSER);
        parserCF.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        parserCF.setActive(true);
        parserCF.setDescription(resourceMessages.getString("mediationJob.parser"));
        parserCF.setFieldType(CustomFieldTypeEnum.STRING);
//        parserCF.setFieldType(CustomFieldTypeEnum.ENTITY);
//        parserCF.setEntityClazz(ScriptInstance.class.getName());
        parserCF.setDefaultValue(null);
        parserCF.setValueRequired(false);
        parserCF.setMaxValue(256L);
        parserCF.setGuiPosition("tab:Configuration:0;field:4");
        result.put(MEDIATION_JOB_PARSER, parserCF);
        
        CustomFieldTemplate readerCF = new CustomFieldTemplate();
        readerCF.setCode(MEDIATION_JOB_READER);
        readerCF.setAppliesTo(JOB_INSTANCE_MEDIATION_JOB);
        readerCF.setActive(true);
        readerCF.setDescription(resourceMessages.getString("mediationJob.reader"));
        readerCF.setFieldType(CustomFieldTypeEnum.STRING);
//        readerCF.setFieldType(CustomFieldTypeEnum.ENTITY);
//        readerCF.setEntityClazz(ScriptInstance.class.getName());
        readerCF.setDefaultValue(null);
        readerCF.setValueRequired(false);
        readerCF.setMaxValue(256L);
        readerCF.setGuiPosition("tab:Configuration:0;field:5");
        result.put(MEDIATION_JOB_READER, readerCF);

        return result;
    }
}