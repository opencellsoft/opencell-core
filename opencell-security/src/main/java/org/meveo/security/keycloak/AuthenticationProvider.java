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

package org.meveo.security.keycloak;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.meveo.commons.utils.ResteasyClientProxyBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.security.KeyCloackConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class AuthenticationProvider {

    @Inject
    private HttpServletRequest httpRequest;

    private static Logger log = LoggerFactory.getLogger(AuthenticationProvider.class);

    private static final KeycloakAdminClientConfig keycloakAdminClientConfig = loadKeycloakConfig();

    public String logout() {
        try {
            httpRequest.logout();
            HttpSession session = httpRequest.getSession();
            session.invalidate();

        } catch (ServletException e) {
            log.error("Failed to logout", e);
        }
        return "indexPage";
    }

    public String getAccountUrl() {
        String kcUrl = System.getProperty("opencell.keycloak.url");
        String kcRealm = System.getProperty("opencell.keycloak.realm");
        String acctUri = KeycloakUriBuilder.fromUri(kcUrl).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH).build(kcRealm).toString();
        return acctUri + "?faces-redirect=true";
    }

    public String getNewUserUrl() {
        String kcUrl = System.getProperty("opencell.keycloak.url");
        String kcRealm = System.getProperty("opencell.keycloak.realm");
        String newUserUrl = KeycloakUriBuilder.fromUri(kcUrl).path(KeyCloackConstants.NEW_USER_PATH).build(kcRealm).toString();
        log.debug(" newUserUrl = {} ", newUserUrl);
        try {
            return URLDecoder.decode(newUserUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.debug(" Error decoding newUserUrl {} ", newUserUrl, e);
            return newUserUrl + "?faces-redirect=true";
        }
    }

    public String getAuthPath() {
        String kcUrl = System.getProperty("opencell.keycloak.url");
        String kcRealm = System.getProperty("opencell.keycloak.realm");
        String uri = KeycloakUriBuilder.fromUri(kcUrl).path(ServiceUrlConstants.AUTH_PATH).build(kcRealm).toString();
        return uri + "?faces-redirect=true";
    }

    /**
     * Get Keycloak authorization client
     * 
     * @return Keycloak authorization client endpoint
     */
    public static AuthzClient getKeycloakAuthzClient() {

        KeycloakAdminClientConfig kcConfig = getKeycloakConfig();

        Map<String, Object> credentials = new HashMap<String, Object>();
        credentials.put("secret", kcConfig.getClientSecret());
        Configuration configuration = new Configuration(kcConfig.getServerUrl(), kcConfig.getRealm(), kcConfig.getClientName(), credentials, null);

        AuthzClient authzClient = AuthzClient.create(configuration);

        return authzClient;
    }

    /**
     * Returns Keycloak connection configuration from system properties:
     * <ul>
     * <li><b>opencell.keycloak.url-internal</b> is used for internal communication with Keycloak and</li>
     * <li><b>opencell.keycloak.url</b> is used for a redirect to login in a browser</li>
     * <li><b>opencell.keycloak.realm</b> realm name</li>
     * <li><b>opencell.keycloak.client</b> client name</li>
     * <li><b>opencell.keycloak.secret</b> client secret value</li>
     * </ul>
     * 
     * @return KeycloakAdminClientConfig
     */
    public static KeycloakAdminClientConfig getKeycloakConfig() {
        return keycloakAdminClientConfig;
    }

    /**
     * Reads Keycloak connection configuration from system properties:
     * <ul>
     * <li><b>opencell.keycloak.url-internal</b> is used for internal communication with Keycloak and</li>
     * <li><b>opencell.keycloak.url</b> is used for a redirect to login in a browser</li>
     * <li><b>opencell.keycloak.realm</b> realm name</li>
     * <li><b>opencell.keycloak.client</b> client name</li>
     * <li><b>opencell.keycloak.secret</b> client secret value</li>
     * </ul>
     * 
     * @return KeycloakAdminClientConfig
     */
    private static KeycloakAdminClientConfig loadKeycloakConfig() {
        KeycloakAdminClientConfig keycloakAdminClientConfig = new KeycloakAdminClientConfig();

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
            keycloakAdminClientConfig.setClientName(clientId);
        }
        String clientSecret = System.getProperty("opencell.keycloak.secret");
        if (!StringUtils.isBlank(clientSecret)) {
            keycloakAdminClientConfig.setClientSecret(clientSecret);
        }

        return keycloakAdminClientConfig;
    }

    /**
     * Get an instance of a Keycloak administration client
     * 
     * @param keycloakAdminClientConfig Keycloak connection configuration. Optional. If not provided a default Keycloak connection configuration from server startup parameters will be used. See
     *        {@link KeycloakAdminClientServiceAdminClientService.loadConfig()}
     * @param accessToken Currently authenticated user's access token as a string
     * @return instance of Keycloak administration client
     */
    public static Keycloak getKeycloakClient(KeycloakAdminClientConfig keycloakAdminClientConfig, String accessToken) {
        
        if (keycloakAdminClientConfig == null) {
            keycloakAdminClientConfig = getKeycloakConfig();
        }

        KeycloakBuilder keycloakBuilder = KeycloakBuilder.builder().serverUrl(keycloakAdminClientConfig.getServerUrl()).realm(keycloakAdminClientConfig.getRealm()).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
            .clientId(keycloakAdminClientConfig.getClientName()).clientSecret(keycloakAdminClientConfig.getClientSecret()).authorization(accessToken);

        keycloakBuilder.resteasyClient(new ResteasyClientProxyBuilder().connectionPoolSize(2).maxPooledPerRoute(2).build());

        return keycloakBuilder.build();
    }
}