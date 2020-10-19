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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

import static org.meveo.admin.job.GenericWorkflowJob.*;

@Stateless
public class GenericWorkflowService extends BusinessService<GenericWorkflow> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private GWFTransitionService gWFTransitionService;

    @Inject
    private WFStatusService wfStatusService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    @Inject
    private WorkflowInstanceHistoryService workflowInstanceHistoryService;

    static Set<Class<?>> WORKFLOWED_CLASSES = ReflectionUtils.getClassesAnnotatedWith(WorkflowedEntity.class, "org.meveo");

    public List<Class<?>> getAllWorkflowedClazz() {
        List<Class<?>> result = new ArrayList<>(WORKFLOWED_CLASSES);
        return result;
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
        List<GenericWorkflow> genericWorkflows = (List<GenericWorkflow>) getEntityManager().createNamedQuery("GenericWorkflow.findByTargetEntityClass", GenericWorkflow.class)
                .setParameter("targetEntityClass", targetEntityClass).getResultList();
        return genericWorkflows;
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
     * @param workflowInstance
     * @param genericWorkflow
     * @return
     * @throws BusinessException
     */
    public WorkflowInstance executeWorkflow(BusinessEntity iwfEntity, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) throws BusinessException {
        log.debug("Executing generic workflow script:{} on instance {}", genericWorkflow.getCode(), workflowInstance);
        try {

            WFStatus currentWFStatus = workflowInstance.getCurrentStatus();
            String currentStatus = currentWFStatus != null ? currentWFStatus.getCode() : null;
            log.trace("Actual status: {}", currentStatus);

            int endIndex = genericWorkflow.getTransitions().size();
            if(!genericWorkflow.getTransitions().get(endIndex-1).getToStatus().equalsIgnoreCase(currentStatus)) {
                int startIndex = IntStream.range(0, endIndex).filter(idx -> genericWorkflow.getTransitions().get(idx).getFromStatus().equals(currentStatus)).findFirst().getAsInt();
                List<GWFTransition> listByFromStatus = genericWorkflow.getTransitions().stream().collect(Collectors.toList()).subList(startIndex, endIndex);
                List<GWFTransition> executedTransition = getExecutedTransitions(genericWorkflow, workflowInstance, listByFromStatus);

                for (GWFTransition gWFTransition : listByFromStatus) {

                    if (matchExpression(gWFTransition.getConditionEl(), iwfEntity) && isInSameBranch(gWFTransition, executedTransition, genericWorkflow)) {

                        log.debug("Processing transition: {} on entity {}", gWFTransition, workflowInstance);
                        WorkflowInstanceHistory wfHistory = new WorkflowInstanceHistory();
                        if (genericWorkflow.isEnableHistory()) {
                            wfHistory.setActionDate(new Date());
                            wfHistory.setWorkflowInstance(workflowInstance);
                            wfHistory.setFromStatus(gWFTransition.getFromStatus());
                            wfHistory.setToStatus(gWFTransition.getToStatus());
                            wfHistory.setTransitionName(gWFTransition.getDescription());
                            wfHistory.setWorkflowInstance(workflowInstance);

                            workflowInstanceHistoryService.create(wfHistory);
                        }

                        WFStatus toStatus = wfStatusService.findByCodeAndGWF(gWFTransition.getToStatus(), genericWorkflow);

                        if (gWFTransition.getActionScript() != null) {
                            ScriptInstance scriptInstance = gWFTransition.getActionScript();
                            String scriptCode = scriptInstance.getCode();
                            ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
                            Map<String, Object> methodContext = new HashMap<String, Object>();
                            methodContext.put(GENERIC_WF, genericWorkflow);
                            methodContext.put(WF_INS, workflowInstance);
                            methodContext.put(IWF_ENTITY, iwfEntity);
                            methodContext.put(Script.CONTEXT_ACTION, scriptCode);
                            methodContext.put(TO_STATUS, toStatus);
                            if (script == null) {
                                log.error("Script is null");
                                throw new BusinessException("script is null");
                            }
                            script.execute(methodContext);
                        }


                        workflowInstance.setCurrentStatus(toStatus);

                        log.trace("Entity status will be updated to {}. Entity {}", workflowInstance, gWFTransition.getToStatus());
                        workflowInstance = workflowInstanceService.update(workflowInstance);
                        executedTransition.add(gWFTransition);

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
}
