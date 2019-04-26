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

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
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

    public WorkflowInstance findByCodeAndGenericWorkflow(String entityInstanceCode, GenericWorkflow genericWorkflow) throws BusinessException {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstance wi where wi.entityInstanceCode = :entityInstanceCode and wi.genericWorkflow = :genericWorkflow";
        params.put("entityInstanceCode", entityInstanceCode);
        params.put("genericWorkflow", genericWorkflow);

        List<WorkflowInstance> wfInstances = (List<WorkflowInstance>) executeSelectQuery(query, params);

        if (wfInstances.size() > 1) {
            throw new BusinessException("Multiple instances result for entity " + entityInstanceCode);
        }

        if (!wfInstances.isEmpty()) {
            return wfInstances.iterator().next();
        }

        return null;
    }

    public List<WorkflowInstance> findByCodeAndClazz(String entityInstanceCode, Class<?> clazz) {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstance wi where wi.entityInstanceCode = :entityInstanceCode and wi.targetEntityClass = :clazz";
        params.put("entityInstanceCode", entityInstanceCode);
        params.put("clazz", clazz.getName());

        return (List<WorkflowInstance>) executeSelectQuery(query, params);
    }

    public List<WorkflowInstance> findByCodeAndCetAndClazz(String entityInstanceCode, String targetCetCode, Class<?> clazz) {

        Map<String, Object> params = Maps.newHashMap();
        String query = "From WorkflowInstance wi where wi.entityInstanceCode = :entityInstanceCode and wi.targetCetCode = :targetCetCode and wi.targetEntityClass = :clazz";
        params.put("entityInstanceCode", entityInstanceCode);
        params.put("targetCetCode", targetCetCode);
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
                + " be where be.code not in (select wi.entityInstanceCode from WorkflowInstance wi where wi.targetEntityClass=:entityClass)";
        params.put("entityClass", gwf.getTargetEntityClass());

        if (!StringUtils.isBlank(gwf.getTargetCetCode())) {
            query = "From " + gwf.getTargetEntityClass() + " be where be.code not in "
                    + "(select wi.entityInstanceCode from WorkflowInstance wi where wi.targetEntityClass=:entityClass and wi.targetCetCode = :cetCode)"
                    + " and be.cetCode=:cetCode";
            params.put("cetCode", gwf.getTargetCetCode());
        }

        return (List<BusinessEntity>) executeSelectQuery(query, params);
    }

    public void create(BusinessEntity e, GenericWorkflow genericWorkflow) throws BusinessException {
        WorkflowInstance linkedWFIns = new WorkflowInstance();
        linkedWFIns.setTargetEntityClass(genericWorkflow.getTargetEntityClass());
        linkedWFIns.setEntityInstanceCode(e.getCode());
        linkedWFIns.setGenericWorkflow(genericWorkflow);
        linkedWFIns.setTargetCetCode(genericWorkflow.getTargetCetCode());

        WFStatus currentStatus = wfStatusService.findByCodeAndGWF(genericWorkflow.getInitStatus(), genericWorkflow);
        linkedWFIns.setCurrentStatus(currentStatus);

        create(linkedWFIns);
    }
}
