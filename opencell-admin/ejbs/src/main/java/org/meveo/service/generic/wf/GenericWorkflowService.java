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
package org.meveo.service.generic.wf;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Optional.ofNullable;

@Stateless
public class GenericWorkflowService extends BusinessService<GenericWorkflow> {

    @EJB
    private GWFTransitionService gWFTransitionService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    @Inject
    private WorkflowInstanceHistoryService workflowInstanceHistoryService;

    static Set<Class<?>> workflowedClasses = ReflectionUtils.getClassesAnnotatedWith(WorkflowedEntity.class, "org.meveo");

    public List<Class<?>> getAllWorkflowedClazz() {
        return new ArrayList<>(workflowedClasses);
    }

    public List<GenericWorkflow> findByBusinessEntity(BusinessEntity entity) {
        return list().stream().filter(g -> {

            String targetQualifiedName = g.getTargetEntityClass();
            Class<?> targetClazz = null;
            try {
                targetClazz = Class.forName(targetQualifiedName);
            } catch (ClassNotFoundException e) {
                return false;
            }

            if (entity instanceof CustomEntityInstance) {
                CustomEntityInstance cei = (CustomEntityInstance) entity;
                return targetClazz.isInstance(entity) && cei.getCetCode().equals(g.getTargetCetCode());
            }

            return targetClazz.isInstance(entity);
        }).collect(Collectors.toList());
    }

    public List<GenericWorkflow> findByTargetEntityClass(String targetEntityClass) {
        return getEntityManager().createNamedQuery("GenericWorkflow.findByTargetEntityClass", GenericWorkflow.class)
                .setParameter("targetEntityClass", targetEntityClass).getResultList();
    }

    /**
     * Execute workflow for Business Entity
     *
     * @param businessEntity
     * @param genericWorkflow
     * @return
     * @throws BusinessException
     */
    public WorkflowInstance executeWorkflow(BusinessEntity businessEntity, GenericWorkflow genericWorkflow) throws BusinessException {

        WorkflowInstance workflowInstance = workflowInstanceService.findByEntityIdAndGenericWorkflow(businessEntity.getId(), genericWorkflow);

        if (workflowInstance == null) {
            throw new BusinessException("No workflow instance for business entity " + businessEntity.getId());
        }

        return executeWorkflow(businessEntity, workflowInstance, genericWorkflow);
    }

    /**
     * Execute workflow for wf instance
     *
     * @param workflowInstance a workflow instance
     * @param genericWorkflow  a generic workflow
     * @return workflowInstance an updated workflow instance
     * @throws BusinessException
     */

    public WorkflowInstance executeWorkflow(BusinessEntity iwfEntity, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) throws BusinessException {
        log.debug("Executing generic workflow script:{} on instance {}", genericWorkflow.getCode(), workflowInstance);
        try {

            WFStatus currentWFStatus = workflowInstance.getCurrentStatus();
            String currentStatus = currentWFStatus != null ? currentWFStatus.getCode() : null;
            log.trace("Actual status: {}", currentStatus);

            int endIndex = genericWorkflow.getTransitions().size();
            if (!genericWorkflow.getTransitions().get(endIndex - 1).getToStatus().equalsIgnoreCase(currentStatus)) {
                int startIndex = IntStream.range(0, endIndex).filter(idx -> genericWorkflow.getTransitions().get(idx).getFromStatus().equals(currentStatus)).findFirst().getAsInt();
                List<GWFTransition> listByFromStatus = genericWorkflow.getTransitions().subList(startIndex, endIndex);
                List<GWFTransition> executedTransition = getExecutedTransitions(genericWorkflow, workflowInstance, listByFromStatus);

                for (GWFTransition gWFTransition : listByFromStatus) {
                    if (matchExpression(gWFTransition.getConditionEl(), iwfEntity) && isInSameBranch(gWFTransition, executedTransition, genericWorkflow)) {
                        workflowInstance = gWFTransitionService.executeTransition(gWFTransition, iwfEntity, workflowInstance, genericWorkflow);
                        executedTransition.add(gWFTransition);
                        executeWorkflow(iwfEntity, workflowInstance, genericWorkflow);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            log.error("Failed to execute generic workflow {} on {}", genericWorkflow.getCode(), workflowInstance, e);
            throw new BusinessException(e);
        }

        return workflowInstance;
    }

    private List<GWFTransition> getExecutedTransitions(GenericWorkflow genericWorkflow, WorkflowInstance workflowInstance, List<GWFTransition> listByFromStatus) {
        List<GWFTransition> executedTransition = new ArrayList<>();
        List<GWFTransition> transitions = new ArrayList<>(genericWorkflow.getTransitions());
        transitions.removeAll(listByFromStatus);
        List<WorkflowInstanceHistory> wfHistory = workflowInstanceHistoryService.findByWorkflowInstance(workflowInstance);
        for (GWFTransition transition : transitions) {
            for (WorkflowInstanceHistory history : wfHistory) {
                if (transition.getToStatus().equals(history.getToStatus())) {
                    executedTransition.add(transition);
                }
            }
        }
        return executedTransition;
    }

    private boolean isInSameBranch(GWFTransition currentTransition, List<GWFTransition> executedTransition, GenericWorkflow genericWorkflow) {
        if (executedTransition.isEmpty() && genericWorkflow.getInitStatus().equals(currentTransition.getFromStatus())) {
            return true;
        }
        List<GWFTransition> previousTransitons = getPreviousTransitions(currentTransition, genericWorkflow.getTransitions());
        if (previousTransitons == null || previousTransitons.isEmpty()) {
            return true;
        }
        if (!containsAtLeastOne(executedTransition, previousTransitons)) {
            return false;
        }
        for (GWFTransition transition : executedTransition) {
            if (transition.getToStatus() != null && transition.getToStatus().equals(currentTransition.getFromStatus())) {
                return true;
            }
        }
        return true;
    }

    private List<GWFTransition> getPreviousTransitions(GWFTransition currentTransition, List<GWFTransition> transitions) {
        return transitions.stream().filter(transition -> {
            if (transition.getToStatus() == null) {
                return false;
            } else {
                return transition.getToStatus().equals(currentTransition.getFromStatus());
            }
        }).collect(Collectors.toList());
    }

    private boolean matchExpression(String expression, Object object) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return true;
        }
        Map<Object, Object> userMap = new HashMap<>();
        if (expression.indexOf("entity") >= 0) {
            userMap.put("entity", object);
        }

        return ValueExpressionWrapper.evaluateToBooleanOneVariable(expression, "entity", object);
    }

    public boolean containsAtLeastOne(List<GWFTransition> executedTransition, List<GWFTransition> previousTransitions) {

        previousTransitions.retainAll(executedTransition);
        return 0 < previousTransitions.size();
    }

    public WorkflowInstance executeTransition(GWFTransition transition, BusinessEntity entity, GenericWorkflow genericWorkflow, boolean ignoreConditionEL) {
        WorkflowInstance workflowInstance = ofNullable(workflowInstanceService.findByEntityIdAndGenericWorkflow(entity.getId(), genericWorkflow))
                .orElseThrow(() -> new BusinessException("No workflow instance found for business entity " + entity.getId()));
        if (ignoreConditionEL) {
            return gWFTransitionService.executeTransition(transition, entity, workflowInstance, genericWorkflow);
        } else {
            return executeTransitionWithConditionEL(transition, entity, workflowInstance, genericWorkflow);
        }
    }

    public WorkflowInstance executeTransitionWithConditionEL(GWFTransition transition, BusinessEntity entity, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) {
        if (matchExpression(transition.getConditionEl(), entity)) {
            return gWFTransitionService.executeTransition(transition, entity, workflowInstance, genericWorkflow);
        } else {
            return null;
        }
    }
}