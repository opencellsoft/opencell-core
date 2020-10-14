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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.BusinessEntity;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.filter.Filter;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.filter.FilterService;

import com.google.common.collect.Maps;

@Stateless
public class WorkflowInstanceService extends PersistenceService<WorkflowInstance> {

    @Inject
    private WFStatusService wfStatusService;

    @Inject
    private FilterService filterService;

    @Inject
    private GenericWorkflowService genericWorkflowService;

    public WorkflowInstance findByEntityIdAndGenericWorkflow(Long entityInstanceId, GenericWorkflow genericWorkflow) throws BusinessException {
        TypedQuery<WorkflowInstance> query = getEntityManager()
            .createQuery("select wi from " + entityClass.getSimpleName() + " wi where wi.entityInstanceId = :entityInstanceId and wi.genericWorkflow = :genericWorkflow",
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

    public List<BusinessEntity> findEntitiesForWorkflow(GenericWorkflow gwf, boolean withoutWFInstance) throws BusinessException {

        Map<String, Object> params = Maps.newHashMap();
        if (gwf.getId() != null) {
            gwf = genericWorkflowService.findById(gwf.getId());
        }
        String operator = withoutWFInstance ? "not ":"";
        if (gwf.getFilter() != null) {

            
			String query = "From " + gwf.getTargetEntityClass()
                    + " be where be.id " + operator  + "in (select wi.entityInstanceId from WorkflowInstance wi where wi.targetEntityClass=:entityClass)";
            params.put("entityClass", gwf.getTargetEntityClass());

            List<BusinessEntity> listAllEntitiesWithoutWFInstance = (List<BusinessEntity>) executeSelectQuery(query, params);

            Filter filter = gwf.getFilter();
            List<BusinessEntity> listFilteredEntities = (List<BusinessEntity>) filterService.filteredListAsObjects(filter);

            List<Long> listIdAllEntitiesWithoutWFInstance = new ArrayList<Long>();
            Map<Long, BusinessEntity> mapAllEntitiesWithoutWFInstance = new HashMap<Long, BusinessEntity>();
            for (BusinessEntity entity : listAllEntitiesWithoutWFInstance) {
                listIdAllEntitiesWithoutWFInstance.add(entity.getId());
                mapAllEntitiesWithoutWFInstance.put(entity.getId(), entity);
            }
            List<Long> listIdFilteredEntities = new ArrayList<Long>();
            for (BusinessEntity entity : listFilteredEntities) {
                listIdFilteredEntities.add(entity.getId());
            }

            Set<Long> setIdAllEntitiesWithoutWFInstanceFiltered = listIdAllEntitiesWithoutWFInstance.stream()
            .distinct()
            .filter(listIdFilteredEntities::contains)
            .collect(Collectors.toSet());

            List<BusinessEntity> listFilteredEntitiesWithoutWFInstance = new ArrayList<BusinessEntity>();
            for (Long id : setIdAllEntitiesWithoutWFInstanceFiltered) {
                listFilteredEntitiesWithoutWFInstance.add(mapAllEntitiesWithoutWFInstance.get(id));
            }
            return listFilteredEntitiesWithoutWFInstance;
        } else {
            String query = "From " + gwf.getTargetEntityClass() + " be where be.id " + operator
                    + "in (select wi.entityInstanceId from WorkflowInstance wi where wi.targetEntityClass=:entityClass)";
            params.put("entityClass", gwf.getTargetEntityClass());

            List<BusinessEntity> entities = (List<BusinessEntity>) executeSelectQuery(query, params);
            if (gwf.getTargetEntityClass().equals(CustomEntityInstance.class.getName())) {
                GenericWorkflow finalGwf = gwf;
                return entities.stream().filter(entity -> {
                    return ((CustomEntityInstance) entity).getCetCode().equals(finalGwf.getTargetCetCode());
                }).collect(Collectors.toList());
            } else {
                return entities;
            }
        }
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
