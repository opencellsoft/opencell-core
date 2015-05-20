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

import org.meveo.admin.job.UnitRatedTransactionsJobBean;
import org.meveo.model.admin.User;
import org.meveo.model.jobs.JobExecutionResultImpl;

/**
 * @author anasseh
 *
 */

@Stateless
public class RatedTransactionAsync {
	
	@Inject
	private UnitRatedTransactionsJobBean unitRatedTransactionsJobBean;
	
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, User currentUser) {
		for (Long walletOperationId : ids) {
			unitRatedTransactionsJobBean.execute(result, currentUser, walletOperationId);
		}
		return new AsyncResult<String>("OK");
	}
}
