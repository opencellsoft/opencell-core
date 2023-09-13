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
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.security.Role;
import org.meveo.security.client.KeycloakAdminClientService;
import org.meveo.service.base.PersistenceService;

/**
 * User Role service implementation.
 */
@Stateless
public class RoleService extends PersistenceService<Role> {

    private static final long serialVersionUID = 6949512629862768876L;

    @Inject
    private KeycloakAdminClientService keycloakAdminClientService;

    /**
     * List/Search the <b>realm</b> roles
     * 
     * @param paginationConfig An optional search and pagination criteria. A filter criteria "name" is used to filter by role name.
     * @return List of roles
     */
    @Override
    public List<Role> list(PaginationConfiguration paginationConfig) {
        return keycloakAdminClientService.listRoles(paginationConfig);
    }

    /**
     * List/Search the <b>realm</b> roles. NOTE: return a list of role names only
     * 
     * @param paginationConfig An optional search and pagination criteria. A filter criteria "name" is used to filter by role name.
     * @return List of role names
     */
    public List<String> listRoleNames(PaginationConfiguration paginationConfig) {
        return list(paginationConfig).stream().map(r -> r.getName()).sorted().collect(Collectors.toList());
    }

    /**
     * Lookup a role by a name. NOTE: Does not create a role record in Opencell if role already exists in Keycloak
     * 
     * @param name Name to lookup by
     * @param extendedInfo Shall child roles be retrieved
     * @return Role found
     */
    public Role findByName(String name, boolean extendedInfo) {
        return findByName(name, extendedInfo, false);
    }

    /**
     * Lookup a role by a name
     * 
     * @param name Name to lookup by
     * @param extendedInfo Shall child roles be retrieved
     * @param syncWithKC Shall a role record be created in Opencell if a role already exists in Keycloak
     * @return User found
     */
    public Role findByName(String name, boolean extendedInfo, boolean syncWithKC) {
        Role kcRole = keycloakAdminClientService.findRole(name, extendedInfo, false);
        if (kcRole == null) {
            return null;
        }

        Role role = null;
        try {
            role = getEntityManager().createNamedQuery("Role.getByName", Role.class).setParameter("name", name.toLowerCase()).getSingleResult();

        } catch (NoResultException ex) {
            role = new Role();
            // Set fields, even they are transient, so they can be used in a notification if any is fired uppon role creation
            role.setName(kcRole.getName());
            role.setRoles(kcRole.getRoles());
            role.setDescription(kcRole.getDescription());
            super.create(role);
        }

        role.setName(kcRole.getName());
        role.setRoles(kcRole.getRoles());
        role.setDescription(kcRole.getDescription());
        return role;

    }
    
    public Role findOrCreateRole(String name,Role parentRole) {
    	Role role=null;
        try {
        	 role = getEntityManager().createNamedQuery("Role.getByName", Role.class).setParameter("name", name.toLowerCase()).getSingleResult();

        } catch (NoResultException ex) {
          super.create(new Role(name, name, true, parentRole));
        }

        return role;

    }

    /**
     * Create a role in Keycloak and then in Opencell. An attempt to create a role again will be ignored and will act as assignment only to a parent role.
     */
    @Override
    public void create(Role role) throws BusinessException {
    	
        if (role.getParentRole() == null) {
            keycloakAdminClientService.createRole(role.getName(), role.getDescription(), role.isClientRole());

        } else {
            keycloakAdminClientService.createRole(role.getName(), role.getDescription(), role.isClientRole(), role.getParentRole().getName(), role.getParentRole().getDescription(), role.getParentRole().isClientRole());
        }

        super.create(role);
    }

    /**
     * Update a role in Keycloak and then in Opencell
     */
    @Override
    public Role update(Role role) throws BusinessException {

        keycloakAdminClientService.updateRole(role.getName(), role.getDescription(), role.isClientRole());

        role = super.update(role);
        return role;
    }

    /**
     * Delete a role in Keycloak and then in Opencell
     */
    @Override
    public void remove(Role role) throws BusinessException {
        keycloakAdminClientService.deleteRole(role.getName(), role.isClientRole());
        super.remove(role);
    }
}