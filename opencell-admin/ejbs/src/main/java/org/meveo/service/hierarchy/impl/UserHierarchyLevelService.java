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
import org.meveo.model.hierarchy.HierarchyLevel;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.base.PersistenceService;

/**
 * User Hierarchy Level service implementation.
 */
@Stateless
public class UserHierarchyLevelService extends PersistenceService<UserHierarchyLevel> {

    @SuppressWarnings("unchecked")
    public List<UserHierarchyLevel> findRoots() {
        Query query = getEntityManager().createQuery("from " + UserHierarchyLevel.class.getSimpleName() + " where parentLevel.id IS NULL");
        if (query.getResultList().size() == 0) {
            return null;
        }

        return query.getResultList();
    }

    public UserHierarchyLevel findByCode(String code) {
        UserHierarchyLevel userHierarchyLevel = null;
        if (StringUtils.isBlank(code)) {
            return null;
        }
        try {
            Query query = getEntityManager().createQuery("from " + UserHierarchyLevel.class.getSimpleName() + " uhl where uhl.code =:code ");
            query.setParameter("code", code);
            userHierarchyLevel = (UserHierarchyLevel) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
        return userHierarchyLevel;
    }

    public UserHierarchyLevel findByCode(String code, List<String> fetchFields) {
        QueryBuilder qb = new QueryBuilder(UserHierarchyLevel.class, "u", fetchFields);

        qb.addCriterion("u.code", "=", code, true);

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
    public void remove(UserHierarchyLevel entity) throws BusinessException {

        if (!canDeleteUserHierarchyLevel(entity.getId())) {
            throw new ExistsRelatedEntityException();
        }

        super.remove(entity);
    }
}