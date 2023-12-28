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
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementAlreadyExistsException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidParameterException;
import org.meveo.admin.exception.UsernameAlreadyExistsException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.commons.utils.ResteasyClientProxyBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.security.AccessScopeEnum;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.UserGroup;
import org.meveo.security.keycloak.AuthenticationProvider;
import org.meveo.service.admin.impl.UserService;
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
     * A prefix to add to a role based policy
     */
    private static final String KC_POLICY_ROLE_PREFIX = "Role ";
	
	@Inject
	private UserService userService;

    /**
     * Reads the configuration from system property.
     * 
     * @return KeycloakAdminClientConfig
     */
    private KeycloakAdminClientConfig loadConfig() {
        KeycloakAdminClientConfig keycloakAdminClientConfig = new KeycloakAdminClientConfig();
        try {
            // override from system property.
            // opencell.keycloak.url-internal is used for internal communication with Keycloak and
            // opencell.keycloak.url is used for a redirect to login in a browser
            String keycloakServerUrl = System.getProperty("opencell.keycloak.url-internal");
            if (StringUtils.isBlank(keycloakServerUrl)) {
                keycloakServerUrl = System.getProperty("opencell.keycloak.url");
            }
            if (!StringUtils.isBlank(keycloakServerUrl)) {
                keycloakAdminClientConfig.setServerUrl(keycloakServerUrl);
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

        keycloakBuilder.resteasyClient(new ResteasyClientProxyBuilder().connectionPoolSize(200).maxPooledPerRoute(200).build());

        return keycloakBuilder.build();
    }

    /**
     * List users in KC from a current realm
     * 
     * @param paginationConfig and pagination criteria
     */
    public List<User> listUsers(PaginationConfiguration paginationConfig) {

        String username = (String) paginationConfig.getFilters().get("userName");
        String firstName = (String) paginationConfig.getFilters().get("name.firstName");
        String lastName = (String) paginationConfig.getFilters().get("name.lastName");
        String email = (String) paginationConfig.getFilters().get("email");

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        List<UserRepresentation> users = usersResource.search(username!=null?username.toLowerCase():null, firstName, lastName, email, paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows());
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
     * @param paginationConfig and pagination criteria
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
        return createOrUpdateUser(userName, firstName, lastName, email, password, userGroup, roles, providerToOverride, false, null);
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
    public String updateUser(String userName, String firstName, String lastName, String email, String password, String userGroup, Collection<String> roles, Map<String, String> attributes)
            throws InvalidParameterException, ElementNotFoundException, UsernameAlreadyExistsException {
        return createOrUpdateUser(userName, firstName, lastName, email, password, userGroup, roles, null, true, attributes);
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
    private String createOrUpdateUser(String userName, String firstName, String lastName, String email, String password, String userGroup, Collection<String> roles, String providerToOverride, boolean isUpdate, Map<String, String> pAttributes)
            throws InvalidParameterException, ElementNotFoundException, UsernameAlreadyExistsException {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
		log.info("realm resource keycloak : " + realmResource);
        UsersResource usersResource = realmResource.users();
	    log.info("usersResource : " + usersResource);
        UserRepresentation user = null;

        if (StringUtils.isBlank(userName) && StringUtils.isBlank(email)) {
            throw new InvalidParameterException("Either userName or email must be provided to create a user");
        }

        // Default username to email if username is not provided
        if (StringUtils.isBlank(userName)) {
            userName = email;
        }

        List<UserRepresentation> users = usersResource.search(userName!=null?userName.toLowerCase():null, true);
        for (UserRepresentation userRepresentation : users) {
            if (userRepresentation.getUsername().equalsIgnoreCase(userName)) {
                user = userRepresentation;
                break;
            }
        }
		User userFromDb = userService.getUserFromDatabase(userName);
		if(userFromDb != null &&  user == null) {
			isUpdate = false;
		}else if (isUpdate && user == null) {
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

        //Check if update and attributes are not empty then add the list to user object
        if(isUpdate && pAttributes != null && !pAttributes.isEmpty()) {
            Map<String, List<String>> attributes = user.getAttributes();

            if(attributes == null) {
                attributes = new HashMap<>();
            }

            for (Map.Entry<String, String> entry : pAttributes.entrySet()) {
                attributes.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }

            user.setAttributes(attributes);
        }

        List<RoleRepresentation> rolesToAdd = checkAndBuildRoles(roles, realmResource);
        List<GroupRepresentation> groupsToAdd = checkAndBuildGroups(userGroup, realmResource);

        String userId = null;

        // Update current user
        if (isUpdate) {
            userId = user.getId();
			try{
				usersResource.get(userId).update(user);
			}catch (BusinessApiException e){
				log.warn("Impossible to update user on keycloak : " + usersResource.get(userId));
				log.error("error when updating user  : " + user);
			}
        
        } else {
            // Create a new user
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

        updateGroups(groupsToAdd, isUpdate, usersResource, user, userId);
        updateRoles(rolesToAdd, isUpdate, usersResource, user, userId);

        // Define password credential
        if (password != null) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            usersResource.get(userId).resetPassword(credential);// Set password credential
        }

        return userId;
    }

    /**
     * Check and build groups
     * @param userGroup User Group
     * @param realmResource {@link RealmResource}
     * @return List of {@link GroupRepresentation}
     */
    private List<GroupRepresentation> checkAndBuildGroups(String userGroup, RealmResource realmResource) {
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

        return groupsToAdd;
    }

    /**
     * Check and build roles
     * @param roles User Group
     * @param realmResource {@link RealmResource}
     * @return List of {@link RoleRepresentation}
     */
    private static List<RoleRepresentation> checkAndBuildRoles(Collection<String> roles, RealmResource realmResource) {
        // Determine roles requested and validate that they exist
        List<RoleRepresentation> rolesToAdd = new ArrayList<>();

        if (roles != null && !roles.isEmpty()) {
            RolesResource rolesResource = realmResource.roles();

            for (String role : roles) {
                try {
                    RoleRepresentation tempRole = rolesResource.get(role).toRepresentation();
                    rolesToAdd.add(tempRole);
                } catch (NotFoundException e) {
                	if("KC".equals(ParamBean.getInstance().getProperty("userManagement.master", "KC"))) {
                		throw new InvalidParameterException("Role " + role + " was not found");
                	}
                    
                }
            }
        }

        return rolesToAdd;
    }

    /**
     * Update Keycloak roles
     * @param rolesToAdd Roles to add
     * @param isUpdate Is update mode
     * @param usersResource {@link org.keycloak.admin.client.resource.UserResource}
     * @param user {@link UserRepresentation}
     * @param userId User Id
     */
    private static void updateRoles(List<RoleRepresentation> rolesToAdd, boolean isUpdate, UsersResource usersResource, UserRepresentation user, String userId) {
        // Check if the roles to add exists already in the current roles then remove it from the roles to add
        if (isUpdate) {
            List<RoleRepresentation> currentRoles = usersResource.get(user.getId()).roles().realmLevel().listAll();
            rolesToAdd.removeAll(currentRoles);
        }
        if (!rolesToAdd.isEmpty()) {
            usersResource.get(userId).roles().realmLevel().add(rolesToAdd);
        }
    }

    /**
     * Update Keycloak Groups
     * @param groupsToAdd Groups to add
     * @param isUpdate Is update mode
     * @param usersResource {@link org.keycloak.admin.client.resource.UserResource}
     * @param user {@link UserRepresentation}
     * @param userId User id
     */
    private void updateGroups(List<GroupRepresentation> groupsToAdd, boolean isUpdate, UsersResource usersResource, UserRepresentation user, String userId) {
        // Check if the groups to add exists already in the current groups then remove it from the groups to add
        if (isUpdate) {
            List<GroupRepresentation> currentGroups = usersResource.get(user.getId()).groups();
            groupsToAdd.removeAll(currentGroups);
        }
        if (!groupsToAdd.isEmpty()) {
            for (GroupRepresentation group : groupsToAdd) {
                usersResource.get(userId).joinGroup(group.getId());
            }
        }
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

        // Delete user (requires manage-users role)
        Response response = usersResource.delete(userRepresentation.getId());

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            log.error("Keycloak user deletion with httpStatusCode={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            throw new BusinessException("Unable to delete user with httpStatusCode=" + response.getStatus());
        }
    }

    /**
     * Delete a role in Keycloak.
     * 
     * @param name Role name
     * @param isClientRole Is it a client role
     */
    public void deleteRole(String name, boolean isClientRole) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();
        ClientResource client = realmResource.clients().get(clientId);

        // Find a role
        RoleRepresentation roleRepresentation = null;
        if (isClientRole) {
            List<RoleRepresentation> roleSearch = client.roles().list(name, false);
            roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;

            if (roleRepresentation != null) {
                client.roles().deleteRole(name);
            }
        } else {
            List<RoleRepresentation> roleSearch = realmResource.roles().list(name, false);
            roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
            if (roleRepresentation != null) {
                realmResource.roles().deleteRole(name);
            }
        }
    }

    /**
     * List/Search the <b>realm</b> roles in Keycloak.
     * 
     * @param paginationConfig An optional search and pagination criteria. A filter criteria "name" is used to filter by role name.
     * @return List of roles
     */
    public List<Role> listRoles(PaginationConfiguration paginationConfig) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        if (paginationConfig != null && paginationConfig.getFilters() != null && paginationConfig.getFilters().containsKey("name")) {
            return realmResource.roles().list((String) paginationConfig.getFilters().get("name"), paginationConfig.getFirstRow(), paginationConfig.getNumberOfRows()).stream()
                .map(p -> new Role(p.getName(), p.getDescription())).collect(Collectors.toList());
        } else {
            return realmResource.roles().list().stream().map(p -> new Role(p.getName(), p.getDescription())).collect(Collectors.toList());
        }
    }

    /**
     * Create a role as a child of a parent role if provided. An attempt to create a role again will be ignored.
     * 
     * @param name Role name
     * @param description Role description
     * @param isClientRole Is it a client role
     */
    public void createRole(String name, String description, boolean isClientRole) {
        createRole(name, description, isClientRole, null, null, isClientRole);
    }

    /**
     * Create a role as a child of a parent role if provided. An attempt to create a role again will be ignored and will act as assignment only to a parent role.
     * 
     * @param name Role name
     * @param description Role description
     * @param isClientRole Is it a client role
     * @param parentRole Parent role name. Role will be created if does not exist yet.
     * @param parentRoleDescription Parent role description
     * @param isClientParentRole Is parent role a client role
     */
    public void createRole(String name, String description, boolean isClientRole, String parentRole, String parentRoleDescription, boolean isClientParentRole) {
        createOrUpdateRole(name, description, isClientRole, false, parentRole, parentRoleDescription, isClientParentRole);
    }

    /**
     * Update a role as a child of a parent role if provided. An attempt to create a role again will be ignored.
     * 
     * @param name Role name
     * @param description Role description
     * @param isClientRole Is it a client role
     */
    public void updateRole(String name, String description, boolean isClientRole) {
        updateRole(name, description, isClientRole, null, null, isClientRole);
    }

    /**
     * Update a role as a child of a parent role if provided. An attempt to create a role again will be ignored and will act as assignment only to a parent role.
     * 
     * @param name Role name
     * @param description Role description
     * @param isClientRole Is it a client role
     * @param parentRole Parent role name. Role will be created if does not exist yet.
     * @param parentRoleDescription Parent role description
     * @param isClientParentRole Is parent role a client role
     */
    public void updateRole(String name, String description, boolean isClientRole, String parentRole, String parentRoleDescription, boolean isClientParentRole) {
        createOrUpdateRole(name, description, isClientRole, true, parentRole, parentRoleDescription, isClientParentRole);
    }

    /**
     * Create a role as a child of a parent role if provided. An attempt to create a role again will be ignored and will act as assignment only to a parent role.
     * 
     * @param name Role name
     * @param description Role description
     * @param isClientRole Is it a client role
     * @param isUpdate Is this update operation
     * @param parentRole Parent role name. Role will be created if does not exist yet.
     * @param parentRoleDescription Parent role description
     * @param isClientParentRole Is parent role a client role
     */
    public void createOrUpdateRole(String name, String description, boolean isClientRole, boolean isUpdate, String parentRole, String parentRoleDescription, boolean isClientParentRole) {

        if (StringUtils.isBlank(name)) {
            throw new InvalidParameterException("Name must be provided to create a role");
        }

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();
        ClientResource client = realmResource.clients().get(clientId);

        RoleResource roleResource = null;
        RoleRepresentation roleRepresentation = null;
        if (isClientRole) {
            roleResource = client.roles().get(name);
            List<RoleRepresentation> roleSearch = client.roles().list(name, false);
            roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;

        } else {
            roleResource = realmResource.roles().get(name);
            List<RoleRepresentation> roleSearch = realmResource.roles().list(name, false);
            roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
        }

        if (isUpdate && roleRepresentation == null) {
            throw new ElementNotFoundException("Role with name " + name + " not found");

            // An attempt to create a role again will be ignored and will act as assignment only to a parent role.
            // } else if (!isUpdate && roleRepresentation != null) {
            // throw new ElementAlreadyExistsException(name, "Role");
        }

        // Create a new role
        if (roleRepresentation == null) {
            roleRepresentation = new RoleRepresentation(name, description, false);
            if (isClientRole) {
                client.roles().create(roleRepresentation);
                List<RoleRepresentation> roleSearch = client.roles().list(name, false);
                roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
            } else {
                realmResource.roles().create(roleRepresentation);
                List<RoleRepresentation> roleSearch = realmResource.roles().list(name, false);
                roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
            }

            // Update existing role
        } else {
            roleRepresentation.setDescription(description);
            roleResource.update(roleRepresentation);
        }

        // Assign role to the parent - create parent role if does not exist yet
        if (parentRole != null) {
            if (isClientRole) {
                roleResource = client.roles().get(parentRole);
                List<RoleRepresentation> roleSearch = client.roles().list(parentRole, false);
                RoleRepresentation parentRoleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
                if (parentRoleRepresentation == null) {
                    RoleRepresentation role = new RoleRepresentation(parentRole, parentRoleDescription, false);
                    client.roles().create(role);
                    parentRoleRepresentation = roleResource.toRepresentation();
                }
            } else {
                roleResource = realmResource.roles().get(parentRole);
                List<RoleRepresentation> roleSearch = realmResource.roles().list(parentRole, false);
                RoleRepresentation parentRoleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
                if (parentRoleRepresentation == null) {
                    RoleRepresentation role = new RoleRepresentation(parentRole, parentRoleDescription, false);
                    realmResource.roles().create(role);
                    parentRoleRepresentation = roleResource.toRepresentation();
                }
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
     * Find a role by name in Keycloak.
     * 
     * @param roleName Role name
     * @param extendedInfo Shall child roles be retrieved - NOT implemented now
     * @param isClientRole Is this a client role
     * @return Role
     */
    public Role findRole(String roleName, boolean extendedInfo, boolean isClientRole) {

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();
        ClientResource client = realmResource.clients().get(clientId);

        // Create a role
        RoleRepresentation roleRepresentation = null;
        if (isClientRole) {
            List<RoleRepresentation> roleSearch = client.roles().list(roleName, false);
            roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;

        } else {
            List<RoleRepresentation> roleSearch = realmResource.roles().list(roleName, false);
            roleRepresentation = roleSearch.size() > 0 ? roleSearch.get(0) : null;
        }

        if (roleRepresentation != null) {
            Role role = new Role();
            role.setName(roleRepresentation.getName());
            role.setDescription(roleRepresentation.getDescription());
            return role;
        }
        return null;
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
        List<UserRepresentation> users = usersResource.search(username!=null?username.toLowerCase():null, true);
        for (UserRepresentation userRepresentation : users) {
            if (username != null && !username.isEmpty() && username.equalsIgnoreCase(userRepresentation.getUsername())) {
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
     * Synchronize API protection. Creates necessary roles and resources in Keycloak and removes the unneeded ones.
     * 
     * @param level Protect API granularity level
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void syncApiProtection(ApiProtectionGranularityEnum level) {

        // By default package level protection
        List<AccessScopeEnum> fullLevel = new ArrayList(Arrays.asList(AccessScopeEnum.values()));
        List<AccessScopeEnum> themeLevel = new ArrayList(Arrays.asList(AccessScopeEnum.values()));
        List<AccessScopeEnum> entityLevel = new ArrayList<AccessScopeEnum>();

        // Requested to remove all protection
        if (level == null) {
            fullLevel.clear();
            themeLevel.clear();

            // Entity class level protection requested
        } else if (level == ApiProtectionGranularityEnum.ENTITY_CLASS) {
            entityLevel.addAll(Arrays.asList(AccessScopeEnum.values()));
        }

        // Create all necessary roles
        if (level != null) {
            syncGenericApiRoles(fullLevel, themeLevel, entityLevel);
        }

        // Create resources, policies and permissions in keycloak based on resource protection level chosen
        createGenericApiAuthorization(level);

        if (level == null) {
            // Will remove all roles
            syncGenericApiRoles(fullLevel, themeLevel, entityLevel);
        }
    }

    /**
     * Create resources, policies and permissions in keycloak based on resource protection level chosen <br/>
     * 
     * /api/rest/v2/generic/all/seller POST - search <br/>
     * /api/rest/v2/generic/seller/<id> POST - find <br/>
     * /api/rest/v2/generic/seller/<id> PUT - update <br/>
     * /api/rest/v2/generic/seller/<id> DELETE - delete <br/>
     * /api/rest/v2/generic/seller POST - create <br/>
     * 
     * @param level Protect API granularity level
     * 
     */
    @SuppressWarnings("rawtypes")
    private void createGenericApiAuthorization(ApiProtectionGranularityEnum level) {

        Map<String, Set<String>> allManagedEntities = getAPIv2ManagedClassesByPackages();

        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();

        String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();

        // Create resources, policies and permissions
        for (Map.Entry<String, Set<String>> entry : allManagedEntities.entrySet()) {

            // Obtain KC REST resources. Moved here from above as sometime KC gives 401 error if execution takes too long
            Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);
            RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
            String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();
            AuthorizationResource authResource = realmResource.clients().get(clientId).authorization();

            AuthzClient authzClient = AuthenticationProvider.getKcAuthzClient();

            ProtectedResource protectedResource = authzClient.protection(accessToken).resource();

            Set<String> subPackageEntities = entry.getValue();
            String packageName = entry.getKey();

            String packageLevelResourceNamePrefix = GENERIC_API_RESOURCE_PACKAGE_LEVEL_PREFIX + packageName;
            String packageLevelPermissionNamePrefix = packageLevelResourceNamePrefix;
            String packageLevelRoleNamePrefix = GENERIC_API_ROLE_PACKAGE_LEVEL_PREFIX + packageName;

            // When level is Package, create a single resource with multiple urls and a single permision and a single policy
            if (level == ApiProtectionGranularityEnum.PACKAGE) {

                Set<String> listUris = new HashSet<String>();
                Set<String> createUris = new HashSet<String>();
                Set<String> fudUris = new HashSet<String>();

                // Add all sub class urls
                for (String entityClass : subPackageEntities) {
                    entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);
                    entityClass = entityClass.substring(0, 1).toLowerCase() + entityClass.substring(1);

                    // List
                    listUris.add("/api/rest/v2/generic/all/" + entityClass);
                    listUris.add("/api/rest/v2/generic/all/" + entityClass + "/*");
                    // Create
                    createUris.add("/api/rest/v2/generic/" + entityClass);
                    createUris.add("/api/rest/v2/generic/" + entityClass + "/");
                    // Find
                    fudUris.add("/api/rest/v2/generic/" + entityClass + "/*");
                    fudUris.add("/api/rest/v2/generic/export/" + entityClass + "/*");
                    // Update and delete
                    fudUris.add("/api/rest/v2/generic/" + entityClass + "/*");

                }

                // Create a resource representing a package level, a resource permission and a role based policy
                createGenericApiAuthorizationResource(packageLevelResourceNamePrefix, packageLevelPermissionNamePrefix, packageLevelRoleNamePrefix, listUris, createUris, fudUris, keycloakAdminClientConfig.getClientId(),
                    clientId, authResource, protectedResource);

                // Remove resources representing a class level, a resource permission and a role based policy
                for (String entityClass : subPackageEntities) {
                    entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);

                    String classLevelResourceNamePrefix = GENERIC_API_RESOURCE_CLASS_LEVEL_PREFIX + entityClass;
                    String classLevelPermissionNamePrefix = classLevelResourceNamePrefix;
                    String classLevelRoleNamePrefix = GENERIC_API_ROLE_CLASS_LEVEL_PREFIX + entityClass;

                    removeGenericApiAuthorizationResource(classLevelResourceNamePrefix, classLevelPermissionNamePrefix, classLevelRoleNamePrefix, authResource, protectedResource);
                }

                // When level is Package, create a separate resource, permission and policy per class
            } else if (level == ApiProtectionGranularityEnum.ENTITY_CLASS) {

                // Remove a resource representing a package level, a resource permission and a role based policy
                removeGenericApiAuthorizationResource(packageLevelResourceNamePrefix, packageLevelPermissionNamePrefix, packageLevelRoleNamePrefix, authResource, protectedResource);

                // Create a resource representing a class level, a resource permission and a role based policy
                for (String entityClass : subPackageEntities) {
                    entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);

                    String classLevelResourceNamePrefix = GENERIC_API_RESOURCE_CLASS_LEVEL_PREFIX + entityClass;
                    String classLevelPermissionNamePrefix = classLevelResourceNamePrefix;
                    String classLevelRoleNamePrefix = GENERIC_API_ROLE_CLASS_LEVEL_PREFIX + entityClass;

                    String entityClassForUrl = entityClass.substring(0, 1).toLowerCase() + entityClass.substring(1);

                    Set<String> listUris = new HashSet<String>();
                    Set<String> createUris = new HashSet<String>();
                    Set<String> fudUris = new HashSet<String>();

                    // List
                    listUris.add("/api/rest/v2/generic/all/" + entityClassForUrl);
                    listUris.add("/api/rest/v2/generic/all/" + entityClassForUrl + "/*");
                    // Create
                    createUris.add("/api/rest/v2/generic/" + entityClassForUrl);
                    createUris.add("/api/rest/v2/generic/" + entityClassForUrl + "/");
                    // Find
                    fudUris.add("/api/rest/v2/generic/" + entityClassForUrl + "/*");
                    // Update and delete
                    fudUris.add("/api/rest/v2/generic/" + entityClassForUrl + "/*");

                    createGenericApiAuthorizationResource(classLevelResourceNamePrefix, classLevelPermissionNamePrefix, classLevelRoleNamePrefix, listUris, createUris, fudUris, keycloakAdminClientConfig.getClientId(),
                        clientId, authResource, protectedResource);
                }

            } else {

                // Remove a resource representing a package level, a resource permission and a role based policy
                removeGenericApiAuthorizationResource(packageLevelResourceNamePrefix, packageLevelPermissionNamePrefix, packageLevelRoleNamePrefix, authResource, protectedResource);

                // Remove resources representing a class level, a resource permission and a role based policy
                for (String entityClass : subPackageEntities) {
                    entityClass = entityClass.substring(entityClass.lastIndexOf('.') + 1);

                    String classLevelResourceNamePrefix = GENERIC_API_RESOURCE_CLASS_LEVEL_PREFIX + entityClass;
                    String classLevelPermissionNamePrefix = classLevelResourceNamePrefix;
                    String classLevelRoleNamePrefix = GENERIC_API_ROLE_CLASS_LEVEL_PREFIX + entityClass;

                    removeGenericApiAuthorizationResource(classLevelResourceNamePrefix, classLevelPermissionNamePrefix, classLevelRoleNamePrefix, authResource, protectedResource);
                }
            }
        }
    }

    /**
     * Create a resource, role based policy and permission associated to a resource
     * 
     * @param resourceNamePrefix Resource name prefix
     * @param permissionNamePrefix Permission name prefix
     * @param roleNamePrefix Role name to link a policy to prefix
     * @param listUris A list of URI to associate with a List action type resource
     * @param createUris A list of URI to associate with a Create action type resource
     * @param fudUris A list of URI to associate with a Find, Update, Delete action type resource
     * @param clientName Client name
     * @param clientId Client id
     * @param authResource Authorization resource endpoint
     * @param protectedResource Protected resource endpoint
     */
    private void createGenericApiAuthorizationResource(String resourceNamePrefix, String permissionNamePrefix, String roleNamePrefix, Set<String> listUris, Set<String> createUris, Set<String> fudUris, String clientName,
            String clientId, AuthorizationResource authResource, ProtectedResource protectedResource) {

        String listResourceName = resourceNamePrefix + " - list";
        String createResourceName = resourceNamePrefix + " - create";
        String findUpdateDeleteResourceName = resourceNamePrefix + " - fud";

        String listFindRoleName = roleNamePrefix + ".list";
        String createRoleName = roleNamePrefix + ".create";
        String updateRoleName = roleNamePrefix + ".update";
        String deleteRoleName = roleNamePrefix + ".delete";

        String listFindPolicyName = KC_POLICY_ROLE_PREFIX + listFindRoleName;
        String createPolicyName = KC_POLICY_ROLE_PREFIX + createRoleName;
        String updatePolicyName = KC_POLICY_ROLE_PREFIX + updateRoleName;
        String deletePolicyName = KC_POLICY_ROLE_PREFIX + deleteRoleName;

        String listPermissionName = listResourceName + " - list";
        String findPermissionName = findUpdateDeleteResourceName + " - find";
        String createPermissionName = createResourceName + " - create";
        String updatePermissionName = findUpdateDeleteResourceName + " - update";
        String deletePermissionName = findUpdateDeleteResourceName + " - delete";

        String postScopeName = "POST";
        String putScopeName = "PUT";
        String deleteScopeName = "DELETE";
        ScopeRepresentation postScope = authResource.scopes().findByName(postScopeName);
        ScopeRepresentation putScope = authResource.scopes().findByName(putScopeName);
        ScopeRepresentation deleteScope = authResource.scopes().findByName(deleteScopeName);

        // ----------
        // Create/find a resource representing a LIST action type resource

        String[] resourceIds = protectedResource.find(null, listResourceName, null, null, null, null, false, false, null, 1);
        String resourceId = resourceIds.length > 0 ? resourceIds[0] : null;
        if (resourceId == null) {
            ResourceRepresentation resource = new ResourceRepresentation(listResourceName);
            resource.addScope(postScopeName);
            resource.setUris(listUris);
            resource.setOwner(new ResourceOwnerRepresentation(clientId));
            resource.setOwnerManagedAccess(false);
            protectedResource.create(resource);
            resourceIds = protectedResource.find(null, listResourceName, null, null, null, null, false, false, null, 1); // findByName(resourceName, username); // When Resource is owner based
            if (resourceIds.length == 0) {
                throw new BusinessException("Was not able to create a KC resource for a resource " + listResourceName);
            }
            resourceId = resourceIds[0];
        }

        // Create List policy and permission
        createPermissionAndPolicyForResource(resourceId, listPermissionName, listFindPolicyName, listFindRoleName, clientName, postScope, authResource);

        // ----------
        // Create/find a resource representing a Create action type resource

        resourceIds = protectedResource.find(null, createResourceName, null, null, null, null, false, false, null, 1);
        resourceId = resourceIds.length > 0 ? resourceIds[0] : null;
        if (resourceId == null) {
            ResourceRepresentation resource = new ResourceRepresentation(createResourceName);
            resource.addScope(postScopeName);
            resource.setUris(createUris);
            resource.setOwner(new ResourceOwnerRepresentation(clientId));
            resource.setOwnerManagedAccess(false);
            protectedResource.create(resource);
            resourceIds = protectedResource.find(null, createResourceName, null, null, null, null, false, false, null, 1); // findByName(resourceName, username); // When Resource is owner based
            if (resourceIds.length == 0) {
                throw new BusinessException("Was not able to create a KC resource for a resource " + createResourceName);
            }
            resourceId = resourceIds[0];
        }

        // Create Create policy and permission
        createPermissionAndPolicyForResource(resourceId, createPermissionName, createPolicyName, createRoleName, clientName, postScope, authResource);

        // ----------
        // Create/find a resource representing a Find, Create, Update, Delete action type resource

        resourceIds = protectedResource.find(null, findUpdateDeleteResourceName, null, null, null, null, false, false, null, 1);
        resourceId = resourceIds.length > 0 ? resourceIds[0] : null;
        if (resourceId == null) {
            ResourceRepresentation resource = new ResourceRepresentation(findUpdateDeleteResourceName);
            resource.addScope(postScopeName);
            resource.addScope(putScopeName);
            resource.addScope(deleteScopeName);
            resource.setUris(fudUris);
            resource.setOwner(new ResourceOwnerRepresentation(clientId));
            resource.setOwnerManagedAccess(false);
            protectedResource.create(resource);
            resourceIds = protectedResource.find(null, findUpdateDeleteResourceName, null, null, null, null, false, false, null, 1); // findByName(resourceName, username); // When Resource is owner based
            if (resourceIds.length == 0) {
                throw new BusinessException("Was not able to create a KC resource for a resource " + findUpdateDeleteResourceName);
            }
            resourceId = resourceIds[0];
        }

        // Create Find policy and permission
        createPermissionAndPolicyForResource(resourceId, findPermissionName, listFindPolicyName, listFindRoleName, clientName, postScope, authResource);
        // Create Update policy and permission
        createPermissionAndPolicyForResource(resourceId, updatePermissionName, updatePolicyName, updateRoleName, clientName, putScope, authResource);
        // Create Delete policy and permission
        createPermissionAndPolicyForResource(resourceId, deletePermissionName, deletePolicyName, deleteRoleName, clientName, deleteScope, authResource);

    }

    /**
     * Create permission and policy for a resource
     * 
     * @param resourceId Resource identifier
     * @param permissionName Permission name
     * @param policyName Policy name
     * @param roleName Role name
     * @param clientName Client name
     * @param scope Scope
     * @param authResource Authorization resource
     */
    private void createPermissionAndPolicyForResource(String resourceId, String permissionName, String policyName, String roleName, String clientName, ScopeRepresentation scope, AuthorizationResource authResource) {
        // Create/find a role based policy
        PoliciesResource resourcePolicies = authResource.policies();
        PolicyRepresentation rolePolicy = resourcePolicies.findByName(policyName);
        if (rolePolicy == null) {
            RolePolicyRepresentation newPolicy = new RolePolicyRepresentation();
            newPolicy.setName(policyName);
            newPolicy.addClientRole(clientName, roleName);
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
     * @param resourceNamePrefix Resource name prefix
     * @param permissionNamePrefix Permission name prefix
     * @param roleNamePrefix Role name to link a policy to prefix
     * @param authResource Authorization resource endpoint
     * @param protectedResource Protected resource endpoint
     */
    private void removeGenericApiAuthorizationResource(String resourceNamePrefix, String permissionNamePrefix, String roleNamePrefix, AuthorizationResource authResource, ProtectedResource protectedResource) {

        String listResourceName = resourceNamePrefix + " - list";
        String createResourceName = resourceNamePrefix + " - create";
        String findUpdateDeleteResourceName = resourceNamePrefix + " - fud";

        String listFindRoleName = roleNamePrefix + ".list";
        String createRoleName = roleNamePrefix + ".create";
        String updateRoleName = roleNamePrefix + ".update";
        String deleteRoleName = roleNamePrefix + ".delete";

        String listFindPolicyName = KC_POLICY_ROLE_PREFIX + listFindRoleName;
        String createPolicyName = KC_POLICY_ROLE_PREFIX + createRoleName;
        String updatePolicyName = KC_POLICY_ROLE_PREFIX + updateRoleName;
        String deletePolicyName = KC_POLICY_ROLE_PREFIX + deleteRoleName;

        String listPermissionName = listResourceName + " - list";
        String findPermissionName = findUpdateDeleteResourceName + " - find";
        String createPermissionName = createResourceName + " - create";
        String updatePermissionName = findUpdateDeleteResourceName + " - update";
        String deletePermissionName = findUpdateDeleteResourceName + " - delete";

        // Remove a resource permission
        String[] permissionNames = { listPermissionName, findPermissionName, createPermissionName, updatePermissionName, deletePermissionName };
        for (String permissionName : permissionNames) {
            ScopePermissionRepresentation resourcePermission = authResource.permissions().scope().findByName(permissionName);
            if (resourcePermission != null) {
                authResource.permissions().scope().findById(resourcePermission.getId()).remove();
            }
        }

        // Remove a resource
        String[] resourceNames = { listResourceName, createResourceName, findUpdateDeleteResourceName };
        for (String resourceName : resourceNames) {
            String[] resourceIds = protectedResource.find(null, resourceName, null, null, null, null, false, false, null, 1);
            String resourceId = resourceIds.length > 0 ? resourceIds[0] : null;
            if (resourceId != null) {
                protectedResource.delete(resourceId);
            }
        }

        // Remove a role based policy
        String[] policyNames = { listFindPolicyName, createPolicyName, updatePolicyName, deletePolicyName };
        for (String policyName : policyNames) {
            PoliciesResource resourcePolicies = authResource.policies();
            PolicyRepresentation rolePolicy = resourcePolicies.findByName(policyName);
            if (rolePolicy != null) {
                resourcePolicies.policy(rolePolicy.getId()).remove();
            }
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

        int newPermissionsCreated = 0;

        for (AccessScopeEnum accessScope : AccessScopeEnum.values()) {

            // Obtain KC REST resources. Moved here from above as sometime KC gives 401 error if execution takes too long
            Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);
            RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
            String clientId = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0).getId();
            ClientResource client = realmResource.clients().get(clientId);

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

    /**
     * Get user representation by username
     * @param username Username
     * @return {@link UserRepresentation}
     */
    public UserRepresentation getUserRepresentationByUsername(String username) throws ElementNotFoundException {
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        List<UserRepresentation> users = usersResource.search(username!=null?username.toLowerCase():null, true);
        for (UserRepresentation userRepresentation : users) {
            if (username.equalsIgnoreCase(userRepresentation.getUsername())) {
                return userRepresentation;
            }
        }
        throw new ElementNotFoundException("No user found with username " + username);
    }
}