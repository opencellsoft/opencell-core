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
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;
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
    UnitRecurringRatingJobBean unitRecurringRatingJobBean;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, User currentUser, Date maxDate) {

        for (Long id : ids) {
        	log.debug("run recurringChargeInstace ID {}",id);
            unitRecurringRatingJobBean.execute(result, currentUser, id, maxDate);
        }
        log.debug("End launchAndForget!");

        return new AsyncResult<String>("OK");
    }
}