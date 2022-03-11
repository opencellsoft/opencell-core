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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.SessionContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Current Meveo user implementation when integrated with Keycloak authentication server
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi(edward.legaspi@manaty.net)
 */
public class MeveoUserKeyCloakImpl extends MeveoUser {

    private static final long serialVersionUID = 1864122036421892837L;

    /**
     * Field in token containing provider code
     */
    private static String CLAIM_PROVIDER = "provider";

    /**
     * JAAS security context
     */
    private SessionContext securityContext;

    Logger log = LoggerFactory.getLogger(getClass());

    public MeveoUserKeyCloakImpl() {
    }

    /**
     * Current user constructor
     * 
     * @param securityContext Current JAAS security context
     * @param forcedUserName Forced authentication username (when authenticated with @RunAs in job or any other timer trigger or at server startup)
     * @param forcedProvider Forced provider (when authenticated with @RunAs in job or any other timer trigger or at server startup)
     * @param additionalRoles Additional roles to assign
     * @param roleToPermissionMapping Role to permission mapping
     */
    @SuppressWarnings("rawtypes")
    public MeveoUserKeyCloakImpl(SessionContext securityContext, String forcedUserName, String forcedProvider, Set<String> additionalRoles, Map<String, Set<String>> roleToPermissionMapping) {

        if (securityContext.getCallerPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) securityContext.getCallerPrincipal();
            KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
            
            AccessToken accessToken = keycloakSecurityContext.getToken();

            // log.trace("Produced user from keycloak from principal is {}, {}, {}, {}, {}", accessToken.getSubject(),
            // accessToken.getName(),
            // accessToken.getRealmAccess() != null ? accessToken.getRealmAccess().getRoles() : null,
            // accessToken.getResourceAccess(RESOURCE_PROVIDER) != null ? accessToken.getResourceAccess(RESOURCE_PROVIDER).getRoles()
            // : null,
            // accessToken.getOtherClaims());
            
            this.subject = accessToken.getSubject();
            this.userName = accessToken.getPreferredUsername();
            this.fullName = accessToken.getName();
            this.authenticatedAt = accessToken.getIssuedAt();
            this.authenticationTokenId = accessToken.getSessionState();
            this.email = accessToken.getEmail();

            if (accessToken.getOtherClaims() != null) {
                this.providerCode = (String) accessToken.getOtherClaims().get(CLAIM_PROVIDER);
            }

            // Import realm roles
            if (accessToken.getRealmAccess() != null) {
                this.roles.addAll(accessToken.getRealmAccess().getRoles());
            }

            // Import client roles
            String clientName = System.getProperty("opencell.keycloak.client");
            if (accessToken.getResourceAccess(clientName) != null) {
                this.roles.addAll(accessToken.getResourceAccess(clientName).getRoles());
            }

            this.locale = accessToken.getLocale();
            this.authenticated = true;

        } else {
            this.securityContext = securityContext;

            log.trace("User is authenticated by jaas principal is {}, forcedUsername is {}", securityContext.getCallerPrincipal().getName(), forcedUserName);

            this.subject = securityContext.getCallerPrincipal().getName();

            if (forcedUserName != null) {
                this.userName = forcedUserName;
                this.providerCode = forcedProvider;
                forcedAuthentication = true;
                authenticated = true;
            }
        }

        // Resolve roles to permissions. At the end this.roles will contain both role and permission names.
        if (additionalRoles != null) {
            this.roles.addAll(additionalRoles);
        }

        Set<String> rolesToResolve = new HashSet<>(this.roles);

        if (roleToPermissionMapping != null) {
            for (String roleName : rolesToResolve) {
                if (roleToPermissionMapping.containsKey(roleName)) {
                    this.roles.addAll(roleToPermissionMapping.get(roleName));
                }
            }
        }

        // log.trace("Current user {} resolved roles/permissions {}", this.userName, this.roles);

        if (this.authenticated && !this.forcedAuthentication && this.providerCode == null) {
            // throw new RuntimeException("User has no provider assigned");
        }
    }

    @Override
    public boolean hasRole(String role) {

        // if (!authenticated) {
        // return false;
        // }

        if (securityContext != null) {
            if (securityContext.isCallerInRole(role)) {
                return true;
            }
        }

        return super.hasRole(role);
    }

    /**
     * Extract username from autentication token - applies to Keycloak implementation only, or default to a forced username
     * 
     * @param securityContext Security context
     * @param forcedUserName Forced username if not available from authentication token
     * @return Username
     */
    @SuppressWarnings("rawtypes")
    protected static String extractUsername(SessionContext securityContext, String forcedUserName) {

        if (securityContext.getCallerPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) securityContext.getCallerPrincipal();
            KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
            return keycloakSecurityContext.getToken().getPreferredUsername();

        } else {
            return forcedUserName;
        }
    }

    /**
     * Extract provider code from autentication token. Applies to Keycloak implementation only.
     * 
     * @param securityContext Security context
     * @return Provider code if set
     */
    @SuppressWarnings("rawtypes")
    protected static String extractProviderCode(SessionContext securityContext) {

        if (securityContext.getCallerPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) securityContext.getCallerPrincipal();
            KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
            if (keycloakSecurityContext.getToken().getOtherClaims() != null) {
                return (String) keycloakSecurityContext.getToken().getOtherClaims().get(CLAIM_PROVIDER);
            }

        }
        return null;
    }

    /**
     * Get roles by application. Applies to Keycloak implementation only.
     * 
     * @param securityContext Security context
     * @return A list of roles grouped by application (keycloak client name). A realm level roles are identified by key "realm".
     */
    protected static Map<String, Set<String>> getRolesByApplication(SessionContext securityContext) {

        if (securityContext.getCallerPrincipal() instanceof KeycloakPrincipal) {

            Map<String, Set<String>> rolesByApplication = new HashMap<String, Set<String>>();
            @SuppressWarnings("rawtypes")
            KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) securityContext.getCallerPrincipal();
            KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
            AccessToken accessToken = keycloakSecurityContext.getToken();

            // Realm roles
            Set<String> realmRoles = null;
            if (accessToken.getRealmAccess() != null) {
                realmRoles = accessToken.getRealmAccess().getRoles();
                rolesByApplication.put("realm", realmRoles);
            }

            // Client roles
            for (Entry<String, Access> client : accessToken.getResourceAccess().entrySet()) {
                rolesByApplication.put(client.getKey(), client.getValue().getRoles());

            }

            return rolesByApplication;
        }
        return null;
    }
}