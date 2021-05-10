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

import com.google.common.collect.Maps;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BusinessEntity;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptInterface;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.meveo.admin.job.GenericWorkflowJob.GENERIC_WF;
import static org.meveo.admin.job.GenericWorkflowJob.IWF_ENTITY;
import static org.meveo.admin.job.GenericWorkflowJob.WF_ACTUAL_TRANSITION;
import static org.meveo.admin.job.GenericWorkflowJob.WF_INS;

@Stateless
public class GWFTransitionService extends PersistenceService<GWFTransition> {
    
    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private BusinessEntityService businessEntityService;

    @Inject
    private WorkflowInstanceHistoryService workflowInstanceHistoryService;
    
    @Inject
    private WFStatusService wfStatusService;
    
    @Inject
    private WorkflowInstanceService workflowInstanceService;

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
    
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public WorkflowInstance executeTransition(GWFTransition transition, BusinessEntity entity,
            WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow) {

        // refresh
        workflowInstance = workflowInstanceService.refreshOrRetrieve(workflowInstance);

        log.debug("Processing transition: {} on entity {}", transition, workflowInstance);
        if (genericWorkflow.isEnableHistory()) {
            WorkflowInstanceHistory workflowInstanceHistory = processTransition(workflowInstance, transition);
            workflowInstanceHistoryService.create(workflowInstanceHistory);
        }
        if (transition.getActionScript() != null) {
            executeActionScript(entity, workflowInstance, genericWorkflow, transition);
        }
        log.trace("Entity status will be updated to {}. Entity {}", workflowInstance, transition.getToStatus());
        WFStatus toStatus = wfStatusService.findByCodeAndGWF(transition.getToStatus(), genericWorkflow);
        workflowInstance.setCurrentStatus(toStatus);
        workflowInstance = workflowInstanceService.update(workflowInstance);
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
    
    public void executeActionScript(BusinessEntity iwfEntity, WorkflowInstance workflowInstance, GenericWorkflow genericWorkflow, GWFTransition gWFTransition) {
        ScriptInstance scriptInstance = gWFTransition.getActionScript();
        String scriptCode = scriptInstance.getCode();
        ScriptInterface script = scriptInstanceService.getScriptInstance(scriptCode);

        //refresh entity  if it was updated by previous transition
        businessEntityService.setEntityClass((Class<BusinessEntity>) ReflectionUtils.getCleanClass(iwfEntity.getClass()));
        iwfEntity = businessEntityService.refreshOrRetrieve(iwfEntity);

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
}
