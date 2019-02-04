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

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.service.base.PersistenceService;

@Stateless
public class GWFTransitionService extends PersistenceService<GWFTransition> {

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
}
