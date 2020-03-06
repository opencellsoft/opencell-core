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
package org.meveo.service.wf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFDecisionRule;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WFTransitionService extends PersistenceService<WFTransition> {

    @Inject
    private WFActionService wfActionService;

    public List<WFTransition> listByFromStatus(String fromStatus, Workflow workflow) {
        if ("*".equals(fromStatus)) {
            return workflow.getTransitions();
        }
        List<WFTransition> wfTransitions = (List<WFTransition>) getEntityManager().createNamedQuery("WFTransition.listByFromStatus", WFTransition.class)
            .setParameter("fromStatusValue", fromStatus).setParameter("workflowValue", workflow).getResultList();
        return wfTransitions;
    }

    public WFTransition findWFTransitionByUUID(String uuid) {
        WFTransition wfTransition = null;
        try {
            wfTransition = (WFTransition) getEntityManager().createQuery("from " + WFTransition.class.getSimpleName() + " where uuid=:uuid ").setParameter("uuid", uuid)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return wfTransition;
    }

    @SuppressWarnings("unchecked")
    public List<WFTransition> listWFTransitionByStatusWorkFlow(String fromStatus, String toStatus, Workflow workflow) {
        List<WFTransition> wfTransitions = ((List<WFTransition>) getEntityManager()
            .createQuery("from " + WFTransition.class.getSimpleName() + " where fromStatus=:fromStatus and toStatus=:toStatus and workflow=:workflow order by priority ASC")
            .setParameter("fromStatus", fromStatus).setParameter("toStatus", toStatus).setParameter("workflow", workflow).getResultList());
        return wfTransitions;
    }

    public synchronized WFTransition duplicate(WFTransition entity, Workflow workflow) throws BusinessException {
        entity = refreshOrRetrieve(entity);

        if (workflow != null) {
            entity.setWorkflow(workflow);
        }

        entity.getWfActions().size();
        entity.getWfDecisionRules().size();

        // Detach and clear ids of entity and related entities
        detach(entity);
        entity.setId(null);
        entity.clearUuid();

        List<WFAction> wfActions = entity.getWfActions();
        entity.setWfActions(new ArrayList<WFAction>());

        Set<WFDecisionRule> wfDecisionRules = entity.getWfDecisionRules();
        entity.setWfDecisionRules(new HashSet<WFDecisionRule>());

        create(entity);
        
        if (workflow != null) {
            workflow.getTransitions().add(entity);
        }

        if (wfActions != null) {
            for (WFAction wfAction : wfActions) {
                wfActionService.detach(wfAction);
                wfAction.setId(null);
                wfAction.clearUuid();
                wfActionService.create(wfAction);

                entity.getWfActions().add(wfAction);
            }
        }

        if (wfDecisionRules != null) {
            for (WFDecisionRule wfDecisionRule : wfDecisionRules) {
                entity.getWfDecisionRules().add(wfDecisionRule);
            }
        }

        entity = update(entity);

        return entity;
    }

}
