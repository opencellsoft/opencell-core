/**
 * 
 */
package org.meveo.admin.async;

import java.util.Map;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitFlatFileProcessingJobBean;
import org.meveo.service.script.ScriptInterface;

/**
 * @author anasseh
 *
 */

@Stateless
public class FlatFileProcessingAsync {

    @Inject
    private UnitFlatFileProcessingJobBean unitFlatFileProcessingJobBean;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<FlatFileAsyncResponse> launchAndForget(ScriptInterface script, Map<String, Object> executeParams, String lineRecord, long lineNumber) {
        FlatFileAsyncResponse result = new FlatFileAsyncResponse();
        result.setLineRecord(lineRecord);
        result.setLineNumber(lineNumber);
        try {
            unitFlatFileProcessingJobBean.execute(script, executeParams);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setReason(e.getMessage());

        }
        return new AsyncResult<FlatFileAsyncResponse>(result);
    }
}
