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

import org.meveo.admin.job.UnitWorkflowJobBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.wf.Workflow;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;

/**
 * @author anasseh
 * 
 */

@Stateless
public class WorkflowAsync {

    @Inject
    UnitWorkflowJobBean unitWorkflowJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * Execute workflow on a list of entities. One entity at a time in a separate transaction.
     * 
     * @param entities A list of entities
     * @param workflow Workflow to execute
     * @param result Job execution result
     * @param lastCurrentUser Current user. In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     *        expirations), current user might be lost, thus there is a need to reestablish.
     * @return Future String
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<BusinessEntity> entities, Workflow workflow, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (BusinessEntity entity : entities) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitWorkflowJobBean.execute(result, entity, workflow);
        }
        return new AsyncResult<String>("OK");
    }
}
