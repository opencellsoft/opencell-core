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

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
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
        QueryBuilder qb = new QueryBuilder(Permission.class, "p");
        boolean superAdmin = currentUser.hasRole("superAdminManagement");
        if (!superAdmin) {
            qb.addSqlCriterion("p.permission != :permission", "permission", "superAdminManagement");
        }
        return qb.getQuery(getEntityManager()).getResultList();
    }

    public Permission findByPermission(String permission) {

        try {
            Permission permissionEntity = getEntityManager().createNamedQuery("Permission.getPermission", Permission.class).setParameter("permission", permission)
                .getSingleResult();
            return permissionEntity;

        } catch (NoResultException | NonUniqueResultException e) {
            log.trace("No permission {} was found. Reason {}", permission, e.getClass().getSimpleName());
            return null;
        }

    }

    public Permission createIfAbsent(String permission, String... rolesToAddTo) throws BusinessException {
        
        // Create permission if does not exist yet
        Permission permissionEntity = findByPermission(permission);
        if (permissionEntity == null) {
            permissionEntity = new Permission();
            permissionEntity.setName(permission);
            permissionEntity.setPermission(permission);
            this.create(permissionEntity);
        }

        // Add to a role, creating role first if does not exist yet
        for (String roleName : rolesToAddTo) {
            Role role = roleService.findByName(roleName);
            if (role == null) {
                role = new Role();
                role.setName(roleName);
                role.setDescription(roleName);
                roleService.create(role);
            }

            if (!role.getPermissions().contains(permissionEntity)) {
                role.getPermissions().add(permissionEntity);
                roleService.update(role);
            }
        }

        return permissionEntity;
    }

}
