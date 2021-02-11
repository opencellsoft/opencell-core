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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.parse.csv.MEVEOCdrFlatFileReader;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.bi.FileStatusEnum;
import org.meveo.model.bi.FlatFile;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.security.MeveoUser;
import org.meveo.service.bi.impl.FlatFileService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;

/**
 * Job implementation to process CDR files converting CDRs to EDR records
 *
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Houssine ZNIBAR
 * @lastModifiedVersion 10.0
 * 
 */
@Stateless
public class MediationJobBean extends BaseJobBean {

    private static final long serialVersionUID = -6818809357366374519L;

    /** The cdr parser. */
    @Inject
    private CDRParsingService cdrParserService;

    /** The flat file service. */
    @Inject
    private FlatFileService flatFileService;

    @Inject
    private CDRService cdrService;

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
     * @param jobExecutionResult Job execution result
     * @param inputDir Input directory
     * @param outputDir Directory to store a successfully processed records
     * @param archiveDir Directory to store a copy of a processed file
     * @param rejectDir Directory to store a failed records
     * @param file File to process
     * @param parameter the parameter
     * @param readerCode the reader code
     * @param parserCode the parser code
     * @param mappingConf the mapping conf
     * @param recordVariableName the record variable name
     */
    @SuppressWarnings("rawtypes")
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, String inputDir, String outputDir, String archiveDir, String rejectDir, File file, String parameter, String readerCode, String parserCode,
            String mappingConf, String recordVariableName) {

        log.debug("Processing mediation file  in inputDir={}, file={}", inputDir, file.getAbsolutePath());

        String fileName = file.getName();
        List<String> errors = new ArrayList<>();

        File rejectFile = null;
        PrintWriter rejectFileWriter = null;
        PrintWriter outputFileWriter = null;

        File currentFile = null;
        ICdrReader cdrReader = null;
        ICdrParser cdrParser = null;

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {

            rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
            rejectFileWriter = new PrintWriter(rejectFile);

            File outputFile = new File(outputDir + File.separator + fileName + ".processed");
            outputFileWriter = new PrintWriter(outputFile);

            currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());

            cdrReader = cdrParserService.getCDRReaderByCode(currentFile, readerCode);
            cdrParser = cdrParserService.getCDRParser(parserCode);

            Integer totalNummberOfRecords = cdrReader.getNumberOfRecords();
            boolean updateTotalCount = totalNummberOfRecords == null;
            if (totalNummberOfRecords != null) {
                jobExecutionResult.setNbItemsToProcess(totalNummberOfRecords);
            }

            if (MEVEOCdrFlatFileReader.class.isAssignableFrom(cdrReader.getClass())) {
                ((MEVEOCdrFlatFileReader) cdrReader).setDataFile(currentFile);
                ((MEVEOCdrFlatFileReader) cdrReader).setMappingDescriptor(mappingConf);
                ((MEVEOCdrFlatFileReader) cdrReader).setDataName(recordVariableName);
                ((MEVEOCdrFlatFileReader) cdrReader).parsing();
            }

            // Launch parallel processing of a file
            List<Future> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();

            int checkJobStatusEveryNr = JobSpeedEnum.FAST.getCheckNb();
            int updateJobStatusEveryNr = nbRuns.longValue() > 3 ? JobSpeedEnum.FAST.getUpdateNb() * nbRuns.intValue() / 2 : JobSpeedEnum.FAST.getUpdateNb();

            ICdrReader cdrReaderFinal = cdrReader;
            ICdrParser cdrParserFinal = cdrParser;
            PrintWriter outputFileWriterFinal = outputFileWriter;
            PrintWriter rejectFileWriterFinal = rejectFileWriter;

            Runnable task = () -> {

                currentUserProvider.reestablishAuthentication(lastCurrentUser);

                int i = 0;
                long globalI = 0;
                CDR cdr = null;

                while (true) {

                    if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobExecutionResult.getJobInstance().getId())) {
                        break;
                    }

                    try {
                        cdr = cdrReaderFinal.getNextRecord(cdrParserFinal);
                        if (cdr == null) {
                            break;
                        }

                        if (StringUtils.isBlank(cdr.getRejectReason())) {
                            List<Access> accessPoints = cdrParserFinal.accessPointLookup(cdr);
                            List<EDR> edrs = cdrParserFinal.convertCdrToEdr(cdr, accessPoints);
                            log.debug("Processing record line content:{} from file {}", cdr.getLine(), fileName);

                            cdrParserService.createEdrs(edrs, cdr);

                            synchronized (outputFileWriterFinal) {
                                outputFileWriterFinal.println(cdr.getLine());
                            }
                            globalI = jobExecutionResult.registerSucces();

                        } else {
                            globalI = jobExecutionResult.registerError("file=" + fileName + ", line=" + (cdr != null ? cdr.getLine() : "") + ": " + cdr.getRejectReason());

                            cdr.setStatus(CDRStatusEnum.ERROR);
                            createOrUpdateCdr(cdr);
                        }

                    } catch (IOException e) {
                        log.error("Failed to read a CDR line from file {}", fileName, e);
                        jobExecutionResult.addReport("Failed to read a CDR line from file " + fileName + " " + e.getMessage());
                        cdr.setStatus(CDRStatusEnum.ERROR);
                        cdr.setRejectReason(e.getMessage());
                        createOrUpdateCdr(cdr);
                        break;

                    } catch (Exception e) {
                        String errorReason = e.getMessage();
                        final Throwable rootCause = getRootCause(e);
                        if (e instanceof EJBTransactionRolledbackException && rootCause instanceof ConstraintViolationException) {
                            StringBuilder builder = new StringBuilder();
                            builder.append("Invalid values passed: ");
                            for (ConstraintViolation<?> violation : ((ConstraintViolationException) rootCause).getConstraintViolations()) {
                                builder.append(
                                    String.format(" %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));
                            }
                            errorReason = builder.toString();
                            log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason);
                        } else if (e instanceof CDRParsingException) {
                            log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason);
                        } else {
                            log.error("Failed to process a CDR line: {} from file {} error {}", cdr != null ? cdr.getLine() : null, fileName, errorReason, e);
                        }

                        synchronized (rejectFileWriterFinal) {
                            rejectFileWriterFinal.println((cdr != null ? cdr.getLine() : "") + "\t" + errorReason);
                        }
                        globalI = jobExecutionResult.registerError("file=" + fileName + ", line=" + (cdr != null ? cdr.getLine() : "") + ": " + errorReason);
                        cdr.setStatus(CDRStatusEnum.ERROR);
                        cdr.setRejectReason(e.getMessage());
                        createOrUpdateCdr(cdr);
                    }

                    // It is not known in advance of a number of records in a file, so total count is being updated with each record
                    if (updateTotalCount) {
                        jobExecutionResult.addNbItemsToProcess(1L);
                    }

                    try {
                        // Record progress
                        if (globalI > 0 && globalI % updateJobStatusEveryNr == 0) {
                            jobExecutionResultService.persistResult(jobExecutionResult);
                        }
                    } catch (EJBTransactionRolledbackException e) {
                        // Will ignore the error here, as its most likely to happen - updating jobExecutionResultImpl entity from multiple threads
                    } catch (Exception e) {
                        log.error("Failed to update job progress", e);
                    }
                    i++;
                }
            };

            for (int i = 0; i < nbRuns; i++) {
                log.info("{}/{} Will submit task to run", jobInstance.getJobTemplate(), jobInstance.getCode());
                futures.add(executor.submit(task));
                try {
                    Thread.sleep(waitingMillis.longValue());
                } catch (InterruptedException e) {
                    log.error("", e);
                }
            }

            // Mark number of threads it will be running on
            JobRunningStatusEnum jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), futures);

            boolean wasKilled = false;

            // Wait for all async methods to finish
            for (Future future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    wasKilled = true;
                    log.error("Thread/future for job {} was canceled", jobInstance);

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    jobExecutionResult.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            // Mark job as stopped if task was killed
            if (wasKilled) {
                jobExecutionService.markJobToStop(jobInstance);

                // Mark that all threads are finished
            } else {
                jobStatus = jobExecutionService.markJobAsRunning(jobInstance, false, jobExecutionResult.getId(), null);
            }

            boolean wasCanceled = wasKilled || jobStatus == JobRunningStatusEnum.REQUEST_TO_STOP;

            errors.addAll(jobExecutionResult.getErrors());

            if (jobExecutionResult.getNbItemsProcessed() == 0) {
                String errorDescription = "\r\n file " + fileName + " is empty";
                jobExecutionResult.addReport(errorDescription);
                errors.add(errorDescription);
            }

            if (wasCanceled) {
                log.info("Canceled processing mediation file {}", fileName);
            } else {
                log.info("Finished processing mediation file {}", fileName);
            }

        } catch (Exception e) {
            log.error("Failed to process mediation file {}", fileName, e);
            jobExecutionResult.addReport(e.getMessage());
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
                jobExecutionResult.addReport("\r\n cannot move file to archive directory " + fileName);
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
            if ((jobExecutionResult.getErrors().isEmpty() && jobExecutionResult.getNbItemsProcessedWithError() == 0) && rejectFile != null) {
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

    private Throwable getRootCause(Throwable e) {
        if (e.getCause() != null) {
            return getRootCause(e.getCause());
        }
        return e;
    }

    /**
     * Save the cdr if the configuration property mediation.persistCDR is true.
     *
     * @param cdr the cdr
     */
    private void createOrUpdateCdr(CDR cdr) {
        boolean persistCDR = "true".equals(ParamBeanFactory.getAppScopeInstance().getProperty("mediation.persistCDR", "false"));
        if (cdr != null && persistCDR) {
            if (cdr.getId() == null) {
                cdrService.create(cdr);
            } else {
                cdrService.update(cdr);
            }
        }
    }
}