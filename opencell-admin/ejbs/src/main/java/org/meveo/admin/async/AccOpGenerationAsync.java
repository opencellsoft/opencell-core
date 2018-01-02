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

import org.meveo.admin.job.UnitAccountOperationsGenerationJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;

/**
 * @author anasseh
 * 
 */

@Stateless
public class AccOpGenerationAsync {

    @Inject
    UnitAccountOperationsGenerationJobBean unitAccountOperationsGenerationJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result) {
        for (Long id : ids) {
            if (!jobExecutionService.isJobRunning(result.getJobInstance())) {
                break;
            }
            unitAccountOperationsGenerationJobBean.execute(result, id);
        }
        return new AsyncResult<String>("OK");
    }
}
