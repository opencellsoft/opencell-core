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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.keycloak.representations.idm.UserRepresentation;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidParameterException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
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

    @Inject
    protected ParamBeanFactory paramBeanFactory;

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
        keycloakAdminClientService.updateUser(user.getUserName(), user.getName().getFirstName(), user.getName().getLastName(), user.getEmail(), user.getPassword(), user.getUserLevel(), user.getRoles(), null);
        return super.update(user);
    }

    @RolesAllowed({ "userManagement", "userSelfManagement", "apiUserManagement", "apiUserSelfManagement" })
    public void updateUserWithAttributes(User user, Map<String, String> attributes) throws ElementNotFoundException, InvalidParameterException {
        user.setUserName(user.getUserName().toUpperCase());
        keycloakAdminClientService.updateUser(user.getUserName(), user.getName().getFirstName(), user.getName().getLastName(), user.getEmail(), user.getPassword(), user.getUserLevel(), user.getRoles(), attributes);
        super.update(user);
    }

    @Override
    @RolesAllowed({ "userManagement", "apiUserManagement" })
    public void remove(User user) throws BusinessException {
        keycloakAdminClientService.deleteUser(user.getUserName());
        super.remove(user);
    }

    /**
     * Lookup a user by a username. NOTE: Does not create a user record in Opencell if user already exists in Keycloak
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
        String lUserManagementSource = paramBeanFactory.getInstance().getProperty("userManagement.master", "KC");

        User lUser = null;

        if(lUserManagementSource.equals("OC")) {
            lUser = getUserFromDatabase(username);

            if (lUser != null) {
                this.fillKeycloakUserInfo(lUser);
            }
        } else  {
            lUser = keycloakAdminClientService.findUser(username, extendedInfo);
        }

        return lUser;
    }

    private User getUserFromDatabase(String pUserName) {
        User lUser = null;
        try {
            lUser = getEntityManager().createNamedQuery("User.getByUsername", User.class).setParameter("username", pUserName.toLowerCase()).getSingleResult();
        } catch (NoResultException ex) {
            //ADD Log
        }

        return lUser;
    }

    @Override
    public List<User> list(PaginationConfiguration config) {
        String lUserManagementSource = paramBeanFactory.getInstance().getProperty("userManagement.master", "KC");

        List<User> users = new ArrayList<>();

        if(lUserManagementSource.equals("OC")) {
            String firstName = (String) config.getFilters().get("name.firstName");
            String lastName = (String) config.getFilters().get("name.lastName");
            String email = (String) config.getFilters().get("email");

            if(StringUtils.isBlank(firstName)) {
                this.removeFilters(config, "name.firstName");
            }

            if(StringUtils.isBlank(lastName)) {
                this.removeFilters(config, "name.lastName");
            }

            if(StringUtils.isBlank(email)) {
                this.removeFilters(config, "email");
            }

            users = super.list(config);
            users.forEach(this::fillKeycloakUserInfo);
        } else {
            //Get user from keycloak
            users = keycloakAdminClientService.listUsers(config);
            this.removeFilters(config, "name.firstName", "name.lastName", "email");

            //Construct a list of names
            List<String> usernamesList = users.stream().map(User::getUserName).collect(Collectors.toList());
            config.getFilters().put("inList userName", usernamesList);

            //Get list of users from database and fill all fields
            List<User> lDbUsers = super.list(config);
            users.forEach(keycloakUser -> {
                lDbUsers.forEach(dbUser -> {
                    if(keycloakUser.getUserName().equalsIgnoreCase(dbUser.getUserName())) {
                        fillEmptyFields(keycloakUser, dbUser);
                    }
                });
            });
        }

        return users;
    }

    @Override
    public long count(PaginationConfiguration config) {
        String userManagementSource = paramBeanFactory.getInstance().getProperty("userManagement.master", "KC");

        List<User> users;

        if(userManagementSource.equals("OC")) {
            String firstName = (String) config.getFilters().get("name.firstName");
            String lastName = (String) config.getFilters().get("name.lastName");
            String email = (String) config.getFilters().get("email");

            if(StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName) && StringUtils.isBlank(email)) {
                this.removeFilters(config, "name.firstName", "name.lastName", "email");
                return super.count(config);
            } else {
                return super.count(config);
            }
        } else {
            users = keycloakAdminClientService.listUsers(config);
            return users.size();
        }
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

    public UserRepresentation getUserRepresentationByUsername(String username) throws ElementNotFoundException {
        String userManagementSource = paramBeanFactory.getInstance().getProperty("userManagement.master", "KC");

        if(userManagementSource.equals("KC")) {
            return keycloakAdminClientService.getUserRepresentationByUsername(username);
        } else {
            return null;
        }
    }

    /**
     * Lookup a user by an id
     *
     * @param extendedInfo Shall group membership and roles be retrieved
     * @return User found
     */
    @Override
    public User findById(Long id, boolean extendedInfo) {
        if(id==null) {
            return null;
        }
        User user=findById(id);
         if (user == null) {
             return null;
         }
        User kcUser = keycloakAdminClientService.findUser(user.getUserName(), extendedInfo);
        if (kcUser == null) {
            return null;
        }
        user.setEmail(kcUser.getEmail());
        user.setName(kcUser.getName());
        user.setRoles(kcUser.getRoles());
        user.setUserLevel(kcUser.getUserLevel());
        return user;
    }

    /**
     * Lookup a keycloak user in database
     *
     * @param kcUser keycloak user to lookup by
     * @return User found
     */
    private User findKeycloakUser(User kcUser) {
        User user = null;
        try {
            user = getEntityManager().createNamedQuery("User.getByUsername", User.class).setParameter("username", kcUser.getUserName().toLowerCase()).getSingleResult();
        } catch (NoResultException ex) {
            user = new User();
            // Set fields, even they are transient, so they can be used in a notification if any is fired uppon user creation
            user.setEmail(kcUser.getEmail());
            user.setName(kcUser.getName());
            user.setRoles(kcUser.getRoles());
            user.setUserLevel(kcUser.getUserLevel());
            user.setUserName(kcUser.getUserName());
            super.create(user);
        }

        user.setEmail(kcUser.getEmail());
        user.setName(kcUser.getName());
        user.setRoles(kcUser.getRoles());
        user.setUserLevel(kcUser.getUserLevel());
        return user;
    }

    /**
     * Lookup a keycloak user info
     *
     * @param user user
     */
    private void fillKeycloakUserInfo(User user) {
        User kcUser = keycloakAdminClientService.findUser(user.getUserName(), true);
        if (kcUser != null) {
            user.setEmail(kcUser.getEmail());
            user.setName(kcUser.getName());
            user.setRoles(kcUser.getRoles());
            user.setUserLevel(kcUser.getUserLevel());
        }
    }

    /**
     * Remove filters from config
     *
     * @param pConfig {@link PaginationConfiguration}
     * @param pKeys A list of keys to remove
     */
    private void removeFilters(PaginationConfiguration pConfig, String ... pKeys) {
        for(String key : pKeys) {
            pConfig.getFilters().remove(key);
        }
    }

    /**
     * Fill Empty field to return it
     * @param pKeycloakUser {@link User} User returned from keycloak
     * @param pDbUser {@link User} User returned from database
     */
    private void fillEmptyFields(User pKeycloakUser, User pDbUser) {
        if(pKeycloakUser.getEmail() == null && pDbUser.getEmail() != null && !pDbUser.getEmail().isBlank()) {
            pKeycloakUser.setEmail(pDbUser.getEmail());
        }

        if(pKeycloakUser.getUuid() == null && pDbUser.getUuid() != null && !pDbUser.getUuid().isEmpty()) {
            pKeycloakUser.setUuid(pDbUser.getUuid());
        }
    }
}