/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.generic.wf;

import static org.meveo.admin.job.GenericWorkflowJob.GENERIC_WF;
import static org.meveo.admin.job.GenericWorkflowJob.IWF_ENTITY;
import static org.meveo.admin.job.GenericWorkflowJob.WF_INS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IWFEntity;
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

        WorkflowInstance workflowInstance = workflowInstanceService.findByCodeAndGenericWorkflow(businessEntity.getCode(), genericWorkflow);

        if (workflowInstance == null) {
            throw new BusinessException("No workflow instance for business entity " + businessEntity.getCode());
        }

        return executeWorkflow(workflowInstance, genericWorkflow);
    }

    /**
     * Execute workflow for wf instance
     * 
     * @param workflowInstance
     * @param genericWorkflow
     * @return
     * @throws BusinessException
     */
    public WorkflowInstance executeWorkflow(WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) throws BusinessException {
        log.debug("Executing generic workflow script:{} on instance {}", genericWorkflow.getCode(), workflowInstance);
        try {

            IWFEntity iwfEntity = (IWFEntity) workflowInstanceService.getBusinessEntity(workflowInstance);
            WFStatus currentWFStatus = workflowInstance.getCurrentStatus();
            String currentStatus = currentWFStatus != null ? currentWFStatus.getCode() : null;
            log.trace("Actual status: {}", currentStatus);
            List<GWFTransition> listByFromStatus = gWFTransitionService.listByFromStatus(currentStatus, genericWorkflow);

            for (GWFTransition gWFTransition : listByFromStatus) {

                if (matchExpression(gWFTransition.getConditionEl(), iwfEntity)) {

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

                    if (gWFTransition.getActionScript() != null) {
                        ScriptInstance scriptInstance = gWFTransition.getActionScript();
                        String scriptCode = scriptInstance.getCode();
                        ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);
                        Map<String, Object> methodContext = new HashMap<String, Object>();
                        methodContext.put(GENERIC_WF, genericWorkflow);
                        methodContext.put(WF_INS, workflowInstance);
                        methodContext.put(IWF_ENTITY, iwfEntity);
                        methodContext.put(Script.CONTEXT_ACTION, scriptCode);
                        if (script == null) {
                            log.error("Script is null");
                            throw new BusinessException("script is null");
                        }
                        script.execute(methodContext);
                    }

                    WFStatus toStatus = wfStatusService.findByCodeAndGWF(gWFTransition.getToStatus(), genericWorkflow);
                    workflowInstance.setCurrentStatus(toStatus);

                    log.trace("Entity status will be updated to {}. Entity {}", workflowInstance, gWFTransition.getToStatus());
                    workflowInstance = workflowInstanceService.update(workflowInstance);
                    return workflowInstance;
                }
            }
        } catch (Exception e) {
            log.error("Failed to execute generic workflow {} on {}", genericWorkflow.getCode(), workflowInstance, e);
            throw new BusinessException(e);
        }

        return workflowInstance;
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
}
