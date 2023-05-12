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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.keycloak.AuthorizationContext;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.MDC;

/**
 * Provides methods to deal with currently authenticated user
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class CurrentUserProvider {

    @Resource
    private SessionContext ctx;

    @Inject
    private Logger log;

    /**
     * Contains a current tenant
     */
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "NA";
        }
    };

    /**
     * Contains a forced authentication user username
     */
    private static final ThreadLocal<String> forcedUserUsername = new ThreadLocal<String>();

    /**
     * Simulate authentication of a user. Allowed only when no security context is present, mostly used in jobs.
     * 
     * @param userName User name
     * @param providerCode Provider code
     */
    public void forceAuthentication(String userName, String providerCode) {
        // Current user is already authenticated, can't overwrite it
        if (ctx.getCallerPrincipal() instanceof KeycloakPrincipal) {
            log.warn("Current user is already authenticated, can't overwrite it keycloak: {}", ctx.getCallerPrincipal() instanceof KeycloakPrincipal);
            return;
        }

        if (providerCode == null) {
            MDC.remove("providerCode");
        } else {
            MDC.put("providerCode", providerCode);
        }
        log.debug("Force authentication to {}/{}", providerCode, userName);
        forcedUserUsername.set(userName);
        setCurrentTenant(providerCode);
    }

    /**
     * Reestablish authentication of a user. Allowed only when no security context is present.In case of multitenancy, when user authentication is forced as result of a fired trigger (scheduled jobs, other timed event
     * expirations), current user might be lost, thus there is a need to reestablish.
     * 
     * @param lastCurrentUser Last authenticated user. Note: Pass a unproxied version of MeveoUser (currentUser.unProxy()), as otherwise it will access CurrentUser producer method
     */
    public void reestablishAuthentication(MeveoUser lastCurrentUser) {

        // Current user is already authenticated, can't overwrite it
        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal)) {

            if (lastCurrentUser.getProviderCode() == null) {
                MDC.remove("providerCode");
            } else {
                MDC.put("providerCode", lastCurrentUser.getProviderCode());
            }

            forcedUserUsername.set(lastCurrentUser.getUserName());
            setCurrentTenant(lastCurrentUser.getProviderCode());
            log.debug("Reestablished authentication to {}/{}", lastCurrentUser.getUserName(), lastCurrentUser.getProviderCode());
        }
    }

    /**
     * Get a current provider code. If value is currently not initialized, obtain it from a current user's security context
     * 
     * @return Current provider's code
     */
    public String getCurrentUserProviderCode() {

        String providerCode = null;

        if (ctx.getCallerPrincipal() instanceof KeycloakPrincipal) {
            providerCode = MeveoUserKeyCloakImpl.extractProviderCode(ctx);

            if (providerCode == null) {
                MDC.remove("providerCode");
            } else {
                MDC.put("providerCode", providerCode);
            }

            // log.trace("Will setting current provider to extracted value from KC token: {}", providerCode);
            setCurrentTenant(providerCode);

        } else if (isCurrentTenantSet()) {
            providerCode = getCurrentTenant();

            if (providerCode == null) {
                MDC.remove("providerCode");
            } else {
                MDC.put("providerCode", providerCode);
            }

            // log.trace("Current provider is {}", providerCode);

            // } else {
            // log.trace("Current provider is not set");
        }

        return providerCode;

    }

    /**
     * Return a current user from JAAS security context
     * 
     * @param providerCode Provider code. Passed here, so not to look it up again
     * @param em Entity manager to use to retrieve user info
     * 
     * @return Current user implementation
     */
    public MeveoUser getCurrentUser(String providerCode, EntityManager em) {

        // String username = MeveoUserKeyCloakImpl.extractUsername(ctx, forcedUserUsername.get());

        MeveoUser user = null;

        // User was forced authenticated, so need to lookup the rest of user information
        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal) && forcedUserUsername.get() != null) {
            user = new MeveoUserKeyCloakImpl(ctx, forcedUserUsername.get(), getCurrentTenant(), null, null);

        } else {
            user = new MeveoUserKeyCloakImpl(ctx, null, null, null, null);
        }
        // log.trace("getCurrentUser username={}, providerCode={}, forcedAuthentication {}/{} ", username, user != null ? user.getProviderCode() : null, getForcedUsername(),
        // getCurrentTenant());

        if (log.isTraceEnabled()) {
            log.trace("Current user is {}", user.toStringLong());
        }
        return user;
    }

    /**
     * Check if current tenant value is set (differs from the initial value)
     * 
     * @return If current tenant value was set
     */
    private static boolean isCurrentTenantSet() {
        return !"NA".equals(currentTenant.get());
    }

    /**
     * Returns a current tenant/provider code. Note, this is raw storage only and might not be initialized. Use currentUserProvider.getCurrentUserProviderCode(); to retrieve and/or initialize current provider value
     * instead.
     * 
     * @return Current provider code
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Set current tenant/provider value
     * 
     * @param tenantName Current tenant/provider code
     */
    private static void setCurrentTenant(final String tenantName) {
        currentTenant.remove();
        currentTenant.set(tenantName);
    }

    /**
     * Get roles by application. Applies to Keycloak implementation only.
     *
     * @param currentUser Currently logged-in user
     * @return A list of roles grouped by application (keycloak client name). A realm level roles are identified by key "realm". Admin application (KC client opencell-web) contains a mix or realm roles, client roles,
     *         roles defined in opencell and their resolution to permissions.
     */
    public Map<String, Set<String>> getRolesByApplication(MeveoUser currentUser) {

        if (ctx.getCallerPrincipal() instanceof KeycloakPrincipal) {
            Map<String, Set<String>> rolesByApplication = MeveoUserKeyCloakImpl.getRolesByApplication(ctx);
            return rolesByApplication;
        }
        return null;
    }

    /**
     * Check if URL for a given scope, is allowed for a current user
     * 
     * @param url URL to check
     * @param scope Scope to match
     * @return True if the URL is accessible
     */
    @SuppressWarnings("rawtypes")
    public boolean isLinkAccesible(String url, String scope) {

        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal)) {
            return true;
        }

        String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();

        AuthorizationContext authContext = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getAuthorizationContext();

        AuthzClient authzClient = AuthenticationProvider.getKcAuthzClient();

        // Get resources best matching the URL
        List<ResourceRepresentation> resources = authzClient.protection(accessToken).resource().findByMatchingUri(url);

        boolean permited = false;
        for (ResourceRepresentation resourceRepresentation : resources) {
            permited = permited || authContext.hasPermission(resourceRepresentation.getName(), scope);
        }

        return permited;
    }

    /**
     * Check if URLs for a given scope are allowed for a current user
     * 
     * @param scope Scope to match
     * @param urls A list of URLs to match
     * @return A corresponding set of True/false if the URL is accessible
     */
    @SuppressWarnings("rawtypes")
    public boolean[] isLinkAccesible(String scope, String... urls) {

        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal)) {
            return new boolean[urls.length];
        }

        String accessToken = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getTokenString();

        AuthorizationContext authContext = ((KeycloakPrincipal) ctx.getCallerPrincipal()).getKeycloakSecurityContext().getAuthorizationContext();

        AuthzClient authzClient = AuthenticationProvider.getKcAuthzClient();

        boolean[] result = new boolean[urls.length];

        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];

            // Get resources best matching the URL
            List<ResourceRepresentation> resources = authzClient.protection(accessToken).resource().findByMatchingUri(url);

            boolean permited = false;
            for (ResourceRepresentation resourceRepresentation : resources) {
                permited = permited || authContext.hasPermission(resourceRepresentation.getName(), scope);
            }
            result[i] = permited;
        }
        return result;
    }

}