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

import java.net.HttpURLConnection;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.BearerAuthFilter;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.meveo.commons.utils.ResteasyClientProxyBuilder;
import org.meveo.model.persistence.JacksonUtil;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.wildfly.security.http.oidc.OidcPrincipal;
import org.wildfly.security.http.oidc.OidcSecurityContext;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Provides methods to deal with currently authenticated user
 * 
 * @author Andrius Karpavicius
 */
@Stateless
@PermitAll
public class CurrentUserProvider {

    @Resource
    private SessionContext ctx;

    @Inject
    private Instance<HttpServletRequest> requestInst;

    private static Logger log = LoggerFactory.getLogger(CurrentUserProvider.class);

    private final static ResteasyClient client = new ResteasyClientProxyBuilder().connectionPoolSize(100).maxPooledPerRoute(100).build();

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
     * Contains a currently logged in user
     */
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<String>() {
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
        setCurrentUsername(userName);
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
            setCurrentUsername(lastCurrentUser.getUserName());
            setCurrentTenant(lastCurrentUser.getProviderCode());
            log.debug("Reestablished authentication to {}/{}", lastCurrentUser.getUserName(), lastCurrentUser.getProviderCode());
        }
    }

    /**
     * Return a current user from JAAS security context
     * 
     * @return Current user implementation
     */
    @SuppressWarnings({ "rawtypes", "unused" })
    public MeveoUser getCurrentUser() {

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

            user = new MeveoUserKeyCloakImpl(oidcPrincipal.getOidcSecurityContext().getToken());
            username = user.getUserName();
            setCurrentUsername(username);
            setCurrentTenant(user.getProviderCode());

            // User was forced authenticated, so need to lookup the rest of user information
        } else if (forcedUserUsername.get() != null) {
            username = forcedUserUsername.get();
            user = new MeveoUserKeyCloakImpl(ctx, forcedUserUsername.get(), getCurrentTenant());

        } else {
            username = ctx.getCallerPrincipal().getName();
            user = new MeveoUserKeyCloakImpl(ctx, null, null);
            setCurrentUsername(user.getUserName());
        }

        // log.trace("getCurrentUser username={}, providerCode={}, forcedAuthentication {}/{} ", username, user != null ? user.getProviderCode() : null, getForcedUsername(),
        // getCurrentTenant());

        if (log.isTraceEnabled()) {
            log.trace("Current user is {}", user.toStringLong());
        }
        return user;
    }

    /**
     * Check if URLs for a given scope are allowed for a current user
     * 
     * @param request Http request
     * @param scope Scope to match
     * @param urls A list of URLs to match
     * @return A corresponding set of True/false if the URL is accessible
     */
    @SuppressWarnings("unchecked")
    public static boolean[] isLinkAccesible(HttpServletRequest request, String scope, String... urls) {

        boolean[] result = new boolean[urls.length];

        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null) {
            for (int i = 0; i < urls.length; i++) {
                result[i] = true;
            }
            return result;
        }
        OidcPrincipal<OidcSecurityContext> oidcPrincipal = (OidcPrincipal<OidcSecurityContext>) userPrincipal;

        String accessTokenString = oidcPrincipal.getOidcSecurityContext().getTokenString();

        KeycloakAdminClientConfig kcConnectionConfig = AuthenticationProvider.getKeycloakConfig();

        // Lookup client id from a client name
        if (kcConnectionConfig.getClientId() == null) {

            try {
                Keycloak keycloak = AuthenticationProvider.getKeycloakClient(kcConnectionConfig, accessTokenString);

                RealmResource realmResource = keycloak.realm(kcConnectionConfig.getRealm());
                String clientId = realmResource.clients().findByClientId(kcConnectionConfig.getClientName()).get(0).getId();
                kcConnectionConfig.setClientId(clientId);

                // User has no access to lookup clients in Keycloak
            } catch (ForbiddenException e) {
                return result;
            }
        }
        // AuthorizationResource authResource = realmResource.clients().get(clientId).authorization();

        ResteasyWebTarget target = client.target(kcConnectionConfig.getServerUrl() + "/admin/realms/" + kcConnectionConfig.getRealm() + "/clients/" + kcConnectionConfig.getClientId() + "/authz/resource-server/resource");
        target.register(new BearerAuthFilter(accessTokenString));
        target = target.queryParam("matchingUri", "true");
        ResteasyWebTarget targetNoUrl = target;

        AuthorizationRequest authRequest = new AuthorizationRequest();
        authRequest.setMetadata(new AuthorizationRequest.Metadata());

        Map<String, List<String>> urlToResourceMap = new HashMap<String, List<String>>();

        for (int i = 0; i < urls.length; i++) {
            String url = urls[i];

            // Get resources best matching the URL

            // List<ResourceRepresentation> resources = authResource.resources().find(null, url, null, null, null, null, null); -- API does not accept matchingUri=true and does not return resource ID.
            // List<ResourceRepresentation> resources = authzClient.protection(accessTokenString).resource().findByMatchingUri(url); -- API takes client id from a token. So if token was issues to opencell-portal client,
            // it will return an error that opencell-portal is not a resource server. It ignores the server configuration passed to AuthzClient initiation.

            target = targetNoUrl.queryParam("uri", url);

            Response response = target.request().get();
            if (response.getStatus() != HttpURLConnection.HTTP_OK) {
                result[i] = false;
                log.error("Failed to determine an authorization resource for url {} reason: {}, info {}", url, response.getStatus(), response.getStatusInfo().getReasonPhrase());
                continue;
            }

            List<ResourceRepresentation> resources = JacksonUtil.fromString(response.readEntity(String.class), new TypeReference<List<ResourceRepresentation>>() {
            });

            // List<ResourceRepresentation> resources = response.readEntity(new GenericType<List<ResourceRepresentation>>() { -- Does not work - ID field is not populated
            // });

            List<String> resourceIds = new ArrayList<String>(resources.size());
            urlToResourceMap.put(url, resourceIds);

            for (ResourceRepresentation resourceRepresentation : resources) {
                authRequest.addPermission(resourceRepresentation.getId(), request.getMethod());
                resourceIds.add(resourceRepresentation.getId());
            }
        }

        if (authRequest.getPermissions() == null || authRequest.getPermissions().getPermissions() == null || authRequest.getPermissions().getPermissions().isEmpty()) {
            return result;
        }

        try {
            AuthzClient authzClient = AuthenticationProvider.getKeycloakAuthzClient();
            List<Permission> permissions = authzClient.authorization(accessTokenString).getPermissions(authRequest);
            if (!permissions.isEmpty()) {

                for (int i = 0; i < urls.length; i++) {
                    String url = urls[i];

                    List<String> resourceIds = urlToResourceMap.get(url);
                    if (resourceIds != null) {
                        loop1: for (String resourceId : resourceIds) {
                            for (Object permission : permissions) {
                                if (resourceId.equals(((Map<String, Object>) permission).get("rsid"))) { // .getResourceId())) {
                                    result[i] = true;
                                    break loop1;
                                }
                            }
                        }
                        if (result[i] == false) {
                            log.error("No permission granted for resource {}, url {}", resourceIds, url);
                        }
                    }
                }
            }

        } catch (AuthorizationDeniedException e) {
            if (log.isErrorEnabled()) {
                log.error("No permissions granted for any of the urls {}", (Object) urls);
            }
        }

        return result;
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
     * Set currently logged in username value
     * 
     * @param username Currently logged in username
     */
    private static void setCurrentUsername(final String username) {
        currentUsername.set(username);
    }

    /**
     * Return a current user name from JAAS security context
     * 
     * @return Current user implementation
     */
    public static String getCurrentUsername() {

        return currentUsername.get();
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