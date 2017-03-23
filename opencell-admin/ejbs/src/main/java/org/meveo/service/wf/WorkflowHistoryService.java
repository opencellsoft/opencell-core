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
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.wf.Workflow;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.service.base.PersistenceService;

@Stateless
public class WorkflowHistoryService extends PersistenceService<WorkflowHistory> {
		
	@SuppressWarnings("unchecked")
	public List<WorkflowHistory> findByEntityCode(String entityInstanceCode, List<Workflow> workflows) {

		String queryStr = "from " + WorkflowHistory.class.getSimpleName() + " where entityInstanceCode=:entityInstanceCode ";

		if (workflows != null && !workflows.isEmpty()) {
			queryStr += " and workflow in (:workflows)";
		}

		Query query = getEntityManager().createQuery(queryStr).setParameter("entityInstanceCode", entityInstanceCode);
		if (workflows != null && !workflows.isEmpty()) {
			query = query.setParameter("workflows", workflows);
		}
		return (List<WorkflowHistory>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowHistory> find(String entityInstanceCode, String workflowCode, String fromStatus, String toStatus) {
				
		QueryBuilder queryBuilder = new QueryBuilder(WorkflowHistory.class, "wfh");	
		if(!StringUtils.isBlank(entityInstanceCode)){
			queryBuilder.addCriterion("wfh.entityInstanceCode", "=", entityInstanceCode, true);
		}
		if(!StringUtils.isBlank(workflowCode)){
			queryBuilder.addCriterion("wfh.workflowCode.code", "=", workflowCode, true);
		}
		if(!StringUtils.isBlank(fromStatus)){
			queryBuilder.addCriterion("wfh.fromStatus", "=", fromStatus, true);
		}	
		if(!StringUtils.isBlank(toStatus)){
			queryBuilder.addCriterion("wfh.toStatus", "=", toStatus, true);
		}		
				
		try {
			return (List<WorkflowHistory>) queryBuilder.getQuery(getEntityManager()).getResultList();
		} catch (Exception e) {
			return null;
		}
				
	}

}
