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

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.service.base.PersistenceService;

import com.google.common.collect.Maps;

@Stateless
public class WorkflowInstanceHistoryService extends PersistenceService<WorkflowInstanceHistory> {

    public List<WorkflowInstanceHistory> findByGenericWorkflow(GenericWorkflow genericWorkflow) {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstanceHistory where workflowInstance.genericWorkflow = :genericWorkflow order by actionDate desc";
        params.put("genericWorkflow", genericWorkflow);

        return (List<WorkflowInstanceHistory>) executeSelectQuery(query, params);
    }

    public List<WorkflowInstanceHistory> findByWorkflowInstance(WorkflowInstance workflowInstance) {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstanceHistory where workflowInstance = :workflowInstance order by actionDate desc";
        params.put("workflowInstance", workflowInstance);

        return (List<WorkflowInstanceHistory>) executeSelectQuery(query, params);
    }

    public List<WorkflowInstanceHistory> findByBusinessEntity(BusinessEntity entity) {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstanceHistory where workflowInstance.entityInstanceCode = :entityInstanceCode order by workflowInstance.genericWorkflow.code, actionDate desc";
        params.put("entityInstanceCode", entity.getCode());

        if (entity instanceof CustomEntityInstance) {
            query = "From WorkflowInstanceHistory where workflowInstance.entityInstanceCode = :entityInstanceCode and workflowInstance.targetCetCode = :targetCetCode order by workflowInstance.genericWorkflow.code, actionDate desc";
            params.put("targetCetCode", ((CustomEntityInstance) entity).getCetCode());
        }

        return (List<WorkflowInstanceHistory>) executeSelectQuery(query, params);
    }

    @SuppressWarnings("unchecked")
    public List<WorkflowInstanceHistory> find(String entityInstanceCode, String workflowCode, String fromStatus, String toStatus) {

        QueryBuilder queryBuilder = new QueryBuilder(WorkflowInstanceHistory.class, "wfih");
        if (!StringUtils.isBlank(entityInstanceCode)) {
            queryBuilder.addCriterion("wfih.workflowInstance.entityInstanceCode", "=", entityInstanceCode, true);
        }
        if (!StringUtils.isBlank(workflowCode)) {
            queryBuilder.addCriterion("wfih.workflowInstance.genericWorkflow.code", "=", workflowCode, true);
        }
        if (!StringUtils.isBlank(fromStatus)) {
            queryBuilder.addCriterion("wfih.fromStatus", "=", fromStatus, true);
        }
        if (!StringUtils.isBlank(toStatus)) {
            queryBuilder.addCriterion("wfih.toStatus", "=", toStatus, true);
        }

        try {
            return (List<WorkflowInstanceHistory>) queryBuilder.getQuery(getEntityManager()).getResultList();
        } catch (Exception e) {
            return null;
        }

    }
}
