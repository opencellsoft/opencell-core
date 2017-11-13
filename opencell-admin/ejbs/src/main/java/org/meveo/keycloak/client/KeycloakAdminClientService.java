package org.meveo.keycloak.client;

import java.util.Arrays;
import java.util.Collections;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 * @created 10 Nov 2017
 **/
@Stateless
public class KeycloakAdminClientService {

    private ParamBean paramBean = ParamBean.getInstance();

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

            keycloakAdminClientConfig.setAdminUsername(paramBean.getProperty("keycloak.realm.admin.username", "opencell.superadmin"));
            keycloakAdminClientConfig.setAdminPassword(paramBean.getProperty("keycloak.realm.admin.password", "opencell.superadmin"));

            log.debug("Found keycloak configuration: {}", keycloakAdminClientConfig);
        } catch (Exception e) {
            log.error("Error: Loading keycloak admin configuration. " + e.getMessage());
        }

        return keycloakAdminClientConfig;
    }

    public String createUser(KeycloakUserAccount keycloakUserAccount) throws BusinessException {
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();

        Keycloak keycloak = KeycloakBuilder.builder() //
            .serverUrl(keycloakAdminClientConfig.getServerUrl()) //
            .realm(keycloakAdminClientConfig.getRealm()) //
            .grantType(OAuth2Constants.PASSWORD) //
            .clientId(keycloakAdminClientConfig.getClientId()) //
            .clientSecret(keycloakAdminClientConfig.getClientSecret()) //
            .username(keycloakAdminClientConfig.getAdminUsername()) //
            .password(keycloakAdminClientConfig.getAdminPassword()) //
            .build();

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(keycloakUserAccount.getEmail());
        user.setFirstName(keycloakUserAccount.getFirstName());
        user.setLastName(keycloakUserAccount.getLastName());
        user.setEmail(keycloakUserAccount.getEmail());
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("OPENCELL-API")));

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource userResource = realmResource.users();

        // Create user (requires manage-users role)
        Response response = userResource.create(user);

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            log.error("Keycloak user creation with httpStatusCode={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            throw new BusinessException("Unable to create user with httpStatusCode=" + response.getStatus());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        log.debug("User created with userId: {}", userId);

        // Get realm role "apiAccess" (requires view-realm role)
        // RoleRepresentation apiAccessRole = realmResource.roles()//
        // .get(KeycloakConstants.ROLE_API_ACCESS).toRepresentation();

        // Assign realm role tester to user
        // userResource.get(userId).roles().realmLevel() //
        // .add(Arrays.asList(apiAccessRole));

        // Get client
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
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(keycloakUserAccount.getPassword());

        // Set password credential
        userResource.get(userId).resetPassword(credential);

        return userId;
    }

    public void updateUser(String userId, KeycloakUserAccount keycloakUserAccount) throws BusinessException {
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();

        Keycloak keycloak = KeycloakBuilder.builder() //
            .serverUrl(keycloakAdminClientConfig.getServerUrl()) //
            .realm(keycloakAdminClientConfig.getRealm()) //
            .grantType(OAuth2Constants.PASSWORD) //
            .clientId(keycloakAdminClientConfig.getClientId()) //
            .clientSecret(keycloakAdminClientConfig.getClientSecret()) //
            .username(keycloakAdminClientConfig.getAdminUsername()) //
            .password(keycloakAdminClientConfig.getAdminPassword()) //
            .build();

        // Define user
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(keycloakUserAccount.getEmail());
        user.setFirstName(keycloakUserAccount.getFirstName());
        user.setLastName(keycloakUserAccount.getLastName());
        user.setEmail(keycloakUserAccount.getEmail());

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UserResource userResource = realmResource.users().get(userId);

        userResource.update(user);
    }

    public void deleteUser(String userId) throws BusinessException {
        KeycloakAdminClientConfig keycloakAdminClientConfig = loadConfig();

        Keycloak keycloak = KeycloakBuilder.builder() //
            .serverUrl(keycloakAdminClientConfig.getServerUrl()) //
            .realm(keycloakAdminClientConfig.getRealm()) //
            .grantType(OAuth2Constants.PASSWORD) //
            .clientId(keycloakAdminClientConfig.getClientId()) //
            .clientSecret(keycloakAdminClientConfig.getClientSecret()) //
            .username(keycloakAdminClientConfig.getAdminUsername()) //
            .password(keycloakAdminClientConfig.getAdminPassword()) //
            .build();

        // Get realm
        RealmResource realmResource = keycloak.realm(keycloakAdminClientConfig.getRealm());
        UsersResource userResource = realmResource.users();

        // Create user (requires manage-users role)
        Response response = userResource.delete(userId);

        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()) {
            log.error("Keycloak user deletion with httpStatusCode={} and reason={}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            throw new BusinessException("Unable to delete user with httpStatusCode=" + response.getStatus());
        }
    }

}
