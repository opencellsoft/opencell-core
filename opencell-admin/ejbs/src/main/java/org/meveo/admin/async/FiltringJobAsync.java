/**
 * 
 */
package org.meveo.admin.async;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitFilteringJobBean;
import org.meveo.model.IEntity;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.ScriptInterface;

/**
 * @author anasseh
 * 
 */

@Stateless
public class FiltringJobAsync {

    @Inject
    private UnitFilteringJobBean unitFilteringJobBean;
    
    @Inject
    private JobExecutionService jobExecutionService;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<? extends IEntity> filtredEntities, JobExecutionResultImpl result, ScriptInterface scriptInterface, String recordVariableName) {
	for (Object filtredEntity : filtredEntities) {
        if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
            break;
        }
	    unitFilteringJobBean.execute(result, filtredEntity, scriptInterface, recordVariableName);
	}
	return new AsyncResult<String>("OK");
    }
}
