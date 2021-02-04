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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.MediationFileProcessing;
import org.meveo.admin.parse.csv.MEVEOCdrFlatFileReader;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.model.bi.FileStatusEnum;
import org.meveo.model.bi.FlatFile;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.bi.impl.FlatFileService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;
import org.slf4j.Logger;

/**
 * The Class MediationJobBean.
 *
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Houssine ZNIBAR
 * @lastModifiedVersion 10.0
 * 
 */
@Stateless
public class MediationJobBean {

    /** The log. */
    @Inject
    private Logger log;

    /** The job execution service. */
    @Inject
    private MediationFileProcessing mediationFileProcessing;

    /** The cdr parser. */
    @Inject
    private CDRParsingService cdrParserService;

    /** The flat file service. */
    @Inject
    private FlatFileService flatFileService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;
    
    @Inject
    protected JobExecutionService jobExecutionService;

    /** The cdr file name. */
    String cdrFileName;

    /** The cdr file. */
    File cdrFile;

    /** The input dir. */
    String inputDir;

    /** The output dir. */
    String outputDir;

    /** The output file writer. */
    PrintWriter outputFileWriter;

    /** The reject dir. */
    String rejectDir;

    /** The reject file writer. */
    PrintWriter rejectFileWriter;

    /** The report. */
    String report;

    /**
     * Process a single file.
     *
     * @param result Job execution result
     * @param inputDir Input directory
     * @param outputDir Directory to store a successfully processed records
     * @param archiveDir Directory to store a copy of a processed file
     * @param rejectDir Directory to store a failed records
     * @param file File to process
     * @param parameter the parameter
     * @param nbRuns Number of parallel executions
     * @param waitingMillis the waiting millis
     * @param readerCode the reader code
     * @param parserCode the parser code
     * @param mappingConf the mapping conf
     * @param recordVariableName the record variable name
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String inputDir, String outputDir, String archiveDir, String rejectDir, File file, String parameter, Long nbRuns,
            Long waitingMillis, String readerCode, String parserCode, String mappingConf, String recordVariableName) {

        log.debug("Processing mediation file  in inputDir={}, file={}", inputDir, file.getAbsolutePath());

        String fileName = file.getName();
        List<String> errors = new ArrayList<>();

        File rejectFile = null;
        PrintWriter rejectFileWriter = null;
        PrintWriter outputFileWriter = null;

        File currentFile = null;
        ICdrReader cdrReader = null;
        ICdrParser cdrParser = null;

        try {

            rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
            rejectFileWriter = new PrintWriter(rejectFile);

            File outputFile = new File(outputDir + File.separator + fileName + ".processed");
            outputFileWriter = new PrintWriter(outputFile);

            currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());

            cdrReader = cdrParserService.getCDRReaderByCode(currentFile, readerCode);
            
            cdrParser = cdrParserService.getCDRParser(parserCode);
            
            if (MEVEOCdrFlatFileReader.class.isAssignableFrom(cdrReader.getClass())) {
                ((MEVEOCdrFlatFileReader) cdrReader).setDataFile(currentFile);
                ((MEVEOCdrFlatFileReader) cdrReader).setMappingDescriptor(mappingConf);
                ((MEVEOCdrFlatFileReader) cdrReader).setDataName(recordVariableName);
                ((MEVEOCdrFlatFileReader) cdrReader).parsing();
            }

            // Launch parallel processing of a file
            List<Future<String>> futures = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            for (long i = 0; i < nbRuns; i++) {

                futures.add(mediationFileProcessing.processFileAsync(cdrReader, cdrParser, result, fileName, rejectFileWriter, outputFileWriter, lastCurrentUser));

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

            log.info("Finished processing mediation {}", fileName);

        } catch (Exception e) {
            log.error("Failed to process mediation file {}", fileName, e);
            result.addReport(e.getMessage());
            errors.add(e.getMessage());
            if (currentFile != null) {
                FileUtils.moveFileDontOverwrite(rejectDir, currentFile, fileName);
            }

        } finally {
            FlatFile flatFile = flatFileService.getFlatFileByFileName(fileName);
            if (flatFile != null) {
                FileStatusEnum status = FileStatusEnum.VALID;
                if (errors != null && !errors.isEmpty()) {
                    status = FileStatusEnum.REJECTED;
                }
                flatFile.setStatus(status);
                flatFileService.update(flatFile);
            }

            try {
                if (cdrReader != null) {
                    cdrReader.close();
                }
            } catch (Exception e) {
                log.error("Failed to close file parser");
            }
            try {
                if (currentFile != null) {
                    FileUtils.moveFileDontOverwrite(archiveDir, currentFile, fileName);
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
            if ((result.getErrors().isEmpty() && result.getNbItemsProcessedWithError() == 0) && rejectFile != null) {
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
}