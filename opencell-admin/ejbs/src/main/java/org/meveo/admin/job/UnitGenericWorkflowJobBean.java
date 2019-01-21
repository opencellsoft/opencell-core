package org.meveo.admin.job;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.slf4j.Logger;

@Stateless
public class UnitGenericWorkflowJobBean {

    @Inject
    private Logger log;

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @JpaAmpNewTx
    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) {
        try {
            genericWorkflowService.executeTransitionScript(workflowInstance, genericWorkflow);
            result.registerSucces();
        } catch (Exception e) {
            log.error("Failed to unit transition script for {}", workflowInstance, e);
            result.registerError(workflowInstance.getClass().getName() + workflowInstance.getId(), e.getMessage());
        }
    }
}