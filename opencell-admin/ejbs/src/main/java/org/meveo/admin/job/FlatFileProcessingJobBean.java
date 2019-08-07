package org.meveo.admin.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.FlatFileAsyncListResponse;
import org.meveo.admin.async.FlatFileAsyncUnitResponse;
import org.meveo.admin.async.FlatFileProcessingAsync;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.admin.util.FlatFileValidator;
import org.meveo.commons.parsers.FileParserBeanio;
import org.meveo.commons.parsers.FileParserFlatworm;
import org.meveo.commons.parsers.IFileParser;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ExcelToCsv;
import org.meveo.commons.utils.FileParsers;
import org.meveo.commons.utils.FileUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.bi.FileStatusEnum;
import org.meveo.model.bi.FlatFile;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.shared.DateUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.bi.impl.FlatFileService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * The Class FlatFileProcessingJobBean.
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
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
    private FlatFileProcessingAsync flatFileProcessingAsync;

    @Inject
    private FlatFileService flatFileService;

    @Inject
    private FlatFileValidator flatFileValidator;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /** The Constant DATETIME_FORMAT. */
    private static final String DATETIME_FORMAT = "dd_MM_yyyy-HHmmss";

    /** The file name. */
    String fileName;

    /** The output dir. */
    String outputDir;

    /** The output file writer. */
    PrintWriter outputFileWriter;

    /** The reject dir. */
    String rejectDir;

    /** The archive dir. */
    String archiveDir;

    /** The reject file writer. */
    PrintWriter rejectFileWriter;

    /** The report. */
    String report;

    /**
     * Execute.
     *
     * @param result job execution result
     * @param inputDir the input dir
     * @param file the file
     * @param mappingConf the mapping conf
     * @param scriptInstanceFlowCode the script instance flow code
     * @param recordVariableName the record variable name
     * @param context the context
     * @param originFilename the origin filename
     * @param formatTransfo the format transfo
     * @param errorAction action to do on error : continue, stop or rollback after an error
     */
    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String inputDir, String outDir, String archDir, String rejDir, File file, String mappingConf, String scriptInstanceFlowCode,
            String recordVariableName, Map<String, Object> context, String originFilename, String formatTransfo, String errorAction) {
        log.debug("Running for inputDir={}, scriptInstanceFlowCode={},formatTransfo={}, errorAction={}", inputDir, scriptInstanceFlowCode, formatTransfo, errorAction);

        outputDir = outDir != null ? outDir : inputDir + File.separator + "output";
        rejectDir = rejDir != null ? rejDir : inputDir + File.separator + "reject";
        archiveDir = archDir != null ? archDir : inputDir + File.separator + "archive";

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
        report = "";
        long cpLines = 0;

        if (file != null) {
            fileName = file.getName();
            ScriptInterface script = null;
            IFileParser fileParser = null;
            File currentFile = null;
            boolean isCsvFromExcel = false;
            List<String> success = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            try {
                log.info("InputFiles job {} in progress...", file.getAbsolutePath());
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
                }
                if (parserUsed == FileParsers.BEANIO) {
                    fileParser = new FileParserBeanio();
                }
                if (fileParser == null) {
                    throw new Exception("Check your mapping discriptor, only flatworm or beanio are allowed");
                }

                fileParser.setDataFile(currentFile);
                fileParser.setMappingDescriptor(mappingConf);
                fileParser.setDataName(recordVariableName);
                fileParser.parsing();

                MeveoUser lastCurrentUser = currentUser.unProxy();

                Future<FlatFileAsyncListResponse> futures = flatFileProcessingAsync.launchAndForget(fileParser, result, script, recordVariableName, fileName, originFilename,
                    errorAction, lastCurrentUser);
                for (FlatFileAsyncUnitResponse flatFileAsyncResponse : futures.get().getResponses()) {
                    cpLines++;
                    if (!flatFileAsyncResponse.isSuccess()) {
                        String errorDescription = "file=" + fileName + ", line=" + flatFileAsyncResponse.getLineNumber() + ": " + flatFileAsyncResponse.getReason();
                        errors.add(errorDescription);
                        result.registerError(errorDescription);
                        rejectRecord(flatFileAsyncResponse.getLineRecord(), flatFileAsyncResponse.getReason());
                    } else {
                        outputRecord(flatFileAsyncResponse.getLineRecord());
                        success.add("success line=" + flatFileAsyncResponse.getLineNumber());
                        result.registerSucces();
                    }
                }
                if (cpLines == 0) {
                    String stateFile = "empty";
                    if (FlatFileProcessingJob.ROLLBBACK.equals(errorAction)) {
                        stateFile = "rollbacked";
                    }
                    String errorDescription = "\r\n file " + fileName + " is " + stateFile;
                    report += errorDescription;
                    errors.add(errorDescription);
                }

                log.info("InputFiles job {} done.", fileName);

            } catch (Exception e) {
                report += "\r\n " + e.getMessage();
                log.error("Failed to process Record file {}", fileName, e);
                result.registerError(e.getMessage());
                errors.add(e.getMessage());
                if (currentFile != null) {
                    moveFile(rejectDir, currentFile, fileName);
                }
            } finally {
                updateFlatFile(success, errors, result.getJobInstance());
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
                    report += "\r\n error in script finailzation : " + e.getMessage();
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
                    report += "\r\n cannot move file to save directory " + fileName;
                }

                try {
                    if (rejectFileWriter != null) {
                        rejectFileWriter.close();
                        rejectFileWriter = null;
                    }
                } catch (Exception e) {
                    log.error("Failed to close rejected Record writer for file {}", fileName, e);
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
            result.addReport(report);
        } else {
            log.info("no file to process");
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

    /**
     * Output record.
     *
     * @param lineRecord the record line
     * @throws FileNotFoundException the file not found exception
     */
    private void outputRecord(String lineRecord) throws FileNotFoundException {
        if (outputFileWriter == null) {
            File outputFile = new File(outputDir + File.separator + fileName + ".processed");
            if (outputFile.exists()) {
                outputFile = new File(outputDir + File.separator + fileName + "_COPY_" + DateUtils.formatDateWithPattern(new Date(), DATETIME_FORMAT) + ".processed");
            }
            outputFileWriter = new PrintWriter(outputFile);
            outputFileWriter.print(lineRecord);
        } else {
            outputFileWriter.println("");
            outputFileWriter.print(lineRecord);
        }
    }

    /**
     * Reject record.
     *
     * @param lineRecord the record line
     * @param reason the reason
     */
    private void rejectRecord(String lineRecord, String reason) {
        if (rejectFileWriter == null) {
            File rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
            if (rejectFile.exists()) {
                rejectFile = new File(rejectDir + File.separator + fileName + "_COPY_" + DateUtils.formatDateWithPattern(new Date(), DATETIME_FORMAT) + ".rejected");
            }
            try {
                rejectFileWriter = new PrintWriter(rejectFile);
                rejectFileWriter.print(lineRecord + "=>" + reason);
            } catch (FileNotFoundException e) {
                log.error("Failed to create a rejection file {}", rejectFile.getAbsolutePath());
            }
        } else {
            rejectFileWriter.println("");
            rejectFileWriter.print(lineRecord + "=>" + reason);
        }
    }

    /**
     * update flat file
     *
     * @param success     success of processed flat file
     * @param errors      errors of processed flat file
     * @param jobInstance job instance
     */
    private void updateFlatFile(List<String> success, List<String> errors, JobInstance jobInstance) {
        try {
            String[] param = fileName.split("_");
            String code = param.length > 0 ? param[0] : null;

            FileStatusEnum status = FileStatusEnum.VALID;
            String errorMessage = null;
            if (errors != null && !errors.isEmpty()) {
                status = FileStatusEnum.REJECTED;
                int maxErrors = errors.size() > flatFileValidator.getBadLinesLimit() ? flatFileValidator.getBadLinesLimit() : errors.size();
                errorMessage = String.join(",", errors.subList(0, maxErrors));
            }
            FlatFile flatFile = flatFileService.findByCode(code);
            Integer linesInSuccess = success != null ? success.size() : null;
            Integer linesInError = errors != null ? errors.size() : null;
            if (flatFile == null) {
                flatFileService.create(fileName, null, errorMessage, status, jobInstance.getCode(), 1, linesInSuccess, linesInError);
            } else {
                flatFile.setFlatFileJobCode(jobInstance.getCode());
                flatFile.setProcessingAttempts(flatFile.getProcessingAttempts() != null ? flatFile.getProcessingAttempts() + 1 : 1);
                flatFile.setLinesInSuccess(linesInSuccess);
                flatFile.setLinesInError(linesInError);
                flatFile.setStatus(status);
                flatFile.setErrorMessage(errorMessage);
                flatFileService.update(flatFile);
            }
        } catch (BusinessException e) {
            log.error("Failed to update flat file {}", fileName, e);
        }
    }

}