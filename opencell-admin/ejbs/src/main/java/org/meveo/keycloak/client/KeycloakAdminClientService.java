package org.meveo.keycloak.client;

import java.util.Arrays;
import java.util.Collections;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @created 10 Nov 2017
 **/
@Stateless
public class KeycloakAdminClientService {

    @Inject
    private Logger log;

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

    public String createUser(HttpServletRequest httpServletRequest, KeycloakUserAccount keycloakUserAccount) throws BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        if (!StringUtils.isBlank(keycloakUserAccount.getUsername())) {
            user.setUsername(keycloakUserAccount.getUsername());
        } else {
            user.setUsername(keycloakUserAccount.getEmail());
        }
        user.setFirstName(keycloakUserAccount.getFirstName());
        user.setLastName(keycloakUserAccount.getLastName());
        user.setEmail(keycloakUserAccount.getEmail());
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("OPENCELL-API")));

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource userResource = realmResource.users();

        // does not work
        // Define password credential
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(keycloakUserAccount.getPassword());
        user.setCredentials(Arrays.asList(credential));

        // Map<String, List<String>> clientRoles = new HashMap<>();
        // clientRoles.put(keycloakAdminClientConfig.getClientId(),
        // Arrays.asList(KeycloakConstants.ROLE_API_ACCESS, KeycloakConstants.ROLE_GUI_ACCESS, KeycloakConstants.ROLE_ADMINISTRATEUR, KeycloakConstants.ROLE_USER_MANAGEMENT));

        // Create user (requires manage-users role)
        Response response = userResource.create(user);

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            log.error("Keycloak user creation with http status.code={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            throw new BusinessException("Unable to create user with httpStatusCode=" + response.getStatus());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        log.debug("User created with userId: {}", userId);

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
        userResource.get(userId).roles() //
            .clientLevel(opencellWebClient.getId()).add(Arrays.asList(apiRole, guiRole, adminRole, userManagementRole));

        // Define password credential
        // CredentialRepresentation credential = new CredentialRepresentation();
        // credential.setTemporary(false);
        // credential.setType(CredentialRepresentation.PASSWORD);
        // credential.setValue(keycloakUserAccount.getPassword());

        // Set password credential
        // userResource.get(userId).resetPassword(credential);

        return userId;
    }

    public void updateUser(HttpServletRequest httpServletRequest, KeycloakUserAccount keycloakUserAccount) throws BusinessException {
        KeycloakSecurityContext session = (KeycloakSecurityContext) httpServletRequest.getAttribute(KeycloakSecurityContext.class.getName());
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();
        Keycloak keycloak = getKeycloakClient(session, keycloakAdminClientConfig);

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource usersResource = realmResource.users();
        try {
            UserRepresentation userRepresentation = usersResource.search(keycloakUserAccount.getUsername(), null, null, null, 0, 1).get(0);
            UserResource userResource = usersResource.get(userRepresentation.getId());

            userRepresentation.setFirstName(keycloakUserAccount.getFirstName());
            userRepresentation.setLastName(keycloakUserAccount.getLastName());
            userRepresentation.setEmail(keycloakUserAccount.getEmail());

            userResource.update(userRepresentation);

            // Define password credential
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(keycloakUserAccount.getPassword());

            // Set password credential
            userResource.resetPassword(credential);
        } catch (Exception e) {
            throw new BusinessException("Failed deleting user with error=" + e.getMessage());
        }
    }

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

}
