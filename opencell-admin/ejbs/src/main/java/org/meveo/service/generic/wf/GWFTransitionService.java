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

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.service.base.PersistenceService;

import com.google.common.collect.Maps;

@Stateless
public class GWFTransitionService extends PersistenceService<GWFTransition> {

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
}
