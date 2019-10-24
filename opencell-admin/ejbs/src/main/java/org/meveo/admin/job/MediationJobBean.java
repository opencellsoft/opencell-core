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
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.FileUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.slf4j.Logger;

/**
 * The Class MediationJobBean.
 *
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
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

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

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
     * Process a single file
     *
     * @param result Job execution result
     * @param inputDir Input directory
     * @param outputDir Directory to store a successfully processed records
     * @param archiveDir Directory to store a copy of a processed file
     * @param rejectDir Directory to store a failed records
     * @param file File to process
     * @param nbRuns Number of parallel executions
     * @param waitingMills Number of milliseconds to wait between launching parallel processing threads
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String inputDir, String outputDir, String archiveDir, String rejectDir, File file, String parameter, Long nbRuns,
            Long waitingMillis) {

        log.debug("Processing mediation file  in inputDir={}, file={}", inputDir, file.getAbsolutePath());

        String fileName = file.getName();

        File rejectFile = null;
        PrintWriter rejectFileWriter = null;
        PrintWriter outputFileWriter = null;

        File currentFile = null;
        CSVCDRParser cdrParser = null;

        try {

            rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
            rejectFileWriter = new PrintWriter(rejectFile);

            File outputFile = new File(outputDir + File.separator + fileName + ".processed");
            outputFileWriter = new PrintWriter(outputFile);

            currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());

            cdrParser = cdrParserService.getCDRParser(currentFile);

            // Launch parallel processing of a file
            List<Future<String>> futures = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            for (long i = 0; i < nbRuns; i++) {

                futures.add(mediationFileProcessing.processFileAsync(cdrParser, result, fileName, rejectFileWriter, outputFileWriter, lastCurrentUser));

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
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            if (result.getNbItemsProcessed() == 0) {
                String errorDescription = "\r\n file " + fileName + " is empty";
                result.addReport(errorDescription);
            }

            log.info("Finished processing mediation {}", fileName);

        } catch (Exception e) {
            log.error("Failed to process mediation file {}", fileName, e);
            result.addReport(e.getMessage());
            if (currentFile != null) {
                FileUtils.moveFileDontOverwrite(rejectDir, currentFile, fileName);
            }

        } finally {
            try {
                if (cdrParser != null) {
                    cdrParser.close();
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