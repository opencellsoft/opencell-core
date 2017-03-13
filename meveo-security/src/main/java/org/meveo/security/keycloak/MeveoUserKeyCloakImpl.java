package org.meveo.security.keycloak;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.SessionContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Current Meveo user implementation when integrated with Keycloak authentication server
 * 
 * @author Andrius Karpavicius
 */
public class MeveoUserKeyCloakImpl extends MeveoUser {

    private static final long serialVersionUID = 1864122036421892837L;

    /**
     * Field in token containing provider code
     */
    private static String CLAIM_PROVIDER = "provider";
    private static String RESOURCE_PROVIDER = "opencell";

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
     * @param forcedUserName Forced authentication username (when authenticated with @RunAs in job)
     * @param additionalRoles Additional roles to assign
     * @param roleToPermissionMapping Role to permission mapping
     */
    @SuppressWarnings("rawtypes")
    public MeveoUserKeyCloakImpl(SessionContext securityContext, String forcedUserName, Set<String> additionalRoles, Map<String, Set<String>> roleToPermissionMapping) {

        if (securityContext.getCallerPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) securityContext.getCallerPrincipal();
            KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
            log.error("Produced user from keycloak from principal is {}, {}, {}, {}, {}", keycloakSecurityContext.getToken().getSubject(),
                keycloakSecurityContext.getToken().getName(),
                keycloakSecurityContext.getToken().getRealmAccess() != null ? keycloakSecurityContext.getToken().getRealmAccess().getRoles() : null,
                keycloakSecurityContext.getToken().getResourceAccess(RESOURCE_PROVIDER) != null ? keycloakSecurityContext.getToken().getResourceAccess(RESOURCE_PROVIDER).getRoles()
                        : null,
                keycloakSecurityContext.getToken().getOtherClaims());

            this.subject = keycloakSecurityContext.getToken().getSubject();
            this.userName = keycloakSecurityContext.getToken().getPreferredUsername();
            this.fullName = keycloakSecurityContext.getToken().getName();

            if (keycloakSecurityContext.getToken().getOtherClaims() != null) {
                this.providerCode = (String) keycloakSecurityContext.getToken().getOtherClaims().get(CLAIM_PROVIDER);
            }

            // Import roles
            if (keycloakSecurityContext.getToken().getRealmAccess() != null) {
                this.roles.addAll(keycloakSecurityContext.getToken().getRealmAccess().getRoles());
            }
            // TODO should add all roles from all resource providers?? as name should not be hardcoded
            if (keycloakSecurityContext.getToken().getResourceAccess(RESOURCE_PROVIDER) != null) {
                this.roles.addAll(keycloakSecurityContext.getToken().getResourceAccess(RESOURCE_PROVIDER).getRoles());
            }
            authenticated = true;

        } else {
            this.securityContext = securityContext;

            log.error("User is authenticated by jaas principal is {}, forcedUsername is {}", securityContext.getCallerPrincipal().getName(), forcedUserName);

            this.subject = securityContext.getCallerPrincipal().getName();
            if (forcedUserName != null) {
                this.userName = forcedUserName;

                forcedAuthentication = true;
                authenticated = true;
            }
        }

        // Resolve roles to permissions. At the end this.roles will contain both role and permission names.
        Set<String> rolesToResolve = new HashSet<>(this.roles);
        if (additionalRoles != null) {
            rolesToResolve.addAll(additionalRoles);
            this.roles.addAll(additionalRoles);
        }

        for (String roleName : rolesToResolve) {
            if (roleToPermissionMapping.containsKey(roleName)) {
                this.roles.addAll(roleToPermissionMapping.get(roleName));
            }
        }

        log.trace("Current user {} resolved roles/permissions {}", this.userName, this.roles);
        
        if (this.authenticated && !this.forcedAuthentication && this.providerCode == null) {
            throw new RuntimeException("User has no provider assigned");
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
}