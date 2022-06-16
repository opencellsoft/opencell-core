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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.wildfly.security.http.oidc.OidcPrincipal;

/**
 * Provides methods to deal with currently authenticated user
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class CurrentUserProvider {

    @Inject
    private UserInfoManagement userInfoManagement;

    @Resource
    private SessionContext ctx;

    @Inject
    private Instance<HttpServletRequest> requestInst;

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
    @SuppressWarnings("rawtypes")
    public void forceAuthentication(String userName, String providerCode) {

        OidcPrincipal oidcPrincipal = null;
        try {
            HttpServletRequest request = null;
            request = requestInst.get();
            if (request != null && request.getUserPrincipal() instanceof OidcPrincipal) {
                oidcPrincipal = (OidcPrincipal) request.getUserPrincipal();
            }
        } catch (Exception e) {
            // Ignore. Its the only way to inject request outside the http code trace
        }

        // Current user is already authenticated via OIDC, can't overwrite it
        if (oidcPrincipal != null) {
            log.warn("Current user is already authenticated, can't overwrite it OIDC principal: {}", oidcPrincipal.getName());
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
    @SuppressWarnings("rawtypes")
    public void reestablishAuthentication(MeveoUser lastCurrentUser) {

        OidcPrincipal oidcPrincipal = null;
        try {
            HttpServletRequest request = null;
            request = requestInst.get();
            if (request != null && request.getUserPrincipal() instanceof OidcPrincipal) {
                oidcPrincipal = (OidcPrincipal) request.getUserPrincipal();
            }
        } catch (Exception e) {
            // Ignore. Its the only way to inject request outside the http code trace
        }

        // Current user is already authenticated via OIDC, can't overwrite it
        if (oidcPrincipal == null) {

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
    @SuppressWarnings("rawtypes")
    public String getCurrentUserProviderCode() {

        OidcPrincipal oidcPrincipal = null;
        try {
            HttpServletRequest request = null;
            request = requestInst.get();
            if (request != null && request.getUserPrincipal() instanceof OidcPrincipal) {
                oidcPrincipal = (OidcPrincipal) request.getUserPrincipal();
            }
        } catch (Exception e) {
            // Ignore. Its the only way to inject request outside the http code trace
        }

        String providerCode = null;

        // Currently authenticated user via OIDC, get provider code from user properties
        if (oidcPrincipal != null) {
            providerCode = MeveoUserKeyCloakImpl.extractProviderCode(oidcPrincipal.getOidcSecurityContext().getIDToken());

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
    @SuppressWarnings({ "rawtypes" })
    public MeveoUser getCurrentUser(String providerCode, EntityManager em) {

        OidcPrincipal oidcPrincipal = null;
        try {
            HttpServletRequest request = null;
            request = requestInst.get();
            if (request != null && request.getUserPrincipal() instanceof OidcPrincipal) {
                oidcPrincipal = (OidcPrincipal) request.getUserPrincipal();
            }
        } catch (Exception e) {
            // Ignore. Its the only way to inject request outside the http code trace
        }

        String username = null;
        MeveoUser user = null;

        // Authenticated via OIDC
        if (oidcPrincipal != null) {

            username = MeveoUserKeyCloakImpl.extractUsername(oidcPrincipal.getOidcSecurityContext().getIDToken());
            user = new MeveoUserKeyCloakImpl(oidcPrincipal.getOidcSecurityContext().getIDToken(), oidcPrincipal.getOidcSecurityContext().getToken(), userInfoManagement.getAdditionalRoles(username, em),
                userInfoManagement.getRoleToPermissionMapping(providerCode, em));

            // User was forced authenticated, so need to lookup the rest of user information
        } else if (forcedUserUsername.get() != null) {
            username = forcedUserUsername.get();
            user = new MeveoUserKeyCloakImpl(ctx, forcedUserUsername.get(), getCurrentTenant(), userInfoManagement.getAdditionalRoles(username, em), userInfoManagement.getRoleToPermissionMapping(providerCode, em));

        } else {
            username = ctx.getCallerPrincipal().getName();
            user = new MeveoUserKeyCloakImpl(ctx, null, null, userInfoManagement.getAdditionalRoles(username, em), userInfoManagement.getRoleToPermissionMapping(providerCode, em));
        }

        // log.trace("getCurrentUser username={}, providerCode={}, forcedAuthentication {}/{} ", username, user != null ? user.getProviderCode() : null, getForcedUsername(),
        // getCurrentTenant());
        if (!userInfoManagement.supplementUserInApp(user, em, forcedUserUsername.get())) {
            userInfoManagement.createUserInApp(user, em, forcedUserUsername.get());
        }

        if (log.isTraceEnabled()) {
            log.trace("Current user is {}", user.toStringLong());
        }
        return user;
    }

    /**
     * Register a user in application if accessing for the first time with that username
     * 
     * @param currentUser Authenticated current user
     */

    /**
     * Invalidate cached role to permission mapping (usually after role save/update event)
     */
    public void invalidateRoleToPermissionMapping() {
        UserInfoManagement.invalidateRoleToPermissionMapping();
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
    private static String getCurrentTenant() {
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
     * Get roles by application. Applies only in case when user is authenticated via OIDC.
     *
     * @param currentUser Currently logged-in user
     * @return A list of roles grouped by application (keycloak client name). A realm level roles are identified by key "realm". Admin application (KC client opencell-web) contains a mix or realm roles, client roles,
     *         roles defined in opencell and their resolution to permissions.
     */
    @SuppressWarnings("rawtypes")
    public Map<String, List<String>> getRolesByApplication(MeveoUser currentUser) {

        OidcPrincipal oidcPrincipal = null;
        try {
            HttpServletRequest request = null;
            request = requestInst.get();
            if (request != null && request.getUserPrincipal() instanceof OidcPrincipal) {
                oidcPrincipal = (OidcPrincipal) request.getUserPrincipal();
            }
        } catch (Exception e) {
            // Ignore. Its the only way to inject request outside the http code trace
        }

        if (oidcPrincipal != null) {
            Map<String, List<String>> rolesByApplication = MeveoUserKeyCloakImpl.getRolesByApplication(oidcPrincipal.getOidcSecurityContext().getToken());

            // Supplement admin application roles with ones resolved in a current user,
            String adminClientName = System.getProperty("opencell.keycloak.client");
            List<String> adminRoles = rolesByApplication.get(adminClientName);
            if (adminRoles == null) {
                adminRoles = new ArrayList<String>();
            }
            adminRoles.addAll(currentUser.getRoles());
            rolesByApplication.put(adminClientName, adminRoles);

            return rolesByApplication;
        }
        return null;
    }

}