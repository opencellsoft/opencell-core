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

package org.meveo.security.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.PoliciesResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.resource.ProtectedResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidParameterException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.ResteasyClientProxyBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.shared.Name;
import org.meveo.security.AccessScopeEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.SecuredEntity;
import org.meveo.security.UserGroup;
import org.slf4j.Logger;

/**
 * Keycloak management services - user, api access rules and secured entities management (last two implemented as authorization resources) <br/>
 * 
 * The following realm-management roles are needed:<br/>
 * <b>user management</b> - manage-authorization, manage-users, view-realm<br/>
 * <b>api access rules</b> - manage-clients, view-authorization, view-clients, view-realm<br/>
 * <b>secured entities</b> - manage-authorization, view-realm, view-clients<br/>
 * 
 **/
@Stateless
public class KeycloakAdminClientService implements Serializable {

    private static final long serialVersionUID = -2606825944327099065L;

    @Inject
    private Logger log;

    @Resource
    private SessionContext ctx;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * A Keycloak client role giving a full API access
     */
    private static final String API_FULL_ACCESS = "genApiFull";

    /**
     * A Keycloak client role prefix for a generic API package level access
     */
    private static final String GENERIC_API_ROLE_PACKAGE_LEVEL_PREFIX = "genApiPkg_";

    /**
     * A Keycloak client role prefix for a generic API class level access
     */
    private static final String GENERIC_API_ROLE_CLASS_LEVEL_PREFIX = "genApi_";

    /**
     * A Keycloak client resource prefix for a generic API package level
     */
    private static final String GENERIC_API_RESOURCE_PACKAGE_LEVEL_PREFIX = "GenApi Pkg ";

    /**
     * A Keycloak client resource prefix for a generic API class level
     */
    private static final String GENERIC_API_RESOURCE_CLASS_LEVEL_PREFIX = "GenApi ";

    /**
     * A prefix to add to a username based policy
     */
    private static final String KC_POLICY_USER_PREFIX = "User ";

    /**
     * A prefix to add to a role based policy
     */
    private static final String KC_POLICY_ROLE_PREFIX = "Role ";

    /**
     * Reads the configuration from system property.
     * 
     * @return KeycloakAdminClientConfig
     */
    private KeycloakAdminClientConfig loadConfig() {
        KeycloakAdminClientConfig keycloakAdminClientConfig = new KeycloakAdminClientConfig();
        try {
            // override from system property
            String keycloakServer = System.getProperty("opencell.keycloak.url");
            if (!StringUtils.isBlank(keycloakServer)) {
                keycloakAdminClientConfig.setServerUrl(keycloakServer);
            }
            String realm = System.getProperty("opencell.keycloak.realm");
            if (!StringUtils.isBlank(realm)) {
                keycloakAdminClientConfig.setRealm(realm);
            }
            String clientId = System.getProperty("opencell.keycloak.client");
            if (!StringUtils.isBlank(clientId)) {
                keycloakAdminClientConfig.setClientId(clientId);
            }
            String clientSecret = System.getProperty("opencell.keycloak.secret");
            if (!StringUtils.isBlank(clientSecret)) {
                keycloakAdminClientConfig.setClientSecret(clientSecret);
            }

            log.trace("Found keycloak configuration: {}", keycloakAdminClientConfig);
        } catch (Exception e) {
            log.error("Error: Loading keycloak admin configuration. " + e.getMessage());
        }

        return keycloakAdminClientConfig;
    }

    /**
     * @param keycloakAdminClientConfig keycloak admin client config.
     * @return instance of Keycloak.
     */
    @SuppressWarnings("rawtypes")
    private Keycloak getKeycloakClient(KeycloakAdminClientConfig keycloakAdminClientConfig) {
        KeycloakSecurityContext session = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext();

        KeycloakBuilder keycloakBuilder = KeycloakBuilder.builder().serverUrl(keycloakAdminClientConfig.getServerUrl()).realm(keycloakAdminClientConfig.getRealm()).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(keycloakAdminClientConfig.getClientId()).clientSecret(keycloakAdminClientConfig.getClientSecret()).authorization(session.getTokenString());

        keycloakBuilder.resteasyClient(new ResteasyClientProxyBuilder().connectionPoolSize(20).build());

        return keycloakBuilder.build();
    }

    /**
     * List users in KC from a current realm
     * 
     * @param Filtering and pagination criteria
     */
    public List<User> listUsers(PaginationConfiguration paginationConfig) {

        String username = (String) paginationConfig.getFilters().get("userName");
        String firstName = (String) paginationConfig.getFilters().get("firstName");
        String lastName = (String) paginationConfig.getFilters().get("lastName");
        String email = (String) paginationConfig.getFilters().get("email");

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> users = usersResource.search(username, firstName, lastName, email, paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows());
        return users.stream().map(u -> {

            List<GroupRepresentation> groups = usersResource.get(u.getId()).groups();
            List<String> groupNames = new ArrayList<>();
            if (groups != null && !groups.isEmpty()) {
                groupNames.add(groups.get(0).getName());
            }

            List<RoleRepresentation> realmRoles = usersResource.get(u.getId()).roles().realmLevel().listAll();
            u.setRealmRoles(realmRoles.stream().map(r -> r.getName()).collect(Collectors.toList()));

            User user = new User(u.getUsername(), u.getFirstName(), u.getLastName(), u.getEmail(), groupNames, u.getRealmRoles());
            return user;
        }).collect(Collectors.toList());
    }

    /**
     * Count users in KC from a current realm
     * 
     * @param Filtering and pagination criteria
     */
    public long countUsers(PaginationConfiguration paginationConfig) {

        String username = (String) paginationConfig.getFilters().get("userName");
        String firstName = (String) paginationConfig.getFilters().get("firstName");
        String lastName = (String) paginationConfig.getFilters().get("lastName");
        String email = (String) paginationConfig.getFilters().get("email");

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        return usersResource.count(lastName, firstName, email, username);

    }

    /**
     * Create a user in Keycloak. It will add a provider code attribute to the user if multitenancy is activated. The provider will be the same as the current user.
     * 
     * @param userName UserName
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @param userGroup User group to assign to
     * @param roles Roles to assign
     * @param providerToOverride Provider code to override with
     * @return A user identifier in Keycloak
     * @throws InvalidParameterException Missing fields
     * @throws UsernameAlreadyExistsException User with such username already exists
     */
    public String createUser(String userName, String firstName, String lastName, String email, String password, String userGroup, Collection<String> roles, String providerToOverride)
            throws InvalidParameterException, ElementNotFoundException, UsernameAlreadyExistsException {
        return createOrUpdateUser(userName, firstName, lastName, email, password, userGroup, roles, providerToOverride, false);
    }

    /**
     * Update a user in Keycloak. It will add a provider code attribute to the user if multitenancy is activated. The provider will be the same as the current user.
     * 
     * @param userName UserName
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @param userGroup User group to assign to
     * @param roles Roles to assign
     * @return A user identifier in Keycloak
     * @throws InvalidParameterException Missing fields
     * @throws ElementNotFoundException User was not found
     */
    public String updateUser(String userName, String firstName, String lastName, String email, String password, String userGroup, Collection<String> roles)
            throws InvalidParameterException, ElementNotFoundException, UsernameAlreadyExistsException {
        return createOrUpdateUser(userName, firstName, lastName, email, password, userGroup, roles, null, true);
    }

    /**
     * Create or update a user in Keycloak. It will add a provider code attribute to the user if multitenancy is activated. The provider will be the same as the current user.
     * 
     * @param userName UserName
     * @param firstName First name
     * @param lastName Last name
     * @param email Email address
     * @param password Password
     * @param userGroup User group to assign to
     * @param roles Roles to assign
     * @param providerToOverride Provider code to override with
     * @param isUpdate Is this an existing user update
     * @return A user identifier in Keycloak
     * @throws InvalidParameterException Missing fields
     * @throws UsernameAlreadyExistsException User with such username already exists
     * @throws ElementNotFoundException User was not found
     */
    private String createOrUpdateUser(String userName, String firstName, String lastName, String email, String password, String userGroup, Collection<String> roles, String providerToOverride, boolean isUpdate)
            throws InvalidParameterException, ElementNotFoundException, UsernameAlreadyExistsException {

        // TODO Should check if user already exists in Keycloak. If so - add roles requested. If not - create a user in Keycloak.
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        UserRepresentation user = null;

        if (StringUtils.isBlank(userName) && StringUtils.isBlank(email)) {
            throw new InvalidParameterException("Either userName or email must be provided to create a user");
        }

        // Default username to email if username is not provided
        if (StringUtils.isBlank(userName)) {
            userName = email;
        }

        List<UserRepresentation> users = usersResource.search(userName, true);
        for (UserRepresentation userRepresentation : users) {
            if (userRepresentation.getUsername().equalsIgnoreCase(userName)) {
                user = userRepresentation;
                break;
            }
        }

        if (isUpdate && user == null) {
            throw new ElementNotFoundException("User with username " + userName + " not found");

        } else if (!isUpdate && user != null) {
            throw new UsernameAlreadyExistsException("User with username " + userName + " already exists");
        }

        if (!isUpdate && StringUtils.isBlank(password)) {
            throw new InvalidParameterException("Password is required to create a user");
        }

        // Define new user
        if (user == null) {
            user = new UserRepresentation();
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setUsername(userName);
        }
        if (firstName != null) {
            user.setFirstName(firstName);
        }
        if (lastName != null) {
            user.setLastName(lastName);
        }
        if (email != null) {
            user.setEmail(email);
        }

        if (!isUpdate) {
            Map<String, List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }
            if (ParamBean.isMultitenancyEnabled()) {
                if (providerToOverride == null) {
                    providerToOverride = currentUser.getProviderCode();
                }

                if (providerToOverride != null) {
                    attributes.put("provider", Arrays.asList(providerToOverride));
                }
            }

            user.setAttributes(attributes);
        }

        // Determine user groups and validate that they exist

        List<GroupRepresentation> groupsToAdd = new ArrayList<>();
        if (userGroup != null) {
            GroupsResource groupResource = realmResource.groups();

            List<GroupRepresentation> groups = groupResource.groups(userGroup, null, null);
            GroupRepresentation groupMatched = findGroup(userGroup, groups);

            if (groupMatched == null) {
                throw new InvalidParameterException("User group '" + userGroup + "' not found");
            }

            groupsToAdd.add(groupMatched);
        }

        // Determine roles requested and validate that they exist
        List<RoleRepresentation> rolesToAdd = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            RolesResource rolesResource = realmResource.roles();

            for (String role : roles) {
                try {
                    RoleRepresentation tempRole = rolesResource.get(role).toRepresentation();
                    rolesToAdd.add(tempRole);
                } catch (NotFoundException e) {
                    throw new InvalidParameterException("Role " + role + " was not found");
                }
            }
        }

        // does not work
        // Define password credential
        // CredentialRepresentation credential = new CredentialRepresentation();
        // credential.setTemporary(false);
        // credential.setType(CredentialRepresentation.PASSWORD);
        // credential.setValue(postData.getPassword());
        // user.setCredentials(Arrays.asList(credential));

        // Map<String, List<String>> clientRoles = new HashMap<>();
        // clientRoles.put(keycloakAdminClientConfig.getClientId(),
        // Arrays.asList(KeycloakConstants.ROLE_API_ACCESS, KeycloakConstants.ROLE_GUI_ACCESS, KeycloakConstants.ROLE_ADMINISTRATEUR, KeycloakConstants.ROLE_USER_MANAGEMENT));

        String userId = null;

        // Update current user
        if (isUpdate) {

            userId = user.getId();
            usersResource.get(userId).update(user);

            // Create a new user
        } else {

            Response response = usersResource.create(user);

            if (response.getStatus() != Status.CREATED.getStatusCode()) {
                log.error("Keycloak user creation or update with http status.code={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());

                if (response.getStatus() == HttpStatus.SC_CONFLICT) {
                    try {
                        UserRepresentation existingUser = getUserRepresentationByUsername(usersResource, userName);

                        log.warn("A user with username {} and id {} already exists in Keycloak", userName, existingUser.getId());
                        throw new UsernameAlreadyExistsException(userName);

                        // Some other field is causing a conflict
                    } catch (ElementNotFoundException e) {
                        throw new BusinessException("Unable to create user with httpStatusCode=" + response.getStatus() + " and reason=" + response.getStatusInfo().getReasonPhrase());

                    }

                } else {
                    throw new BusinessException("Unable to create user with httpStatusCode=" + response.getStatus() + " and reason=" + response.getStatusInfo().getReasonPhrase());
                }
            }

            userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        }

        log.debug("User {} created or updated in Keycloak with userId: {}", user.getUsername(), userId);

        // ---------- Set/update user groups

        // Determine new user groups to add or remove
        List<GroupRepresentation> groupsToDelete = new ArrayList<>();

        if (isUpdate) {
            List<GroupRepresentation> currentGroups = usersResource.get(user.getId()).groups();
            groupsToDelete.addAll(currentGroups);
            groupsToDelete.removeAll(groupsToAdd);
            groupsToAdd.removeAll(currentGroups);
        }

        if (!groupsToDelete.isEmpty()) {
            for (GroupRepresentation group : groupsToDelete) {
                usersResource.get(userId).leaveGroup(group.getId());
            }
        }
        if (!groupsToAdd.isEmpty()) {
            for (GroupRepresentation group : groupsToAdd) {
                usersResource.get(userId).joinGroup(group.getId());
            }
        }

        // ---------- Set/update roles

        // Determine new roles to add or remove
        List<RoleRepresentation> rolesToDelete = new ArrayList<>();

        if (isUpdate) {
            List<RoleRepresentation> currentRoles = usersResource.get(user.getId()).roles().realmLevel().listAll();
            rolesToDelete.addAll(currentRoles);
            rolesToDelete.removeAll(rolesToAdd);
            rolesToAdd.removeAll(currentRoles);
        }

        if (!rolesToAdd.isEmpty()) {
            usersResource.get(userId).roles().realmLevel().add(rolesToAdd);
        }
        if (!rolesToDelete.isEmpty()) {
            usersResource.get(userId).roles().realmLevel().remove(rolesToDelete);
        }

        // ---------- Define password credential
        if (password != null) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);

            // Set password credential
            usersResource.get(userId).resetPassword(credential);
        }
        return userId;

    }

    /**
     * Deletes a user in keycloak.
     * 
     * @param username user name
     * @throws ElementNotFoundException No user found with a given username
     */
    public void deleteUser(String username) throws ElementNotFoundException {
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        UserRepresentation userRepresentation = getUserRepresentationByUsername(usersResource, username);

        // Create user (requires manage-users role)
        Response response = usersResource.delete(userRepresentation.getId());

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            log.error("Keycloak user deletion with httpStatusCode={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            throw new BusinessException("Unable to delete user with httpStatusCode=" + response.getStatus());
        }
    }

    /**
     * Search for a user in keycloak via username.
     * 
     * @param username user name
     * @return list of role
     * @throws BusinessException business exception
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public List<String> findUserRoles(String username) throws BusinessException {
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        try {
            UserRepresentation userRepresentation = getUserRepresentationByUsername(usersResource, username);

            return userRepresentation != null
                    ? usersResource.get(userRepresentation.getId()).roles().realmLevel().listEffective().stream().filter(p -> !KeycloakConstants.ROLE_KEYCLOAK_DEFAULT_EXCLUDED.contains(p.getName())).map(p -> {
                        return p.getName();
                    }).collect(Collectors.toList())
                    : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<String>();
        }
    }

    /**
     * Search for a user in keycloak via username.
     * 
     * @param username user name
     * @return keycloak email
     * @throws BusinessException business exception
     */
    public String findUserEmail(String username) throws BusinessException {
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        UserRepresentation userRepresentation = getUserRepresentationByUsername(usersResource, username);

        try {
            return userRepresentation.getEmail();
        } catch (Exception e) {
            return new String();
        }

    }

    /**
     * List/Search the <b>realm</b> roles in Keycloak.
     * 
     * @param paginationConfig An optional search and pagination criteria. A filter criteria "name" is used to filter by role name.
     * @return List of roles
     */
    public List<String> listRoles(PaginationConfiguration paginationConfig) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        if (paginationConfig != null && paginationConfig.getFilters() != null && paginationConfig.getFilters().containsKey("name")) {
            return realmResource.roles().list((String) paginationConfig.getFilters().get("name"), paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows()).stream().map(p -> p.getName())
                .collect(Collectors.toList());
        } else {
            return realmResource.roles().list().stream().map(p -> p.getName()).collect(Collectors.toList());
        }
    }

    /**
     * Create a <b>client</b> role as a child of a parent role if provided. An attempt to create a role again will be ignored.
     * 
     * @param name Role name
     * @param parentRole Parent role name. Role will be created if does not exist yet.
     */
    public void createClientRole(String name, String parentRole) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();
        ClientResource client = realmResource.clients().get(clientId);

        // Create a role
        RoleResource roleResource = client.roles().get(name);
        List<RoleRepresentation> roleSearch = client.roles().list(name, false);
        RoleRepresentation roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
        if (roleRepresentation == null) {
            RoleRepresentation role = new RoleRepresentation(name, null, false);
            client.roles().create(role);
            roleRepresentation = roleResource.toRepresentation();
        }

        // Assign role to the parent - create parent role if does not exist yet
        if (parentRole != null) {
            roleResource = client.roles().get(parentRole);
            roleSearch = client.roles().list(parentRole, false);
            RoleRepresentation parentRoleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
            if (parentRoleRepresentation == null) {
                RoleRepresentation role = new RoleRepresentation(parentRole, null, false);
                client.roles().create(role);
                parentRoleRepresentation = roleResource.toRepresentation();
            }
            List<RoleRepresentation> childRoles = new ArrayList<RoleRepresentation>();
            childRoles.add(roleRepresentation);
            roleResource.addComposites(childRoles);
        }
    }

    /**
     * List all user groups in a realm in Keycloak
     * 
     * @param paginationConfig An optional search and pagination criteria. A filter criteria "name" is used to filter by userGroup name.
     * @return A list of user group hierarchy
     */
    public List<UserGroup> listGroups(PaginationConfiguration paginationConfig) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        if (paginationConfig != null && paginationConfig.getFilters() != null && paginationConfig.getFilters().containsKey("name")) {
            return realmResource.groups().groups((String) paginationConfig.getFilters().get("name"), paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows()).stream().map(g -> new UserGroup(g))
                .collect(Collectors.toList());
        } else {
            return realmResource.groups().groups().stream().map(g -> new UserGroup(g)).collect(Collectors.toList());
        }
    }

    /**
     * Find a user group by a name in Keycloak
     * 
     * @param userGroupName User group name to match
     * @return A user group including it's children
     */
    public UserGroup findGroup(String userGroupName) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        GroupsResource groupResource = realmResource.groups();

        List<GroupRepresentation> groups = groupResource.groups(userGroupName, null, null);
        GroupRepresentation groupMatched = findGroup(userGroupName, groups);

        return new UserGroup(groupMatched);
    }

    /**
     * Find a user by a username
     * 
     * @param userName Username to lookup by
     * @param extendedInfo Shall group membership and roles be retrieved
     * @return User information
     */
    public User findUser(String userName, boolean extendedInfo) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        try {
            UserRepresentation userRepresentation = getUserRepresentationByUsername(usersResource, userName);

            User user = new User();
            user.setUserName(userName);
            user.setName(new Name(null, userRepresentation.getFirstName(), userRepresentation.getLastName()));
            user.setEmail(userRepresentation.getEmail());

            if (extendedInfo) {
                List<GroupRepresentation> groups = usersResource.get(userRepresentation.getId()).groups();
                if (groups != null && !groups.isEmpty()) {
                    user.setUserLevel(groups.get(0).getName());
                }

                List<RoleRepresentation> currentRoles = usersResource.get(userRepresentation.getId()).roles().realmLevel().listAll();
                user.setRoles(new HashSet<String>(currentRoles.stream().map(r -> r.getName()).collect(Collectors.toList())));
            }

            return user;

        } catch (ElementNotFoundException e) {
            log.debug("No user with username {} was found", userName);
        }
        return null;
    }

    /**
     * As the search function from keycloack doesn't perform exact search, we need to browse results to pick the exact username
     * 
     * @param usersResource Users resource
     * @param username Username
     * @return User information
     * @throws ElementNotFoundException No user found with a given username.
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    private UserRepresentation getUserRepresentationByUsername(UsersResource usersResource, String username) throws ElementNotFoundException {
        List<UserRepresentation> users = usersResource.search(username, true);
        for (UserRepresentation userRepresentation : users) {
            if (username.equalsIgnoreCase(userRepresentation.getUsername())) {
                return userRepresentation;
            }
        }
        throw new ElementNotFoundException("No user found with username " + username);
    }

    /**
     * Get all entities that should be managed by API, which are basically, classes marked with {@code @Entity} annotation
     *
     * @return All entities manageable by API mapped with their first model subpackage
     */
    private Map<String, Set<String>> getAPIv2ManagedClassesByPackages() {
        Map<String, Set<String>> entitiesByPackages = new HashMap<>();
        Set<Class<?>> classesAnnotatedWith = ReflectionUtils.getClassesAnnotatedWith(Entity.class);
        Set<String> managedEntities = classesAnnotatedWith.stream().map(Class::getName).collect(Collectors.toSet());

        for (String entityName : managedEntities) {
            // Get first sub package name under model package of the entity.
            // If the entity is directly under model package then return COMMON
            int subPackageStart = StringUtils.ordinalIndexOf(entityName, ".", 3);
            int subPackageEnd = StringUtils.ordinalIndexOf(entityName, ".", 4);
            String subPackageName;
            if (subPackageEnd != -1) {
                subPackageName = entityName.substring(subPackageStart + 1, subPackageEnd);
            } else {
                subPackageName = "COMMON";
            }

            entitiesByPackages.computeIfAbsent(subPackageName, key -> new HashSet<>()).add(entityName);
        }
        log.debug("{} Entity classes are found under {} model sub-packages", managedEntities.size(), entitiesByPackages.size());

        return entitiesByPackages;
    }

    /**
     * Get a list of secured entities applied to a given user. It is represented as a resource in Keycloak of type "SE" and owner being the user in question.
     * 
     * @param username Username of a user to retrieve
     * @return A list of secured entities
     */
    public List<SecuredEntity> getSecuredEntities(String username) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm and a client's internal id
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        String clientId = realmResource.clients().findByClientId("opencell-web").get(0).getId();

        AuthorizationResource authResource = realmResource.clients().get(clientId).authorization();
        PoliciesResource policies = authResource.policies();

        // Create a username based policy
        PolicyRepresentation userPolicy = policies.findByName(KC_POLICY_USER_PREFIX + username.toLowerCase());
        if (userPolicy != null) {
            List<PolicyRepresentation> depPolicies = policies.policy(userPolicy.getId()).dependentPolicies();
            if (depPolicies != null) {
                return depPolicies.stream().filter(depPolicy -> depPolicy.getName().startsWith(SecuredEntity.RESOURCE_NAME_PREFIX)).map(depPolicy -> new SecuredEntity(depPolicy.getName(), depPolicy.getId()))
                    .collect(Collectors.toList());
            }
        }
        return new ArrayList<SecuredEntity>();

//        String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();
//
//        AuthzClient authzClient = AuthzClient.create();
//
//        List<ResourceRepresentation> resources = authzClient.protection(accessToken).resource().find(null, null, null, userName, "SE", null, false, true, null, null);
//
//        return resources.stream().map(resource -> new SecuredEntity(resource.getName(), resource.getId())).collect(Collectors.toList());
    }

    /**
     * Delete a secured entity configuration
     * 
     * @param securedEntity Secured entity to remove
     * @param username Username of a user
     */
    public void deleteSecuredEntity(SecuredEntity securedEntity, String username) {
        deleteSecuredEntities(Arrays.asList(securedEntity), username);
    }

    /**
     * Delete a secured entity configuration from a user
     * 
     * @param securedEntities A list of secured entities to remove
     * @param username Username of a user
     */
    @SuppressWarnings("rawtypes")
    public void deleteSecuredEntities(List<SecuredEntity> securedEntities, String username) {

//        // Deletion when Resource is owner based
//        String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();
//
//        AuthzClient authzClient = AuthzClient.create();
//        authzClient.protection(accessToken).resource().delete(securedEntity.getKcId());

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm and a client's internal id
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();

        AuthorizationResource authResource = realmResource.clients().get(clientId).authorization();
        PoliciesResource resourcePolicies = authResource.policies();

        String userPolicyName = KC_POLICY_USER_PREFIX + username.toLowerCase();

        // Deletion when Resource has no owner
        for (SecuredEntity securedEntity : securedEntities) {

            String resourceName = securedEntity.getRule();
            String permissionName = resourceName;// + " - " + username.toLowerCase(); // When Resource is owner based

            PolicyRepresentation userPolicy = resourcePolicies.findByName(userPolicyName);

            // Disacociate username based policy from a resource permission.
            // In case that was the only policy associated, remove permission altogether and delete a resource.
            ResourcePermissionRepresentation resourcePermission = authResource.permissions().resource().findByName(permissionName);
            if (resourcePermission != null && userPolicy != null) {

                // Retrieve current policies and remove the username based policy
                List<PolicyRepresentation> policies = authResource.permissions().resource().findById(resourcePermission.getId()).associatedPolicies();
                Set<String> policyIds = new HashSet<String>();
                for (PolicyRepresentation policy : policies) {
                    if (!policy.getId().equals(userPolicy.getId())) {
                        policyIds.add(policy.getId());
                    }
                }

                // Remove permission altogether and delete a resource.
                if (policyIds.isEmpty()) {

                    List<ResourceRepresentation> resources = authResource.permissions().resource().findById(resourcePermission.getId()).resources();

                    authResource.permissions().resource().findById(resourcePermission.getId()).remove();

                    String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();
                    AuthzClient authzClient = AuthzClient.create();

                    ProtectedResource protectedResource = authzClient.protection(accessToken).resource();

                    for (ResourceRepresentation resource : resources) {

                        String[] resourceIds = protectedResource.find(null, resource.getName(), null, null, null, null, false, false, null, 1); // findByName(resourceName, username); // When Resource is owner
                        String resourceId = resourceIds.length > 0 ? resourceIds[0] : null;
                        if (resourceId != null) {
                            authResource.resources().resource(resourceId).remove();
                        }
                    }

                    // Update permission with remaining policies
                } else {
                    resourcePermission.setPolicies(policyIds);
                    authResource.permissions().resource().findById(resourcePermission.getId()).update(resourcePermission);
                }
            }
        }
    }

    /**
     * Add a secured entity to a user
     * 
     * @param securedEntity Secured entity to add
     * @param username Username of a user to add secured entity to
     */
    public void addSecuredEntity(SecuredEntity securedEntity, String username) {
        addSecuredEntity(Arrays.asList(securedEntity), username);
    }

    /**
     * Add a list of secured entities to a user
     * 
     * @param securedEntity Secured entities to add
     * @param username Username of a user to add secured entity to
     */
    @SuppressWarnings("rawtypes")
    public void addSecuredEntity(List<SecuredEntity> securedEntities, String username) {

        String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();
        AuthzClient authzClient = AuthzClient.create();
        ProtectedResource protectedResource = authzClient.protection(accessToken).resource();

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm and a client's internal id
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();

        AuthorizationResource authResource = realmResource.clients().get(clientId).authorization();
        PoliciesResource resourcePolicies = authResource.policies();

        String userPolicyName = KC_POLICY_USER_PREFIX + username.toLowerCase();

        for (SecuredEntity securedEntity : securedEntities) {

            String resourceName = securedEntity.getRule();
            String permissionName = resourceName;// + " - " + username.toLowerCase(); // When Resource is owner based

            // Create/find a resource representing a secured entity
            String[] resourceIds = protectedResource.find(null, resourceName, null, null, null, null, false, false, null, 1); // findByName(resourceName, username); // When Resource is owner
            String resourceId = resourceIds.length > 0 ? resourceIds[0] : null; // based
            if (resourceId == null) {
                ResourceRepresentation resource = new ResourceRepresentation(resourceName);
//            resource.setType(SecuredEntity.RESOURCE_TYPE);
//            resource.setOwner(username); // When Resource is owner based
                protectedResource.create(resource);
                resourceIds = protectedResource.find(null, resourceName, null, null, null, null, false, false, null, 1); // findByName(resourceName, username); // When Resource is owner based
                if (resourceIds.length == 0) {
                    throw new BusinessException("Was not able to create a KC resource for a secured entity");
                }
                resourceId = resourceIds[0];
            }

//        // For some reason created resource when returned by find does not contain ID
//        List<ResourceRepresentation> resources = authResource.resources().findByName(resourceName); // When Resource is owner
//        String resourceId = resources.size() > 0 ? resources.get(0).getId() : null;
//        if (resourceId == null) {
//            ResourceRepresentation resource = new ResourceRepresentation(resourceName);
//            resource.setType(SecuredEntity.RESOURCE_TYPE);
////            resource.setOwner(username); // When Resource is owner based
//            authResource.resources().create(resource);
//
//            resources = authResource.resources().findByName(resourceName); // findByName(resourceName, username); // When Resource is owner
//            if (resources.isEmpty()) {
//                throw new BusinessException("Was not able to create a KC resource for a secured entity");
//            }
//            resourceId = resources.get(0).getId();
//        }

            // Create a username based policy
            PolicyRepresentation userPolicy = resourcePolicies.findByName(userPolicyName);
            if (userPolicy == null) {
                UserPolicyRepresentation newPolicy = new UserPolicyRepresentation();
                newPolicy.setName(userPolicyName);
                newPolicy.addUser(username.toLowerCase());
                resourcePolicies.user().create(newPolicy);
                userPolicy = resourcePolicies.findByName(userPolicyName);
            }

            // Create a resource permission and associate username based policy to it
            ResourcePermissionRepresentation resourcePermission = authResource.permissions().resource().findByName(permissionName);
            if (resourcePermission == null) {
                resourcePermission = new ResourcePermissionRepresentation();
                resourcePermission.setName(permissionName);
                resourcePermission.addPolicy(userPolicy.getId());
                resourcePermission.addResource(resourceId);
                resourcePermission.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
                authResource.permissions().resource().create(resourcePermission);

            } else {
                List<PolicyRepresentation> policies = authResource.permissions().resource().findById(resourcePermission.getId()).associatedPolicies();
                resourcePermission.addPolicy(userPolicy.getId());
                for (PolicyRepresentation policy : policies) {
                    resourcePermission.addPolicy(policy.getId());
                }
                authResource.permissions().resource().findById(resourcePermission.getId()).update(resourcePermission);
            }
        }
    }

    /**
     * Synchronize API protection. Creates necessary roles and resources in Keycloak and removes the unneeded ones.
     * 
     * @param readLevel Protect API granularity level for find/search access
     * @param createLevel Protect API granularity level for create access
     * @param updateLevel Protect API granularity level for update access
     * @param deleteLevel Protect API granularity level for delete access
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void syncApiProtection(ApiProtectionGranularityEnum readLevel, ApiProtectionGranularityEnum createLevel, ApiProtectionGranularityEnum updateLevel, ApiProtectionGranularityEnum deleteLevel) {

        List<AccessScopeEnum> fullLevel = new ArrayList(Arrays.asList(AccessScopeEnum.values()));
        List<AccessScopeEnum> themeLevel = new ArrayList(Arrays.asList(AccessScopeEnum.values()));
        List<AccessScopeEnum> entityLevel = new ArrayList<AccessScopeEnum>();

        if (readLevel == null) {
            fullLevel.remove(AccessScopeEnum.LIST);
            themeLevel.remove(AccessScopeEnum.LIST);
        } else if (readLevel == ApiProtectionGranularityEnum.ENTITY_CLASS) {
            entityLevel.add(AccessScopeEnum.LIST);
        }

        if (createLevel == null) {
            fullLevel.remove(AccessScopeEnum.CREATE);
            themeLevel.remove(AccessScopeEnum.CREATE);
        } else if (createLevel == ApiProtectionGranularityEnum.ENTITY_CLASS) {
            entityLevel.add(AccessScopeEnum.CREATE);
        }

        if (updateLevel == null) {
            fullLevel.remove(AccessScopeEnum.UPDATE);
            themeLevel.remove(AccessScopeEnum.UPDATE);
        } else if (updateLevel == ApiProtectionGranularityEnum.ENTITY_CLASS) {
            entityLevel.add(AccessScopeEnum.UPDATE);
        }

        if (deleteLevel == null) {
            fullLevel.remove(AccessScopeEnum.DELETE);
            themeLevel.remove(AccessScopeEnum.DELETE);
        } else if (deleteLevel == ApiProtectionGranularityEnum.ENTITY_CLASS) {
            entityLevel.add(AccessScopeEnum.DELETE);
        }

        // Create all necessary roles
        syncGenericApiRoles(fullLevel, themeLevel, entityLevel);

        // Create resources, policies and permissions in keycloak based on resource protection level chosen
        createGenericApiAuthorization(readLevel, createLevel, updateLevel, deleteLevel);
    }

    /**
     * Create resources, policies and permissions in keycloak based on resource protection level chosen <br/>
     * 
     * /api/rest/generic/all/seller POST - search <br/>
     * /api/rest/generic/seller/<id> POST - find <br/>
     * /api/rest/generic/seller/<id> PUT - update <br/>
     * /api/rest/generic/seller/<id> DELETE - delete <br/>
     * /api/rest/generic/seller POST - create <br/>
     * 
     * @param readLevel Protect API granularity level for find/search access
     * @param createLevel Protect API granularity level for create access
     * @param updateLevel Protect API granularity level for update access
     * @param deleteLevel Protect API granularity level for delete access
     * 
     */
    @SuppressWarnings("rawtypes")
    private void createGenericApiAuthorization(ApiProtectionGranularityEnum readLevel, ApiProtectionGranularityEnum createLevel, ApiProtectionGranularityEnum updateLevel, ApiProtectionGranularityEnum deleteLevel) {

        Map<String, Set<String>> allManagedEntities = getAPIv2ManagedClassesByPackages();

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();

        AuthorizationResource authResource = realmResource.clients().get(clientId).authorization();

        String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();
        AuthzClient authzClient = AuthzClient.create();
        ProtectedResource protectedResource = authzClient.protection(accessToken).resource();

        Object[] actions = { AccessScopeEnum.LIST, readLevel, AccessScopeEnum.CREATE, createLevel, AccessScopeEnum.UPDATE, updateLevel, AccessScopeEnum.DELETE, deleteLevel };

        for (int i = 0; i < actions.length; i = i + 2) {

            AccessScopeEnum accessScope = (AccessScopeEnum) actions[i];

            String operation = accessScope.name().toLowerCase();
            ApiProtectionGranularityEnum level = (ApiProtectionGranularityEnum) actions[i + 1];

            String scopeName = accessScope == AccessScopeEnum.LIST || accessScope == AccessScopeEnum.CREATE ? "POST" : accessScope == AccessScopeEnum.UPDATE ? "PUT" : "DELETE";
            ScopeRepresentation scope = authResource.scopes().findByName(scopeName);

            // Create resources, policies and permissions
            for (Map.Entry<String, Set<String>> entry : allManagedEntities.entrySet()) {

                Set<String> subPackageEntities = entry.getValue();

                String packageLevelRoleName = GENERIC_API_ROLE_PACKAGE_LEVEL_PREFIX + entry.getKey() + "." + operation;
                String packageLevelResourceName = GENERIC_API_RESOURCE_PACKAGE_LEVEL_PREFIX + entry.getKey() + " - " + operation;
                String packageLevelPermissionName = packageLevelResourceName;
                String packageLevelRolePolicyName = KC_POLICY_ROLE_PREFIX + packageLevelRoleName;

                // When level is Package, create a single resource with multiple urls and a single permision and a single policy
                if (level == ApiProtectionGranularityEnum.PACKAGE) {

                    // Add all sub class urls
                    Set<String> uris = new HashSet<String>();
                    for (String entityClass : subPackageEntities) {
                        entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);
                        if (accessScope == AccessScopeEnum.LIST) {
                            uris.add("/api/rest/generic/all/" + entityClass);
                            uris.add("/api/rest/generic/" + entityClass + "/*");
                        } else if (accessScope == AccessScopeEnum.CREATE) {
                            uris.add("/api/rest/generic/" + entityClass);
                        } else if (accessScope == AccessScopeEnum.UPDATE) {
                            uris.add("/api/rest/generic/" + entityClass + "/*");
                        } else if (accessScope == AccessScopeEnum.DELETE) {
                            uris.add("/api/rest/generic/" + entityClass + "/*");
                        }
                    }

                    // Create a resource representing a package level, a resource permission and a role based policy
                    createGenericApiAuthorizationResource(packageLevelResourceName, packageLevelPermissionName, packageLevelRolePolicyName, packageLevelRoleName, uris, scope, keycloakAdminClientConfig.getClientId(),
                        authResource, protectedResource);

                    // Remove resources representing a class level, a resource permission and a role based policy
                    for (String entityClass : subPackageEntities) {
                        entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);

                        String classLevelRoleName = GENERIC_API_ROLE_CLASS_LEVEL_PREFIX + entityClass + "." + operation;
                        String classLevelResourceName = GENERIC_API_RESOURCE_CLASS_LEVEL_PREFIX + entityClass + " - " + operation;
                        String classLevelPermissionName = classLevelResourceName;
                        String classLevelRolePolicyName = KC_POLICY_ROLE_PREFIX + classLevelRoleName;
                        removeGenericApiAuthorizationResource(classLevelResourceName, classLevelPermissionName, classLevelRolePolicyName, authResource, protectedResource);
                    }

                    // When level is Package, create a separate resource, permission and policy per class
                } else if (level == ApiProtectionGranularityEnum.ENTITY_CLASS) {

                    // Remove a resource representing a package level, a resource permission and a role based policy
                    removeGenericApiAuthorizationResource(packageLevelResourceName, packageLevelPermissionName, packageLevelRolePolicyName, authResource, protectedResource);

                    // Create a resource representing a class level, a resource permission and a role based policy
                    for (String entityClass : subPackageEntities) {
                        entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);

                        String classLevelRoleName = GENERIC_API_ROLE_CLASS_LEVEL_PREFIX + entityClass + "." + operation;
                        String classLevelResourceName = GENERIC_API_RESOURCE_CLASS_LEVEL_PREFIX + entityClass + " - " + operation;
                        String classLevelPermissionName = classLevelResourceName;
                        String classLevelRolePolicyName = KC_POLICY_ROLE_PREFIX + classLevelRoleName;

                        Set<String> uris = new HashSet<String>();
                        if (accessScope == AccessScopeEnum.LIST) {
                            uris.add("/api/rest/generic/all/" + entityClass);
                            uris.add("/api/rest/generic/" + entityClass + "/*");
                        } else if (accessScope == AccessScopeEnum.CREATE) {
                            uris.add("/api/rest/generic/" + entityClass);
                        } else if (accessScope == AccessScopeEnum.UPDATE) {
                            uris.add("/api/rest/generic/" + entityClass + "/*");
                        } else if (accessScope == AccessScopeEnum.DELETE) {
                            uris.add("/api/rest/generic/" + entityClass + "/*");
                        }

                        createGenericApiAuthorizationResource(classLevelResourceName, classLevelPermissionName, classLevelRolePolicyName, classLevelRoleName, uris, scope, keycloakAdminClientConfig.getClientId(),
                            authResource, protectedResource);
                    }

                } else {

                    // Remove a resource representing a package level, a resource permission and a role based policy
                    removeGenericApiAuthorizationResource(packageLevelResourceName, packageLevelPermissionName, packageLevelRolePolicyName, authResource, protectedResource);

                    // Remove resources representing a class level, a resource permission and a role based policy
                    for (String entityClass : subPackageEntities) {
                        entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);

                        String classLevelRoleName = GENERIC_API_ROLE_CLASS_LEVEL_PREFIX + entityClass + "." + operation;
                        String classLevelResourceName = GENERIC_API_RESOURCE_CLASS_LEVEL_PREFIX + entityClass + " - " + operation;
                        String classLevelPermissionName = classLevelResourceName;
                        String classLevelRolePolicyName = KC_POLICY_ROLE_PREFIX + classLevelRoleName;
                        removeGenericApiAuthorizationResource(classLevelResourceName, classLevelPermissionName, classLevelRolePolicyName, authResource, protectedResource);
                    }
                }
            }
        }
    }

    /**
     * Create a resource, role based policy and permission associated to a resource
     * 
     * @param resourceName Resource name
     * @param permissionName Permission name
     * @param policyName Policy name
     * @param roleName Role name to link a policy to
     * @param uris A list of URI to associate with a resource
     * @param scope Scope
     * @param clientId Client id
     * @param authResource Authorization resource endpoint
     * @param protectedResource Protected resource endpoint
     */
    private void createGenericApiAuthorizationResource(String resourceName, String permissionName, String policyName, String roleName, Set<String> uris, ScopeRepresentation scope, String clientId,
            AuthorizationResource authResource, ProtectedResource protectedResource) {

        // Create/find a resource representing a package level

        String[] resourceIds = protectedResource.find(null, resourceName, null, null, null, null, false, false, null, 1);
        String resourceId = resourceIds.length > 0 ? resourceIds[0] : null;
        if (resourceId == null) {
            ResourceRepresentation resource = new ResourceRepresentation(resourceName);
            resource.addScope(scope);
            resource.setUris(uris);
            protectedResource.create(resource);
            resourceIds = protectedResource.find(null, resourceName, null, null, null, null, false, false, null, 1); // findByName(resourceName, username); // When Resource is owner based
            if (resourceIds.length == 0) {
                throw new BusinessException("Was not able to create a KC resource for a package " + resourceName);
            }
            resourceId = resourceIds[0];
        }

        // Create/find a role based policy
        PoliciesResource resourcePolicies = authResource.policies();
        PolicyRepresentation rolePolicy = resourcePolicies.findByName(policyName);
        if (rolePolicy == null) {
            RolePolicyRepresentation newPolicy = new RolePolicyRepresentation();
            newPolicy.setName(policyName);
            newPolicy.addClientRole(clientId, roleName);
            resourcePolicies.role().create(newPolicy);
            rolePolicy = resourcePolicies.findByName(policyName);
        }

        // Create a resource permission and associate role based policy to it
        ScopePermissionRepresentation resourcePermission = authResource.permissions().scope().findByName(permissionName);
        if (resourcePermission == null) {
            resourcePermission = new ScopePermissionRepresentation();
            resourcePermission.setName(permissionName);
            resourcePermission.addPolicy(rolePolicy.getId());
            resourcePermission.addResource(resourceId);
            resourcePermission.addScope(scope.getId());
            resourcePermission.setDecisionStrategy(DecisionStrategy.AFFIRMATIVE);
            authResource.permissions().scope().create(resourcePermission);

        } else {
            List<PolicyRepresentation> policies = authResource.permissions().scope().findById(resourcePermission.getId()).associatedPolicies();
            resourcePermission.addPolicy(rolePolicy.getId());
            for (PolicyRepresentation policy : policies) {
                resourcePermission.addPolicy(policy.getId());
            }
            authResource.permissions().scope().findById(resourcePermission.getId()).update(resourcePermission);
        }
    }

    /**
     * Remove resource, policy and permission associated to a resource
     * 
     * @param resourceName Resource name
     * @param permissionName Permission name
     * @param policyName Policy name
     * @param authResource Authorization resource endpoint
     * @param protectedResource Protected resource endpoint
     */
    private void removeGenericApiAuthorizationResource(String resourceName, String permissionName, String policyName, AuthorizationResource authResource, ProtectedResource protectedResource) {
        // Remove a resource permission
        ScopePermissionRepresentation resourcePermission = authResource.permissions().scope().findByName(permissionName);
        if (resourcePermission != null) {
            authResource.permissions().scope().findById(resourcePermission.getId()).remove();
        }

        // Remove a resource representing a package level
        String[] resourceIds = protectedResource.find(null, resourceName, null, null, null, null, false, false, null, 1);
        String resourceId = resourceIds.length > 0 ? resourceIds[0] : null;
        if (resourceId != null) {
            protectedResource.delete(resourceId);
        }

        // Remove a role based policy
        PoliciesResource resourcePolicies = authResource.policies();
        PolicyRepresentation rolePolicy = resourcePolicies.findByName(policyName);
        if (rolePolicy != null) {
            resourcePolicies.policy(rolePolicy.getId()).remove();
        }

    }

    /**
     * Create missing and remove unwanted roles for API entity based permissions.
     * 
     * @param fullLevel Expected access scope granularity for Full access level
     * @param packageLevel Expected access scope granularity for theme/package access level
     * @param classLevel Expected access scope granularity for entity class access level
     * 
     * @return A number or permissions created/deleted
     */
    private int syncGenericApiRoles(List<AccessScopeEnum> fullLevel, List<AccessScopeEnum> packageLevel, List<AccessScopeEnum> classLevel) {

        Map<String, Set<String>> allManagedEntities = getAPIv2ManagedClassesByPackages();

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();
        ClientResource client = realmResource.clients().get(clientId);

        int newPermissionsCreated = 0;

        for (AccessScopeEnum accessScope : AccessScopeEnum.values()) {
            String operationSuffix = "." + accessScope.name().toLowerCase();

            boolean fullAccessLevelRoleNeeded = fullLevel.contains(accessScope);
            boolean packageLevelAccessRoleNeeded = packageLevel.contains(accessScope);
            boolean classLevelAccessRoleNeeded = classLevel.contains(accessScope);

            // Create/find Full access role
            String fullAccessLevelRoleName = API_FULL_ACCESS + operationSuffix;
            RoleResource fullAccessRoleResource = client.roles().get(fullAccessLevelRoleName);

            List<RoleRepresentation> roleSearch = client.roles().list(fullAccessLevelRoleName, false);
            RoleRepresentation fullAccessRoleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
            if (fullAccessRoleRepresentation == null && fullAccessLevelRoleNeeded) {
                RoleRepresentation role = new RoleRepresentation(fullAccessLevelRoleName, null, false);
                role.setComposite(true);
                client.roles().create(role);
                fullAccessRoleRepresentation = client.roles().get(fullAccessLevelRoleName).toRepresentation();

            } else if (fullAccessRoleRepresentation != null && !fullAccessLevelRoleNeeded) {
                fullAccessRoleResource.remove();
                fullAccessRoleResource = null;

            } else if (!fullAccessLevelRoleNeeded) {
                fullAccessRoleResource = null;
            }

            // Create/find package and class level roles

            for (Map.Entry<String, Set<String>> entry : allManagedEntities.entrySet()) {
                String packageLevelRoleName = GENERIC_API_ROLE_PACKAGE_LEVEL_PREFIX + entry.getKey() + operationSuffix;
                Set<String> subPackageEntities = entry.getValue();

                // Create/find package level role
                RoleResource packageLevelRoleResource = client.roles().get(packageLevelRoleName);

                roleSearch = client.roles().list(packageLevelRoleName, false);
                RoleRepresentation packageLevelRoleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
                if (packageLevelRoleRepresentation == null && packageLevelAccessRoleNeeded) {
                    RoleRepresentation role = new RoleRepresentation(packageLevelRoleName, null, false);
                    role.setComposite(true);
                    client.roles().create(role);
                    packageLevelRoleRepresentation = client.roles().get(packageLevelRoleName).toRepresentation();
                    newPermissionsCreated++;

                    // Add it to full access role as a child
                    if (fullAccessRoleResource != null) {
                        fullAccessRoleResource.addComposites(Arrays.asList(packageLevelRoleRepresentation));
                    }
                } else if (packageLevelRoleRepresentation != null && !packageLevelAccessRoleNeeded) {
                    packageLevelRoleResource.remove();
                    packageLevelRoleResource = null;

                } else if (!packageLevelAccessRoleNeeded) {
                    packageLevelRoleResource = null;
                }

                List<RoleRepresentation> childRolesToAdd = new ArrayList<RoleRepresentation>();
                // Create/find class level role
                for (String entityFullName : subPackageEntities) {
                    String entityName = entityFullName.substring(entityFullName.lastIndexOf(".") + 1);

                    String classLevelRoleName = GENERIC_API_ROLE_CLASS_LEVEL_PREFIX + entityName + operationSuffix;

                    RoleResource classLevelRoleResource = client.roles().get(classLevelRoleName);
                    roleSearch = client.roles().list(classLevelRoleName, true);
                    RoleRepresentation classLevelRoleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
                    if (classLevelRoleRepresentation == null && classLevelAccessRoleNeeded) {
                        RoleRepresentation role = new RoleRepresentation(classLevelRoleName, null, false);
                        client.roles().create(role);
                        classLevelRoleRepresentation = client.roles().get(classLevelRoleName).toRepresentation();

                        newPermissionsCreated++;

                        // Add it to theme or full access role as a child
                        childRolesToAdd.add(classLevelRoleRepresentation);

                    } else if (classLevelRoleRepresentation != null && !classLevelAccessRoleNeeded) {
                        classLevelRoleResource.remove();
                    }
                }

                // Add it to theme or full access role as a child
                if (!childRolesToAdd.isEmpty()) {
                    if (packageLevelRoleResource != null) {
                        packageLevelRoleResource.addComposites(childRolesToAdd);

                    } else if (fullAccessRoleResource != null) {
                        fullAccessRoleResource.addComposites(childRolesToAdd);
                    }
                }
            }
        }
        return newPermissionsCreated;
    }

    /**
     * Lookup recursively a group by its name
     * 
     * @param groupName Group name to match
     * @param groups A list of groups to iterate through recusively
     * @return A matching group by its name
     */
    private GroupRepresentation findGroup(String groupName, List<GroupRepresentation> groups) {

        for (GroupRepresentation group : groups) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
            if (!group.getSubGroups().isEmpty()) {
                group = findGroup(groupName, group.getSubGroups());
                if (group != null) {
                    return group;
                }
            }
        }
        return null;
    }
}