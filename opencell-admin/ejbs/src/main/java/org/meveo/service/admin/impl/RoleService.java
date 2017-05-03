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
package org.meveo.service.admin.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.event.monitoring.ClusterEventDto.CrudActionEnum;
import org.meveo.event.monitoring.ClusterEventPublisher;
import org.meveo.model.security.Role;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.PersistenceService;

/**
 * User Role service implementation.
 */
@Stateless
public class RoleService extends PersistenceService<Role> {

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    private ClusterEventPublisher clusterEventPublisher;

    @SuppressWarnings("unchecked")
    public List<Role> getAllRoles() {
        QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
        Query query = queryBuilder.getQuery(getEntityManager());
        return query.getResultList();
    }

    public Role findByName(String role) {
        QueryBuilder qb = new QueryBuilder(Role.class, "r", null);

        try {
            qb.addCriterion("name", "=", role, true);
            return (Role) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            log.trace("No role {} was found. Reason {}", role, e.getClass().getSimpleName());
            return null;
        }
    }

    @Override
    public void create(Role role) throws BusinessException {
        super.create(role);
        currentUserProvider.invalidateRoleToPermissionMapping();
        clusterEventPublisher.publishEvent(role, CrudActionEnum.create);
    }

    @Override
    public Role update(Role role) throws BusinessException {
        role = super.update(role);
        currentUserProvider.invalidateRoleToPermissionMapping();

        clusterEventPublisher.publishEvent(role, CrudActionEnum.update);
        return role;
    }

    @Override
    public void remove(Role role) throws BusinessException {
        super.remove(role);

        currentUserProvider.invalidateRoleToPermissionMapping();

        clusterEventPublisher.publishEvent(role, CrudActionEnum.remove);
    }
}