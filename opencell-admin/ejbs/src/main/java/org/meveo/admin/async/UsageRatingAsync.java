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

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.UnitUsageRatingJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;

/**
 * @author anasseh
 * 
 */

@Stateless
public class UsageRatingAsync {

    @Inject
    private UnitUsageRatingJobBean unitUsageRatingJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result) throws BusinessException {
        for (Long id : ids) {
            if (!jobExecutionService.isJobRunning(result.getJobInstance())) {
                break;
            }
            try {
                unitUsageRatingJobBean.execute(result, id);
            } catch (BusinessException be) {
                unitUsageRatingJobBean.registerFailedEdr(result, id, be);
            }
        }
        return new AsyncResult<String>("OK");
    }
}
