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
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.service.base.PersistenceService;

import com.google.common.collect.Maps;

@Stateless
public class WorkflowInstanceService extends PersistenceService<WorkflowInstance> {

    @Inject
    private BusinessEntityService businessEntityService;

    @Inject
    private WFStatusService wfStatusService;

    public WorkflowInstance findByEntityIdAndGenericWorkflow(Long entityInstanceId, GenericWorkflow genericWorkflow) throws BusinessException {
        TypedQuery<WorkflowInstance> query = getEntityManager()
            .createQuery("select be from " + entityClass.getSimpleName() + " wi where wi.entityInstanceId = :entityInstanceId and wi.genericWorkflow = :genericWorkflow",
                WorkflowInstance.class)
            .setParameter("entityInstanceId", entityInstanceId).setParameter("genericWorkflow", genericWorkflow).setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of entity id {} found", entityClass.getSimpleName(), entityInstanceId);
            return null;
        }
    }

    public List<WorkflowInstance> findByEntityIdAndClazz(Long entityInstanceId, Class<?> clazz) {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstance wi where wi.entityInstanceId = :entityInstanceId and wi.targetEntityClass = :clazz";
        params.put("entityInstanceId", entityInstanceId);
        params.put("clazz", clazz.getName());

        return (List<WorkflowInstance>) executeSelectQuery(query, params);
    }

    public BusinessEntity getBusinessEntity(WorkflowInstance workflowInstance) throws BusinessException {

        BusinessEntity businessEntity = null;
        try {
            String qualifiedName = workflowInstance.getTargetEntityClass();
            Class<BusinessEntity> clazz = (Class<BusinessEntity>) Class.forName(qualifiedName);
            businessEntityService.setEntityClass(clazz);
            businessEntity = businessEntityService.findByWorkflowInstance(workflowInstance);
        } catch (Exception e) {
            throw new BusinessException(e);
        }
        return businessEntity;
    }

    public List<BusinessEntity> findEntitiesWithoutWFInstance(GenericWorkflow gwf) throws BusinessException {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From " + gwf.getTargetEntityClass()
                + " be where be.id not in (select wi.entityInstanceId from WorkflowInstance wi where wi.targetEntityClass=:entityClass)";
        params.put("entityClass", gwf.getTargetEntityClass());

        return (List<BusinessEntity>) executeSelectQuery(query, params);
    }

    public void create(BusinessEntity e, GenericWorkflow genericWorkflow) throws BusinessException {
        WorkflowInstance linkedWFIns = new WorkflowInstance();
        linkedWFIns.setTargetEntityClass(genericWorkflow.getTargetEntityClass());
        linkedWFIns.setEntityInstanceId(e.getId());
        linkedWFIns.setEntityInstanceCode(e.getCode());
        linkedWFIns.setGenericWorkflow(genericWorkflow);
        linkedWFIns.setTargetCetCode(genericWorkflow.getTargetCetCode());

        WFStatus currentStatus = wfStatusService.findByCodeAndGWF(genericWorkflow.getInitStatus(), genericWorkflow);
        linkedWFIns.setCurrentStatus(currentStatus);

        create(linkedWFIns);
    }
}
