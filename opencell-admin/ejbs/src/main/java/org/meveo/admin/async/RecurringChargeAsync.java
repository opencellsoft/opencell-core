/**
 * 
 */
package org.meveo.admin.async;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.job.UnitRecurringRatingJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * @author anasseh
 * 
 */

@Stateless
public class RecurringChargeAsync {

    @Inject
    private Logger log;

    @Inject
    private UnitRecurringRatingJobBean unitRecurringRatingJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, Date maxDate) {

        for (Long id : ids) {
            if (!jobExecutionService.isJobRunning(result.getJobInstance().getId())) {
                break;
            }
            log.debug("run recurringChargeInstace ID {}", id);
            unitRecurringRatingJobBean.execute(result, id, maxDate);
        }
        log.debug("End launchAndForget!");

        return new AsyncResult<String>("OK");
    }
}