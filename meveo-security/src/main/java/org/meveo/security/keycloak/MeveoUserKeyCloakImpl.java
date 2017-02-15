package org.meveo.security.keycloak;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.SessionContext;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.meveo.security.MeveoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MeveoUserKeyCloakImpl extends MeveoUser {

    private static final long serialVersionUID = 1864122036421892837L;

    private static String CLAIM_PROVIDER = "provider";
    private static String RESOURCE_PROVIDER = "meveo";

    private Set<String> roles = new HashSet<>();

    private SessionContext securityContext;

    public MeveoUserKeyCloakImpl() {
    }

    Logger log = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("rawtypes")
    public MeveoUserKeyCloakImpl(SessionContext securityContext, String forcedSubject, String forcedUserName, String forcedProvider, Set<String> roles) {

        if (securityContext.getCallerPrincipal() instanceof KeycloakPrincipal) {
            KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) securityContext.getCallerPrincipal();
            KeycloakSecurityContext keycloakSecurityContext = keycloakPrincipal.getKeycloakSecurityContext();
            log.error("AKK produces keycloak from principal is {}, {}, {}, {}, {}", keycloakSecurityContext.getToken().getSubject(), keycloakSecurityContext.getToken().getName(),
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
            if (keycloakSecurityContext.getToken().getResourceAccess(RESOURCE_PROVIDER) != null) {
                this.roles.addAll(keycloakSecurityContext.getToken().getResourceAccess(RESOURCE_PROVIDER).getRoles());
            }
            authenticated = true;

        } else {
            this.securityContext = securityContext;

            log.error("AKK User is authenticated by jaas principal is {}, forcedUsername is {}, has user {}, has adminas {}", securityContext.getCallerPrincipal().getName(),
                forcedSubject, securityContext.isCallerInRole("user"), securityContext.isCallerInRole("adminas"));

            if (forcedSubject != null) {
                this.subject = forcedSubject;
                this.userName = forcedUserName;
                this.providerCode = forcedProvider;
                this.roles = roles;

                forcedAuthentication = true;
                authenticated = true;
            }
        }

        if (this.authenticated && this.providerCode == null) {
            throw new RuntimeException("User has no provider assigned");
        }
    }

    public boolean hasRole(String role) {

        // if (!authenticated) {
        // return false;
        // }

        if (securityContext != null) {
            return securityContext.isCallerInRole(role);

        } else if (roles != null) {
            return roles.contains(role);
        }
        return false;
    }
}