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

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 * @since Apr 4, 2013
 */
@Stateless
public class PermissionService extends PersistenceService<Permission> {

    @Inject
    private RoleService roleService;

    @SuppressWarnings("unchecked")
    @Override
    public List<Permission> list() {
        QueryBuilder qb = new QueryBuilder("from Permission p");
        boolean superAdmin = identity.hasPermission("superAdmin", "superAdminManagement");
        if (!superAdmin) {
            qb.addSqlCriterion("p.resource != :resource", "resource", "superAdmin");
            qb.addSqlCriterion("p.resource != :permission", "permission", "superAdminManagement");
        }
        return qb.getQuery(getEntityManager()).getResultList();
    }

    public Permission findByPermissionAndResource(String resource, String permission) {

        try {
            Permission permissionEntity = getEntityManager().createNamedQuery("Permission.getPermission", Permission.class).setParameter("permission", permission)
                .setParameter("resource", resource).getSingleResult();
            return permissionEntity;

        } catch (NoResultException | NonUniqueResultException e) {
            log.trace("No permission {},{} was found. Reason {}", resource, permission, e.getClass().getSimpleName());
            return null;
        }

    }

    public Permission createIfAbsent(String permission, String resource, User currentUser, String... rolesToAddTo) throws BusinessException {

        // Create permission if does not exist yet
        Permission permissionEntity = findByPermissionAndResource(resource, permission);
        if (permissionEntity == null) {
            permissionEntity = new Permission();
            permissionEntity.setName(resource + "-" + permission);
            permissionEntity.setPermission(permission);
            permissionEntity.setResource(resource);
            this.create(permissionEntity, currentUser);
        }

        // Add to a role, creating role first if does not exist yet
        for (String roleName : rolesToAddTo) {
            Role role = roleService.findByName(roleName, currentUser.getProvider());
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                role.setDescription(roleName);
                roleService.create(role, currentUser);
            }

            if (!role.getPermissions().contains(permissionEntity)) {
                role.getPermissions().add(permissionEntity);
                roleService.update(role, currentUser);
            }
        }

        return permissionEntity;
    }
}
