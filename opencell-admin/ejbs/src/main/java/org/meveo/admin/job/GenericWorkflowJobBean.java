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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.filter.Filter;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.filter.FilterService;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.meveo.service.generic.wf.WorkflowInstanceService;
import org.meveo.service.job.JobExecutionService.JobSpeedEnum;

/**
 * Job implementation to execute the transition script on each workflowed entity instance.
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class GenericWorkflowJobBean extends IteratorBasedJobBean<Object[]> {

    private static final long serialVersionUID = -360953605862140212L;

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    @Inject
    private FilterService filterService;

    /**
     * Workflow to run - - job execution parameter
     */
    private GenericWorkflow genericWf;

    @Override
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl jobExecutionResult, JobInstance jobInstance) {
        super.execute(jobExecutionResult, jobInstance, this::initJobAndGetDataToProcess, this::executeWorkflow, null, null, JobSpeedEnum.NORMAL);
        genericWf = null;
    }

    /**
     * Initialize job settings and retrieve data to process
     * 
     * @param jobExecutionResult Job execution result
     * @return An iterator over a list of entities to execute workflow on
     */
    @SuppressWarnings("unchecked")
    private Optional<Iterator<Object[]>> initJobAndGetDataToProcess(JobExecutionResultImpl jobExecutionResult) {

        JobInstance jobInstance = jobExecutionResult.getJobInstance();

        String genericWfCode = null;
        EntityReferenceWrapper wfReference = (EntityReferenceWrapper) this.getParamOrCFValue(jobInstance, "gwfJob_generic_wf");
        if (wfReference != null) {
            genericWfCode = wfReference.getCode();
            genericWf = genericWorkflowService.findByCode(genericWfCode);
            if (genericWf == null) {
                jobExecutionResult.addErrorReport("No active workflow found with code " + genericWfCode);
                return Optional.empty();
            }

            if (!genericWf.isActive()) {
                jobExecutionResult.addErrorReport("The workflow " + genericWfCode + " is disabled");
                return Optional.empty();
            }

        } else {
            jobExecutionResult.addErrorReport("No workflow referenced from the job instance");
            return Optional.empty();
        }

        // Create wf instances for entities without WF
        List<BusinessEntity> entitiesWithoutWFInstance = workflowInstanceService.findEntitiesForWorkflow(genericWf, true);
        for (BusinessEntity entity : entitiesWithoutWFInstance) {
            workflowInstanceService.create(entity, genericWf);
        }

        Filter wfFilter = genericWf.getFilter();
        Filter filter = null;
        if (wfFilter != null) {
            filter = filterService.findById(wfFilter.getId());
        }

        List<BusinessEntity> entities = null;
        if (filter != null) {
            entities = (List<BusinessEntity>) filterService.filteredListAsObjects(filter, null);
        } else {
            entities = workflowInstanceService.findEntitiesForWorkflow(genericWf, false);
        }

        List<WorkflowInstance> wfInstances = genericWorkflowService.findByCode(genericWfCode, Arrays.asList("wfInstances")).getWfInstances();

        if (genericWf.getId() != null) {
            genericWf = genericWorkflowService.refreshOrRetrieve(genericWf);
        }

        Map<Long, BusinessEntity> mapFilteredEntities = entities.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));

        List<Object[]> wfInstancesFiltered = new ArrayList<Object[]>();
        for (WorkflowInstance workflowInstance : wfInstances) {
            Long entityInstanceId = workflowInstance.getEntityInstanceId();
            if (entityInstanceId != null) {
                BusinessEntity businessEntity = mapFilteredEntities.get(entityInstanceId);
                if (businessEntity != null) {
                    wfInstancesFiltered.add(new Object[] { businessEntity, workflowInstance });
                }
            }
        }

        return Optional.of(new SynchronizedIterator<Object[]>(wfInstancesFiltered));
    }

    /**
     * Execute workflow
     * 
     * @param workflowInfo An array consisting of business entity and a workflow instance to execute on an entity
     * @param jobExecutionResult Job execution result
     */
    private void executeWorkflow(Object[] workflowInfo, JobExecutionResultImpl jobExecutionResult) {

        BusinessEntity be = (BusinessEntity) workflowInfo[0];
        WorkflowInstance workflowInstance = (WorkflowInstance) workflowInfo[1];

        genericWorkflowService.executeWorkflow(be, workflowInstance, genericWf);
    }
}