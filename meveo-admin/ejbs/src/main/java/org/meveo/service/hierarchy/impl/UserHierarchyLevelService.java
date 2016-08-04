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

import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

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

    public List<UserHierarchyLevel> findRoots() {
        Query query = getEntityManager()
                .createQuery(
                        "from " + UserHierarchyLevel.class.getSimpleName()
                                + " where parentLevel.id IS NULL");
        if (query.getResultList().size() == 0) {
            return null;
        }

        return query.getResultList();
    }
}