/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.job;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.BusinessEntity;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@Stateless
public class UnitGenericWorkflowJobBean {

    @Inject
    private Logger log;

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, BusinessEntity be, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) {
        try {
            genericWorkflowService.executeWorkflow(be, workflowInstance, genericWorkflow);
            result.registerSucces();
        } catch (Exception e) {
            log.error("Failed to unit generic workflow for {}", workflowInstance, e);
            result.registerError(workflowInstance.getClass().getName() + workflowInstance.getId(), e.getMessage());
        }
    }

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeWithLoop(JobExecutionResultImpl result, BusinessEntity be, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) {
        try {
            String oldStatusCode = null;
            while (true) {
                int endIndex = genericWorkflow.getTransitions().size();
                if (!genericWorkflow.getTransitions().get(endIndex - 1).getToStatus().equalsIgnoreCase(oldStatusCode)) {
                    workflowInstance = genericWorkflowService.executeWorkflow(be, workflowInstance, genericWorkflow);
                    WFStatus currentWFStatus = workflowInstance.getCurrentStatus();
                    String currentStatusCode = currentWFStatus != null ? currentWFStatus.getCode() : null;
                    if (currentStatusCode != null && !currentStatusCode.equals(oldStatusCode)) {
                        oldStatusCode = currentStatusCode;
                    } else
                        break;
                } else
                    break;
            }
            result.registerSucces();
        } catch (Exception e) {
            log.error("Failed to unit generic workflow for {}", workflowInstance, e);
            result.registerError(workflowInstance.getClass().getName() + workflowInstance.getId(), e.getMessage());
        }
    }
}