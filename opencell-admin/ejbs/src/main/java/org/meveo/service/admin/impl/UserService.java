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

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidParameterException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.admin.User;
import org.meveo.security.client.KeycloakAdminClientService;
import org.meveo.service.base.PersistenceService;

/**
 * User service implementation.
 */
@Stateless
@DeclareRoles({ "userManagement", "userSelfManagement", "apiUserManagement", "apiUserSelfManagement" })
public class UserService extends PersistenceService<User> {

    static User systemUser = null;

    @Inject
    KeycloakAdminClientService keycloakAdminClientService;

    @Override
    @RolesAllowed({ "userManagement", "userSelfManagement", "apiUserManagement", "apiUserSelfManagement" })
    public void create(User user) throws UsernameAlreadyExistsException, InvalidParameterException {

        user.setUserName(user.getUserName().toUpperCase());

        keycloakAdminClientService.createUser(user.getUserName(), user.getName().getFirstName(), user.getName().getLastName(), user.getEmail(), user.getPassword(), user.getUserLevel(), user.getRoles(), null);

        super.create(user);
    }

    @Override
    @RolesAllowed({ "userManagement", "userSelfManagement", "apiUserManagement", "apiUserSelfManagement" })
    public User update(User user) throws ElementNotFoundException, InvalidParameterException {

        user.setUserName(user.getUserName().toUpperCase());

        keycloakAdminClientService.updateUser(user.getUserName(), user.getName().getFirstName(), user.getName().getLastName(), user.getEmail(), user.getPassword(), user.getUserLevel(), user.getRoles());

        return super.update(user);
    }

    @Override
    @RolesAllowed({ "userManagement", "apiUserManagement" })
    public void remove(User user) throws BusinessException {
        keycloakAdminClientService.deleteUser(user.getUserName());
        super.remove(user);
    }

    /**
     * Lookup a user by a username. Note: Does noy create a user record in Opencell if user already exists in Keycloak
     * 
     * @param username Username to lookup by
     * @param extendedInfo Shall group membership and roles be retrieved
     * @return User found
     */
    public User findByUsername(String username, boolean extendedInfo) {
        return findByUsername(username, extendedInfo, false);
    }

    /**
     * Lookup a user by a username
     * 
     * @param username Username to lookup by
     * @param extendedInfo Shall group membership and roles be retrieved
     * @param syncWithKC Shall a user record be created in Opencell if a user already exists in Keycloak
     * @return User found
     */
    public User findByUsername(String username, boolean extendedInfo, boolean syncWithKC) {
        User kcUser = keycloakAdminClientService.findUser(username, extendedInfo);
        if (kcUser == null) {
            return null;
        }

        User user = null;
        try {
            user = getEntityManager().createNamedQuery("User.getByUsername", User.class).setParameter("username", username.toLowerCase()).getSingleResult();

        } catch (NoResultException ex) {
            user = new User();
            // Set fields, even they are transient, so they can be used in a notification if any is fired uppon user creation
            user.setEmail(kcUser.getEmail());
            user.setName(kcUser.getName());
            user.setRoles(kcUser.getRoles());
            user.setUserLevel(kcUser.getUserLevel());
            user.setUserName(username);
            super.create(user);
        }

        user.setEmail(kcUser.getEmail());
        user.setName(kcUser.getName());
        user.setRoles(kcUser.getRoles());
        user.setUserLevel(kcUser.getUserLevel());
        return user;

    }

    @Override
    public List<User> list(PaginationConfiguration config) {
        return keycloakAdminClientService.listUsers(config);
    }

    @Override
    public long count(PaginationConfiguration config) {
        return keycloakAdminClientService.countUsers(config);
    }

    /**
     * Check if user belongs to a group or a higher group
     * 
     * @param belongsToUserGroup A group to check
     * @return True if user belongs to a given group of to a parent of the group
     */
    public boolean isUserBelongsGroup(String belongsToUserGroup) {
        // TODO finish checking the hierarchy
        return belongsToUserGroup.equalsIgnoreCase(currentUser.getUserGroup());
    }
}