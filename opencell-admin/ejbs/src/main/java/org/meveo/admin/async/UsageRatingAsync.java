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
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
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

	@Inject
	private CurrentUserProvider currentUserProvider;

	/**
	 * Rate usage charges for a list of EDRs. One EDR at a time in a separate
	 * transaction.
	 * 
	 * @param ids
	 *            A list of EDR ids
	 * @param result
	 *            Job execution result
	 * @param lastCurrentUser
	 *            Current user. In case of multitenancy, when user authentication is
	 *            forced as result of a fired trigger (scheduled jobs, other timed
	 *            event expirations), current user might be lost, thus there is a
	 *            need to reestablish.
	 * @return Future String
	 * @throws BusinessException
	 *             BusinessException
	 */
	@Asynchronous
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, MeveoUser lastCurrentUser)
			throws BusinessException {

		currentUserProvider.reestablishAuthentication(lastCurrentUser);

		for (Long id : ids) {
			if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
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
