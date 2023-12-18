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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.async.FlatFileProcessing;
import org.meveo.admin.parse.csv.MEVEOCdrFlatFileReader;
import org.meveo.admin.storage.StorageFactory;
import org.meveo.cache.JobRunningStatusEnum;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.security.MeveoUser;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.job.Job;
import org.meveo.service.mediation.MediationSettingService;
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
    private CDRParsingService cdrParsingService;

    @Inject
    private FlatFileProcessing flatFileProcessing;

    @Inject
    private CDRService cdrService;

    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;

    @EJB
    private MediationJobBean thisNewTX;
    
    @Inject
    private MediationSettingService mediationsettingService;

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
        String fileCurrentName = null;
        String rejectedfileName = fileName + ".rejected";
        String processedfileName = fileName + ".processed";

        File currentFile = null;
        ICdrReader cdrReader = null;
        ICdrParser cdrParser = null;

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        Long batchSize = (Long) getParamOrCFValue(jobInstance, Job.CF_BATCH_SIZE, 1000L);

        Long nbThreads = (Long) this.getParamOrCFValue(jobInstance, Job.CF_NB_RUNS, -1L);
        if (nbThreads == -1) {
            nbThreads = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, Job.CF_WAITING_MILLIS, 0L);

        try {

            rejectFile = new File(rejectDir + File.separator + rejectedfileName);
            rejectFileWriter = StorageFactory.getPrintWriter(rejectFile);

            File outputFile = new File(outputDir + File.separator + processedfileName);
            outputFileWriter = StorageFactory.getPrintWriter(outputFile);

            currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());

            // Failed to rename a file, probably because it was not there anymore as other mediation job has processed it
            if (currentFile == null) {
                log.debug("Mediation file {} probably was processed already and failed to be renamed, will continue to another file", inputDir, file.getAbsolutePath());
                return;
            }

            cdrReader = cdrParsingService.getCDRReaderByCode(currentFile, readerCode);
            cdrParser = cdrParsingService.getCDRParser(parserCode);

            Integer totalNummberOfRecords = cdrReader.getNumberOfRecords();
            boolean updateTotalCount = totalNummberOfRecords == null;
            if (totalNummberOfRecords != null) {
                jobExecutionResult.addNbItemsToProcess(totalNummberOfRecords);
                jobExecutionResultService.persistResult(jobExecutionResult);
            }

            if (cdrReader != null && MEVEOCdrFlatFileReader.class.isAssignableFrom(cdrReader.getClass())) {
                ((MEVEOCdrFlatFileReader) cdrReader).setDataFile(currentFile);
                ((MEVEOCdrFlatFileReader) cdrReader).setMappingDescriptor(mappingConf);
                ((MEVEOCdrFlatFileReader) cdrReader).setDataName(recordVariableName);
                ((MEVEOCdrFlatFileReader) cdrReader).parsing();
            }

            // Launch parallel processing of a file
            List<Future> futures = new ArrayList<>();
            MeveoUser lastCurrentUser = currentUser.unProxy();

            ICdrReader cdrReaderFinal = cdrReader;
            ICdrParser cdrParserFinal = cdrParser;
            PrintWriter outputFileWriterFinal = outputFileWriter;
            PrintWriter rejectFileWriterFinal = rejectFileWriter;

            List<Runnable> tasks = new ArrayList<Runnable>(nbThreads.intValue());
            String auditOriginName = jobInstance.getJobTemplate() + "/" + jobInstance.getCode();
            boolean isDuplicateCheckOn = cdrParserFinal.isDuplicateCheckOn();

            String originRecordEL = appProvider.getCdrDeduplicationKeyEL();

            Long jobInstanceId = jobExecutionResult.getJobInstance().getId();

            for (int k = 0; k < nbThreads; k++) {

                int finalK = k;
                tasks.add(() -> {

                    Thread.currentThread().setName(jobInstance.getCode() + "-" + finalK);

                    currentUserProvider.reestablishAuthentication(lastCurrentUser);
                    AuditOrigin.setAuditOriginAndName(ChangeOriginEnum.JOB, auditOriginName);

                    mainLoop: while (true) {

                        final List<CDR> cdrs = new ArrayList<CDR>();
                        int nrOfItemsInBatch = 0;

                        while (nrOfItemsInBatch < batchSize) {

                            try {
                                CDR cdr = cdrReaderFinal.getNextRecord(cdrParserFinal, originRecordEL);
                                if (cdr == null) {
                                    break;
                                }

                                if (!StringUtils.isBlank(cdr.getRejectReason())) {
                                    cdr.setStatus(CDRStatusEnum.ERROR);
                                }

                                cdrs.add(cdr);

                                if (isJobRequestedToStop(jobInstanceId)) {
                                    return;
                                }
                                nrOfItemsInBatch++;

                            } catch (IOException e) {
                                log.error("Failed to read a CDR line from file {}", fileName, e);
                                jobExecutionResult.addReport("Failed to read a CDR line from file " + fileName + " " + e.getMessage());
                                break mainLoop;
                            }
                        }

                        if (cdrs.isEmpty()) {
                            break mainLoop;
                        }

                        thisNewTX.processCDRs(cdrs, jobExecutionResult, cdrParserFinal, outputFileWriterFinal, rejectFileWriterFinal, fileName, isDuplicateCheckOn, updateTotalCount);

                        if (isJobRequestedToStop(jobInstanceId)) {
                            return;
                        }
                    }
                });
            }

            // Tracks if job's main thread is still running. Used only to stop job status reporting thread.
            boolean[] isProcessing = { !jobExecutionService.isJobCancelled(jobInstanceId) };

            // Start job status report task. Not run in future, so it will die when main thread dies
            Runnable jobStatusReportTask = IteratorBasedJobBean.getJobStatusReportingTask(jobInstance, lastCurrentUser, jobInstance.getJobStatusReportFrequency(), jobExecutionResult, isProcessing,
                currentUserProvider, log, jobExecutionResultService, jobExecutionService);
            Thread jobStatusReportThread = new Thread(jobStatusReportTask);
            jobStatusReportThread.start();

            // Launch main processing tasks
            int i = 0;
            for (Runnable task : tasks) {
                log.info("{}/{} Will submit task #{} to run", jobInstance.getJobTemplate(), jobInstance.getCode(), i++);
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

                } catch (InterruptedException | CancellationException e) {
                    wasKilled = true;
                    log.error("Thread/future for job {} was canceled", jobInstance);

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    jobExecutionResult.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            // This will exit the status report task
            isProcessing[0] = false;
            jobStatusReportThread.interrupt();

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
                jobExecutionResult.addReport("Processed file partially: " + fileName);

            } else {
                log.info("Finished processing mediation file {}", fileName);
                jobExecutionResult.addReport("Processed file: " + fileName);
            }

        } catch (Exception e) {
            log.error("Failed to process mediation file {}", fileName, e);
            jobExecutionResult.addReport(e.getMessage());
            errors.add(e.getMessage());
            if (currentFile != null) {
                fileCurrentName = FileUtils.moveFileDontOverwrite(rejectDir, currentFile, fileName);
            }

        } finally {
            if (cdrReader != null && MEVEOCdrFlatFileReader.class.isAssignableFrom(cdrReader.getClass())) {
                flatFileProcessing.updateFlatFile(fileName, fileCurrentName, rejectedfileName, processedfileName, rejectDir, outputDir, errors, jobExecutionResult.getNbItemsCorrectlyProcessed(),
                    jobExecutionResult.getNbItemsProcessedWithError(), jobExecutionResult.getJobInstance().getCode());
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

    public static Throwable getRootCause(Throwable e) {
        if (e.getCause() != null) {
            return getRootCause(e.getCause());
        }
        return e;
    }
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processCDRs(List<CDR> cdrs, JobExecutionResultImpl jobExecutionResult, ICdrParser cdrParserFinal, PrintWriter outputFileWriter, PrintWriter rejectFileWriter, String fileName, boolean isDuplicateCheckOn,
            boolean updateTotalCount) {

        for (CDR cdr : cdrs) {

            try {

                if (!StringUtils.isBlank(cdr.getRejectReason())) {
                    failedCDR(jobExecutionResult, fileName, cdr, CDRStatusEnum.ERROR, rejectFileWriter);
                } else {

                    List<Access> accessPoints = cdrParserFinal.accessPointLookup(cdr);
                    List<EDR> edrs = cdrParserFinal.convertCdrToEdr(cdr, accessPoints);

                    if (isDuplicateCheckOn) {
                        cdrParserFinal.deduplicate(cdr);
                    }
                    
                    cdrParsingService.createEdrs(edrs, cdr);
                    
                    mediationsettingService.applyEdrVersioningRule(edrs, cdr, false);
                    if (!StringUtils.isBlank(cdr.getRejectReason())) {
                        failedCDR(jobExecutionResult, fileName, cdr, cdr.getStatus(), rejectFileWriter);
                    }

                    outputFileWriter.println(cdr.getLine());

                    jobExecutionResult.registerSucces();
                }

            } catch (Exception e) {
                String errorReason = e.getMessage();
                final Throwable rootCause = getRootCause(e);
                if (e instanceof EJBTransactionRolledbackException && rootCause instanceof ConstraintViolationException) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Invalid values passed: ");
                    for (ConstraintViolation<?> violation : ((ConstraintViolationException) rootCause).getConstraintViolations()) {
                        builder
                            .append(String.format(" %s.%s: value '%s' - %s;", violation.getRootBeanClass().getSimpleName(), violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage()));
                    }
                    errorReason = builder.toString();
                    log.error("Failed to process a CDR line: {} from file {}. Reason: {}", cdr.getLine(), fileName, errorReason);
                } else if (e instanceof CDRParsingException) {
                    log.error("Failed to process a CDR line: {} from file {}. Reason: {}", cdr.getLine(), fileName, errorReason);
                } else {
                    log.error("Failed to process a CDR line: {} from file {}. Reason: {}", cdr.getLine(), fileName, errorReason, e);
                }

                rejectFileWriter.println(cdr.getLine() + "\t" + errorReason);

                jobExecutionResult.registerError("file=" + fileName + ", line=" + cdr.getLine() + ": " + errorReason);
                cdr.setStatus(CDRStatusEnum.ERROR);
                cdr.setRejectReason(e.getMessage());

                rejectededCdrEventProducer.fire(cdr);
                cdrService.createOrUpdateCdr(cdr);
            }

            // It is not known in advance of a number of records in a file, so total count is being updated with each record
            if (updateTotalCount) {
                jobExecutionResult.addNbItemsToProcess(1L);
            }
        }
    }
    
    private void failedCDR(JobExecutionResultImpl jobExecutionResult,String fileName, CDR cdr, CDRStatusEnum status, PrintWriter rejectFileWriter) {
        log.error("Failed to process a CDR line: {} from file {}. Reason: {}", cdr.getLine(), fileName, cdr.getRejectReason());
        rejectFileWriter.println(cdr.getLine() + "\t" + cdr.getRejectReason());
        jobExecutionResult.registerError("file=" + fileName + ", line=" + cdr.getLine() + ": " + cdr.getRejectReason());
        jobExecutionResult.unRegisterSucces();
        cdr.setStatus(status);
        rejectededCdrEventProducer.fire(cdr);
        cdrService.createOrUpdateCdr(cdr);
    }
    
}