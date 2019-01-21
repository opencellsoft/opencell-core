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

import org.meveo.admin.job.UnitGenericWorkflowJobBean;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.job.JobExecutionService;

@Stateless
public class GenericWorkflowAsync {

    @Inject
    UnitGenericWorkflowJobBean unitGenericWorkflowJobBean;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private CurrentUserProvider currentUserProvider;

    /**
     * 
     * @param wfInstances
     * @param genericWorkflow
     * @param result
     * @param lastCurrentUser
     * @return
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Future<String> launchAndForget(List<WorkflowInstance> wfInstances, GenericWorkflow genericWorkflow, JobExecutionResultImpl result, MeveoUser lastCurrentUser) {

        currentUserProvider.reestablishAuthentication(lastCurrentUser);

        for (WorkflowInstance workflowInstance : wfInstances) {
            if (!jobExecutionService.isJobRunningOnThis(result.getJobInstance().getId())) {
                break;
            }
            unitGenericWorkflowJobBean.execute(result, workflowInstance, genericWorkflow);
        }
        return new AsyncResult<String>("OK");
    }
}
