package org.meveo.keycloak.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ClientErrorException;
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
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @since 10 Nov 2017
 **/
@Stateless
public class KeycloakAdminClientService {

    @Inject
    private Logger log;

    /**
     * Reads the configuration from system property.
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
        Keycloak keycloak = KeycloakBuilder.builder() //
            .serverUrl(keycloakAdminClientConfig.getServerUrl()) //
            .realm(keycloakAdminClientConfig.getRealm()) //
            .grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
            .clientId(keycloakAdminClientConfig.getClientId()) //
            .clientSecret(keycloakAdminClientConfig.getClientSecret()) //
            .authorization(session.getTokenString()) //
            .build();

        return keycloak;
    }

    /**
     * Creates a user in keycloak. Also assigns the role.
     * @param httpServletRequest http request
     * @param postData posted data to API
     * @return user created id.
     * @throws BusinessException business exception
     * @throws EntityDoesNotExistsException entity does not exist exception.
     */
    public String createUser(HttpServletRequest httpServletRequest, UserDto postData) throws BusinessException, EntityDoesNotExistsException {
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
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("OPENCELL-API")));

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
                    throw new EntityDoesNotExistsException(RoleRepresentation.class, externalRole.getName());
                }
            }
        }

        // Create user (requires manage-users role)
        Response response = usersResource.create(user);

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            log.error("Keycloak user creation with http status.code={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());

            if (response.getStatus() == HttpStatus.SC_CONFLICT) {
                throw new BusinessException("Username or email already exists.");
            } else {
                throw new BusinessException("Unable to create user with httpStatusCode=" + response.getStatus());
            }
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        log.debug("User created with userId: {}", userId);

        usersResource.get(userId).roles().realmLevel().add(externalRolesRepresentation);

        ClientRepresentation opencellWebClient = realmResource.clients() //
            .findByClientId(keycloakAdminClientConfig.getClientId()).get(0);

        // Get client level role (requires view-clients role)
        RoleRepresentation apiRole = realmResource.clients().get(opencellWebClient.getId()) //
            .roles().get(KeycloakConstants.ROLE_API_ACCESS).toRepresentation();
        RoleRepresentation guiRole = realmResource.clients().get(opencellWebClient.getId()) //
            .roles().get(KeycloakConstants.ROLE_GUI_ACCESS).toRepresentation();
        RoleRepresentation adminRole = realmResource.clients().get(opencellWebClient.getId()) //
            .roles().get(KeycloakConstants.ROLE_ADMINISTRATEUR).toRepresentation();
        RoleRepresentation userManagementRole = realmResource.clients().get(opencellWebClient.getId()) //
            .roles().get(KeycloakConstants.ROLE_USER_MANAGEMENT).toRepresentation();

        // Assign client level role to user
        usersResource.get(userId).roles() //
            .clientLevel(opencellWebClient.getId()).add(Arrays.asList(apiRole, guiRole, adminRole, userManagementRole));

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
     * Updates a user in keycloak. Also assigns the role.
     * @param httpServletRequest http request
     * @param postData posted data.
     * @throws BusinessException business exception.
     */
    public void updateUser(HttpServletRequest httpServletRequest, UserDto postData) throws BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        try {
            UserRepresentation userRepresentation = usersResource.search(postData.getUsername(), null, null, null, 0, 1).get(0);
            UserResource userResource = usersResource.get(userRepresentation.getId());

            userRepresentation.setFirstName(postData.getFirstName());
            userRepresentation.setLastName(postData.getLastName());
            userRepresentation.setEmail(postData.getEmail());

            // find realm roles and assign to the newly create user
            List<RoleRepresentation> externalRolesRepresentation = new ArrayList<>();
            if (postData.getExternalRoles() != null && !postData.getExternalRoles().isEmpty()) {
                RolesResource rolesResource = realmResource.roles();

                for (RoleDto externalRole : postData.getExternalRoles()) {
                    try {
                        RoleRepresentation tempRole = rolesResource.get(externalRole.getName()).toRepresentation();
                        externalRolesRepresentation.add(tempRole);
                    } catch (NotFoundException e) {
                        throw new EntityDoesNotExistsException(RoleRepresentation.class, externalRole.getName());
                    }
                }

            }

            userResource.update(userRepresentation);

            // clear all roles
            usersResource.get(userRepresentation.getId()).roles().realmLevel().remove(realmResource.roles().list());
            // add from posted data
            usersResource.get(userRepresentation.getId()).roles().realmLevel().add(externalRolesRepresentation);

            if (!StringUtils.isBlank(postData.getPassword())) {
                // Define password credential
                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setTemporary(false);
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(postData.getPassword());

                // Set password credential
                userResource.resetPassword(credential);
            }
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatus() == HttpStatus.SC_CONFLICT) {
                throw new BusinessException("Username or email already exists.");
            } else {
                throw new BusinessException("Failed updating user with error=" + e.getMessage());
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
            UserRepresentation userRepresentation = usersResource.search(username, null, null, null, 0, 1).get(0);

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
     */
    public List<RoleDto> findUserRoles(HttpServletRequest httpServletRequest, String username) throws BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();

        try {
            UserRepresentation userRepresentation = usersResource.search(username, null, null, null, 0, 1).get(0);

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

}
