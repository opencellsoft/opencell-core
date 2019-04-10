package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.async.GenericWorkflowAsync;
import org.meveo.admin.async.SubListCreator;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.meveo.service.generic.wf.WorkflowInstanceService;
import org.slf4j.Logger;

@Stateless
public class GenericWorkflowJobBean extends BaseJobBean {

    @Inject
    private Logger log;

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    @Inject
    private GenericWorkflowAsync genericWorkflowAsync;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    @SuppressWarnings("unchecked")
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Running with parameter={}", jobInstance.getParametres());

        try {
            Long nbRuns = new Long(1);
            Long waitingMillis = new Long(0);
            String genericWfCode = null;
            try {
                nbRuns = (Long) this.getParamOrCFValue(jobInstance, "gwfJob_nbRuns");
                waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "gwfJob_waitingMillis");
                if (nbRuns == -1) {
                    nbRuns = (long) Runtime.getRuntime().availableProcessors();
                }
                genericWfCode = ((EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "gwfJob_generic_wf")).getCode();
            } catch (Exception e) {
                log.warn("Cant get customFields for " + jobInstance.getJobTemplate(), e.getMessage());
                log.error("error:", e);
                nbRuns = new Long(1);
                waitingMillis = new Long(0);
            }

            GenericWorkflow genericWf = genericWorkflowService.findByCode(genericWfCode);
            if (genericWf == null) {
                throw new BusinessException(String.format("No Workflow found with code = [%s]", genericWfCode));
            }

            if (!genericWf.isActive()) {
                log.debug("The workflow " + genericWfCode + " is disabled, the job will exit.");
                return;
            }

            // Create wf instances
            List<BusinessEntity> entities = workflowInstanceService.findEntitiesWithoutWFInstance(genericWf);
            for (BusinessEntity entity : entities) {
                workflowInstanceService.create(entity, genericWf);
            }

            List<WorkflowInstance> wfInstances = genericWorkflowService.findByCode(genericWfCode, Arrays.asList("wfInstances")).getWfInstances();
            log.debug("wfInstances:" + wfInstances.size());
            result.setNbItemsToProcess(wfInstances.size());

            List<Future<String>> futures = new ArrayList<Future<String>>();
            SubListCreator subListCreator = new SubListCreator(wfInstances, nbRuns.intValue());
            log.debug("block to run:" + subListCreator.getBlocToRun());
            log.debug("nbThreads:" + nbRuns);

            MeveoUser lastCurrentUser = currentUser.unProxy();
            while (subListCreator.isHasNext()) {
                futures.add(genericWorkflowAsync.launchAndForget((List<WorkflowInstance>) subListCreator.getNextWorkSet(), genericWf, result, lastCurrentUser));

                if (subListCreator.isHasNext()) {
                    try {
                        Thread.sleep(waitingMillis.longValue());
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
            // Wait for all async methods to finish
            for (Future<String> future : futures) {
                try {
                    future.get();

                } catch (InterruptedException e) {
                    // It was cancelled from outside - no interest

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    result.registerError(cause.getMessage());
                    log.error("Failed to execute async method", cause);
                }
            }
        } catch (Exception e) {
            log.error("Failed to run generic workflow job", e);
            result.registerError(e.getMessage());
        }
    }
}
