/**
 * 
 */
package org.meveo.admin.async;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.FlatFileProcessingJob;
import org.meveo.admin.job.UnitFlatFileProcessingJobBean;
import org.meveo.admin.util.FlatFileValidator;
import org.meveo.commons.parsers.IFileParser;
import org.meveo.commons.parsers.RecordContext;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.bi.FileStatusEnum;
import org.meveo.model.bi.FlatFile;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.bi.impl.FlatFileService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * FlatFile processing
 * 
 * @author Abdelhadi
 */
@Stateless
public class FlatFileProcessing {

    /** The log. */
    @Inject
    private Logger log;

    /** The unit flat file processing job bean. */
    @Inject
    private UnitFlatFileProcessingJobBean unitFlatFileProcessingJobBean;

    /** The job execution service. */
    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private FlatFileService flatFileService;

    @Inject
    private FlatFileValidator flatFileValidator;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Read/parse file and execute script for each line. NOTE: Executes in NO transaction - each line will be processed in a separate transaction, one line failure will not affect
     * processing of other lines
     * 
     * @param fileParser FlatFile parser
     * @param result Job execution result
     * @param script Script to execute
     * @param recordVariableName Record variable name as it will appear in the script context
     * @param fileName File name being processed
     * @param filenameVariableName Filename variable name as it will appear in the script context
     * @param actionOnError Action to take when error happens: continue, stop or rollback after an error
     * @param outputFileWriter File writer to output processed data
     * @param rejectFileWriter File writer to output failed data
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future
     * @throws BusinessException General exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> processFileAsync(IFileParser fileParser, JobExecutionResultImpl result, ScriptInterface script, String recordVariableName, String fileName,
            String filenameVariableName, String actionOnError, PrintWriter rejectFileWriter, PrintWriter outputFileWriter, MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        processFile(fileParser, result, script, recordVariableName, fileName, filenameVariableName, actionOnError, rejectFileWriter, outputFileWriter);
        return new AsyncResult<String>("OK");
    }

    /**
     * Read/parse file and execute script for each line. NOTE: Process all file in ONE transaction. Failure in one line will rollback all changes. To use only with
     * errorAction=rollback
     * 
     * @param fileParser FlatFile parser
     * @param result Job execution result
     * @param script Script to execute
     * @param recordVariableName Record variable name as it will appear in the script context
     * @param fileName File name being processed
     * @param filenameVariableName Filename variable name as it will appear in the script context
     * @param actionOnError Action to take when error happens: continue, stop or rollback after an error
     * @param outputFileWriter File writer to output processed data
     * @param rejectFileWriter File writer to output failed data
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future
     * @throws BusinessException General exception
     */
    @Asynchronous
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<String> processFileAsyncInOneTx(IFileParser fileParser, JobExecutionResultImpl result, ScriptInterface script, String recordVariableName, String fileName,
            String filenameVariableName, String actionOnError, PrintWriter rejectFileWriter, PrintWriter outputFileWriter, MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        processFile(fileParser, result, script, recordVariableName, fileName, filenameVariableName, actionOnError, rejectFileWriter, outputFileWriter);
        return new AsyncResult<String>("OK");
    }

    /**
     * Read/parse file and execute script for each line.
     * 
     * @param fileParser FlatFile parser
     * @param result Job execution result
     * @param script Script to execute
     * @param recordVariableName Record variable name as it will appear in the script context
     * @param fileName File name being processed
     * @param filenameVariableName Filename variable name as it will appear in the script context
     * @param actionOnError Action to take when error happens: continue, stop or rollback after an error
     * @param outputFileWriter File writer to output processed data
     * @param rejectFileWriter File writer to output failed data
     * @return Future
     * @throws BusinessException General exception
     */
    private void processFile(IFileParser fileParser, JobExecutionResultImpl result, ScriptInterface script, String recordVariableName, String fileName, String filenameVariableName,
            String actionOnError, PrintWriter rejectFileWriter, PrintWriter outputFileWriter) throws BusinessException {

        int i = 0;
        Map<String, Object> executeParams = new HashMap<String, Object>();
        executeParams.put(Script.CONTEXT_CURRENT_USER, currentUser);
        executeParams.put(Script.CONTEXT_APP_PROVIDER, appProvider);
        executeParams.put(filenameVariableName, fileName);

        RecordContext recordContext = null;

        while (true) {

            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_SLOW == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }

            try {
                recordContext = fileParser.getNextRecord();
                if (recordContext == null) {
                    break;
                }

                log.info("Processing record line content:{} from file {}", recordContext.getLineContent(), fileName);

                if (recordContext.getRecord() == null) {
                    throw recordContext.getRejectReason();
                }

                executeParams.put(recordVariableName, recordContext.getRecord());

                if (FlatFileProcessingJob.ROLLBACK.equals(actionOnError)) {
                    script.execute(executeParams);
                } else {
                    unitFlatFileProcessingJobBean.execute(script, executeParams);
                }

                synchronized (outputFileWriter) {
                    outputFileWriter.println(recordContext.getLineContent());
                }
                result.registerSucces();

            } catch (Exception e) {
                String errorReason = ((recordContext == null || recordContext.getRejectReason() == null) ? e.getMessage() : recordContext.getRejectReason().getMessage());
                log.error("Failed to process a record line content:{} from file {} error {}", recordContext != null ? recordContext.getLineContent() : null, fileName, errorReason,
                    e);

                synchronized (rejectFileWriter) {
                    rejectFileWriter.println(recordContext.getLineContent() + "=>" + errorReason);
                }
                result.registerError("file=" + fileName + ", line=" + recordContext.getLineNumber() + ": " + errorReason);

                if (FlatFileProcessingJob.STOP.equals(actionOnError)) {
                    log.warn("Processing of file {} will stop as error was encountered", fileName);
                    break;

                } else if (FlatFileProcessingJob.ROLLBACK.equals(actionOnError)) {
                    log.warn("Processing of file {} will stop and any changes will be reverted as error was encountered", fileName);
                    throw new BusinessException(e.getMessage());
                }
            }
        }

    }

    /**
     * Update flat file record with information on errors
     *
     * @param errors processed flat file errors
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateFlatFile(String fileName, List<String> errors) {
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
            if (flatFile == null) {
                flatFileService.create(fileName, null, errorMessage, status);
            } else {
                flatFile.setStatus(status);
                flatFile.setErrorMessage(errorMessage);
                flatFileService.update(flatFile);
            }
        } catch (BusinessException e) {
            log.error("Failed to update flat file {}", fileName, e);
        }
    }
}