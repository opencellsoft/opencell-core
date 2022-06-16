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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.SessionContext;

import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.http.oidc.AccessToken;
import org.wildfly.security.http.oidc.IDToken;
import org.wildfly.security.http.oidc.RealmAccessClaim;

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

    private Logger log = LoggerFactory.getLogger(getClass());

    public MeveoUserKeyCloakImpl() {
    }

    /**
     * Current user constructor
     * 
     * @param idToken ID token
     * @param Access token Access token
     * @param additionalRoles Additional roles to assign
     * @param roleToPermissionMapping Role to permission mapping
     */
    public MeveoUserKeyCloakImpl(IDToken idToken, AccessToken accessToken, Set<String> additionalRoles, Map<String, Set<String>> roleToPermissionMapping) {

        log.trace("Produced user from Access token from principal is {}, {}, {}, {}", accessToken.getSubject(), idToken.getName(),
            accessToken.getRealmAccessClaim() != null ? accessToken.getRealmAccessClaim().getRoles() : null, accessToken.getClaimNames());

        this.subject = idToken.getSubject();
        this.userName = idToken.getPreferredUsername();
        this.fullName = idToken.getName();
        this.authenticatedAt = idToken.getIssuedAt();
        this.authenticationTokenId = idToken.getID();
        this.email = idToken.getEmail();
        this.providerCode = (String) idToken.getClaimValueAsString(CLAIM_PROVIDER);

        // Import realm roles
        if (accessToken.getRealmAccessClaim() != null) {
            this.roles.addAll(accessToken.getRealmAccessClaim().getRoles());
        }

        // Import client roles
        String clientName = System.getProperty("opencell.keycloak.client");
        if (accessToken.getResourceAccessClaim(clientName) != null) {
            this.roles.addAll(accessToken.getResourceAccessClaim(clientName).getRoles());
        }

        this.locale = idToken.getLocale();
        this.authenticated = true;

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

        // if (this.authenticated && !this.forcedAuthentication && this.providerCode == null) {
        // throw new RuntimeException("User has no provider assigned");
        // }
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
    public MeveoUserKeyCloakImpl(SessionContext securityContext, String forcedUserName, String forcedProvider, Set<String> additionalRoles, Map<String, Set<String>> roleToPermissionMapping) {

        this.securityContext = securityContext;

        log.trace("User is authenticated by jaas principal is {}, forcedUsername is {}", securityContext.getCallerPrincipal().getName(), forcedUserName);

        this.subject = securityContext.getCallerPrincipal().getName();

        if (forcedUserName != null) {
            this.userName = forcedUserName;
            this.providerCode = forcedProvider;
            forcedAuthentication = true;
            authenticated = true;
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

        // if (this.authenticated && !this.forcedAuthentication && this.providerCode == null) {
        // throw new RuntimeException("User has no provider assigned");
        // }
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
     * Extract username from authentication ID token. Applies only in case when user is authenticated by OIDC.
     * 
     * @param securityContext Security context
     * @return Username
     */
    protected static String extractUsername(IDToken idToken) {

        if (idToken != null) {
            return idToken.getPreferredUsername();
        }
        return null;
    }

    /**
     * Extract provider code from authentication ID token. Applies only in case when user is authenticated by OIDC.
     * 
     * @param idToken ID token
     * @return Provider code if set
     */
    protected static String extractProviderCode(IDToken idToken) {

        if (idToken == null) {
            return null;
        }
        return idToken.getClaimValueAsString(CLAIM_PROVIDER);
    }

    /**
     * Get roles by application from Access token. Applies only in case when user is authenticated by OIDC.
     * 
     * @param accessToken Access token
     * @return A list of roles grouped by application (keycloak client name). A realm level roles are identified by key "realm".
     */
    protected static Map<String, List<String>> getRolesByApplication(AccessToken accessToken) {

        if (accessToken == null) {
            return null;
        }
        Map<String, List<String>> rolesByApplication = new HashMap<String, List<String>>();

        // Realm roles
        List<String> realmRoles = null;
        if (accessToken.getRealmAccessClaim() != null) {
            realmRoles = accessToken.getRealmAccessClaim().getRoles();
            rolesByApplication.put("realm", realmRoles);
        }

        // Client roles
        for (Entry<String, RealmAccessClaim> client : accessToken.getResourceAccessClaim().entrySet()) {
            rolesByApplication.put(client.getKey(), client.getValue().getRoles());

        }

        return rolesByApplication;
    }
}