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

import org.meveo.admin.job.UnitFilteringJobBean;
import org.meveo.model.IEntity;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.script.ScriptInterface;

/**
 * @author anasseh
 * 
 */

@Stateless
public class FiltringJobAsync {

    @Inject
    private UnitFilteringJobBean unitFilteringJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Run script on filtered entities one entity at a time in a separate transaction.
     * 
     * @param filtredEntities Filtered entities
     * @param result Job execution result
     * @param scriptInterface Script to run
     * @param recordVariableName Name of a variable to give to an entity being processed
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<? extends IEntity> filtredEntities, JobExecutionResultImpl result, ScriptInterface scriptInterface, String recordVariableName,
            MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (Object filtredEntity : filtredEntities) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance())) {
                break;
            }
            unitFilteringJobBean.execute(result, filtredEntity, scriptInterface, recordVariableName);
        }
        return new AsyncResult<String>("OK");
    }
}
