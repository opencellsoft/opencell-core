/**
 * 
 */
package org.meveo.admin.async;

import java.util.HashMap;
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
import org.meveo.commons.parsers.IFileParser;
import org.meveo.commons.parsers.RecordContext;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInterface;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;


/**
 * Asynchronous FlatFile processing.
 * 
 * @author anasseh
 * @lastModifiedVersion willBeSetLater
 *
 */

@Stateless
public class FlatFileProcessingAsync {

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

    /**
     * Read/parse file and execute script for each line.
     * 
     * @param fileParser FlatFile parser
     * @param result job execution result
     * @param script script to execute
     * @param recordVariableName record var name
     * @param fileName file name
     * @param originFilename originFilename var name
     * @param errorAction action to do on error : continue, stop or rollback after an error
     * @return Future of FlatFileAsyncListResponse
     * @throws Exception Exception
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Future<FlatFileAsyncListResponse> launchAndForget(IFileParser fileParser, JobExecutionResultImpl result, ScriptInterface script, String recordVariableName,
            String fileName, String originFilename, String errorAction) throws Exception {
        long cpLines = 0;
        FlatFileAsyncListResponse flatFileAsyncListResponse = new FlatFileAsyncListResponse();
        while (fileParser.hasNext() && jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
            RecordContext recordContext = null;
            cpLines++;
            FlatFileAsyncUnitResponse flatFileAsyncResponse = new FlatFileAsyncUnitResponse();
            flatFileAsyncResponse.setLineNumber(cpLines);
            try {
                recordContext = fileParser.getNextRecord();
                flatFileAsyncResponse.setLineRecord(recordContext.getLineContent());
                log.trace("record line content:{}", recordContext.getLineContent());
                if (recordContext.getRecord() == null) {
                    throw new Exception(recordContext.getReason());
                }
                Map<String, Object> executeParams = new HashMap<String, Object>();
                executeParams.put(recordVariableName, recordContext.getRecord());
                executeParams.put(originFilename, fileName);
                if (FlatFileProcessingJob.ROLLBBACK.equals(errorAction)) {
                    executeParams.put(Script.CONTEXT_CURRENT_USER, currentUser);
                    executeParams.put(Script.CONTEXT_APP_PROVIDER, appProvider);
                    script.execute(executeParams);
                } else {
                    unitFlatFileProcessingJobBean.execute(script, executeParams);
                }
                flatFileAsyncResponse.setSuccess(true);
            } catch (Throwable e) {
                if (FlatFileProcessingJob.ROLLBBACK.equals(errorAction)) {
                    throw new BusinessException(e.getMessage());
                }
                String erreur = (recordContext == null || recordContext.getReason() == null) ? e.getMessage() : recordContext.getReason();
                log.warn("record on error :" + erreur);
                flatFileAsyncResponse.setSuccess(false);
                flatFileAsyncResponse.setReason(erreur);
                if (FlatFileProcessingJob.STOP.equals(errorAction)) {
                    flatFileAsyncListResponse.getResponses().add(flatFileAsyncResponse);
                    break;
                }
            }
            flatFileAsyncListResponse.getResponses().add(flatFileAsyncResponse);
        }
        return new AsyncResult<FlatFileAsyncListResponse>(flatFileAsyncListResponse);
    }
}
