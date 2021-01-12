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

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.meveo.admin.job.GenericWorkflowJob.GENERIC_WF;
import static org.meveo.admin.job.GenericWorkflowJob.IWF_ENTITY;
import static org.meveo.admin.job.GenericWorkflowJob.WF_ACTUAL_TRANSITION;
import static org.meveo.admin.job.GenericWorkflowJob.WF_INS;
import static org.meveo.api.dto.generic.wf.ActionTypesEnum.ACTION_SCRIPT;
import static org.meveo.api.dto.generic.wf.ActionTypesEnum.LOG;
import static org.meveo.api.dto.generic.wf.ActionTypesEnum.NOTIFICATION;
import static org.meveo.api.dto.generic.wf.ActionTypesEnum.UPDATE_FIELD;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.generic.wf.Action;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.notification.DefaultNotificationService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

@Stateless
public class GenericWorkflowService extends BusinessService<GenericWorkflow> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private WFStatusService wfStatusService;

    @Inject
    private WorkflowInstanceService workflowInstanceService;

    @Inject
    private WorkflowInstanceHistoryService workflowInstanceHistoryService;

    @Inject
    private DefaultNotificationService defaultNotificationService;

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

			log.trace(" genericWorkflow.getTransitions(): {}", genericWorkflow.getTransitions());

			List<GWFTransition> listByFromStatus = genericWorkflow.getTransitions().stream()
					.filter(transition -> (transition.getFromStatus() == null || currentStatus.equals(transition.getFromStatus()))).collect(Collectors.toList());

			log.trace("listByFromStatus: {}", listByFromStatus);

			List<GWFTransition> executedTransition = getExecutedTransitions(genericWorkflow, workflowInstance, listByFromStatus);
			log.trace("executedTransition: {}", executedTransition);

			for (GWFTransition gWFTransition : listByFromStatus) {

				if (matchExpression(gWFTransition.getConditionEl(), iwfEntity) && isInSameBranch(gWFTransition, executedTransition, genericWorkflow)) {
					log.debug("Processing transition: {} on entity {}", gWFTransition, workflowInstance);
					WorkflowInstanceHistory wfHistory;
					if (genericWorkflow.isEnableHistory()) {
						wfHistory = processTransition(workflowInstance, gWFTransition);
						workflowInstanceHistoryService.create(wfHistory);
					}

					if (gWFTransition.getActionScript() != null) {
						ScriptInstance scriptInstance = gWFTransition.getActionScript();
						String scriptCode = scriptInstance.getCode();
						executeActionScript(iwfEntity, workflowInstance, genericWorkflow, gWFTransition, scriptCode);
					}

					WFStatus toStatus = wfStatusService.findByCodeAndGWF(gWFTransition.getToStatus(), genericWorkflow);
					workflowInstance.setCurrentStatus(toStatus);

					log.trace("Entity status will be updated to {}. Entity {}", workflowInstance, gWFTransition.getToStatus());
					workflowInstance = workflowInstanceService.update(workflowInstance);
					executedTransition.add(gWFTransition);
				}
			}

		} catch (Exception e) {
			log.error("Failed to execute generic workflow {} on {}", genericWorkflow.getCode(), workflowInstance, e);
			throw new BusinessException(e);
		}

		return workflowInstance;
	}


    private WorkflowInstanceHistory processTransition(WorkflowInstance workflowInstance, GWFTransition gWFTransition) {
        WorkflowInstanceHistory wfHistory = new WorkflowInstanceHistory();
        wfHistory.setActionDate(new Date());
        wfHistory.setWorkflowInstance(workflowInstance);
        wfHistory.setFromStatus(gWFTransition.getFromStatus());
        wfHistory.setToStatus(gWFTransition.getToStatus());
        wfHistory.setTransitionName(gWFTransition.getDescription());
        wfHistory.setWorkflowInstance(workflowInstance);
        return wfHistory;
    }

    private void executeActionScript(BusinessEntity iwfEntity, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow,
                                     GWFTransition gWFTransition, String scriptCode) {
        ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> methodContext = new HashMap<>();
        methodContext.put(GENERIC_WF, genericWorkflow);
        methodContext.put(WF_INS, workflowInstance);
        methodContext.put(IWF_ENTITY, iwfEntity);
        methodContext.put(Script.CONTEXT_ACTION, scriptCode);
        methodContext.put(WF_ACTUAL_TRANSITION, gWFTransition);
        if (script == null) {
            log.error("Script is null");
            throw new BusinessException("script is null");
        }
        script.execute(methodContext);
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

    public WorkflowInstance executeTransition(GWFTransition transition, BusinessEntity entity,
                                              GenericWorkflow genericWorkflow, boolean ignoreConditionEL) {
        WorkflowInstance workflowInstance = ofNullable(workflowInstanceService
                .findByEntityIdAndGenericWorkflow(entity.getId(), genericWorkflow))
                .orElseThrow(() -> new BusinessException("No workflow instance found for business entity " + entity.getId()));
        if (ignoreConditionEL) {
            return executeTransition(transition, entity, workflowInstance, genericWorkflow);
        } else {
            return executeTransitionWithConditionEL(transition, entity, workflowInstance, genericWorkflow);
        }
    }

    public WorkflowInstance executeTransition(GWFTransition transition, BusinessEntity entity,
                                              WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) {

        if (genericWorkflow.isEnableHistory()) {
            WorkflowInstanceHistory workflowInstanceHistory = processTransition(workflowInstance, transition);
            workflowInstanceHistoryService.create(workflowInstanceHistory);
        }
        if (transition.getActionScript() != null) {
            ScriptInstance scriptInstance = transition.getActionScript();
            String scriptCode = scriptInstance.getCode();
            executeActionScript(entity, workflowInstance, genericWorkflow, transition, scriptCode);
        }
        if(transition.getActions() != null) {
            executeActions(entity, workflowInstance, genericWorkflow, transition);
        }
        WFStatus toStatus = wfStatusService.findByCodeAndGWF(transition.getToStatus(), genericWorkflow);
        workflowInstance.setCurrentStatus(toStatus);
        workflowInstance = workflowInstanceService.update(workflowInstance);
        return workflowInstance;
    }

    public WorkflowInstance executeTransitionWithConditionEL(GWFTransition transition, BusinessEntity entity,
                                                             WorkflowInstance workflowInstance,
                                                             GenericWorkflow genericWorkflow) {
        if (matchExpression(transition.getConditionEl(), entity)) {
            return executeTransition(transition, entity, workflowInstance, genericWorkflow);
        } else {
            return null;
        }
    }

    private void executeActions(BusinessEntity entity, WorkflowInstance workflowInstance,
                                GenericWorkflow genericWorkflow, GWFTransition transition) {
        Map<Object, Object> context = new HashMap<>();
        context.put("entity", entity);
        context.put("transition", transition);
        context.put("workflowInstance ", workflowInstance);

        for (Action action : transition.getActions()) {
            try {
                if(action.isAsynchronous()) {
                    asyncExecution(entity, workflowInstance, genericWorkflow, transition, action, context);
                } else {
                    syncExecution(entity, workflowInstance, genericWorkflow, transition, action, context);
                }
            } catch (Exception exception) {
                log.error(format("Action failed priority [%d] description : %s",
                        action.getPriority(), action.getDescription()));
                log.error(exception.getMessage());
            }
        }
    }

    @Asynchronous
    private void asyncExecution(BusinessEntity entity, WorkflowInstance workflowInstance,
                                           GenericWorkflow genericWorkflow, GWFTransition transition, Action action,
                                           Map<Object, Object> context) {
        try {
            if(action.getType().equalsIgnoreCase(ACTION_SCRIPT.name())) {
                ScriptInstance scriptInstance = action.getActionScript();
                String scriptCode = scriptInstance.getCode();
                executeActionScript(entity, workflowInstance, genericWorkflow, transition, scriptCode);
            }
            if(action.getType().equalsIgnoreCase(LOG.name())) {
                String inputToLog = evaluateExpression(action.getValueEL(), context, String.class);
                log(inputToLog, action.getLogLevel());
            }
            if(action.getType().equalsIgnoreCase(UPDATE_FIELD.name())) {
                Object result = evaluateExpression(action.getValueEL(), context, Object.class);
                updateEntity(entity, action.getFieldToUpdate().split("\\.")[1], result);
            }
            if (action.getType().equalsIgnoreCase(NOTIFICATION.name())) {
                Notification notification = action.getNotification();
                defaultNotificationService.fireNotificationAsync(notification, entity);
            }
        } catch (Exception exception) {
            throw exception;
        }
    }

    private void syncExecution(BusinessEntity entity, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow,
                                GWFTransition transition, Action action, Map<Object, Object> context) {

        if(action.getType().equalsIgnoreCase(ACTION_SCRIPT.name())) {
            ScriptInstance scriptInstance = action.getActionScript();
            String scriptCode = scriptInstance.getCode();
            executeActionScript(entity, workflowInstance, genericWorkflow, transition, scriptCode);
        }
        if(action.getType().equalsIgnoreCase(LOG.name())) {
            String inputToLog = evaluateExpression(action.getValueEL(), context, String.class);
            log(inputToLog, action.getLogLevel());
        }

        if(action.getType().equalsIgnoreCase(UPDATE_FIELD.name())) {
            Object result = evaluateExpression(action.getValueEL(), context, Object.class);
            updateEntity(entity, action.getFieldToUpdate().split("\\.")[1], result);
        }

        if (action.getType().equalsIgnoreCase(NOTIFICATION.name())) {
            Notification notification = action.getNotification();
            defaultNotificationService.fireNotification(notification, entity);
        }
    }

    private void log(String inputToLog, String level) {
        try {
            log.getClass().getMethod(level.toLowerCase(), String.class).invoke(log, inputToLog);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new BusinessException(exception);
        }
    }

    private void updateEntity(BusinessEntity entity, String fieldToUpdate, Object valueToSet) {
        try {
            String methodName = "set" + capitalize(fieldToUpdate);
            Class<?> current = entity.getClass();
            PersistenceService persistenceService = (PersistenceService) EjbUtils.getServiceInterface(entity.getClass());
            persistenceService.refreshOrRetrieve(entity);
            boolean update = false;
            do {
                try {
                    current.getMethod(methodName, current.getDeclaredField(fieldToUpdate).getType())
                            .invoke(entity, valueToSet);
                    update = true;
                } catch (NoSuchFieldException e) {
                    current = current.getSuperclass();
                }
            } while(current != BaseEntity.class && !update);
            if (!update) {
                throw new BusinessException("Filed does not exists");
            }
            persistenceService.update(entity);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}