package org.meveo.admin.job;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.parsers.FileParserBeanio;
import org.meveo.commons.parsers.FileParserFlatworm;
import org.meveo.commons.parsers.IFileParser;
import org.meveo.commons.parsers.RecordContext;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ExcelToCsv;
import org.meveo.commons.utils.FileParsers;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

/**
 * The Class FlatFileProcessingJobBean.
 */
@Stateless
public class FlatFileProcessingJobBean {

    /** The log. */
    @Inject
    private Logger log;

    /** The script instance service. */
    @Inject
    private ScriptInstanceService scriptInstanceService;

    /** The job execution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    /** The file name. */
    String fileName;

    /** The input dir. */
    String inputDir;

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

    /** The username. */
    String username;

    
    @Inject
    private ParamBeanFactory paramBeanFactory;

    /**
     * Execute.
     *
     * @param result the result
     * @param inputDir the input dir
     * @param file the file
     * @param mappingConf the mapping conf
     * @param scriptInstanceFlowCode the script instance flow code
     * @param recordVariableName the record variable name
     * @param context the context
     * @param originFilename the origin filename
     * @param formatTransfo the format transfo
     */
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String inputDir, File file, String mappingConf, String scriptInstanceFlowCode, String recordVariableName,
            Map<String, Object> context, String originFilename, String formatTransfo) {
        log.debug("Running for inputDir={}, scriptInstanceFlowCode={},formatTransfo={}", inputDir, scriptInstanceFlowCode, formatTransfo);

        outputDir = inputDir + File.separator + "output";
        rejectDir = inputDir + File.separator + "reject";
        archiveDir = inputDir + File.separator + "archive";

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
            try {
                log.info("InputFiles job {} in progress...", file.getAbsolutePath());
                if ("Xlsx_to_Csv".equals(formatTransfo)) {
                    isCsvFromExcel = true;
                    ExcelToCsv excelToCsv = new ExcelToCsv();
                    excelToCsv.convertExcelToCSV(file.getAbsolutePath(), file.getParent(), ";");
                    FileUtils.moveFile(archiveDir, file, fileName);
                    file = new File(inputDir + File.separator + fileName.replaceAll(".xlsx", ".csv").replaceAll(".xls", ".csv"));
                }
                currentFile = FileUtils.addExtension(file, ".processing_" + EjbUtils.getCurrentClusterNode());

                script = scriptInstanceService.getScriptInstance(scriptInstanceFlowCode);

                script.init(context);

                FileParsers parserUsed = getParserType(mappingConf);

                if (parserUsed == FileParsers.FLATWORM) {
                    fileParser = new FileParserFlatworm();
                }
                if (parserUsed == FileParsers.BEANIO) {
                    fileParser = new FileParserBeanio();
                }
                if (fileParser == null) {
                    throw new Exception("Check your mapping discriptor, only flatworm and beanio are allowed");
                }

                fileParser.setDataFile(currentFile);
                fileParser.setMappingDescriptor(mappingConf);
                fileParser.setDataName(recordVariableName);
                fileParser.parsing();
                boolean continueAfterError = "true".equals(paramBeanFactory.getInstance().getProperty("flatfile.continueOnError", "true"));
                while (fileParser.hasNext() && jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                    RecordContext recordContext = null;
                    cpLines++;
                    try {
                        recordContext = fileParser.getNextRecord();
                        log.debug("record line content:{}", recordContext.getLineContent());
                        Map<String, Object> executeParams = new HashMap<String, Object>();
                        executeParams.put(recordVariableName, recordContext.getRecord());
                        executeParams.put(originFilename, fileName);
                        script.execute(executeParams);
                        outputRecord(recordContext);
                        result.registerSucces();

                    } catch (Throwable e) {
                        String erreur = (recordContext == null || recordContext.getReason() == null) ? e.getMessage() : recordContext.getReason();
                        log.warn("error on reject record ", e);
                        result.registerError("file=" + fileName + ", line=" + cpLines + ": " + erreur);
                        rejectRecord(recordContext, erreur);
                        if (!continueAfterError) {
                            break;
                        }
                    }
                }

                if (cpLines == 0) {
                    report += "\r\n file is empty ";
                }

                log.info("InputFiles job {} done.", fileName);

            } catch (Exception e) {
                report += "\r\n " + e.getMessage();
                log.error("Failed to process Record file {}", fileName, e);
                result.registerError(e.getMessage());
                FileUtils.moveFile(rejectDir, currentFile, fileName);

            } finally {
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
                            FileUtils.moveFile(archiveDir, currentFile, fileName);
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
     * @param record the record
     * @throws FileNotFoundException the file not found exception
     */
    private void outputRecord(RecordContext record) throws FileNotFoundException {
        if (outputFileWriter == null) {
            File outputFile = new File(outputDir + File.separator + fileName + ".processed");
            outputFileWriter = new PrintWriter(outputFile);
        }
        outputFileWriter.println(record == null ? null : record.getRecord().toString());
    }

    /**
     * Reject record.
     *
     * @param record the record
     * @param reason the reason
     */
    private void rejectRecord(RecordContext record, String reason) {
        if (rejectFileWriter == null) {
            File rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
            try {
                rejectFileWriter = new PrintWriter(rejectFile);
            } catch (FileNotFoundException e) {
                log.error("Failed to create a rejection file {}", rejectFile.getAbsolutePath());
            }
        }
        rejectFileWriter.println((record == null ? null : record.getLineContent()) + ";" + reason);
    }

}
