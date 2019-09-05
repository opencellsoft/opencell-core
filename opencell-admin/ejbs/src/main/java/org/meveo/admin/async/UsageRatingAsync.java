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
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.job.JobExecutionService;

/**
 * @author anasseh
 * 
 */

@Stateless
public class UsageRatingAsync {

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Rate usage charges for a list of EDRs. One EDR at a time in a separate transaction.
     * 
     * @param edrs A list of EDRs
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     * @throws BusinessException BusinessException
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> edrs, JobExecutionResultImpl result, MeveoUser lastCurrentUser) throws BusinessException {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);
        int i = 0;
        for (Long edrId : edrs) {
            i++;
            if (i % JobExecutionService.CHECK_IS_JOB_RUNNING_EVERY_NR_FAST == 0 && !jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            try {
                usageRatingService.ratePostpaidUsage(edrId);
                result.registerSucces();

            } catch (Exception e) {

                String rejectReason = org.meveo.commons.utils.StringUtils.truncate(e.getMessage(), 255, true);

                StringBuilder aLine = new StringBuilder("Edr Id : ").append(edrId).append(" RejectReason : ").append(rejectReason);
                result.registerError(aLine.toString());
            }
        }
        return new AsyncResult<String>("OK");
    }
}
