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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GWFTransitionAction;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.notification.DefaultNotificationService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

import com.google.common.collect.Maps;

@Stateless
public class GWFTransitionService extends PersistenceService<GWFTransition> {
    
    @Inject
    private ScriptInstanceService scriptInstanceService;
    
    @Inject
    private WorkflowInstanceHistoryService workflowInstanceHistoryService;
    
    @Inject
    private WFStatusService wfStatusService;
    
    @Inject
    private WorkflowInstanceService workflowInstanceService;
    
    @Inject
    private DefaultNotificationService defaultNotificationService;

    public List<GWFTransition> listByFromStatus(String fromStatus, GenericWorkflow genericWorkflow) {

        Map<String, Object> params = Maps.newHashMap();
        String criteria;

        if (StringUtils.isBlank(fromStatus)) {
            criteria = "fromStatus is null";
        } else {
            criteria = "fromStatus=:fromStatusValue";
            params.put("fromStatusValue", fromStatus);
        }

        String query = "From GWFTransition where " + criteria + " and genericWorkflow=:genericWorkflowValue order by priority ASC";

        params.put("genericWorkflowValue", genericWorkflow);

        return (List<GWFTransition>) executeSelectQuery(query, params);
    }

    public synchronized GWFTransition duplicate(GWFTransition entity, GenericWorkflow genericWorkflow) throws BusinessException {
        entity = refreshOrRetrieve(entity);

        if (genericWorkflow != null) {
            entity.setGenericWorkflow(genericWorkflow);
        }

        // Detach and clear ids of entity and related entities
        detach(entity);
        entity.setId(null);
        entity.clearUuid();

        create(entity);

        if (genericWorkflow != null) {
            genericWorkflow.getTransitions().add(entity);
        }

        entity = update(entity);

        return entity;
    }

    public GWFTransition findWFTransitionByUUID(String uuid) {
        GWFTransition gwfTransition = null;
        try {
            gwfTransition = (GWFTransition) getEntityManager().createQuery("from " + GWFTransition.class.getSimpleName() + " where uuid=:uuid").setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return gwfTransition;
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
    
    public void executeActions(BusinessEntity entity, WorkflowInstance workflowInstance,
            GenericWorkflow genericWorkflow, GWFTransition transition) {
        Map<Object, Object> context = new HashMap<>();
        context.put("entity", entity);
        context.put("transition", transition);
        context.put("workflowInstance ", workflowInstance);

        for (GWFTransitionAction action : transition.getActions()) {
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
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WorkflowInstance executeTransition(GWFTransition transition, BusinessEntity entity,
            WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) {

        if (genericWorkflow.isEnableHistory()) {
            WorkflowInstanceHistory workflowInstanceHistory = processTransition(workflowInstance, transition);
            workflowInstanceHistoryService.create(workflowInstanceHistory);
        }

        if(transition.getActions() != null && !transition.getActions().isEmpty()) {
            executeActions(entity, workflowInstance, genericWorkflow, transition);
        }
        WFStatus toStatus = wfStatusService.findByCodeAndGWF(transition.getToStatus(), genericWorkflow);
        workflowInstance.setCurrentStatus(toStatus);
        workflowInstance = workflowInstanceService.update(workflowInstance);
        return workflowInstance;
    }

    @Asynchronous
    private void asyncExecution(BusinessEntity entity, WorkflowInstance workflowInstance,
            GenericWorkflow genericWorkflow, GWFTransition transition, GWFTransitionAction action,
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
            GWFTransition transition, GWFTransitionAction action, Map<Object, Object> context) {

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
            log.error("error = {}", e);
        }
    }
}
