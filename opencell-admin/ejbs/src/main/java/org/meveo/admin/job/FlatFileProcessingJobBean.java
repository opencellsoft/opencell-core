package org.meveo.admin.job;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
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
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * The Class FlatFileProcessingJobBean.
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.3.0
 */
@Stateless
public class FlatFileProcessingJobBean {

    /** The log. */
    @Inject
    private Logger log;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private FlatFileProcessing flatFileProcessing;

    /** The Constant DATETIME_FORMAT. */
    private static final String DATETIME_FORMAT = "dd_MM_yyyy-HHmmss";

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Execute.
     *
     * @param result job execution result
     * @param inputDir the input dir
     * @param file the file
     * @param mappingConf the mapping configuration
     * @param scriptInstanceFlowCode the script instance flow code
     * @param recordVariableName the record variable name
     * @param context the context
     * @param originFilename the origin filename
     * @param formatTransfo the format transform to
     * @param errorAction action to do on error : continue, stop or rollback after an error
     * @param nbRuns Number of parallel executions
     * @param waitingMills Number of milliseconds to wait between launching paralel processing threads
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, String inputDir, String outDir, String archDir, String rejDir, File file, String mappingConf, String scriptInstanceFlowCode,
            String recordVariableName, Map<String, Object> context, String originFilename, String formatTransfo, String errorAction, Long nbRuns, Long waitingMillis) {

        log.debug("Running for inputDir={}, file={}, scriptInstanceFlowCode={},formatTransfo={}, errorAction={}", inputDir, file.getAbsolutePath(), scriptInstanceFlowCode,
            formatTransfo, errorAction);

        String outputDir = outDir != null ? outDir : inputDir + File.separator + "output";
        String rejectDir = rejDir != null ? rejDir : inputDir + File.separator + "reject";
        String archiveDir = archDir != null ? archDir : inputDir + File.separator + "archive";

        File f = new File(outputDir);
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
            log.debug("saveDir {} not exist", archiveDir);
            f.mkdirs();
            log.debug("saveDir {} creation ok", archiveDir);
        }

        String fileName = file.getName();
        ScriptInterface script = null;
        IFileParser fileParser = null;
        File currentFile = null;
        boolean isCsvFromExcel = false;
        List<String> errors = new ArrayList<>();

        File rejectFile = null;
        PrintWriter rejectFileWriter = null;
        PrintWriter outputFileWriter = null;
        try {
            if ("Xlsx_to_Csv".equals(formatTransfo)) {
                isCsvFromExcel = true;
                ExcelToCsv excelToCsv = new ExcelToCsv();
                excelToCsv.convertExcelToCSV(file.getAbsolutePath(), file.getParent(), ";");
                moveFile(archiveDir, file, fileName);
                file = new File(inputDir + File.separator + fileName.replaceAll(".xlsx", ".csv").replaceAll(".xls", ".csv"));
            }
            currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());
            script = scriptInstanceService.getScriptInstance(scriptInstanceFlowCode);
            context.put("outputDir", outputDir);
            context.put(originFilename, fileName);
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

            rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
            rejectFileWriter = new PrintWriter(rejectFile);

            File outputFile = new File(outputDir + File.separator + fileName + ".processed");
            outputFileWriter = new PrintWriter(outputFile);

            // Launch parallel processing of a file
            List<Future<String>> futures = new ArrayList<Future<String>>();
            MeveoUser lastCurrentUser = currentUser.unProxy();
            for (long i = 0; i < nbRuns; i++) {
                if (FlatFileProcessingJob.ROLLBACK.equals(errorAction)) {
                    futures.add(flatFileProcessing.processFileAsyncInOneTx(fileParser, result, script, recordVariableName, fileName, originFilename, errorAction, rejectFileWriter,
                        outputFileWriter, lastCurrentUser));
                } else {
                    futures.add(flatFileProcessing.processFileAsync(fileParser, result, script, recordVariableName, fileName, originFilename, errorAction, rejectFileWriter,
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
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }

            errors.addAll(result.getErrors());

            if (result.getNbItemsProcessed() == 0) {
                String errorDescription = "\r\n file " + fileName + " is empty";
                result.addReport(errorDescription);
                errors.add(errorDescription);
            }

            log.info("InputFiles job {} done.", fileName);

        } catch (Exception e) {
            log.error("Failed to process Record file {}", fileName, e);
            result.addReport(e.getMessage());
            errors.add(e.getMessage());
            if (currentFile != null) {
                moveFile(rejectDir, currentFile, fileName);
            }

        } finally {
            flatFileProcessing.updateFlatFile(fileName, errors);
            try {
                if (fileParser != null) {
                    fileParser.close();
                }
            } catch (Exception e) {
                log.error("Failed to close file parser");
            }
            try {
                if (script != null) {
                    script.finalize(context);
                }
            } catch (Exception e) {
                result.addReport("\r\n error in script finailzation : " + e.getMessage());
            }
            try {
                if (currentFile != null) {
                    // Move current CSV file to save directory, if his origin from an Excel transformation, else CSV file was deleted.
                    if (isCsvFromExcel == false) {
                        moveFile(archiveDir, currentFile, fileName);
                    } else {
                        currentFile.delete();
                    }
                }
            } catch (Exception e) {
                result.addReport("\r\n cannot move file to save directory " + fileName);
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
     * Move file.
     *
     * @param dest the destination
     * @param file the file
     * @param name the file name
     */
    private void moveFile(String dest, File file, String name) {
        String destName = name;
        if ((new File(dest + File.separator + name)).exists()) {
            destName += "_COPY_" + DateUtils.formatDateWithPattern(new Date(), DATETIME_FORMAT);
        }
        FileUtils.moveFile(dest, file, destName);
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