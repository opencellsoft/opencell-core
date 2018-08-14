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
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.ScriptInterface;

/**
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.2
 */

@Stateless
public class AccOpGenerationAsync {

    @Inject
    UnitAccountOperationsGenerationJobBean unitAccountOperationsGenerationJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Generate account operations for a given list of Invoice ids. One invoice at a time in a separate transaction.
     * 
     * @param ids List of Invoice Ids to process
     * @param result Job Execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @param script script to execute        
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<Long> ids, JobExecutionResultImpl result, MeveoUser lastCurrentUser, ScriptInterface script) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (Long id : ids) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            unitAccountOperationsGenerationJobBean.execute(result, id, script);
        }
        return new AsyncResult<String>("OK");
    }
}
