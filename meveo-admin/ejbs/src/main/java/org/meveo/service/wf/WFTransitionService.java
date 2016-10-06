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

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.model.crm.Provider;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WFTransitionService extends PersistenceService<WFTransition> {
	
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

    public WFTransition findWFTransitionByUUID(String uuid, Provider provider) {
        WFTransition wfTransition = null;
        try {
            wfTransition = (WFTransition) getEntityManager()
                    .createQuery(
                            "from "
                                    + WFTransition.class
                                    .getSimpleName()
                                    + " where uuid=:uuid and provider=:provider")
                    .setParameter("uuid", uuid)
                    .setParameter("provider", provider)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.error("failed to find WFTransition", e);
        }
        return wfTransition;
    }

    public List<WFTransition> listWFTransitionByStatusWorkFlow(String fromStatus, String toStatus, Workflow workflow, Provider provider){
        List<WFTransition> wfTransitions =  (List<WFTransition>) getEntityManager()
                .createQuery(
                        "from "
                                + WFTransition.class
                                .getSimpleName()
                                + " where fromStatus=:fromStatus and toStatus=:toStatus and workflow=:workflow and provider=:provider order by priority ASC")
                .setParameter("fromStatus", fromStatus)
                .setParameter("toStatus", toStatus)
                .setParameter("workflow", workflow)
                .setParameter("provider", provider)
                .getResultList();
        return wfTransitions;
    }

}
