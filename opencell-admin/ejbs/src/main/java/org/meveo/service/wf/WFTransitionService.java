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

	public List<WFTransition> listByFromStatus(String fromStatus ,Workflow workflow){
		if("*".equals(fromStatus)){
			return workflow.getTransitions();
		}
		 List<WFTransition> wfTransitions =  (List<WFTransition>) getEntityManager()
 				.createNamedQuery("WFTransition.listByFromStatus", WFTransition.class)
 				.setParameter("fromStatusValue", fromStatus)
 				.setParameter("workflowValue", workflow)
 				.getResultList();
		 return wfTransitions;
	}

    public WFTransition findWFTransitionByUUID(String uuid) {
        WFTransition wfTransition = null;
        try {
            wfTransition = (WFTransition) getEntityManager()
                    .createQuery(
                            "from "
                                    + WFTransition.class
                                    .getSimpleName()
                                    + " where uuid=:uuid ")
                    .setParameter("uuid", uuid)
                    
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error("failed to find WFTransition", e);
        }
        return wfTransition;
    }

    @SuppressWarnings("unchecked")
	public List<WFTransition> listWFTransitionByStatusWorkFlow(String fromStatus, String toStatus, Workflow workflow){
        List<WFTransition> wfTransitions =  ((List<WFTransition>) getEntityManager()
                .createQuery(
                        "from "
                                + WFTransition.class
                                .getSimpleName()
                                + " where fromStatus=:fromStatus and toStatus=:toStatus and workflow=:workflow order by priority ASC")
                .setParameter("fromStatus", fromStatus)
                .setParameter("toStatus", toStatus)
                .setParameter("workflow", workflow)
                
                .getResultList());
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
		
		workflow.getTransitions().add(entity);
		
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

		update(entity);

		return entity;
	}

}
