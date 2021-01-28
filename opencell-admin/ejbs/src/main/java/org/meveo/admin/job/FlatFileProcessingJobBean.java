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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.FlatFileProcessing;
import org.meveo.commons.parsers.FileParserBeanio;
import org.meveo.commons.parsers.FileParserFlatworm;
import org.meveo.commons.parsers.IFileParser;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ExcelToCsv;
import org.meveo.commons.utils.FileParsers;
import org.meveo.commons.utils.FileUtils;
import org.meveo.model.bi.FlatFile;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.bi.impl.FlatFileService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * The Class FlatFileProcessingJobBean.
 *
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 10.0.0
 */
@Stateless
public class FlatFileProcessingJobBean {

    /**
     * The log.
     */
    @Inject
    private Logger log;

    /**
     * The script instance service.
     */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private FlatFileProcessing flatFileProcessing;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    private FlatFileService flatFileService;

    @Inject
    protected JobExecutionService jobExecutionService;

    /**
     * Process a single file
     *
     * @param result                 Job execution result
     * @param inputDir               Input directory
     * @param outputDir              Directory to store a successfully processed records
     * @param archiveDir             Directory to store a copy of a processed file
     * @param rejectDir              Directory to store a failed records
     * @param file                   File to process
     * @param mappingConf            File record mapping configuration
     * @param scriptInstanceFlowCode Script to invoke for each record
     * @param recordVariableName     Variable name in script for record
     * @param context                Processing parameters
     * @param filenameVariableName   Filename variable name as it will appear in the script context
     * @param formatTransfo          Format to transform to
     * @param errorAction            action to do on error : continue, stop or rollback after an error
     * @param nbRuns                 Number of parallel executions
     * @param waitingMills           Number of milliseconds to wait between launching parallel processing threads
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String inputDir, String outputDir, String archiveDir, String rejectDir, File file, String mappingConf,
                        String scriptInstanceFlowCode, String recordVariableName, Map<String, Object> context, String filenameVariableName, String formatTransfo,Long nbLinesToProcess, String errorAction,
                        Long nbRuns, Long waitingMillis) {

        log.debug("Processing FlatFile in inputDir={}, file={}, scriptInstanceFlowCode={},formatTransfo={}, errorAction={}", inputDir, file.getAbsolutePath(),
                scriptInstanceFlowCode, formatTransfo, errorAction);

        String fileName = file.getName();
        ScriptInterface script = null;
        IFileParser fileParser = null;
        File currentFile = null;
        boolean isCsvFromExcel = false;
        List<String> errors = new ArrayList<>();

        File rejectFile = null;
        PrintWriter rejectFileWriter = null;
        PrintWriter outputFileWriter = null;
        String fileCurrentName = null;
        String rejectedfileName = fileName + ".rejected";
        String processedfileName = fileName + ".processed";
        try {
            if ("Xlsx_to_Csv".equals(formatTransfo)) {
                isCsvFromExcel = true;
                ExcelToCsv excelToCsv = new ExcelToCsv();
                excelToCsv.convertExcelToCSV(file.getAbsolutePath(), file.getParent(), ";");
                FileUtils.moveFileDontOverwrite(archiveDir, file, fileName);
                file = new File(inputDir + File.separator + fileName.replaceAll(".xlsx", ".csv").replaceAll(".xls", ".csv"));
            }
            currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());
            FlatFile flatFile = flatFileService.getFlatFileByFileName(fileName);
            if (flatFile != null) {
                flatFile.setFileCurrentName(currentFile.getName());
                flatFileService.update(flatFile);
            }

            script = scriptInstanceService.getScriptInstance(scriptInstanceFlowCode);
            context.put("outputDir", outputDir);
            context.put(filenameVariableName, fileName);
            script.init(context);
            FileParsers parserUsed = getParserType(mappingConf);
            if (parserUsed == FileParsers.FLATWORM) {
                fileParser = new FileParserFlatworm();

            } else if (parserUsed == FileParsers.BEANIO) {
                fileParser = new FileParserBeanio();
            } else {
                throw new Exception("Check your mapping discriptor, only flatworm or beanio are allowed");
            }

            fileParser.setDataFile(currentFile);
            fileParser.setMappingDescriptor(mappingConf);
            fileParser.setDataName(recordVariableName);
            fileParser.parsing();

            rejectFile = new File(rejectDir + File.separator + rejectedfileName);
            rejectFileWriter = new PrintWriter(rejectFile);

            File outputFile = new File(outputDir + File.separator + processedfileName);
            outputFileWriter = new PrintWriter(outputFile);

            // Launch parallel processing of a file
            List<Future<String>> futures = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            for (long i = 0; i < nbRuns; i++) {
                if (FlatFileProcessingJob.ROLLBACK.equals(errorAction)) {
                    futures.add(flatFileProcessing.processFileAsyncInOneTx(fileParser, result, script, recordVariableName, fileName, filenameVariableName, nbLinesToProcess, errorAction,
                            rejectFileWriter, outputFileWriter, lastCurrentUser));
                } else {
                    futures.add(flatFileProcessing.processFileAsync(fileParser, result, script, recordVariableName, fileName, filenameVariableName, nbLinesToProcess, errorAction, rejectFileWriter,
                            outputFileWriter, lastCurrentUser));
                }
                if (waitingMillis > 0) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }

            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    jobExecutionService.registerError(result, cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            errors.addAll(result.getErrors());

            if (result.getNbItemsProcessed() == 0) {
                String errorDescription = "\r\n file " + fileName + " is empty";
                result.addReport(errorDescription);
                errors.add(errorDescription);
            }

            log.info("Finished processing FlatFile {}", fileName);

        } catch (Exception e) {
            log.error("Failed to process FlatFile file {}", fileName, e);
            result.addReport(e.getMessage());
            errors.add(e.getMessage());
            if (currentFile != null) {
                fileCurrentName = FileUtils.moveFileDontOverwrite(rejectDir, currentFile, fileName);
            }

        } finally {
            flatFileProcessing.updateFlatFile(fileName, fileCurrentName, rejectedfileName, processedfileName, rejectDir, outputDir, errors, result.getNbItemsCorrectlyProcessed(), result.getNbItemsProcessedWithError(), result.getJobInstance().getCode());
            try {
                if (fileParser != null) {
                    fileParser.close();
                }
            } catch (Exception e) {
                log.error("Failed to close file parser");
            }
            try {
                if (script != null) {
                    script.terminate(context);
                }
            } catch (Exception e) {
                result.addReport("\r\n error in script finalization : " + e.getMessage());
            }
            try {
                if (currentFile != null) {
                    // Move current CSV file to save directory, if his origin from an Excel transformation, else CSV file was deleted.
                    if (isCsvFromExcel == false) {
                        FileUtils.moveFileDontOverwrite(archiveDir, currentFile, fileName);
                    } else {
                        currentFile.delete();
                    }
                }
            } catch (Exception e) {
                result.addReport("\r\n cannot move file to archive directory " + fileName);
            }

            try {
                if (rejectFileWriter != null) {
                    rejectFileWriter.close();
                    rejectFileWriter = null;
                }
            } catch (Exception e) {
                log.error("Failed to close rejected Record writer for file {}", fileName, e);
            }

            // Delete reject file if it is empty
            if (errors.isEmpty() && rejectFile != null) {
                try {
                    rejectFile.delete();
                } catch (Exception e) {
                    log.error("Failed to delete an empty reject file {}", rejectFile.getAbsolutePath(), e);
                }

            }

            try {
                if (outputFileWriter != null) {
                    outputFileWriter.close();
                    outputFileWriter = null;
                }
            } catch (Exception e) {
                log.error("Failed to close output file writer for file {}", fileName, e);
            }
        }
    }

    /**
     * Gets the parser type from the mapping conf.
     *
     * @param mappingConf the mapping conf
     * @return the parser type, beanIO or Flatworm.
     */
    private FileParsers getParserType(String mappingConf) {
        if (mappingConf.indexOf("<beanio") >= 0) {
            return FileParsers.BEANIO;
        }
        if (mappingConf.indexOf("<file-format>") >= 0) {
            return FileParsers.FLATWORM;
        }
        return null;
    }
}