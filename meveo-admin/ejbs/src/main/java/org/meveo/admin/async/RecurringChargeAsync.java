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
import javax.inject.Inject;

import org.meveo.admin.job.UnitRecurringRatingJobBean;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * @author anasseh
 * 
 */

@Stateless
public class RecurringChargeAsync {

    @Inject
    UnitRecurringRatingJobBean unitRecurringRatingJobBean;

    @Asynchronous
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, User currentUser, Date maxDate) {

        for (Long id : ids) {
            unitRecurringRatingJobBean.execute(result, currentUser, id, maxDate);
        }

        return new AsyncResult<String>("OK");
    }
}