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
