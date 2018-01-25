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

import org.meveo.admin.job.UnitPaymentCardJobBean;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.payments.PaymentGateway;

/**
 * @author anasseh
 * 
 */

@Stateless
public class PaymentCardAsync {

    @Inject
    UnitPaymentCardJobBean unitPaymentCardJobBean;

    /**
     * @param ids list of ids
     * @param result job execution result
     * @param createAO true/ false to  create account operation
     * @param matchingAO matching account operation
     * @return future result
     */
    @Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, boolean createAO, boolean matchingAO,PaymentGateway paymentGateway) {
		for (Long id : ids) {
			unitPaymentCardJobBean.execute(result, id, createAO, matchingAO,paymentGateway);
		}
        return new AsyncResult<String>("OK");
    }
}
