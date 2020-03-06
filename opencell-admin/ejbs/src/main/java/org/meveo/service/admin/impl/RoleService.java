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