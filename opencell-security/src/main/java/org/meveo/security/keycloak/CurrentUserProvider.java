package org.meveo.security.keycloak;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.keycloak.KeycloakPrincipal;
import org.meveo.model.admin.User;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.model.shared.Name;
import org.meveo.security.ForcedAuthenticationHolder;
import org.meveo.security.MeveoUser;
import org.meveo.security.UserAuthTimeProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class CurrentUserProvider {

    /**
     * Map<providerCode, Map<roleName, rolePermissions>>
     */
    private static Map<String, Map<String, Set<String>>> roleToPermissionMapping;

    @Resource
    private SessionContext ctx;

    @Inject
    private Instance<UserAuthTimeProducer> userAuthTimeProducer;

    private Logger log = LoggerFactory.getLogger(getClass());

    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "NA";
        }
    };

    /**
     * Simulate authentication of a user. Allowed only when no security context is present, mostly used in jobs.
     * 
     * @param userName User name
     * @param providerCode Provider code
     */
    public void forceAuthentication(String userName, String providerCode) {
        log.debug("Force authentication to {}/{}", providerCode, userName);
        // Current user is already authenticated, can't overwrite it
        if (ctx.getCallerPrincipal() instanceof KeycloakPrincipal) {
            log.info("Current user is already authenticated, can't overwrite it keycloak: {}", ctx.getCallerPrincipal() instanceof KeycloakPrincipal);
            return;
        }
        ForcedAuthenticationHolder.setForcedUsername(userName);
        setCurrentTenant(providerCode);
    }

    /**
     * Get a current provider code. If value is currently not initialized, obtain it from a current user's security context
     * 
     * @return Current provider's code
     */
    public String getCurrentUserProviderCode() {

        String providerCode = null;

        if (!isCurrentTenantSet() && ctx.getCallerPrincipal() instanceof KeycloakPrincipal) {
            providerCode = MeveoUserKeyCloakImpl.extractProviderCode(ctx);
            log.trace("Will setting current provider to extracted value from KC token: {}", providerCode);
            setCurrentTenant(providerCode);

        } else if (isCurrentTenantSet()) {
            providerCode = getCurrentTenant();
            log.trace("Current provider is {}", providerCode);

        } else {
            log.trace("Current provider is not set");
        }

        return providerCode;

    }

    /**
     * Return a current user from JAAS security context
     * 
     * @return Current user implementation
     */
    public MeveoUser getCurrentUser(String providerCode, EntityManager em) {

        String username = MeveoUserKeyCloakImpl.extractUsername(ctx, ForcedAuthenticationHolder.getForcedUsername());

        MeveoUser user = null;

        // User was forced authenticated, so need to lookup the rest of user information
        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal) && ForcedAuthenticationHolder.getForcedUsername() != null) {
            user = new MeveoUserKeyCloakImpl(ctx, ForcedAuthenticationHolder.getForcedUsername(), getCurrentTenant(), getAdditionalRoles(username, em),
                getRoleToPermissionMapping(providerCode, em));

        } else {
            user = new MeveoUserKeyCloakImpl(ctx, null, null, getAdditionalRoles(username, em), getRoleToPermissionMapping(providerCode, em));
        }
        log.trace("getCurrentUser username={}, providerCode={}, forcedAuthentication {}/{} ", username, user != null ? user.getProviderCode() : null,
            ForcedAuthenticationHolder.getForcedUsername(), getCurrentTenant());
        supplementOrCreateUserInApp(user, em);

        log.trace("Current user is {}", user);
        return user;
    }

    /**
     * Register a user in application if accesing for the first time with that username
     * 
     * @param currentUser Authenticated current user
     */
    private void supplementOrCreateUserInApp(MeveoUser currentUser, EntityManager em) {

        // Takes care of anonymous users
        if (currentUser.getUserName() == null) {
            return;
        }

        // Create or retrieve current user
        try {
            User user = null;
            try {
                user = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", currentUser.getUserName().toLowerCase()).getSingleResult();

                if (userAuthTimeProducer.get().getAuthTime() != currentUser.getAuthTime()) {
                    userAuthTimeProducer.get().setAuthTime(currentUser.getAuthTime());
                    user.setLastLoginDate(new Date());
                    em.merge(user);
                    em.flush();
                }

                currentUser.setFullName(user.getNameOrUsername());

            } catch (NoResultException e) {

                user = new User();
                user.setUserName(currentUser.getUserName().toUpperCase());
                if (currentUser.getFullName() != null) {
                    if (user.getName() == null) {
                        user.setName(new Name());
                    }
                    int spacePos = currentUser.getFullName().indexOf(' ');
                    if (spacePos > 0) {
                        user.getName().setFirstName(currentUser.getFullName().substring(0, spacePos));
                        user.getName().setLastName(currentUser.getFullName().substring(spacePos + 1));
                    } else {
                        user.getName().setFirstName(currentUser.getFullName());
                    }
                }
                user.setLastLoginDate(new Date());
                user.updateAudit(currentUser);
                em.persist(user);
                em.flush();
                log.info("A new application user was registered with username {} and name {}", user.getUserName(), user.getName().getFullName());

            } catch (ContextNotActiveException e) {
                log.error("No session context={}", e.getMessage());
            }

        } catch (Exception e) {
            log.error("Failed to supplement current user information from db and/or create new user in db", e);
        }

    }

    /**
     * Return and load if necessary a mapping between roles and permissions
     * 
     * @return A mapping between roles and permissions
     */
    private Map<String, Set<String>> getRoleToPermissionMapping(String providerCode, EntityManager em) {

        synchronized (this) {
            if (CurrentUserProvider.roleToPermissionMapping == null || roleToPermissionMapping.get(providerCode) == null) {
                CurrentUserProvider.roleToPermissionMapping = new HashMap<>();

                try {
                    List<Role> userRoles = em.createNamedQuery("Role.getAllRoles", Role.class).getResultList();
                    Map<String, Set<String>> roleToPermissionMappingForProvider = new HashMap<>();

                    for (Role role : userRoles) {
                        Set<String> rolePermissions = new HashSet<>();
                        for (Permission permission : role.getAllPermissions()) {
                            rolePermissions.add(permission.getPermission());
                        }

                        roleToPermissionMappingForProvider.put(role.getName(), rolePermissions);
                    }
                    CurrentUserProvider.roleToPermissionMapping.put(providerCode, roleToPermissionMappingForProvider);
                } catch (Exception e) {
                    log.error("Failed to construct role to permission mapping", e);
                }
            }

            return CurrentUserProvider.roleToPermissionMapping.get(providerCode);
        }
    }

    /**
     * Invalidate cached role to permission mapping (usually after role save/update event)
     */
    public void invalidateRoleToPermissionMapping() {
        CurrentUserProvider.roleToPermissionMapping = null;
    }

    /**
     * Get additional roles that user has assigned in application
     * 
     * @param username Username to check
     * @return A set of role names that given username has in application
     */
    private Set<String> getAdditionalRoles(String username, EntityManager em) {

        // Takes care of anonymous users
        if (username == null) {
            return null;
        }

        try {
            User user = em.createNamedQuery("User.getByUsername", User.class).setParameter("username", username.toLowerCase()).getSingleResult();

            Set<String> additionalRoles = new HashSet<>();

            for (Role role : user.getRoles()) {
                additionalRoles.add(role.getName());
            }

            return additionalRoles;

        } catch (NoResultException e) {
            return null;

        } catch (Exception e) {
            log.error("Failed to retrieve additional roles for a user {}", username, e);
            return null;
        }
    }

    private static boolean isCurrentTenantSet() {
        return !"NA".equals(currentTenant.get());
    }

    private static String getCurrentTenant() {
        return currentTenant.get();
    }

    private static void setCurrentTenant(final String tenantName) {
        currentTenant.remove();
        currentTenant.set(tenantName);
    }
}