package org.meveo.security.keycloak;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
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
import org.meveo.security.ForcedAuthentication;
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

    @Inject
    private ForcedAuthentication forcedAuthentication;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void forceAuthentication(String currentUserUserName, String providerCode) {
        log.debug("forceAuthentication currentUserUserName={}, forcedProvider={}", currentUserUserName, providerCode);
        // Current user is already authenticated, can't overwrite it
        if (ctx.getCallerPrincipal() instanceof KeycloakPrincipal) {
            log.info("Current user is already authenticated, can't overwrite it keycloak: {}", ctx.getCallerPrincipal() instanceof KeycloakPrincipal);
            return;
        }
        forcedAuthentication.forceAuthentication(currentUserUserName, providerCode);
    }

    public String getCurrentUserProviderCode() {
        String providerCode = null;
        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal) && forcedAuthentication.getForcedProvider() != null) {
            providerCode = forcedAuthentication.getForcedProvider();
        } else {
            providerCode = MeveoUserKeyCloakImpl.extractProviderCode(ctx);
        }
        log.trace("Current provider {}, forcedAuthentication {}", providerCode, forcedAuthentication);
        return providerCode;

    }

    /**
     * return a current user from JAAS security context
     * 
     * @return Current user implementation
     */
    public MeveoUser getCurrentUser(String providerCode, EntityManager em) {

        String username = MeveoUserKeyCloakImpl.extractUsername(ctx, forcedAuthentication.getForcedUserUsername());

        MeveoUser user = null;

        // User was forced authenticated, so need to lookup the rest of user information
        if (!(ctx.getCallerPrincipal() instanceof KeycloakPrincipal) && forcedAuthentication.getForcedUserUsername() != null) {
            user = new MeveoUserKeyCloakImpl(ctx, forcedAuthentication.getForcedUserUsername(), forcedAuthentication.getForcedProvider(), getAdditionalRoles(username, em),
                getRoleToPermissionMapping(providerCode, em));

        } else {
            user = new MeveoUserKeyCloakImpl(ctx, null, null, getAdditionalRoles(username, em), getRoleToPermissionMapping(providerCode, em));
        }
        log.trace("getCurrentUser username={}, providerCode={}, forcedAuthentication {} ", username, user != null ? user.getProviderCode() : null, forcedAuthentication);
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

    @PostConstruct
    public void boo() {
        log.error("AKK @PostConstruct {}, {}", getClass().getSimpleName(), forcedAuthentication);
    }

    @PreDestroy
    public void muu() {
        log.error("AKK @PreDestroy {}, {}", getClass().getSimpleName(), forcedAuthentication);
    }

    @PostActivate
    public void aaa() {
        log.error("AKK @PostActivate {}, {}", getClass().getSimpleName(), forcedAuthentication);
    }

    @PrePassivate
    public void bbb() {
        log.error("AKK @PrePassivate {}, {}", getClass().getSimpleName(), forcedAuthentication);
    }

    public void init() {
        log.error("AKK @init {}, {}", getClass().getSimpleName(), forcedAuthentication);
    }
}