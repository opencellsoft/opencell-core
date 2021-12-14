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

package org.meveo.keycloak.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpStatus;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ResteasyClientProxyBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @since 10 Nov 2017
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0.1
 **/
@Stateless
public class KeycloakAdminClientService {

    @Inject
    private Logger log;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    /**
     * Reads the configuration from system property.
     * 
     * @return KeycloakAdminClientConfig
     */
    public KeycloakAdminClientConfig loadConfig() {
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

            log.debug("Found keycloak configuration: {}", keycloakAdminClientConfig);
        } catch (Exception e) {
            log.error("Error: Loading keycloak admin configuration. " + e.getMessage());
        }

        return keycloakAdminClientConfig;
    }

    /**
     * @param session keycloak session
     * @param keycloakAdminClientConfig keycloak admin client config.
     * @return instance of Keycloak.
     */
    private Keycloak getKeycloakClient(KeycloakSecurityContext session, KeycloakAdminClientConfig keycloakAdminClientConfig) {

        KeycloakBuilder keycloakBuilder = KeycloakBuilder.builder().serverUrl(keycloakAdminClientConfig.getServerUrl()).realm(keycloakAdminClientConfig.getRealm())
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS).clientId(keycloakAdminClientConfig.getClientId()).clientSecret(keycloakAdminClientConfig.getClientSecret())
            .authorization(session.getTokenString());

        keycloakBuilder.resteasyClient(new ResteasyClientProxyBuilder().connectionPoolSize(20).build());

        return keycloakBuilder.build();
    }

    /**
     * Creates a user in keycloak and assigns the role.
     * 
     * @param httpServletRequest Http request
     * @param postData Posted data to API
     * @param provider Provider code to be added as attribute
     * @return User identifier in Keycloak. Returns null in case use already was present in Keycloak.
     * @throws BusinessException User was not created in Keycloak.
     * @lastModifiedVersion 5.0.1
     */
    public String createUser(HttpServletRequest httpServletRequest, UserDto postData, String provider) throws BusinessException {

        // TODO Should check if user already exists in Keycloak. If so - add roles requested. If not - create a user in Keycloak.

        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmailVerified(true);
        if (!StringUtils.isBlank(postData.getUsername())) {
            user.setUsername(postData.getUsername());
        } else {
            user.setUsername(postData.getEmail());
        }
        user.setFirstName(postData.getFirstName());
        user.setLastName(postData.getLastName());
        user.setEmail(postData.getEmail());

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("origin", Arrays.asList("OPENCELL-API"));
        if (ParamBean.isMultitenancyEnabled() && !StringUtils.isBlank(provider)) {
            attributes.put("provider", Arrays.asList(provider));
        }

        user.setAttributes(attributes);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

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

        // check if realm role exists
        // find realm roles and assign to the newly create user
        List<RoleRepresentation> externalRolesRepresentation = new ArrayList<>();
        if (postData.getExternalRoles() != null && !postData.getExternalRoles().isEmpty()) {
            RolesResource rolesResource = realmResource.roles();

            for (RoleDto externalRole : postData.getExternalRoles()) {
                try {
                    RoleRepresentation tempRole = rolesResource.get(externalRole.getName()).toRepresentation();
                    externalRolesRepresentation.add(tempRole);
                } catch (NotFoundException e) {
                    // Lets not care if roles exist - if not they will be assigned in Keycloak directly
                    log.warn("Role {} not found in Keycloak. Please assign it to user {} manually in Keycloak", externalRole.getName(), user.getUsername());
                }
            }
        }

        // Create user (requires manage-users role)
        Response response = usersResource.create(user);

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            log.error("Keycloak user creation with http status.code={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());

            if (response.getStatus() == HttpStatus.SC_CONFLICT) {
                UserRepresentation existingUser = getUserRepresentationByUsername(usersResource, postData.getUsername());

                log.warn("A user with username {} and id {} already exists in Keycloak", user.getUsername(), existingUser.getId());
                return existingUser.getId();

            } else {
                throw new BusinessException("Unable to create user with httpStatusCode=" + response.getStatus());
            }
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        log.debug("User {} created in Keycloak with userId: {}", user.getUsername(), userId);

        usersResource.get(userId).roles().realmLevel().add(externalRolesRepresentation);

        ClientRepresentation opencellWebClient = realmResource.clients().findByClientId(keycloakAdminClientConfig.getClientId()).get(0);

        // Get client level role (requires view-clients role)
        RoleRepresentation apiRole = realmResource.clients().get(opencellWebClient.getId()).roles().get(KeycloakConstants.ROLE_API_ACCESS).toRepresentation();
        RoleRepresentation guiRole = realmResource.clients().get(opencellWebClient.getId()).roles().get(KeycloakConstants.ROLE_GUI_ACCESS).toRepresentation();
        RoleRepresentation adminRole = realmResource.clients().get(opencellWebClient.getId()).roles().get(KeycloakConstants.ROLE_ADMINISTRATEUR).toRepresentation();
        RoleRepresentation userManagementRole = realmResource.clients().get(opencellWebClient.getId()).roles().get(KeycloakConstants.ROLE_USER_MANAGEMENT).toRepresentation();

        // Assign client level role to user
        usersResource.get(userId).roles().clientLevel(opencellWebClient.getId()).add(Arrays.asList(apiRole, guiRole, adminRole, userManagementRole));

        // Define password credential
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(postData.getPassword());

        // Set password credential
        usersResource.get(userId).resetPassword(credential);

        return userId;
    }

    /**
     * Remove a role representation from list of role representation.
     * 
     * @param listRoleRepresentation list of role representation.
     * @param roleRepresentation role representation to remove.
     * @throws BusinessException business exception.
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    private List<RoleRepresentation> removeRole(List<RoleRepresentation> listRoleRepresentation, RoleRepresentation roleRepresentation) throws BusinessException {
        List<RoleRepresentation> updatedListRoleRepresentation = new ArrayList<>();
        for (RoleRepresentation roleRepresentationItem : listRoleRepresentation) {
            if (!roleRepresentation.getName().equalsIgnoreCase(roleRepresentationItem.getName())) {
                updatedListRoleRepresentation.add(roleRepresentationItem);
            }
        }
        return updatedListRoleRepresentation;
    }

    /**
     * Updates a user in keycloak. Also assigns the role.
     * 
     * @param httpServletRequest http request
     * @param postData posted data.
     * @throws BusinessException business exception.
     * @throws EntityDoesNotExistsException Role
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public void updateUser(HttpServletRequest httpServletRequest, UserDto postData) throws EntityDoesNotExistsException, BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        try {

            UserRepresentation userRepresentation = getUserRepresentationByUsername(usersResource, postData.getUsername());
            UserResource userResource = usersResource.get(userRepresentation.getId());

            userRepresentation.setFirstName(postData.getFirstName());
            userRepresentation.setLastName(postData.getLastName());
            userRepresentation.setEmail(postData.getEmail());

            // find realm roles and assign to the newly create user
            List<RoleRepresentation> rolesToAdd = new ArrayList<>();
            List<RoleRepresentation> rolesToDelete = realmResource.roles().list();

            if (postData.getExternalRoles() != null && !postData.getExternalRoles().isEmpty()) {
                RolesResource rolesResource = realmResource.roles();

                for (RoleDto externalRole : postData.getExternalRoles()) {
                    try {
                        RoleRepresentation tempRole = rolesResource.get(externalRole.getName()).toRepresentation();
                        rolesToAdd.add(tempRole);
                        rolesToDelete = removeRole(rolesToDelete, tempRole);
                    } catch (NotFoundException e) {
                        throw new EntityDoesNotExistsException(RoleRepresentation.class, externalRole.getName());
                    }
                }
                // add from posted data
                usersResource.get(userRepresentation.getId()).roles().realmLevel().add(rolesToAdd);
                // delete other roles
                usersResource.get(userRepresentation.getId()).roles().realmLevel().remove(rolesToDelete);
            }else if(postData.getExternalRoles() != null) {
                // delete other roles
                usersResource.get(userRepresentation.getId()).roles().realmLevel().remove(rolesToDelete);
            }
            
            userResource.update(userRepresentation);

            if (!StringUtils.isBlank(postData.getPassword())) {
                // Define password credential
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setTemporary(false);
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(postData.getPassword());

                // Set password credential
                userResource.resetPassword(credential);
            }
        } catch (Exception e) {
            throw new BusinessException("Failed updating user with error=" + e.getMessage());
        }
    }

    /**
     * Deletes a user in keycloak.
     * 
     * @param httpServletRequest http request
     * @param username user name
     * @throws BusinessException business exception.
     */
    public void deleteUser(HttpServletRequest httpServletRequest, String username) throws BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        try {
            UserRepresentation userRepresentation = getUserRepresentationByUsername(usersResource, username);

            // Create user (requires manage-users role)
            Response response = usersResource.delete(userRepresentation.getId());

            if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
                log.error("Keycloak user deletion with httpStatusCode={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
                throw new BusinessException("Unable to delete user with httpStatusCode=" + response.getStatus());
            }
        } catch (Exception e) {
            throw new BusinessException("Failed deleting user with error=" + e.getMessage());
        }
    }

    /**
     * Search for a user in keycloak via username.
     * 
     * @param httpServletRequest http request
     * @param username user name
     * @return list of role
     * @throws BusinessException business exception
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public List<RoleDto> findUserRoles(HttpServletRequest httpServletRequest, String username) throws BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        try {
            UserRepresentation userRepresentation = getUserRepresentationByUsername(usersResource, username);

            return userRepresentation != null ? usersResource.get(userRepresentation.getId()).roles().realmLevel().listEffective().stream()
                .filter(p -> !KeycloakConstants.ROLE_KEYCLOAK_DEFAULT_EXCLUDED.contains(p.getName())).map(p -> {
                    return new RoleDto(p.getName());
                }).collect(Collectors.toList()) : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<RoleDto>();
        }
    }

    /**
     * List all the realm roles in keycloak.
     * 
     * @param httpServletRequest http servlet request
     * @return list of role
     * @throws BusinessException business exception.
     */
    public List<RoleDto> listRoles(HttpServletRequest httpServletRequest) throws BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());

        try {
            return realmResource.roles().list().stream().map(p -> {
                return new RoleDto(p.getName());
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new BusinessException("Unable to list role.");
        }
    }

    /**
     * As the search function from keycloack doesn't perform exact search, we need to browse results to pick the exact username
     * 
     * @param usersResource Users resource
     * @param username Username
     * @return User information
     * @throws BusinessException business exception.
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.0
     */
    public UserRepresentation getUserRepresentationByUsername(UsersResource usersResource, String username) throws BusinessException {
        UserRepresentation userRepresentation = null;
        List<UserRepresentation> userRepresentations = usersResource.search(username, null, null, null, null, null);
        for (UserRepresentation userRepresentationListItem : userRepresentations) {
            if (username.equalsIgnoreCase(userRepresentationListItem.getUsername())) {
                userRepresentation = userRepresentationListItem;
            }
        }

        if (userRepresentation == null) {
            throw new BusinessException("Unable to find user on keycloack.");
        }

        return userRepresentation;
    }

    /**
     * Creates a user in keycloak. Also assigns the role. It will add a provider code attribute to the user if multitenancy is activated. The provider will be the same as the
     * current user.
     * 
     * @param httpServletRequest http request
     * @param postData posted data to API
     * @return user created id.
     * @throws BusinessException business exception
     * @throws EntityDoesNotExistsException entity does not exist exception.
     * @lastModifiedVersion 5.0.1
     */
    public String createUser(HttpServletRequest httpServletRequest, UserDto postData) throws BusinessException, EntityDoesNotExistsException {
        if (ParamBean.isMultitenancyEnabled() && !StringUtils.isBlank(currentUser.getProviderCode())) {
            return createUser(httpServletRequest, postData, currentUser.getProviderCode());
        }
        return createUser(httpServletRequest, postData, null);
    }
}