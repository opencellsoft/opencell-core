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
package org.meveo.service.hierarchy.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ExistsRelatedEntityException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.base.PersistenceService;

/**
 * User Hierarchy Level service implementation.
 */
@Stateless
public class UserHierarchyLevelService extends PersistenceService<UserHierarchyLevel> {

    /**
     * Check entity provider if not super admin user
     */
    @Override
    protected void checkProvider(UserHierarchyLevel entity) {
        // Super administrator - don't care
        if (identity.hasPermission("superAdmin", "superAdminManagement")) {
            return;
            // Other users - a regular check
        } else {
            super.checkProvider(entity);
        }
    }

    @SuppressWarnings("unchecked")
    public List<UserHierarchyLevel> findRoots() {
        Query query = getEntityManager().createQuery("from " + UserHierarchyLevel.class.getSimpleName() + " where parentLevel.id IS NULL");
        if (query.getResultList().size() == 0) {
            return null;
        }

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<UserHierarchyLevel> findRoots(Provider provider) {
        Query query = getEntityManager().createQuery("from " + UserHierarchyLevel.class.getSimpleName() + " where parentLevel.id IS NULL and provider=:provider");
        query.setParameter("provider", provider);
        if (query.getResultList().size() == 0) {
            return null;
        }

        return query.getResultList();
    }

    public UserHierarchyLevel findByCode(String code, Provider provider) {
        UserHierarchyLevel userHierarchyLevel = null;
        if (StringUtils.isBlank(code)) {
            return null;
        }
        try {
            Query query = getEntityManager().createQuery("from " + UserHierarchyLevel.class.getSimpleName() + " uhl where uhl.code =:code and uhl.provider=:provider");
            query.setParameter("code", code);
            query.setParameter("provider", provider);
            userHierarchyLevel = (UserHierarchyLevel) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return userHierarchyLevel;
    }

    public UserHierarchyLevel findByCode(String code, Provider provider, List<String> fetchFields) {
        QueryBuilder qb = new QueryBuilder(UserHierarchyLevel.class, "u", fetchFields, provider);

        qb.addCriterion("u.code", "=", code, true);
        qb.addCriterionEntity("u.provider", provider);

        try {
            return (UserHierarchyLevel) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Boolean canDeleteUserHierarchyLevel(Long id) {
        List<Boolean> hasUsersInSubNodes = new ArrayList<>();
        userGroupLevelInSubNode(id, hasUsersInSubNodes);
        if (hasUsersInSubNodes.contains(Boolean.TRUE)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    private void userGroupLevelInSubNode(Long id, List<Boolean> booleanList) {
        List<String> fieldsFetch = Arrays.asList("childLevels", "users");

        UserHierarchyLevel userHierarchyLevel = findById(id, fieldsFetch);
        if (userHierarchyLevel != null && CollectionUtils.isNotEmpty(userHierarchyLevel.getUsers())) {
            booleanList.add(Boolean.TRUE);
        } else {
            booleanList.add(Boolean.FALSE);
        }

        if (userHierarchyLevel != null && CollectionUtils.isNotEmpty(userHierarchyLevel.getChildLevels())) {
            for (HierarchyLevel child : userHierarchyLevel.getChildLevels()) {
                userGroupLevelInSubNode(child.getId(), booleanList);
            }
        }
    }

    @Override
    public void remove(UserHierarchyLevel entity, User currentUser) throws BusinessException {

        if (!canDeleteUserHierarchyLevel(entity.getId())) {
            throw new ExistsRelatedEntityException();
        }

        super.remove(entity, currentUser);
    }
}