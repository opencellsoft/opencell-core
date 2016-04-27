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

import org.beanio.BeanReader;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.parsers.FileParserBeanio;
import org.meveo.commons.parsers.FileParserFlatworm;
import org.meveo.commons.parsers.IFileParser;
import org.meveo.commons.parsers.RecordContext;
import org.meveo.commons.utils.ExcelToCsv;
import org.meveo.commons.utils.FileParsers;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;

@Stateless
public class FlatFileProcessingJobBean {

    @Inject
    private Logger log;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    String fileName;
    String inputDir;
    String outputDir;
    PrintWriter outputFileWriter;
    String rejectDir;
    String archiveDir;
    PrintWriter rejectFileWriter;
    String report;
    String username;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobExecutionResultImpl result, String inputDir, User currentUser, File file, String mappingConf, String scriptInstanceFlowCode, String recordVariableName, Map<String, Object> context, String originFilename, String formatTransfo) {
        log.debug("Running for user={}, inputDir={}, scriptInstanceFlowCode={},formatTransfo={}", currentUser, inputDir, scriptInstanceFlowCode, formatTransfo);
        Provider provider = currentUser.getProvider();

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
            BeanReader beanReader = null;
            File currentFile = null;
            boolean isCsvFromExcel = false;
            try {
                log.info("InputFiles job {} in progress...", file.getAbsolutePath());
                if ("Xlsx_to_Csv".equals(formatTransfo)) {
                	isCsvFromExcel = true;
                    ExcelToCsv excelToCsv = new ExcelToCsv();
                    excelToCsv.convertExcelToCSV(file.getAbsolutePath(), file.getParent(), ";");
                    FileUtils.moveFile(archiveDir, file, fileName);                   
                    file = new File(inputDir + File.separator + fileName.replaceAll(".xlsx", ".csv").replaceAll(".xls", ".csv") );
                }
                currentFile = FileUtils.addExtension(file, ".processing");

                script = scriptInstanceService.getScriptInstance(provider, scriptInstanceFlowCode);
                

                script.init(context, currentUser);

                FileParsers parserUsed = getParserType(mappingConf);
                IFileParser fileParser = null;

                if (parserUsed == FileParsers.FLATWORM) {
                    fileParser = new FileParserFlatworm();
                }
                if (parserUsed == FileParsers.BEANIO) {
                    fileParser = new FileParserBeanio();
                }
                if(fileParser == null){
                    throw new Exception("Check your mapping discriptor, only flatworm and beanio are allowed");
                }

                fileParser.setDataFile(currentFile);
                fileParser.setMappingDescriptor(mappingConf);
                fileParser.setDataName(recordVariableName);
                fileParser.parsing();
                boolean continueAfterError = "true".equals(ParamBean.getInstance().getProperty("flatfile.continueOnError", "true"));
                while (fileParser.hasNext()) {
                	RecordContext recordContext = null;
                    cpLines++;
                    try {
                        recordContext = fileParser.getNextRecord();
                        log.debug("recordObject:{}", recordContext.getLineContent());
                        Map<String, Object> executeParams = new HashMap<String, Object>();
                        executeParams.put(recordVariableName, recordContext.getRecord());
                        executeParams.put(originFilename, fileName);
                        script.execute(executeParams, currentUser);
                        outputRecord(recordContext);
                        result.registerSucces();
                    } catch (Throwable e) {
                        log.warn("error on reject record ", e);
                        result.registerError("file=" + fileName + ", line=" + cpLines + ": " + recordContext.getReason());
                        rejectRecord(recordContext, e.getMessage());
                        if(!continueAfterError){
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
                    if (beanReader != null) {
                        beanReader.close();
                    }
                    if (script != null) {
                        script.finalize(context, currentUser);
                    }
                } catch (Exception e) {
                    report += "\r\n error in script finailzation" + e.getMessage();
                }
                try {
                    if (currentFile != null) {
                        // Move current CSV file to save directory, if his origin from an Excel transformation, else CSV file was deleted.
						if (isCsvFromExcel == false) {
							FileUtils.moveFile(archiveDir, currentFile,
									fileName);
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

    private FileParsers getParserType(String mappingConf) {
        if (mappingConf.indexOf("<beanio") >= 0) {
            return FileParsers.BEANIO;
        }
        if (mappingConf.indexOf("<file-format>") >= 0) {
            return FileParsers.FLATWORM;
        }
        return null;
    }

    private void outputRecord(RecordContext record) throws FileNotFoundException {
        if (outputFileWriter == null) {
            File outputFile = new File(outputDir + File.separator + fileName + ".processed");
            outputFileWriter = new PrintWriter(outputFile);
        }
        outputFileWriter.println(record == null ? null : record.getRecord().toString());
    }

    private void rejectRecord(RecordContext record, String reason) {
        if (rejectFileWriter == null) {
            File rejectFile = new File(rejectDir + File.separator + fileName + ".rejected");
            try {
                rejectFileWriter = new PrintWriter(rejectFile);
            } catch (FileNotFoundException e) {
                log.error("Failed to create a rejection file {}", rejectFile.getAbsolutePath());
            }
        }
        rejectFileWriter.println((record == null ? null : record.getLineContent().toString()) + ";" + reason);
    }

}
